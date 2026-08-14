import { existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

const here = path.dirname(fileURLToPath(import.meta.url));

/** Repo / package root (contains templates/, package.json). */
export function packageRoot() {
  return path.resolve(here, "..", "..");
}

export function templateDir(name) {
  return path.join(packageRoot(), "templates", name);
}

export function walkUp(start, relpath, maxDepth = 12) {
  let cur = path.resolve(start);
  for (let i = 0; i < maxDepth; i++) {
    const cand = path.join(cur, relpath);
    if (existsSync(cand)) return cand;
    const parent = path.dirname(cur);
    if (parent === cur) break;
    cur = parent;
  }
  return null;
}
