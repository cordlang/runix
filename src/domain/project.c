#include "domain/project.h"

#include <ctype.h>

int project_name_ok(const char *name) {
  if (!name || !*name) return 0;
  if (!isalpha((unsigned char)name[0]) && name[0] != '_') return 0;
  for (const char *p = name; *p; p++) {
    if (!isalnum((unsigned char)*p) && *p != '-' && *p != '_') return 0;
  }
  return 1;
}
