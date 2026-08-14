# Allowed APIs — Phase 0 discovery

Only APIs that exist in the local Cordlang clone (`../cordlang`, SPEC 1.0 / CLI 0.0.013). If it is not here, do not call it.

## Cordlang CLI (spawn or PATH)

| Command | Use in Runix |
|---------|----------------|
| `cordlang --version` | `runix doctor` |
| `cordlang check [path] [--json]` | `runix check` |
| `cordlang analyze [path] [--json]` | later DX |
| `cordlang run` / `run --no-open` | `runix dev` (Fase 1) |
| `cordlang build esm` | `runix build` (Fase 1) |
| `cordlang compile <file.cord> --ir` | debug / humans only |
| `cordlang compile <file.cord> --backend esm` | flattened ESM dump |
| `cordlang symbols` / `goto <Name>` | later DX |
| `cordlang fmt` | later DX |

**Does not exist:** `--port`, `--host`, `--backend runix`, JSON IR, Remix `loader`, Next RSC.

**Bind of `cordlang run`:** `127.0.0.1`, start port **4173**, then next 20 ports. No override.

Requires `cordlang.json` with `"entry"` (default `src/app.cord`).

## In-process C (Fase 2+, link compiler)

From `../cordlang/src/domain/ir.h` and `compiler_port.h`:

```c
CompileResult compiler_parse_project(const char *entry);
IrProgram *ir_from_ast(Node *ast_root, const char *entry_file);
void ir_free(IrProgram *p);
char *ir_dump(const char *p); /* debug text — not a protocol */
```

Walk `IrNode`. Do not parse `ir_dump`.

**`IrKind`:** `IR_PROJECT`, `IR_COMPONENT`, `IR_LAYOUT`, `IR_ROUTE`, `IR_PROP`, `IR_STATE`, `IR_COMPUTED`, `IR_EFFECT`, `IR_ELEMENT`, `IR_TEXT`, `IR_INTERP`, `IR_IF`, `IR_FOR`, `IR_EVENT`, `IR_ATTR`, `IR_SLOT`, `IR_FETCH`, `IR_HOOK`, `IR_MODULE_USE`, `IR_AWAIT`, `IR_SNIPPET`, `IR_STORE`, `IR_RENDER`, `IR_FOREIGN`.

**`IR_HOOK.name` used by Runix SEO later:** `"head"`. Official emit only uses the title string today.

## Language surface Runix apps may write (SPEC 1.0)

Frozen: indentation UI, `def` / `props` / `state` / `computed`, `if` / `for`, `#{…}`, `use` / `route` / `layout` / `slot`, `@events`, `bind`, `theme`, typed props (`string|number|boolean|any`), `fetch`, `title` / `head`, `params`, `link to=`.

```cord
theme app
  primary: "#2563eb"

use layouts/default
route / => pages/HomePage
route /shop/:id => pages/ShopPage

def HomePage
  title "Inicio"
  state n=0
  col gap=16 p=24
    h1 "Hola" size=2xl bold
    btn "+" @click=setN(n + 1)
```

**Not language:** file-based routing, SSR/SSG keywords, server actions, `meta`/`og` as dedicated keywords, arbitrary JS/TS, JSX.

`fetch x = "/api/x.json"` is **client** fetch. Derived: `x`, `xLoading`, `xError`. Not a server loader.

`action form = fn` is **client** `useActionState` / SPA FormData. Not `+page.server`.

## Preview URLs (Fase 1 delegates here)

`/`, `*.cord` → JS, `/@cord/runtime.js`, `/@cord/styles.css`, `/@cord/hmr`, `public/**`, SPA fallback for extensionless paths.

Runtime `$`: `state`, `effect`, `ref`, `resource`, `params`, `query`, `path`, `navigate`.

## Sources

- `../cordlang/docs/SPEC.md`
- `../cordlang/docs/IR.md`
- `../cordlang/docs/PREVIEW.md`
- `../cordlang/docs/RUNIX.md`
- `../cordlang/docs/GUIDE.md`
- `../cordlang/src/adapters/inbound/cli.c`
- `../cordlang/src/domain/ir.h`
