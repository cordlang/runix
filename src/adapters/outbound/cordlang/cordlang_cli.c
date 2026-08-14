#include "application/ports/cordlang_port.h"
#include "application/ports/fs_port.h"
#include "adapters/outbound/process/process_spawn.h"

#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#ifdef _WIN32
#ifndef WIN32_LEAN_AND_MEAN
#define WIN32_LEAN_AND_MEAN
#endif
#include <windows.h>
#define CORD_BIN "cordlang.exe"
#else
#define CORD_BIN "cordlang"
#endif

static char *dup_if_file(const char *path) {
  if (path && fs_is_file(path)) return strdup(path);
  return NULL;
}

static char *join_if_file(const char *dir, const char *rel) {
  if (!dir) return NULL;
  char *p = fs_join(dir, rel);
  if (!p) return NULL;
  if (fs_is_file(p)) return p;
  free(p);
  return NULL;
}

/* From `start`, walk parents looking for sibling `cordlang/<bin>`. */
static char *walk_find_cordlang(const char *start) {
  char *cur = fs_norm_path(start);
  if (!cur) return NULL;
  for (int depth = 0; depth < 12; depth++) {
    char *hit = join_if_file(cur, "cordlang/" CORD_BIN);
    if (hit) {
      free(cur);
      return hit;
    }
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

char *cordlang_find(void) {
  char *found = dup_if_file(getenv("RUNIX_CORDLANG"));
  if (found) return found;

#ifdef _WIN32
  char app[MAX_PATH];
  if (SearchPathA(NULL, "cordlang", ".exe", MAX_PATH, app, NULL))
    return strdup(app);
#endif

  /* Walk up from exe / cwd looking for sibling checkout `cordlang/<bin>`. */
  char *exe_dir = fs_exe_dir();
  if (exe_dir) {
    found = walk_find_cordlang(exe_dir);
    free(exe_dir);
    if (found) return found;
  }

  char *cwd = fs_cwd();
  if (cwd) {
    found = walk_find_cordlang(cwd);
    free(cwd);
    if (found) return found;
  }

#ifndef _WIN32
  const char *path = getenv("PATH");
  if (path) {
    char *tmp = strdup(path);
    if (tmp) {
      char *p = tmp;
      while (p && *p) {
        char *colon = strchr(p, ':');
        if (colon) *colon = '\0';
        found = join_if_file(p, CORD_BIN);
        if (found) {
          free(tmp);
          return found;
        }
        p = colon ? colon + 1 : NULL;
      }
      free(tmp);
    }
  }
#endif
  return NULL;
}

int cordlang_exec(char *const argv[]) {
  char *exe = cordlang_find();
  if (!exe) {
    fprintf(stderr,
            "runix: cordlang not found\n"
            "  run `runix doctor` or set RUNIX_CORDLANG\n");
    return 1;
  }

  char *fwd[32];
  int n = 0;
  fwd[n++] = exe;
  if (argv) {
    for (int i = 0; argv[i] && n < 30; i++) fwd[n++] = argv[i];
  }
  fwd[n] = NULL;

  int rc = process_run(NULL, fwd, 1);
  free(exe);
  if (rc < 0) return 1;
  return rc;
}
