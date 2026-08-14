# AGENTS — Runix

Runix is the **web framework** product. Cordlang is the **language**. Do not merge the two.

Hexagonal layout matches `../cordlang` (micromodular services, ports, no I/O in `domain/`).

## Before any change

1. Read `docs/ALLOWED_APIS.md`. If an API is not listed, it does not exist.
2. Read `docs/ROADMAP.md` for the current phase.
3. New behavior = a new `application/*_service.c`, not a blob in `cli.c`.
4. Prefer consuming Cordlang (`cordlang_port`, later `IrProgram`) over re-parsing `.cord`.

## Hard rules

- **Do not** add `runix` as a Cordlang `--backend`.
- **Do not** reimplement the lexer/parser.
- **Do not** put filesystem, network, or process I/O in `src/domain/`.
- **Do not** treat `cordlang compile --ir` text as a machine protocol.
- **Do not** write JSX / `className` / `onClick` / `{count}` in `.cord`.
- **Do not** invent Cordlang keywords (`loader`, `generateMetadata`, `+page.server`).
- No LLM on the compile path.

## Layout map

| Path | Role |
|------|------|
| `src/domain/` | Version + project conventions (no I/O) |
| `src/application/` | One service per use case |
| `src/application/ports/` | `fs_port`, `cordlang_port` |
| `src/adapters/inbound/` | CLI |
| `src/adapters/outbound/fs/` | Filesystem |
| `src/adapters/outbound/process/` | `process_run` (Cordlang contract) |
| `src/adapters/outbound/cordlang/` | Find + exec language CLI |
| `npm/` | **Published** CLI + Node API (`npx runix`) |
| `templates/` | Runix starters (still `.cord`) |
| `schema/` | `runix.json` JSON Schema |
| `skills/write-runix/` | Portable AI skill |
| `docs/` | Contract + roadmap |

## Verify

```bat
npm test
build.bat
powershell -ExecutionPolicy Bypass -File tests\run_tests.ps1
```

If Cordlang is available: `npx runix doctor` then `npx runix check` inside `templates/starter`.
