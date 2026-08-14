# Publicar Runix en npmjs

El paquete se llama **`@cordlang/runix`**. Es ESM, Node ≥ 18.19, **cero dependencias**. El binario CLI sigue siendo `runix`.

## Qué se publica

`package.json` → `files`:

- `npm/` — CLI (`bin/runix.js`) + API (`lib/`)
- `templates/` — `starter`, `landing`
- `schema/runix.schema.json`
- `docs/`, `README.md`, `CHANGELOG.md`, `LICENSE`

**No** se publica: `src/` C, `*.exe`, tests locales, `node_modules`.

## Preflight

```bash
npm test
npm pack --dry-run
```

Comprueba que el tarball incluye `npm/bin/runix.js` y `templates/starter/src/app.cord`.

El `prepublishOnly` corre `npm test`.

## Publicar

Cuenta con acceso de owner a la org **`@cordlang`**. Tag de prerelease: `alpha`.

```bash
npm publish --access public --tag alpha
```

Versión actual: `0.0.1-alpha.0`. Sube a `0.0.1` (sin tag `alpha`) cuando Fase 1 esté aburrida.

## Después de publicar

```bash
npx @cordlang/runix@alpha --version
npx @cordlang/runix@alpha doctor
npx @cordlang/runix@alpha init demo
```

Tras `npm i -g @cordlang/runix@alpha` el comando global es `runix`.

Los usuarios siguen necesitando **Cordlang** instalado (lenguaje). Este paquete no embebe el compilador.

## CI

`.github/workflows/ci.yml` corre `npm test`. Publicar a npm es manual (o un workflow `release` en tag `v*`) — no automatizar `npm publish` desde cada push.
