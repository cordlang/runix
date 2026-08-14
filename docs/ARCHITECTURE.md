# Runix — Internal architecture

Two surfaces, one contract:

1. **npm package (`npm/`)** — what ships to npmjs (`npx runix`). Prefer this for product work.
2. **Native C CLI (`src/`)** — same Fase 1 commands; hexagonal cut like Cordlang ([ARCHITECTURE.md](https://github.com/cordlang/cordlang/blob/master/docs/ARCHITECTURE.md)).

Do not re-parse `.cord` in either surface.

## Layers

```
adapters/inbound/cli.c
        │
        ▼
application/*_service.c     (one use case per file)
        │
   ports/*.h                (interfaces)
        │
        ├── domain/         (conventions — no I/O)
        └── adapters/outbound/
              ├── fs/
              ├── process/
              └── cordlang/   (find + exec the language CLI)
```

| Layer | Path | Responsibility |
|-------|------|----------------|
| Inbound | `src/adapters/inbound/` | CLI argv → service calls |
| Application | `src/application/` | `doctor`, `init`, `check`, `dev`, `build` |
| Ports | `src/application/ports/` | `fs_port`, `cordlang_port` |
| Domain | `src/domain/` | Version, project name/paths — **no I/O** |
| Outbound | `src/adapters/outbound/` | FS, `process_run`, Cordlang spawn |

**Rule:** no filesystem, network, or process I/O inside `src/domain/`.

## File map

| Stage | Files |
|-------|--------|
| CLI entry | `src/main.c` → `cli_run` in `adapters/inbound/cli.c` |
| Domain | `domain/version.h`, `domain/project.{c,h}` |
| Doctor | `application/doctor_service.{c,h}` |
| Init | `application/init_service.{c,h}` |
| Check / dev / build | `application/{check,dev,build}_service.{c,h}` |
| FS | `adapters/outbound/fs/fs.c` implements `ports/fs_port.h` |
| Process | `adapters/outbound/process/process_spawn.{c,h}` — same contract as Cordlang |
| Language CLI | `adapters/outbound/cordlang/cordlang_cli.c` implements `ports/cordlang_port.h` |

## How to add a use case

Copy the Cordlang service pattern:

1. `src/application/<name>_service.h` — `int <name>_service_run(…);`
2. `src/application/<name>_service.c` — talk only to ports / domain.
3. Wire one branch in `adapters/inbound/cli.c`.
4. Add the `.c` to `build.bat`, `Makefile`, and `CMakeLists.txt`.

Do **not** put spawn/FS logic in the CLI. Do **not** add a `--backend runix` in the language repo.

## How Runix talks to Cordlang

```
Fase 1                         Fase 2+
──────                         ───────
cordlang_port (spawn)          still this repo; optionally link
  check / run / build esm        sibling Cordlang IrProgram in-process
```

`cordlang_find()`: `RUNIX_CORDLANG` → `PATH` → walk up for sibling `cordlang/cordlang[.exe]`.

## What stays out of domain

- Opening files, walking templates, `CreateProcess` / `fork`
- Parsing `.cord` (that is Cordlang)
- HTTP / SEO emit (future outbound adapters, not domain)

## npm map

| Path | Role |
|------|------|
| `npm/bin/runix.js` | `bin.runix` — registers `@/` then loads `@/npm/lib/cli.js` |
| `npm/loader.mjs` | `@/` → package root |
| `npm/lib/cli.js` | argv → commands |
| `npm/lib/cordlang.js` | find + spawn language CLI |
| `npm/lib/commands/` | doctor / init / check / dev / build / delegate |
| `schema/runix.schema.json` | `runix.json` |

## Related

- Product plan: [`ROADMAP.md`](./ROADMAP.md)
- Allowed Cordlang APIs: [`ALLOWED_APIS.md`](./ALLOWED_APIS.md)
- Language compiler map: `../cordlang/docs/ARCHITECTURE.md`
