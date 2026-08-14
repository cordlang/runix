#!/usr/bin/env node
import { register } from "node:module";
import { dirname, join } from "node:path";
import { fileURLToPath, pathToFileURL } from "node:url";

const here = dirname(fileURLToPath(import.meta.url));
register(pathToFileURL(join(here, "../loader.mjs")).href);

const { runCli } = await import("@/npm/lib/cli.js");
const code = await runCli(process.argv.slice(2));
process.exit(code);
