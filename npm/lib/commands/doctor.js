import { existsSync } from "node:fs";
import path from "node:path";
import { execCordlang, findCordlang } from "@/npm/lib/cordlang.js";
import { packageRoot, templateDir } from "@/npm/lib/paths.js";
import { DEFAULT_TEMPLATE, MANIFEST_CORD, MANIFEST_RUNIX } from "@/npm/lib/project.js";
import { LANGUAGE, RUNIX_VERSION } from "@/npm/lib/version.js";

export function doctor() {
  console.log(`runix ${RUNIX_VERSION} (npm)`);
  console.log(`language: ${LANGUAGE} — this package does not parse .cord`);

  const cord = findCordlang();
  if (!cord) {
    console.error(
      "runix doctor: cordlang not found\n" +
        "  set RUNIX_CORDLANG to the executable, add it to PATH,\n" +
        "  or keep a sibling clone at ../cordlang"
    );
    return 1;
  }
  console.log(`cordlang: ${cord}`);
  const ver = execCordlang(["--version"], { capture: true });
  if (ver.status !== 0) {
    process.stderr.write(ver.stderr || "runix doctor: cordlang --version failed\n");
    return 1;
  }
  process.stdout.write(ver.stdout);

  const tmpl = templateDir(DEFAULT_TEMPLATE);
  if (existsSync(tmpl)) console.log(`templates: ${tmpl}`);
  else console.log("templates: missing from this install — reinstall the runix package");

  console.log(`package: ${packageRoot()}`);
  console.log(`node: ${process.version}`);

  const cwd = process.cwd();
  if (existsSync(path.join(cwd, MANIFEST_CORD))) {
    console.log(`project: ${cwd} (${MANIFEST_CORD})`);
  } else {
    console.log(`project: (no ${MANIFEST_CORD} in cwd — not an app directory)`);
  }
  if (existsSync(path.join(cwd, MANIFEST_RUNIX))) console.log(`${MANIFEST_RUNIX}: yes`);
  console.log("ok");
  return 0;
}
