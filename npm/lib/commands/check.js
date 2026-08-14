import { execCordlang } from "@/npm/lib/cordlang.js";

export function checkProject(args) {
  return execCordlang(["check", ...args]);
}
