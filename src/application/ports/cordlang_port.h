#ifndef RUNIX_CORDLANG_PORT_H
#define RUNIX_CORDLANG_PORT_H

/* Outbound port: the Cordlang language CLI (sibling product, not a backend).
 *
 * Resolution: RUNIX_CORDLANG, PATH, then ../cordlang/cordlang[.exe]
 * next to this executable or cwd.
 */

/* Absolute path to the cordlang executable. Caller frees. NULL if missing. */
char *cordlang_find(void);

/*
 * Run `cordlang <argv…>`. argv is NULL-terminated arguments AFTER the exe
 * (e.g. {"check", "--json", NULL}). Returns the process exit code, or 1
 * if cordlang cannot be found / started.
 */
int cordlang_exec(char *const argv[]);

#endif
