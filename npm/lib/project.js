export const MANIFEST_CORD = "cordlang.json";
export const MANIFEST_RUNIX = "runix.json";
export const DEFAULT_ENTRY = "src/app.cord";
export const DEFAULT_NAME = "my-app";
export const DEFAULT_TEMPLATE = "starter";
export const DIST_ESM = "dist/esm";
export const DIST_RUNIX = "dist/runix";
export const TEMPLATES = ["starter", "landing"];

/** Letter or '_' first; then alnum, '-' or '_'. Same rule as domain/project.c. */
export function projectNameOk(name) {
  if (!name || typeof name !== "string") return false;
  if (!/^[A-Za-z_][A-Za-z0-9_-]*$/.test(name)) return false;
  return name !== "." && name !== "..";
}
