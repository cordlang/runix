# Runix

**Framework web de producto sobre [Cordlang](https://github.com/cordlang/cordlang).**

Escribes `.cord`. **Cordlang es el lenguaje** (sintaxis 1.0, IR, `check`, preview). **Runix es el framework**: SEO, HTML por ruta, runtime de producción y deploy. No es un `--backend` del CLI de Cordlang.

```
npm create / npx runix init   →   .cord
                                      │
                            cordlang check / IR
                                      │
                                      ▼
                                   Runix
                     SEO · SSG/SSR · runtime · deploy
```

| Hoy (Fase 1, este paquete) | Destino (Fases 2–7) |
|----------------------------|---------------------|
| `npx runix init` / `dev` / `check` / `build` | HTML real por ruta, `<head>` de producto, datos de servidor, deploy |
| Preview = `cordlang run` | Runtime propio sobre el IR, no un wrap de Next/Kit |

## Install (npm)

```bash
npm i -g runix
# or, no install:
npx runix init mi-sitio
cd mi-sitio
npx runix check
npx runix dev
```

Requisito: el CLI de **Cordlang** en `PATH`, o `RUNIX_CORDLANG` apuntando al exe, o un clone hermano `../cordlang`. Runix no reparsea `.cord`.

```bash
runix doctor              # encuentra cordlang + este paquete
runix init mi-sitio
runix init land --template landing
runix check
runix fmt
runix analyze
runix dev                 # preview del lenguaje
runix build               # export ESM → dist/runix
```

El source de la app es **Cordlang 1.0**. No inventes JSX, `onClick`, `className` ni file-routing tipo Next dentro de `.cord`.

## Paquete npm vs binario nativo

| Artefacto | Rol |
|-----------|-----|
| **`runix` en npm** | Superficie de producto (`npx runix`). Este es el paquete que se publica. |
| `runix.exe` (C) | Mismo contrato Fase 1, para quien compile este repo. Opcional. |

La API pública Node:

```js
import { runCli, findCordlang, RUNIX_VERSION } from "runix";
```

Schema: [`schema/runix.schema.json`](./schema/runix.schema.json).

## Split (no negociable)

| | Cordlang | Runix |
|---|----------|--------|
| Qué | Lenguaje + compilador + IR + preview | Framework web opinado |
| Repo | `cordlang/cordlang` | este (`cordlang/runix`) |
| Owns | `.cord`, `check`, emit Official | SEO, SSG/SSR, runtime, hosting |
| No es | Next / Vite / el framework | un `cordlang --backend runix` |

## Docs

| Doc | Qué |
|-----|-----|
| [docs/VISION.md](./docs/VISION.md) | Por qué existe Runix |
| [docs/ROADMAP.md](./docs/ROADMAP.md) | Fases 1–8 |
| [docs/NPM.md](./docs/NPM.md) | Publicar en npmjs |
| [docs/CONVENTIONS.md](./docs/CONVENTIONS.md) | Forma de un proyecto |
| [docs/ALLOWED_APIS.md](./docs/ALLOWED_APIS.md) | Superficie real (sin inventar) |
| [docs/CLI.md](./docs/CLI.md) | Comandos |
| [docs/ARCHITECTURE.md](./docs/ARCHITECTURE.md) | C hexagonal + paquete npm |

## Build nativo (opcional)

```bat
build.bat
```

```bash
make
```

```bash
npm test
powershell -ExecutionPolicy Bypass -File tests\run_tests.ps1
```

## License

[Cordlang Attribution License 1.0](./LICENSE). Los productos públicos deben acreditar **"Built with Cordlang"**.
