#include "application/dev_service.h"
#include "application/ports/cordlang_port.h"
#include "application/ports/fs_port.h"
#include "domain/project.h"

#include <stdio.h>

int dev_service_run(int no_open) {
  if (!fs_is_file(PROJECT_MANIFEST_CORD)) {
    fprintf(stderr,
            "runix dev: no %s in cwd (run `runix init` first)\n",
            PROJECT_MANIFEST_CORD);
    return 1;
  }
  if (no_open) {
    char *fwd[] = {"run", "--no-open", NULL};
    return cordlang_exec(fwd);
  }
  char *fwd[] = {"run", NULL};
  return cordlang_exec(fwd);
}
