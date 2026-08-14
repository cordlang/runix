#ifndef RUNIX_FS_PORT_H
#define RUNIX_FS_PORT_H

#include <stddef.h>

/* Outbound port: filesystem. Same shape as Cordlang's fs_port (host only). */

char *fs_read_file(const char *path, size_t *out_len);
int fs_write_file(const char *path, const char *content);
int fs_mkdir_p(const char *path);
int fs_exists(const char *path);
int fs_is_dir(const char *path);
int fs_is_file(const char *path);
char *fs_join(const char *a, const char *b);
char *fs_cwd(void);
char *fs_dirname(const char *path);
char *fs_basename(const char *path);
char *fs_norm_path(const char *path);
int fs_copy_file(const char *src, const char *dst);
int fs_copy_tree(const char *src, const char *dst);

/* Directory of this executable (malloc). NULL if unknown. */
char *fs_exe_dir(void);
/* 1 = empty or missing, 0 = has entries, -1 = error. */
int fs_dir_is_empty(const char *path);

#endif
