import { execCordlang } from "../cordlang.js";

export function checkProject(args) {
  return execCordlang(["check", ...args]);
}
