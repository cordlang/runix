#ifndef RUNIX_CHECK_SERVICE_H
#define RUNIX_CHECK_SERVICE_H

/* Delegate to `cordlang check` with the remaining argv (path, --json, …). */
int check_service_run(int argc, char **argv);

#endif
