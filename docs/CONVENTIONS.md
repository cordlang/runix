# Project conventions

Copy the shape Cordlang already documents in `GUIDE.md`. Runix does not invent an `app/` file router.

```
mi-sitio/
  cordlang.json
  runix.json
  public/
    api/                 # JSON that `fetch` can hit as /api/…
  src/
    app.cord
    layouts/default.cord
    pages/HomePage.cord
    pages/AboutPage.cord
    components/
```

## `cordlang.json`

Required for `cordlang run` / `check`. Minimal:

```json
{
  "name": "mi-sitio",
  "entry": "src/app.cord",
  "title": "mi-sitio",
  "lang": "es"
}
```

## `runix.json`

```json
{
  "name": "mi-sitio",
  "framework": "runix",
  "entry": "src/app.cord",
  "seo": {
    "siteName": "mi-sitio",
    "lang": "es"
  }
}
```

## Entry (`src/app.cord`)

Thin: theme, `use` of layouts, `route` lines. Pages are modules.

```cord
theme app
  primary: "#2563eb"
  bg: "#fafaf9"
  text: "#1c1917"
  muted: "#78716c"
  radius: 12

use layouts/default

route / => pages/HomePage
route /about => pages/AboutPage
```

Unused files in `pages/` are **not** routes. Only `route` statements count.

## Layout

```cord
layout default
  header sticky bg=white shadow=sm
    row between center p=16
      span "Runix" bold
      nav
        row gap=16
          link "Home" to=/
          link "About" to=/about
  main p=24
    slot
```

## Page

```cord
title "Inicio"

col gap=16 max-w=640
  h1 "Hola" size=2xl bold
  p "Escrito en Cordlang, servido por Runix." muted
```

## Syntax that is wrong

| Don't | Do |
|-------|-----|
| `onClick=` / `className=` | `@click=` / attrs Cord |
| `{count}` | `#{count}` |
| `pages/about.cord` as implicit route | `route /about => pages/AboutPage` |
| `export async function loader` | `fetch` + `public/api` (hoy) |
| JSX / TS en `.cord` | tags + `#{expr}` |
