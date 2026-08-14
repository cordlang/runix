#ifndef RUNIX_DELEGATE_SERVICE_H
#define RUNIX_DELEGATE_SERVICE_H

/* Forward argv to `cordlang <cmd> …` (fmt, analyze, …). */
int delegate_service_run(const char *cmd, int argc, char **argv);

#endif
