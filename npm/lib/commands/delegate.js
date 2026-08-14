import { execCordlang } from "../cordlang.js";

export function delegate(cmd, args) {
  return execCordlang([cmd, ...args]);
}
