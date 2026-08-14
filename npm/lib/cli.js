import { LANGUAGE, RUNIX_VERSION } from "./version.js";
import { DEFAULT_NAME, DEFAULT_TEMPLATE, DIST_RUNIX } from "./project.js";
import { doctor } from "./commands/doctor.js";
import { initProject } from "./commands/init.js";
import { checkProject } from "./commands/check.js";
import { devProject } from "./commands/dev.js";
import { buildProject } from "./commands/build.js";
import { delegate } from "./commands/delegate.js";

function usage() {
  console.log(`runix ${RUNIX_VERSION} — web framework on ${LANGUAGE}

Usage:
  runix doctor                 Locate cordlang and print status
  runix init [name] [-t id]    Scaffold a .cord app (starter | landing)
  runix check [path]           cordlang check
  runix fmt [path]             cordlang fmt
  runix analyze [path]         cordlang analyze
  runix dev [--no-open]        cordlang run (language preview)
  runix build                  cordlang build esm → ${DIST_RUNIX}
  runix --version | -v
  runix help | --help | -h

Language files are .cord (Cordlang 1.0). This package is the framework.
Cordlang is the language — not a Runix backend. See docs/ROADMAP.md.`);
}

function parseInit(args) {
  let name = DEFAULT_NAME;
  let template = DEFAULT_TEMPLATE;
  for (let i = 0; i < args.length; i++) {
    const a = args[i];
    if (a === "--template" || a === "-t") {
      const next = args[++i];
      if (!next) {
        console.error("runix init: --template needs a name (starter | landing)");
        return { error: 1 };
      }
      template = next;
    } else if (a.startsWith("-")) {
      console.error(`runix init: unknown flag ${a}`);
      return { error: 1 };
    } else {
      name = a;
    }
  }
  return { name, template };
}

export async function runCli(argv) {
  if (!argv.length) {
    usage();
    return 1;
  }
  const cmd = argv[0];
  if (cmd === "help" || cmd === "--help" || cmd === "-h") {
    usage();
    return 0;
  }
  if (cmd === "--version" || cmd === "-v" || cmd === "version") {
    console.log(`runix ${RUNIX_VERSION}`);
    return 0;
  }
  if (cmd === "doctor") return doctor();
  if (cmd === "init" || cmd === "create" || cmd === "new") {
    const parsed = parseInit(argv.slice(1));
    if (parsed.error) return parsed.error;
    return initProject(parsed.name, parsed.template);
  }
  if (cmd === "check") return checkProject(argv.slice(1));
  if (cmd === "fmt") return delegate("fmt", argv.slice(1));
  if (cmd === "analyze") return delegate("analyze", argv.slice(1));
  if (cmd === "dev") {
    let noOpen = false;
    for (const a of argv.slice(1)) {
      if (a === "--no-open") noOpen = true;
      else {
        console.error(`runix dev: unknown flag ${a}`);
        return 1;
      }
    }
    return devProject(noOpen);
  }
  if (cmd === "build") return buildProject();

  console.error(`runix: unknown command '${cmd}'`);
  usage();
  return 1;
}
