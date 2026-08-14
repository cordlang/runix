#ifndef RUNIX_PROCESS_SPAWN_H
#define RUNIX_PROCESS_SPAWN_H

#ifdef __cplusplus
extern "C" {
#endif

/* Same contract as Cordlang process_run.
 * argv is NULL-terminated. cwd may be NULL (inherit).
 * wait_child != 0 → wait and return exit status (0 = success).
 * wait_child == 0 → detach; return 0 if spawn succeeded.
 * Returns -1 if the process could not be started. */
int process_run(const char *cwd, char *const argv[], int wait_child);

#ifdef __cplusplus
}
#endif

#endif
