import { existsSync } from "node:fs";
import { execCordlang } from "../cordlang.js";
import { MANIFEST_CORD } from "../project.js";

export function devProject(noOpen) {
  if (!existsSync(MANIFEST_CORD)) {
    console.error(`runix dev: no ${MANIFEST_CORD} in cwd (run \`runix init\` first)`);
    return 1;
  }
  return execCordlang(noOpen ? ["run", "--no-open"] : ["run"]);
}
