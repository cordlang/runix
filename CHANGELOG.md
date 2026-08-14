# Changelog

## 0.0.1-alpha.0 — 2026-08-14

- **Publicado en npmjs** como `@cordlang/runix` (`npm publish --tag alpha`).
- **Paquete npm `@cordlang/runix`** — `npx @cordlang/runix` (ESM, Node ≥ 18, cero deps). API `import { runCli } from "@cordlang/runix"`.
- **CLI Node = superficie de producto** — mismos comandos que el binario C; listo para npmjs (`docs/NPM.md`).
- `init --template starter|landing`, aliases `create` / `new`.
- `fmt` / `analyze` delegan en Cordlang (C + npm).
- Schema `schema/runix.schema.json`, skill `skills/write-runix`, docs VISION + NPM.
- Tests Node: `npm test`.

## 0.0.1-alpha — 2026-08-14

- Contrato del producto: Cordlang = lenguaje, Runix = framework.
- Roadmap por fases (SSG → SEO → datos de servidor → SSR → deploy).
- CLI Fase 1 nativo: `doctor`, `init`, `check`, `dev`, `build`.
- Árbol hexagonal micromodular (mismo corte que Cordlang: domain / application+ports / adapters).
- Template `starter` (rutas, layout, counter, `title`).
