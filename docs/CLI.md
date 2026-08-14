# Runix CLI

Version: `0.0.1-alpha` (nativo) / `0.0.1-alpha.0` (npm).

```
runix --version | -v
runix help | --help | -h
runix doctor
runix init [name] [--template starter|landing]
runix check [path]
runix fmt [path]
runix analyze [path]
runix dev [--no-open]
runix build
```

`init` aliases: `create`, `new`.

| Command | Fase 1 behavior |
|---------|-----------------|
| `doctor` | Locate `cordlang`, print path + `cordlang --version` + templates |
| `init` | Create `name/` from `templates/<id>` |
| `check` | `cordlang check [path]` |
| `fmt` | `cordlang fmt [path]` |
| `analyze` | `cordlang analyze [path]` |
| `dev` | `cordlang run` or `cordlang run --no-open` |
| `build` | `cordlang build esm` then copy `dist/esm` → `dist/runix` |

Exit codes: `0` ok, `1` usage / missing cordlang / delegated command failed, `2` I/O (nativo).

Environment: `RUNIX_CORDLANG` = absolute path to the Cordlang executable.

npm entry: `npx runix` → `npm/bin/runix.js`. Native: `./runix` after `build.bat`.
