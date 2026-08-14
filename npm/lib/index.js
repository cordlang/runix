/** Public Node API for the Runix framework package.
 *  Cordlang remains the language; this module does not parse .cord.
 */
export { RUNIX_VERSION, LANGUAGE } from "./version.js";
export { findCordlang, execCordlang } from "./cordlang.js";
export { runCli } from "./cli.js";
export { initProject } from "./commands/init.js";
export {
  MANIFEST_CORD,
  MANIFEST_RUNIX,
  DEFAULT_ENTRY,
  DEFAULT_NAME,
  DEFAULT_TEMPLATE,
  DIST_RUNIX,
  TEMPLATES,
  projectNameOk
} from "./project.js";
