#ifndef RUNIX_DOMAIN_PROJECT_H
#define RUNIX_DOMAIN_PROJECT_H

/* Pure project conventions. No filesystem or process I/O. */

#define PROJECT_MANIFEST_CORD "cordlang.json"
#define PROJECT_MANIFEST_RUNIX "runix.json"
#define PROJECT_DEFAULT_ENTRY "src/app.cord"
#define PROJECT_DEFAULT_NAME "my-app"
#define PROJECT_DEFAULT_TEMPLATE "starter"
#define PROJECT_DIST_ESM "dist/esm"
#define PROJECT_DIST_RUNIX "dist/runix"

/* Letter / '_' first; then alnum, '-' or '_'. */
int project_name_ok(const char *name);

#endif
