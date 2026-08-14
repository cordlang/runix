# Publicar Runix en npmjs

El paquete se llama **`runix`**. Es ESM, Node ≥ 18, **cero dependencias**.

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

## Primera publicación

1. Cuenta npm con 2FA.
2. Nombre: si `runix` está tomado, usar `@cordlang/runix` y ajustar `package.json` (`name` + `bin`).
3. Repo GitHub: `cordlang/runix` (el `repository` del manifiesto ya apunta ahí).
4. Tag alineado con la versión:

```bash
npm login
npm publish --access public --tag alpha
```

Versión actual: `0.0.1-alpha.0` (prerelease). Sube a `0.0.1` cuando Fase 1 esté aburrida (doctor/init/check/dev/build estables).

## Después de publicar

```bash
npx runix@alpha --version
npx runix@alpha doctor
npx runix@alpha init demo
```

Los usuarios siguen necesitando **Cordlang** instalado (lenguaje). Este paquete no embebe el compilador.

## CI

`.github/workflows/ci.yml` corre `npm test`. Publicar a npm es manual (o un workflow `release` en tag `v*`) — no automatizar `npm publish` desde cada push.
