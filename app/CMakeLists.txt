cmake_minimum_required(VERSION 3.6)

project("app")

add_library(
        cpp_code
        SHARED
        src/main/cpp/cpp_code.cpp
)

target_link_libraries(
        cpp_code
        log
)


# Set C++ standard
set(CMAKE_CXX_STANDARD 11)