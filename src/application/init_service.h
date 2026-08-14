#ifndef RUNIX_INIT_SERVICE_H
#define RUNIX_INIT_SERVICE_H

/* Scaffold a Runix project from templates/<name>.
 * project_name NULL → my-app. template_name NULL → starter. */
int init_service_run(const char *project_name, const char *template_name);

#endif
