# Runix — roadmap

> Cordlang es el **lenguaje**. Runix es el **framework web**. Este archivo es el plan ejecutable del producto. La visión corta vive en [`../cordlang/docs/RUNIX.md`](../../cordlang/docs/RUNIX.md).

## Norte

Un sitio se escribe en `.cord` denso (pocos tokens, `check` determinista). Runix lo convierte en una app web de verdad: HTML por URL, documento SEO, datos en el servidor, runtime de producción y deploy. Los backends Official de Cordlang (ESM / React / Svelte / Vue) siguen siendo **interop y preview**, no el producto.

```
src/**/*.cord
        │
        ▼
 cordlang (parse → AST → IrProgram)
        │
        ├── check / analyze / LSP     (lenguaje)
        ├── cordlang run              (preview del lenguaje, Fase 1)
        └── Runix                     (producto)
              ├── SSG: HTML por ruta + hydrate
              ├── SSR: mismo IR, HTML en el request
              ├── head / sitemap / robots
              └── adapters de deploy
```

**Éxito 90 días:** `runix init` → `runix dev` → `runix build` produce un sitio con **una URL = un HTML** (no un SPA vacío), `<title>` / descripción por página, y `runix check` verde.

**Éxito 12 meses:** SSR opcional, datos de servidor sin inventar JSX, un adapter estático y uno de proceso (Node o Workers), templates y skill de IA.

---

## Split (no negociable)

| | Cordlang | Runix |
|---|----------|--------|
| Repo | `cordlang/cordlang` | `cordlangorg/runix` (este) |
| Artefacto | `cordlang.exe` | paquete npm `runix` + `runix.exe` opcional |
| Sintaxis | SPEC 1.0 (`.cord`) | no añade keywords al lenguaje |
| Preview | `cordlang run` (ESM JIT, :4173) | `runix dev` (Fase 1 delega; luego runtime propio) |
| Framework | no | sí: SEO, SSG/SSR, deploy |

**El lenguaje no se renombra.** El producto se llama Runix; los archivos siguen siendo `.cord`. Un alias de extensión `.runix` → mismo parser es opcional y **posterior** (no Fase 1). Renombrar Cordlang rompería compilador, skills, schema y goldens.

---

## Phase 0 — Discovery ✅

Hecho al abrir este repo. Contrato: [`ALLOWED_APIS.md`](./ALLOWED_APIS.md).

Hallazgos que condicionan todo el plan:

1. Cordlang **no tiene SSR/SSG**. `next` / `sveltekit` son wraps SPA congelados.
2. Las rutas son sentencias `route /x => pages/Foo`, **no** file-routing.
3. SEO hoy = `title "…"` / `head` + `document.title`. No hay `meta` / OG / sitemap en el lenguaje.
4. `fetch` es **cliente**, una vez al montar. No refetch por `:id`.
5. No hay IR JSON. Consumir `IrProgram` en C o spawnear el CLI.
6. **Prohibido** registrar Runix como `--backend` en Cordlang.

---

## Phase 1 — CLI + contrato de proyecto  ← ahora (casi cerrado)

**Qué:** CLI de producto **en npm** (`npx runix`) y binario nativo opcional. Un proyecto Runix **es** un proyecto Cordlang (`cordlang.json` + `src/**/*.cord`) más `runix.json`.

| # | Entregable | Cómo | Estado |
|---|------------|------|--------|
| 1.1 | Repo + docs de contrato | este árbol | ✅ |
| 1.2 | `runix doctor` | resuelve `cordlang` (PATH, `RUNIX_CORDLANG`, `../cordlang`) | ✅ npm + C |
| 1.3 | `runix check` | spawnea `cordlang check` | ✅ |
| 1.4 | `runix dev` | spawnea `cordlang run` | ✅ |
| 1.5 | `runix build` | `cordlang build esm` → `dist/runix` | ✅ |
| 1.6 | `runix init [name] [-t]` | `templates/starter` + `landing` | ✅ |
| 1.7 | Tests | `npm test` + `tests/run_tests.ps1` | ✅ |
| 1.8 | Paquete npmjs | `package.json` + `docs/NPM.md` | ✅ listo para publicar |

**No hacer en Fase 1:** parser propio, HTML por ruta, server, puerto custom, keywords nuevas.

**Verify:**

```bat
build.bat
runix --version
runix doctor
runix init tmp-app && cd tmp-app && ..\runix.exe check
```

**Anti-patrones:** no copiar `parser.c`; no escribir `dist/` a mano; no añadir `backend: runix` en Cordlang.

---

## Phase 2 — SSG: una URL, un HTML

**Qué:** primer valor de producto. El crawler ve contenido, no un `#app` vacío.

Consume **`IrProgram` en proceso** (link del compilador Cordlang o un crate/wrapper estable). Por cada `IR_ROUTE`:

1. Resolver layout (`layout=` → `default` → primer layout) — misma regla que [`PREVIEW.md`](../../cordlang/docs/PREVIEW.md).
2. Bajar IR a HTML estático (tags, texto, `#{…}` con datos conocidos en build).
3. Escribir `dist/runix/<path>/index.html`.
4. Adjuntar el runtime ESM de Cordlang para hidratar `state` / eventos.

**Copy from:** emit estático de `../cordlang/src/adapters/outbound/backends/static_html/` + shell de `esm_runtime.c`. No reimplementar CSS JIT: reutilizar `esm_css` / `theme_css` o invocar `cordlang build esm` y **envolver** cada ruta.

**Verify:** `curl` de `/` y `/about` en el output contiene el `h1` del `.cord`, no solo `<div id="app">`. Fixture: `templates/starter`.

**Anti-patrones:** no pretender paridad con React SSR; no ejecutar JS de usuario en build todavía; no inventar file-router.

---

## Phase 3 — Documento / SEO

**Qué:** head de producto usando superficie **ya parseada**.

Hoy el parser acepta:

```cord
title "Inicio — Shop"
head
  title "Producto"
```

Official emit solo usa el string del título (`IR_HOOK` `"head"`). Runix interpreta **hijos de `head` que ya son elementos** (si el parser los deja pasar como `IR_ELEMENT`):

```cord
head
  title "Producto #{id}"
  meta name=description content="Ficha del producto"
  meta property=og:title content="Producto"
```

Si el parser **no** baja `meta` bajo `head` de forma útil, **no se inventa un keyword**. Se documenta `runix.json`:

```json
{
  "seo": {
    "siteName": "Shop",
    "lang": "es",
    "description": "fallback del sitio"
  }
}
```

y un archivo opcional `src/seo.json` por ruta. Preferir `head` existente.

También: `robots.txt`, `sitemap.xml` generados de `IR_ROUTE` (rutas estáticas; `:param` se lista solo si hay un manifiesto de paths).

**Verify:** HTML de `/` incluye `<title>`, `<meta name="description">`, canonical. `dist/runix/sitemap.xml` lista `/` y `/about`.

**Anti-patrones:** no añadir `generateMetadata` ni `seo` como keyword Cordlang; no tocar `schema/attrs.json` en el repo del lenguaje salvo un PR coordinado y justificado.

---

## Phase 4 — Datos de servidor (sin nuevo dialecto)

**Qué:** el HTML de Fase 2 necesita datos **antes** del paint.

Orden de implementación (el primero que no requiera sintaxis nueva gana):

1. **Convención Runix:** `src/data/<Page>.json` o `public/api/*.json` leídos en build e inyectados en el HTML (el `fetch` del cliente sigue funcionando para hidratar).
2. **Reusar `fetch`:** en SSG, Runix resuelve `IR_FETCH` en build (HTTP o archivo bajo `public/`) y serializa el JSON en el HTML. Los nombres `products` / `productsLoading` / `productsError` ya existen.
3. Solo si 1–2 no bastan: proponer a Cordlang un hook documentado. **No** se llama `loader`. El nombre, si algún día existe, se decide en el repo del lenguaje.

**Verify:** página con `fetch products = "/api/products.json"` y `public/api/products.json` → el HTML de build contiene el nombre del primer producto.

**Anti-patrones:** no clonar Remix `loader`/`action`; `action` de Cordlang es client `useActionState`.

---

## Phase 5 — SSR por request

**Qué:** mismo HTML que Fase 2, calculado en el request (params, query, cookies más adelante).

Runtime: proceso nativo que ya sirve estáticos +, para paths dinámicos (`/shop/:id`), baja IR → HTML. Hidratación = mismo ESM.

Motor: **C + IR** primero (sin Node). Si hace falta evaluar expresiones JS de usuario, embeber un engine (QuickJS) es Fase 5.1, no un prerrequisito.

**Verify:** `runix start` (nombre tentativo) responde `200` en `/shop/42` con `#{id}` resuelto a `42` en el HTML.

---

## Phase 6 — Endpoints de servidor

**Qué:** POST/JSON para forms reales, no solo SPA.

Convención de **archivos Runix**, no sintaxis Cordlang:

```
src/server/contact.rxs   # o .c / script documentado
```

Hasta que exista un dialecto, un endpoint es un módulo servido por el runtime (ruta `/api/...` mapeada en `runix.json`). Los forms `.cord` pueden `action=` hacia esa URL cuando el helper SPA no alcanza.

**Verify:** `POST /api/contact` con FormData persiste o eco JSON; la página no requiere JS para el POST básico.

---

## Phase 7 — Deploy

Adapters, no un host único:

| Adapter | Output |
|---------|--------|
| `static` | `dist/runix` listo para Pages / Netlify / S3 |
| `node` | server de Fase 5 empaquetado |
| `workers` | Worker + assets (Cloudflare) — solo cuando el static/SSR esté aburrido |

`runix deploy --adapter static` es el primero.

**Verify:** un sitio starter publicado como estáticos tiene HTML por ruta y sitemap.

---

## Phase 8 — DX / IA

- Templates Runix (`starter`, `landing`, `docs`) que reusan seeds Cordlang.
- Skill `write-runix` que apunta a este contrato + `../cordlang/docs/AI.md`.
- `runix fmt` / `symbols` delegan en Cordlang.
- No LLM en `build`.

---

## Key decisions

| Decisión | Por qué |
|----------|---------|
| Cordlang = lenguaje, Runix = framework | Ya es el contrato no negociable de `RUNIX.md` / `BACKENDS.md` / M12 |
| Archivos `.cord`, CLI `runix` | SPEC 1.0 está congelado; renombrar el lenguaje es otro producto |
| No `--backend runix` | El freeze de backends lo prohíbe; Runix no es un emit más |
| Fase 1 spawnea Cordlang; Fase 2+ linka `IrProgram` | `ir_dump` no es API; el CLI sí lo es |
| SSG antes que SSR | SEO real sin un JS engine embebido el día 1 |
| Cero keywords nuevas en `.cord` | El framework no forkear el lenguaje |
| Reusar `title` / `head` / `fetch` | Superficie ya parseada; Official emit es incompleto, Runix lo completa |
| Rutas explícitas (`route`), no file-router | Así funciona el compilador (`compiler.c` no crawlea `pages/`) |

---

## Anti-patrones (globales)

- Reparsear `.cord` en Runix.
- Tratar `cordlang compile --ir` como JSON.
- Marketear Cordlang como “el Next de nosotros”.
- Ampliar `next` / `sveltekit` dentro de Cordlang para “hacer SSR”.
- Enseñar a la IA `import` de React/Vue dentro de `.cord`.
- Inventar `loader`, `generateMetadata`, `+page`, `app/page.cord` como si fueran Cordlang.
- Poner LLM en el path de `build`.

---

## Orden de PRs

| PR | Título | Depende |
|----|--------|---------|
| 0 | Contrato: docs + LICENSE + `.gitignore` | — |
| 1 | CLI Fase 1 (`doctor`/`check`/`dev`/`build`/`init`) + starter | 0 |
| 2 | SSG HTML por `IR_ROUTE` + tests de snapshot HTML | 1 |
| 3 | Head / sitemap / robots | 2 |
| 4 | Resolución de `IR_FETCH` en build | 2 |
| 5 | Server por request (SSR dinámico) | 2–4 |
| 6 | Endpoints `/api` | 5 |
| 7 | Adapter `static` (+ workers más tarde) | 3–4 |
| 8 | Templates + skill IA | 1+ |

Cada PR debe citar `ALLOWED_APIS.md` y no añadir APIs no listadas.

---

*Última actualización: 2026-08-14 — Fase 1 en npm (`npx runix`) + CLI C. Siguiente producto: Fase 2 SSG. Lenguaje: Cordlang 1.0. CLI Cordlang de referencia: 0.0.013.*
