#include "application/delegate_service.h"
#include "application/ports/cordlang_port.h"

#include <stddef.h>

int delegate_service_run(const char *cmd, int argc, char **argv) {
  if (!cmd || !*cmd) return 1;
  char *fwd[32];
  int n = 0;
  fwd[n++] = (char *)cmd;
  for (int i = 0; i < argc && n < 30; i++) fwd[n++] = argv[i];
  fwd[n] = NULL;
  return cordlang_exec(fwd);
}
