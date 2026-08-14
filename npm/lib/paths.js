import { existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath } from "node:url";

/** Repo / package root (contains templates/, package.json). Walks up — no ../../. */
export function packageRoot() {
  let cur = path.dirname(fileURLToPath(import.meta.url));
  for (let i = 0; i < 12; i++) {
    if (existsSync(path.join(cur, "package.json"))) return cur;
    const parent = path.dirname(cur);
    if (parent === cur) break;
    cur = parent;
  }
  throw new Error("runix: package.json not found from npm/lib");
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
