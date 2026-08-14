#include "application/init_service.h"
#include "application/ports/fs_port.h"
#include "domain/project.h"

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

static char *resolve_template_dir(const char *template_name) {
  if (!template_name || !*template_name) template_name = PROJECT_DEFAULT_TEMPLATE;

  char rel[256];
  snprintf(rel, sizeof(rel), "templates/%s", template_name);

  char *exe = fs_exe_dir();
  if (exe) {
    char *cand = fs_join(exe, rel);
    if (cand && fs_is_dir(cand)) {
      free(exe);
      return cand;
    }
    free(cand);
    char *up = fs_join(exe, "../");
    free(exe);
    if (up) {
      cand = fs_join(up, rel);
      free(up);
      if (cand && fs_is_dir(cand)) return cand;
      free(cand);
    }
  }

  char *cwd = fs_cwd();
  if (!cwd) return NULL;
  char *found = find_up(cwd, rel);
  free(cwd);
  return found;
}

int init_service_run(const char *project_name, const char *template_name) {
  if (!project_name || !*project_name) project_name = PROJECT_DEFAULT_NAME;
  if (!template_name || !*template_name) template_name = PROJECT_DEFAULT_TEMPLATE;

  if (!project_name_ok(project_name)) {
    fprintf(stderr, "runix init: invalid name '%s'\n", project_name);
    return 1;
  }

  char *src = resolve_template_dir(template_name);
  if (!src) {
    fprintf(stderr, "runix init: cannot find templates/%s\n", template_name);
    return 1;
  }

  if (fs_is_dir(project_name)) {
    int empty = fs_dir_is_empty(project_name);
    if (empty == 0) {
      fprintf(stderr, "runix init: '%s' already exists and is not empty\n",
              project_name);
      free(src);
      return 1;
    }
  } else if (fs_mkdir_p(project_name) != 0) {
    fprintf(stderr, "runix init: cannot create '%s'\n", project_name);
    free(src);
    return 1;
  }

  if (fs_copy_tree(src, project_name) != 0) {
    fprintf(stderr, "runix init: failed to copy template '%s' → '%s'\n",
            template_name, project_name);
    free(src);
    return 1;
  }
  free(src);

  printf("created %s\n", project_name);
  printf("  cd %s\n", project_name);
  printf("  runix check\n");
  printf("  runix dev\n");
  return 0;
}
