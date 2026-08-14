#include "application/doctor_service.h"
#include "application/ports/cordlang_port.h"
#include "application/ports/fs_port.h"
#include "domain/project.h"
#include "domain/version.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

static char *find_up(const char *start, const char *relpath) {
  char *cur = fs_norm_path(start);
  if (!cur) return NULL;
  for (int depth = 0; depth < 12; depth++) {
    char *cand = fs_join(cur, relpath);
    if (cand && fs_exists(cand)) {
      free(cur);
      return cand;
    }
    free(cand);
    char *parent = fs_dirname(cur);
    if (!parent) break;
    if (strcmp(parent, cur) == 0) {
      free(parent);
      break;
    }
    free(cur);
    cur = parent;
  }
  free(cur);
  return NULL;
}

static char *resolve_templates(void) {
  char *exe = fs_exe_dir();
  if (exe) {
    char *t = fs_join(exe, "templates/" PROJECT_DEFAULT_TEMPLATE);
    if (t && fs_is_dir(t)) {
      free(exe);
      return t;
    }
    free(t);
    t = fs_join(exe, "../templates/" PROJECT_DEFAULT_TEMPLATE);
    if (t && fs_is_dir(t)) {
      free(exe);
      return t;
    }
    free(t);
    free(exe);
  }
  char *cwd = fs_cwd();
  if (!cwd) return NULL;
  char *found = find_up(cwd, "templates/" PROJECT_DEFAULT_TEMPLATE);
  free(cwd);
  return found;
}

int doctor_service_run(void) {
  printf("runix %s\n", RUNIX_VERSION);

  char *cord = cordlang_find();
  if (!cord) {
    fprintf(stderr,
            "runix doctor: cordlang not found\n"
            "  set RUNIX_CORDLANG to the executable, add it to PATH,\n"
            "  or keep a sibling clone at ../cordlang\n");
    return 1;
  }
  printf("cordlang: %s\n", cord);
  free(cord);

  char *ver[] = {"--version", NULL};
  int rc = cordlang_exec(ver);
  if (rc != 0) {
    fprintf(stderr, "runix doctor: cordlang --version failed (%d)\n", rc);
    return 1;
  }

  char *templates = resolve_templates();
  if (templates) {
    printf("templates: %s\n", templates);
    free(templates);
  } else {
    printf("templates: (not next to this exe — init will fail unless you run "
           "from the repo)\n");
  }

  char *cwd = fs_cwd();
  if (cwd) {
    char *manifest = fs_join(cwd, PROJECT_MANIFEST_CORD);
    if (manifest && fs_is_file(manifest)) {
      printf("project: %s (%s)\n", cwd, PROJECT_MANIFEST_CORD);
    } else {
      printf("project: (no %s in cwd — not an app directory)\n",
             PROJECT_MANIFEST_CORD);
    }
    free(manifest);
    char *rx = fs_join(cwd, PROJECT_MANIFEST_RUNIX);
    if (rx && fs_is_file(rx)) printf("%s: yes\n", PROJECT_MANIFEST_RUNIX);
    free(rx);
    free(cwd);
  }
  printf("ok\n");
  return 0;
}
