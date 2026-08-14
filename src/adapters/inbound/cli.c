#include "application/build_service.h"
#include "application/check_service.h"
#include "application/delegate_service.h"
#include "application/dev_service.h"
#include "application/doctor_service.h"
#include "application/init_service.h"
#include "domain/project.h"
#include "domain/version.h"

#include <stdio.h>
#include <string.h>

static void print_usage(void) {
  printf("runix %s — web framework on Cordlang\n\n", RUNIX_VERSION);
  printf("Usage:\n");
  printf("  runix doctor                 Locate cordlang and print status\n");
  printf("  runix init [name] [-t id]    Scaffold a .cord app (starter | landing)\n");
  printf("  runix check [path]           cordlang check\n");
  printf("  runix fmt [path]             cordlang fmt\n");
  printf("  runix analyze [path]         cordlang analyze\n");
  printf("  runix dev [--no-open]        cordlang run (language preview)\n");
  printf("  runix build                  cordlang build esm → %s\n",
         PROJECT_DIST_RUNIX);
  printf("  runix --version | -v\n");
  printf("  runix help | --help | -h\n\n");
  printf("Language files are .cord (Cordlang 1.0). This CLI is the framework.\n");
  printf("npm: npx @cordlang/runix …  —  See docs/NPM.md, docs/ROADMAP.md.\n");
}

int cli_run(int argc, char **argv) {
  setvbuf(stdout, NULL, _IONBF, 0);
  setvbuf(stderr, NULL, _IONBF, 0);

  if (argc < 2) {
    print_usage();
    return 1;
  }

  const char *cmd = argv[1];
  if (strcmp(cmd, "help") == 0 || strcmp(cmd, "--help") == 0 ||
      strcmp(cmd, "-h") == 0) {
    print_usage();
    return 0;
  }
  if (strcmp(cmd, "--version") == 0 || strcmp(cmd, "-v") == 0 ||
      strcmp(cmd, "version") == 0) {
    printf("runix %s\n", RUNIX_VERSION);
    return 0;
  }
  if (strcmp(cmd, "doctor") == 0) return doctor_service_run();
  if (strcmp(cmd, "init") == 0 || strcmp(cmd, "create") == 0 ||
      strcmp(cmd, "new") == 0) {
    const char *name = PROJECT_DEFAULT_NAME;
    const char *tmpl = PROJECT_DEFAULT_TEMPLATE;
    for (int i = 2; i < argc; i++) {
      if (strcmp(argv[i], "--template") == 0 || strcmp(argv[i], "-t") == 0) {
        if (i + 1 >= argc) {
          fprintf(stderr, "runix init: --template needs a name\n");
          return 1;
        }
        tmpl = argv[++i];
      } else if (argv[i][0] == '-') {
        fprintf(stderr, "runix init: unknown flag %s\n", argv[i]);
        return 1;
      } else {
        name = argv[i];
      }
    }
    return init_service_run(name, tmpl);
  }
  if (strcmp(cmd, "check") == 0) return check_service_run(argc - 2, argv + 2);
  if (strcmp(cmd, "fmt") == 0)
    return delegate_service_run("fmt", argc - 2, argv + 2);
  if (strcmp(cmd, "analyze") == 0)
    return delegate_service_run("analyze", argc - 2, argv + 2);
  if (strcmp(cmd, "dev") == 0) {
    int no_open = 0;
    for (int i = 2; i < argc; i++) {
      if (strcmp(argv[i], "--no-open") == 0) {
        no_open = 1;
      } else {
        fprintf(stderr, "runix dev: unknown flag %s\n", argv[i]);
        return 1;
      }
    }
    return dev_service_run(no_open);
  }
  if (strcmp(cmd, "build") == 0) return build_service_run();

  fprintf(stderr, "runix: unknown command '%s'\n", cmd);
  print_usage();
  return 1;
}
