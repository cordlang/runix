import { existsSync } from "node:fs";
import path from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";

/** @/ → package root (the folder that contains package.json). */
function packageRootFrom(url) {
  let cur = path.dirname(fileURLToPath(url));
  for (let i = 0; i < 12; i++) {
    if (existsSync(path.join(cur, "package.json"))) return cur;
    const parent = path.dirname(cur);
    if (parent === cur) break;
    cur = parent;
  }
  return path.dirname(fileURLToPath(url));
}

export async function resolve(specifier, context, nextResolve) {
  if (specifier.startsWith("@/")) {
    const parent = context.parentURL || import.meta.url;
    const abs = path.join(packageRootFrom(parent), specifier.slice(2));
    return nextResolve(pathToFileURL(abs).href, context);
  }
  return nextResolve(specifier, context);
}
