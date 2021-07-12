cmake_minimum_required(VERSION 3.15)
project(player)

set(CMAKE_CXX_STANDARD 11)

add_executable(player main.cpp src/cjson/cJSON.h src/cjson/cJSON_Utils.h src/test/test3.cpp src/test/test3.h)