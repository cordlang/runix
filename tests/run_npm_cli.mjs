#!/usr/bin/env node
import { mkdtempSync, existsSync, rmSync, readFileSync } from "node:fs";
import { tmpdir } from "node:os";
import path from "node:path";
import { fileURLToPath } from "node:url";
import { runCli } from "@/npm/lib/cli.js";
import { RUNIX_VERSION } from "@/npm/lib/version.js";
import { projectNameOk } from "@/npm/lib/project.js";

const root = path.resolve(path.dirname(fileURLToPath(import.meta.url)), "..");
let failed = 0;

function assert(cond, msg) {
  if (!cond) {
    console.error("FAIL:", msg);
    failed++;
  } else {
    console.log("ok", msg);
  }
}

const logs = [];
const origLog = console.log;
const origErr = console.error;
function capture() {
  logs.length = 0;
  console.log = (...a) => {
    logs.push(a.join(" "));
    origLog(...a);
  };
  console.error = (...a) => {
    logs.push(a.join(" "));
    origErr(...a);
  };
}
function restore() {
  console.log = origLog;
  console.error = origErr;
}

process.chdir(root);

capture();
const ver = await runCli(["--version"]);
restore();
assert(ver === 0, "version exit 0");
assert(logs.some((l) => l.includes(RUNIX_VERSION)), "version string");

capture();
const help = await runCli(["help"]);
restore();
assert(help === 0, "help exit 0");
assert(logs.some((l) => l.includes("doctor")), "help lists doctor");
assert(logs.some((l) => l.includes("init")), "help lists init");

assert(projectNameOk("my-app"), "name my-app");
assert(!projectNameOk("../x"), "reject path name");
assert(!projectNameOk(""), "reject empty name");

const tmp = mkdtempSync(path.join(tmpdir(), "runix-npm-"));
try {
  process.chdir(tmp);
  capture();
  const initRc = await runCli(["init", "demo"]);
  restore();
  assert(initRc === 0, "init starter");
  assert(existsSync(path.join(tmp, "demo", "src", "app.cord")), "starter app.cord");
  assert(existsSync(path.join(tmp, "demo", "runix.json")), "starter runix.json");
  const rx = JSON.parse(readFileSync(path.join(tmp, "demo", "runix.json"), "utf8"));
  assert(rx.framework === "runix", "runix.json framework");

  capture();
  const landRc = await runCli(["init", "land", "--template", "landing"]);
  restore();
  assert(landRc === 0, "init landing");
  assert(existsSync(path.join(tmp, "land", "src", "pages", "HomePage.cord")), "landing page");

  capture();
  const badT = await runCli(["init", "x", "--template", "nextjs"]);
  restore();
  assert(badT === 1, "unknown template fails");
} finally {
  process.chdir(root);
  rmSync(tmp, { recursive: true, force: true });
}

if (failed) {
  console.error(`\n${failed} failed`);
  process.exit(1);
}
console.log("\nPASS: npm CLI");
