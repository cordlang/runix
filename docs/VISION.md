# Visión — Runix

**Cordlang** es un lenguaje intermedio denso para vibecode/IA.  
**Runix** es el framework con el que se **envían** sitios escritos en ese lenguaje.

No esperamos a “terminar el lenguaje” para extraer el framework: el contrato de producto vive aquí desde el día 1. El compilador se queda en `cordlang/cordlang`. Este repo **consume** Cordlang (`check` / IR / preview) y **posee** SEO, SSG/SSR, runtime y deploy.

## Por qué un paquete npm ahora

1. La gente instala frameworks con `npm i` / `npx`, no con un `.exe` suelto.
2. Deploy (Pages, Node, Workers) y adapters viven en JS/TS; si eso nace dentro de Cordlang C, más tarde hay que arrancar el framework.
3. El CLI de producto (`runix init|dev|build`) debe existir **antes** del HTML por ruta, para que las apps ya sean proyectos Runix (`runix.json`), no apps “solo Cordlang” que luego hay que migrar.

## Qué no se migra aquí

- Lexer, parser, AST, IR, `cordlang check`, backends Official, playground WASM.
- Keywords nuevas en `.cord`.
- Un `--backend runix` en el CLI del lenguaje.

## Qué sí se arma aquí

- Contrato de proyecto (`runix.json` + convenciones).
- CLI de producto (npm + nativo).
- Templates y skill de IA (`write-runix`).
- Más adelante: SSG HTML por `IR_ROUTE`, SEO, datos de servidor, SSR, adapters de deploy.

Plan ejecutable: [`ROADMAP.md`](./ROADMAP.md).
