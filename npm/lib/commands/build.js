import { cpSync, existsSync, mkdirSync, rmSync } from "node:fs";
import { execCordlang } from "@/npm/lib/cordlang.js";
import { DIST_ESM, DIST_RUNIX, MANIFEST_CORD } from "@/npm/lib/project.js";

export function buildProject() {
  if (!existsSync(MANIFEST_CORD)) {
    console.error(`runix build: no ${MANIFEST_CORD} in cwd`);
    return 1;
  }
  const rc = execCordlang(["build", "esm"]);
  if (rc !== 0) return rc;
  if (!existsSync(DIST_ESM)) {
    console.error(`runix build: expected ${DIST_ESM} after cordlang build esm`);
    return 1;
  }
  rmSync(DIST_RUNIX, { recursive: true, force: true });
  mkdirSync(DIST_RUNIX, { recursive: true });
  cpSync(DIST_ESM, DIST_RUNIX, { recursive: true });
  console.log(`runix build: ${DIST_RUNIX}`);
  console.log(
    "  (Fase 1: ESM preview export. HTML-per-route is Fase 2 — docs/ROADMAP.md)"
  );
  return 0;
}
