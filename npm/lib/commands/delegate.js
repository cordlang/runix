import { execCordlang } from "@/npm/lib/cordlang.js";

export function delegate(cmd, args) {
  return execCordlang([cmd, ...args]);
}
