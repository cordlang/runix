import { spawnSync } from "node:child_process";
import { existsSync } from "node:fs";
import path from "node:path";
import { walkUp } from "@/npm/lib/paths.js";

function which(cmd) {
  const tool = process.platform === "win32" ? "where" : "which";
  const r = spawnSync(tool, [cmd], { encoding: "utf8" });
  if (r.status !== 0) return null;
  const line = (r.stdout || "").split(/\r?\n/).map((s) => s.trim()).find(Boolean);
  return line || null;
}

/**
 * Resolve the Cordlang language CLI. Runix never re-parses .cord.
 * Order: RUNIX_CORDLANG → PATH → sibling ../cordlang checkout.
 */
export function findCordlang(cwd = process.cwd()) {
  const env = process.env.RUNIX_CORDLANG;
  if (env && existsSync(env)) return path.resolve(env);

  const onPath = which("cordlang") || which("cordlang.exe");
  if (onPath && existsSync(onPath)) return onPath;

  const names =
    process.platform === "win32"
      ? ["cordlang.exe", "cordlang"]
      : ["cordlang", "cordlang.exe"];

  const roots = [cwd, path.resolve(cwd, "..")];
  for (const root of roots) {
    for (const name of names) {
      const hit = walkUp(root, path.join("cordlang", name));
      if (hit) return hit;
    }
  }
  return null;
}

export function execCordlang(args, opts = {}) {
  const exe = findCordlang(opts.cwd || process.cwd());
  if (!exe) {
    console.error(
      "runix: cordlang not found\n" +
        "  set RUNIX_CORDLANG, add cordlang to PATH,\n" +
        "  or keep a sibling clone at ../cordlang"
    );
    return 1;
  }
  const r = spawnSync(exe, args, {
    stdio: opts.capture ? ["ignore", "pipe", "pipe"] : "inherit",
    encoding: "utf8",
    cwd: opts.cwd || process.cwd(),
    env: process.env
  });
  if (opts.capture) {
    return {
      status: r.status ?? 1,
      stdout: r.stdout || "",
      stderr: r.stderr || ""
    };
  }
  if (r.error) {
    console.error("runix: failed to start cordlang:", r.error.message);
    return 1;
  }
  return r.status ?? 1;
}
