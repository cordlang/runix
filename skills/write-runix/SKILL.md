---
name: write-runix
description: >
  Build a Runix app (web framework on Cordlang). Use when the user wants a
  site/app on Runix, SEO, SSG, deploy, or says "runix", "framework cord",
  or /write-runix. Write .cord. Do not invent Cordlang keywords.
---

# Write Runix (framework), not Cordlang-the-framework

**Cordlang** is the **language**. **Runix** is the **framework** product
(SEO / runtime / deploy). Apps are still `.cord`. Do not add `runix` as a
Cordlang `--backend`. Do not invent `loader`, `generateMetadata`, or
file-routing.

## Read first

In this repo: `docs/CONVENTIONS.md`, `docs/ALLOWED_APIS.md`, `docs/ROADMAP.md`.
Language syntax: sibling `../cordlang/docs/AI_CONTEXT.md` and `GUIDE.md`.

## Default behavior

1. Deliver **`.cord`** under `src/` (`app.cord` + `pages/` + `components/` + `layouts/`).
2. Always emit `cordlang.json` + `runix.json`.
3. Routes are explicit: `route /about => pages/AboutPage` — never `app/about/page.cord`.
4. Interpolation `#{x}`, events `@click=`, no `className` / `onClick` / `{count}`.
5. After edits: `runix check` (delegates to `cordlang check`).
6. Preview: `runix dev`. Product HTML-per-route is Fase 2 — do not claim it is done.

## runix.json

```json
{
  "name": "mi-sitio",
  "framework": "runix",
  "entry": "src/app.cord",
  "seo": { "siteName": "mi-sitio", "lang": "es", "description": "…" }
}
```

SEO strings live here or in existing `title` / `head`. **Do not** invent a
`seo` keyword in `.cord`.

## Commands

```bash
npx runix init mi-sitio
npx runix init land --template landing
runix check
runix dev
runix build
```

## Do not claim as done

- HTML por URL (Fase 2), sitemap/robots (Fase 3), SSR (Fase 5), deploy adapters (Fase 7)
- Remote Cordlang package registry
- Next RSC / SvelteKit SSR via Cordlang meta backends
