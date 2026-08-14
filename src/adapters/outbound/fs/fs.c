#include "application/ports/fs_port.h"

#include <errno.h>
#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <sys/stat.h>

#ifdef _WIN32
#include <direct.h>
#include <windows.h>
#define PATH_SEP '\\'
#define mkdir_one(p) _mkdir(p)
#else
#include <dirent.h>
#include <unistd.h>
#define PATH_SEP '/'
#define mkdir_one(p) mkdir((p), 0755)
#endif

#ifndef _WIN32
#ifndef S_ISREG
#define S_ISREG(m) (((m) & S_IFMT) == S_IFREG)
#endif
#endif

char *fs_read_file(const char *path, size_t *out_len) {
  FILE *f = fopen(path, "rb");
  if (!f) return NULL;
  if (fseek(f, 0, SEEK_END) != 0) {
    fclose(f);
    return NULL;
  }
  long size = ftell(f);
  if (size < 0) {
    fclose(f);
    return NULL;
  }
  rewind(f);
  char *buf = malloc((size_t)size + 1);
  if (!buf) {
    fclose(f);
    return NULL;
  }
  size_t n = fread(buf, 1, (size_t)size, f);
  fclose(f);
  buf[n] = '\0';
  if (out_len) *out_len = n;
  return buf;
}

int fs_write_file(const char *path, const char *content) {
  if (!path || !content) return -1;
  char *copy = strdup(path);
  if (!copy) return -1;
  for (char *p = copy + 1; *p; p++) {
    if (*p == '/' || *p == '\\') {
      char saved = *p;
      *p = '\0';
      mkdir_one(copy);
      *p = saved;
    }
  }
  free(copy);

  FILE *f = fopen(path, "wb");
  if (!f) return -1;
  size_t len = strlen(content);
  size_t written = fwrite(content, 1, len, f);
  fclose(f);
  return written == len ? 0 : -1;
}

int fs_mkdir_p(const char *path) {
  if (!path || !*path) return -1;
  char *copy = strdup(path);
  if (!copy) return -1;

  size_t len = strlen(copy);
  while (len > 0 && (copy[len - 1] == '/' || copy[len - 1] == '\\')) {
    copy[--len] = '\0';
  }

  for (char *p = copy + 1; *p; p++) {
    if (*p == '/' || *p == '\\') {
      char saved = *p;
      *p = '\0';
      if (mkdir_one(copy) != 0 && errno != EEXIST) {
        /* ignore if exists as file; mkdir_p is best-effort like Cordlang */
      }
      *p = saved;
    }
  }
  if (mkdir_one(copy) != 0 && errno != EEXIST) {
    free(copy);
    return -1;
  }
  free(copy);
  return 0;
}

int fs_exists(const char *path) {
  struct stat st;
  return path && stat(path, &st) == 0;
}

int fs_is_dir(const char *path) {
  struct stat st;
  if (!path || stat(path, &st) != 0) return 0;
#ifdef _WIN32
  return (st.st_mode & _S_IFDIR) != 0;
#else
  return S_ISDIR(st.st_mode);
#endif
}

int fs_is_file(const char *path) {
  struct stat st;
  if (!path || stat(path, &st) != 0) return 0;
#ifdef _WIN32
  return (st.st_mode & _S_IFREG) != 0;
#else
  return S_ISREG(st.st_mode);
#endif
}

char *fs_join(const char *a, const char *b) {
  if (!a) a = "";
  if (!b) b = "";
  while (*b == '/' || *b == '\\') b++;
  size_t la = strlen(a);
  while (la > 0 && (a[la - 1] == '/' || a[la - 1] == '\\')) la--;
  size_t lb = strlen(b);
  char *out = malloc(la + lb + 2);
  if (!out) return NULL;
  memcpy(out, a, la);
  if (la > 0) {
    out[la] = PATH_SEP;
    memcpy(out + la + 1, b, lb + 1);
  } else {
    memcpy(out, b, lb + 1);
  }
  return out;
}

char *fs_cwd(void) {
  char buf[4096];
#ifdef _WIN32
  if (!_getcwd(buf, sizeof(buf))) return NULL;
#else
  if (!getcwd(buf, sizeof(buf))) return NULL;
#endif
  return strdup(buf);
}

char *fs_dirname(const char *path) {
  if (!path || !*path) return strdup(".");
  char *copy = strdup(path);
  if (!copy) return NULL;
  size_t len = strlen(copy);
  while (len > 0 && (copy[len - 1] == '/' || copy[len - 1] == '\\')) {
    copy[--len] = '\0';
  }
  char *slash = NULL;
  for (char *p = copy; *p; p++) {
    if (*p == '/' || *p == '\\') slash = p;
  }
  if (!slash) {
    free(copy);
    return strdup(".");
  }
  *slash = '\0';
  if (copy[0] == '\0') {
    free(copy);
    return strdup("/");
  }
  return copy;
}

char *fs_basename(const char *path) {
  if (!path || !*path) return strdup("");
  const char *base = path;
  for (const char *p = path; *p; p++) {
    if (*p == '/' || *p == '\\') base = p + 1;
  }
  return strdup(base);
}

char *fs_norm_path(const char *path) {
  if (!path) return NULL;
  size_t len = strlen(path);
  char *out = malloc(len + 1);
  if (!out) return NULL;
  size_t o = 0;
  for (size_t i = 0; i < len; i++) {
    char c = path[i];
    if (c == '\\') c = '/';
    if (c == '.' && (i + 1 < len) && (path[i + 1] == '/' || path[i + 1] == '\\')) {
      i++;
      continue;
    }
    if (c == '.' && i + 1 == len) continue;
    out[o++] = c;
  }
  out[o] = '\0';
  return out;
}

int fs_copy_file(const char *src, const char *dst) {
  if (!src || !dst) return -1;
  size_t len = 0;
  char *data = fs_read_file(src, &len);
  if (!data) return -1;

  char *copy = strdup(dst);
  if (!copy) {
    free(data);
    return -1;
  }
  for (char *p = copy + 1; *p; p++) {
    if (*p == '/' || *p == '\\') {
      char saved = *p;
      *p = '\0';
      mkdir_one(copy);
      *p = saved;
    }
  }
  free(copy);

  FILE *f = fopen(dst, "wb");
  if (!f) {
    free(data);
    return -1;
  }
  size_t written = fwrite(data, 1, len, f);
  fclose(f);
  free(data);
  return written == len ? 0 : -1;
}

#ifdef _WIN32
int fs_copy_tree(const char *src, const char *dst) {
  if (!src || !dst) return -1;
  if (!fs_is_dir(src)) return -1;
  if (fs_mkdir_p(dst) != 0) return -1;

  char *pattern = fs_join(src, "*");
  if (!pattern) return -1;

  WIN32_FIND_DATAA fd;
  HANDLE h = FindFirstFileA(pattern, &fd);
  free(pattern);
  if (h == INVALID_HANDLE_VALUE) return 0;

  int rc = 0;
  do {
    if (strcmp(fd.cFileName, ".") == 0 || strcmp(fd.cFileName, "..") == 0)
      continue;
    char *src_child = fs_join(src, fd.cFileName);
    char *dst_child = fs_join(dst, fd.cFileName);
    if (!src_child || !dst_child) {
      free(src_child);
      free(dst_child);
      rc = -1;
      break;
    }
    if (fd.dwFileAttributes & FILE_ATTRIBUTE_DIRECTORY) {
      if (fs_copy_tree(src_child, dst_child) != 0) rc = -1;
    } else {
      if (fs_copy_file(src_child, dst_child) != 0) rc = -1;
    }
    free(src_child);
    free(dst_child);
    if (rc != 0) break;
  } while (FindNextFileA(h, &fd));

  FindClose(h);
  return rc;
}

int fs_dir_is_empty(const char *path) {
  if (!path) return -1;
  if (!fs_exists(path)) return 1;
  if (!fs_is_dir(path)) return -1;
  char *pattern = fs_join(path, "*");
  if (!pattern) return -1;
  WIN32_FIND_DATAA fd;
  HANDLE h = FindFirstFileA(pattern, &fd);
  free(pattern);
  if (h == INVALID_HANDLE_VALUE) return 1;
  int empty = 1;
  do {
    if (strcmp(fd.cFileName, ".") != 0 && strcmp(fd.cFileName, "..") != 0) {
      empty = 0;
      break;
    }
  } while (FindNextFileA(h, &fd));
  FindClose(h);
  return empty;
}

char *fs_exe_dir(void) {
  char buf[4096];
  DWORD n = GetModuleFileNameA(NULL, buf, (DWORD)sizeof(buf));
  if (n == 0 || n >= sizeof(buf)) return NULL;
  return fs_dirname(buf);
}

#else

int fs_copy_tree(const char *src, const char *dst) {
  if (!src || !dst) return -1;
  if (!fs_is_dir(src)) return -1;
  if (fs_mkdir_p(dst) != 0) return -1;

  DIR *d = opendir(src);
  if (!d) return 0;

  int rc = 0;
  struct dirent *ent;
  while ((ent = readdir(d)) != NULL) {
    if (strcmp(ent->d_name, ".") == 0 || strcmp(ent->d_name, "..") == 0)
      continue;
    char *src_child = fs_join(src, ent->d_name);
    char *dst_child = fs_join(dst, ent->d_name);
    if (!src_child || !dst_child) {
      free(src_child);
      free(dst_child);
      rc = -1;
      break;
    }
    if (fs_is_dir(src_child)) {
      if (fs_copy_tree(src_child, dst_child) != 0) rc = -1;
    } else {
      if (fs_copy_file(src_child, dst_child) != 0) rc = -1;
    }
    free(src_child);
    free(dst_child);
    if (rc != 0) break;
  }
  closedir(d);
  return rc;
}

int fs_dir_is_empty(const char *path) {
  if (!path) return -1;
  if (!fs_exists(path)) return 1;
  if (!fs_is_dir(path)) return -1;
  DIR *d = opendir(path);
  if (!d) return 1;
  int empty = 1;
  struct dirent *ent;
  while ((ent = readdir(d)) != NULL) {
    if (strcmp(ent->d_name, ".") != 0 && strcmp(ent->d_name, "..") != 0) {
      empty = 0;
      break;
    }
  }
  closedir(d);
  return empty;
}

char *fs_exe_dir(void) {
  char buf[4096];
  ssize_t n = readlink("/proc/self/exe", buf, sizeof(buf) - 1);
  if (n <= 0) return NULL;
  buf[n] = '\0';
  return fs_dirname(buf);
}

#endif
