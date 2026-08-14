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

char *cordlang_find(void) {
  char *found = dup_if_file(getenv("RUNIX_CORDLANG"));
  if (found) return found;

#ifdef _WIN32
  char app[MAX_PATH];
  if (SearchPathA(NULL, "cordlang", ".exe", MAX_PATH, app, NULL))
    return strdup(app);
#endif

  const char *rels[] = {
      "../cordlang/" CORD_BIN,
      "../../cordlang/" CORD_BIN,
      NULL,
  };

  char *exe_dir = fs_exe_dir();
  if (exe_dir) {
    for (int i = 0; rels[i]; i++) {
      found = join_if_file(exe_dir, rels[i]);
      if (found) {
        free(exe_dir);
        return found;
      }
    }
    free(exe_dir);
  }

  char *cwd = fs_cwd();
  if (cwd) {
    for (int i = 0; rels[i]; i++) {
      found = join_if_file(cwd, rels[i]);
      if (found) {
        free(cwd);
        return found;
      }
    }
    free(cwd);
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
