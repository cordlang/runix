# Contributing to Runix

Runix is the **framework**. Cordlang is the **language**. Do not merge them.

## Setup

```bash
npm test
build.bat          # optional native CLI
```

Need a sibling `../cordlang` (or `RUNIX_CORDLANG`) for `doctor` / `check`.

## Rules

1. Read `docs/ALLOWED_APIS.md`. If it is not listed, it does not exist.
2. New CLI behavior: `npm/lib/commands/` **and** (if native) `application/*_service.c`.
3. Do not re-parse `.cord`. Do not add `--backend runix` in the language repo.
4. Do not invent Cordlang keywords.
5. Keep `package.json` `files` tight — this package ships to npmjs.

## PRs

Prefer small PRs. Update `CHANGELOG.md` and `docs/ROADMAP.md` in the same change when the product surface moves.
