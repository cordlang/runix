import { cpSync, existsSync, mkdirSync, readdirSync } from "node:fs";
import path from "node:path";
import { templateDir } from "@/npm/lib/paths.js";
import { projectNameOk, TEMPLATES } from "@/npm/lib/project.js";

function dirEmpty(dir) {
  if (!existsSync(dir)) return true;
  return readdirSync(dir).length === 0;
}

export function initProject(projectName, templateName) {
  if (!projectNameOk(projectName)) {
    console.error(`runix init: invalid name '${projectName}'`);
    return 1;
  }
  if (!TEMPLATES.includes(templateName)) {
    console.error(
      `runix init: unknown template '${templateName}' (want: ${TEMPLATES.join(", ")})`
    );
    return 1;
  }
  const src = templateDir(templateName);
  if (!existsSync(src)) {
    console.error(`runix init: cannot find templates/${templateName}`);
    return 1;
  }
  const dest = path.resolve(process.cwd(), projectName);
  if (existsSync(dest) && !dirEmpty(dest)) {
    console.error(`runix init: '${projectName}' already exists and is not empty`);
    return 1;
  }
  mkdirSync(dest, { recursive: true });
  cpSync(src, dest, { recursive: true });
  console.log(`created ${projectName}`);
  console.log(`  cd ${projectName}`);
  console.log("  runix check");
  console.log("  runix dev");
  return 0;
}
