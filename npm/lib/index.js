/** Public Node API. Registers `@/` (package root) then loads the rest. */
import { register } from "node:module";
import { dirname, join } from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
register(pathToFileURL(join(here, "../loader.mjs")).href);

const version = await import("@/npm/lib/version.js");
const cord = await import("@/npm/lib/cordlang.js");
const cli = await import("@/npm/lib/cli.js");
const init = await import("@/npm/lib/commands/init.js");
const project = await import("@/npm/lib/project.js");

export const RUNIX_VERSION = version.RUNIX_VERSION;
export const LANGUAGE = version.LANGUAGE;
export const findCordlang = cord.findCordlang;
export const execCordlang = cord.execCordlang;
export const runCli = cli.runCli;
export const initProject = init.initProject;
export const MANIFEST_CORD = project.MANIFEST_CORD;
export const MANIFEST_RUNIX = project.MANIFEST_RUNIX;
export const DEFAULT_ENTRY = project.DEFAULT_ENTRY;
export const DEFAULT_NAME = project.DEFAULT_NAME;
export const DEFAULT_TEMPLATE = project.DEFAULT_TEMPLATE;
export const DIST_RUNIX = project.DIST_RUNIX;
export const TEMPLATES = project.TEMPLATES;
export const projectNameOk = project.projectNameOk;
