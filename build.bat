@echo off
echo Building Runix (hexagonal + micromodular)...
gcc -Wall -Wextra -Werror -Wno-unused-parameter -Wno-unused-function -g -std=c17 -D_POSIX_C_SOURCE=200809L -Isrc -o runix ^
  src/main.c ^
  src/domain/project.c ^
  src/application/doctor_service.c ^
  src/application/init_service.c ^
  src/application/check_service.c ^
  src/application/dev_service.c ^
  src/application/build_service.c ^
  src/application/delegate_service.c ^
  src/adapters/inbound/cli.c ^
  src/adapters/outbound/fs/fs.c ^
  src/adapters/outbound/process/process_spawn.c ^
  src/adapters/outbound/cordlang/cordlang_cli.c
if %ERRORLEVEL% EQU 0 (
  echo Build successful: runix.exe
) else (
  echo Build failed.
  exit /b 1
)
