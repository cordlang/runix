#include "application/build_service.h"
#include "application/ports/cordlang_port.h"
#include "application/ports/fs_port.h"
#include "domain/project.h"

#include <stdio.h>

int build_service_run(void) {
  if (!fs_is_file(PROJECT_MANIFEST_CORD)) {
    fprintf(stderr, "runix build: no %s in cwd\n", PROJECT_MANIFEST_CORD);
    return 1;
  }

  char *fwd[] = {"build", "esm", NULL};
  int rc = cordlang_exec(fwd);
  if (rc != 0) return rc;

  if (!fs_is_dir(PROJECT_DIST_ESM)) {
    fprintf(stderr, "runix build: expected %s after cordlang build esm\n",
            PROJECT_DIST_ESM);
    return 1;
  }
  if (fs_copy_tree(PROJECT_DIST_ESM, PROJECT_DIST_RUNIX) != 0) {
    fprintf(stderr, "runix build: failed to copy %s → %s\n", PROJECT_DIST_ESM,
            PROJECT_DIST_RUNIX);
    return 1;
  }
  printf("runix build: %s\n", PROJECT_DIST_RUNIX);
  printf("  (Fase 1: ESM preview export. HTML-per-route is Fase 2 — "
         "docs/ROADMAP.md)\n");
  return 0;
}
