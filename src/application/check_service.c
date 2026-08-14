#include "application/check_service.h"
#include "application/ports/cordlang_port.h"

#include <stddef.h>

int check_service_run(int argc, char **argv) {
  char *fwd[32];
  int n = 0;
  fwd[n++] = "check";
  for (int i = 0; i < argc && n < 30; i++) fwd[n++] = argv[i];
  fwd[n] = NULL;
  return cordlang_exec(fwd);
}
