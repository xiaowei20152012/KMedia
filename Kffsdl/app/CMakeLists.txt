cmake_minimum_required(VERSION 3.4.1)

#直接添加SDL库
#include_directories(${CMAKE_SOURCE_DIR}/libs/SDL2/include)
#add_subdirectory(${CMAKE_SOURCE_DIR}/libs/SDL2)

# 添加FFMpeg
#native-lib
set(FFMPEG_INCLUDE ${CMAKE_SOURCE_DIR}/libs/ffmpeg/include)
include_directories(${FFMPEG_INCLUDE})

#set(ANDROID_ABI armeabi-v7a)

set(LINK_DIR ${CMAKE_SOURCE_DIR}/libs/${ANDROID_ABI})

add_library(ffcodec SHARED IMPORTED)
set_target_properties(ffcodec PROPERTIES IMPORTED_LOCATION ${LINK_DIR}/libavcodec.so)

add_library(ffavfilter SHARED IMPORTED)
set_target_properties(ffavfilter PROPERTIES IMPORTED_LOCATION ${LINK_DIR}/libavfilter.so)

add_library(ffavformat SHARED IMPORTED)
set_target_properties(ffavformat PROPERTIES IMPORTED_LOCATION ${LINK_DIR}/libavformat.so)

add_library(ffavutil SHARED IMPORTED)
set_target_properties(ffavutil PROPERTIES IMPORTED_LOCATION ${LINK_DIR}/libavutil.so)

add_library(ffswscale SHARED IMPORTED)
set_target_properties(ffswscale PROPERTIES IMPORTED_LOCATION ${LINK_DIR}/libswscale.so)


# 添加SDL2
set(SDL_INCLUDE ${CMAKE_SOURCE_DIR}/libs/sdl/include)
include_directories(${SDL_INCLUDE})


add_library(sdl2 SHARED IMPORTED)
set_target_properties(sdl2 PROPERTIES IMPORTED_LOCATION ${LINK_DIR}/libSDL2.so)





#LOCAL_SHARED_LIBRARIES := SDL2
#
#LOCAL_LDLIBS := -lGLESv1_CM -lGLESv2 -llog
#file(GLOB MAIN_SOURCE src/main/cpp/log.h
#        src/main/cpp/native-lib.cpp
#        src/main/cpp/native-lib-audio.cpp
#        src/main/cpp/native-lib-bmg.cpp
#        src/main/cpp/native-lib-su.cpp)

add_library(
        native_lib
        # Sets the library as a shared library.
        SHARED
        # Provides a relative path to your source file(s).
        src/main/cpp/native_lib.cpp
)


find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

target_link_libraries( # Specifies the target library.
        native_lib

        ffcodec
        ffavfilter
        ffavformat
        ffavutil
        ffswscale

        sdl2
        GLESv1_CM
        GLESv2
#        ffmpeg
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})