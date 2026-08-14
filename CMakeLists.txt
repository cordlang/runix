cmake_minimum_required(VERSION 3.10)
project(runix C)

set(CMAKE_C_STANDARD 17)
set(CMAKE_C_STANDARD_REQUIRED ON)

set(SOURCES
  src/main.c
  src/domain/project.c
  src/application/doctor_service.c
  src/application/init_service.c
  src/application/check_service.c
  src/application/dev_service.c
  src/application/build_service.c
  src/application/delegate_service.c
  src/adapters/inbound/cli.c
  src/adapters/outbound/fs/fs.c
  src/adapters/outbound/process/process_spawn.c
  src/adapters/outbound/cordlang/cordlang_cli.c
)

add_executable(runix ${SOURCES})
target_include_directories(runix PRIVATE src)
target_compile_definitions(runix PRIVATE _POSIX_C_SOURCE=200809L)
target_compile_options(runix PRIVATE
  -Wall -Wextra -Werror -Wno-unused-parameter -Wno-unused-function)

option(RUNIX_ASAN "Build with AddressSanitizer + UBSan" OFF)
if(RUNIX_ASAN)
  target_compile_options(runix PRIVATE -fsanitize=address,undefined -fno-omit-frame-pointer)
  target_link_options(runix PRIVATE -fsanitize=address,undefined)
endif()
