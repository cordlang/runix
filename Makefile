CC ?= gcc
CFLAGS ?= -Wall -Wextra -Werror -Wno-unused-parameter -Wno-unused-function -g -std=c17 -D_POSIX_C_SOURCE=200809L -Isrc

SRCS = \
  src/main.c \
  src/domain/project.c \
  src/application/doctor_service.c \
  src/application/init_service.c \
  src/application/check_service.c \
  src/application/dev_service.c \
  src/application/build_service.c \
  src/application/delegate_service.c \
  src/adapters/inbound/cli.c \
  src/adapters/outbound/fs/fs.c \
  src/adapters/outbound/process/process_spawn.c \
  src/adapters/outbound/cordlang/cordlang_cli.c

.PHONY: all clean test

all: runix

runix: $(SRCS)
	$(CC) $(CFLAGS) -o $@ $(SRCS)

clean:
	rm -f runix runix.exe

test: runix
	./runix --version
	./runix help
