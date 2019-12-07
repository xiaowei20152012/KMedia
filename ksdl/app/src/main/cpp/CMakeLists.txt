# For more information about using CMake with Android Studio, read the
# documentation: https://d.android.com/studio/projects/add-native-code.html

# Sets the minimum version of CMake required to build the native library.

cmake_minimum_required(VERSION 3.4.1)

# Creates and names a library, sets it as either STATIC
# or SHARED, and provides the relative paths to its source code.
# You can define multiple libraries, and CMake builds them for you.
# Gradle automatically packages shared libraries with your APK.

#set(include_dir
#        SDL/include)

#set(source_files
#        SDL/src/SDL.c)

add_library( # Sets the name of the library.
        native-lib

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        native-lib.cpp)

set(include_dir
        SDL/include)

include_directories(${include_dir})

# 设置要编译的源文件
set(source_files
        SDL/src/SDL.c
        SDL/src/SDL_assert.c
        SDL/src/SDL_dataqueue.c
        SDL/src/SDL_error.c
        SDL/src/SDL_hints.c
        SDL/src/SDL_log.c

        SDL/src/audio/SDL_audio.c
        SDL/src/audio/SDL_audiocvt.c
        SDL/src/audio/SDL_audiodev.c
        SDL/src/audio/SDL_audiotypecvt.c
        SDL/src/audio/SDL_mixer.c
        SDL/src/audio/SDL_wave.c

        SDL/src/audio/android/SDL_androidaudio.c

        SDL/src/audio/dummy/SDL_dummyaudio.c

        SDL/src/audio/openslES/SDL_openslES.c

        SDL/src/atomic/SDL_atomic.c
        SDL/src/atomic/SDL_spinlock.c

        SDL/src/core/android/SDL_android.c

        SDL/src/cpuinfo/SDL_cpuinfo.c

        SDL/src/dynapi/SDL_dynapi.c

        SDL/src/events/SDL_clipboardevents.c
        SDL/src/events/SDL_displayevents.c
        SDL/src/events/SDL_dropevents.c
        SDL/src/events/SDL_events.c
        SDL/src/events/SDL_gesture.c
        SDL/src/events/SDL_keyboard.c
        SDL/src/events/SDL_mouse.c
        SDL/src/events/SDL_quit.c
        SDL/src/events/SDL_touch.c
        SDL/src/events/SDL_windowevents.c

        SDL/src/file/SDL_rwops.c

        SDL/src/haptic/SDL_haptic.c

        SDL/src/haptic/android/SDL_syshaptic.c

        SDL/src/joystick/SDL_gamecontroller.c
        SDL/src/joystick/SDL_joystick.c

        SDL/src/joystick/hidapi/SDL_hidapi_ps4.c
        SDL/src/joystick/hidapi/SDL_hidapi_switch.c
        SDL/src/joystick/hidapi/SDL_hidapi_xbox360.c
        SDL/src/joystick/hidapi/SDL_hidapi_xboxone.c
        SDL/src/joystick/hidapi/SDL_hidapijoystick.c

        SDL/src/loadso/dlopen/SDL_sysloadso.c

        SDL/src/power/SDL_power.c

        SDL/src/power/android/SDL_syspower.c

        SDL/src/filesystem/android/SDL_sysfilesystem.c

        SDL/src/sensor/SDL_sensor.c

        SDL/src/sensor/android/SDL_androidsensor.c

        SDL/src/render/SDL_d3dmath.c
        SDL/src/render/SDL_render.c
        SDL/src/render/SDL_yuv_sw.c

        SDL/src/render/direct3d/SDL_render_d3d.c
        SDL/src/render/direct3d/SDL_shaders_d3d.c
        SDL/src/render/direct3d11/SDL_render_d3d11.c
        SDL/src/render/direct3d11/SDL_render_winrt.cpp
        SDL/src/render/direct3d11/SDL_shaders_d3d11.c
        SDL/src/render/opengl/SDL_render_gl.c
        SDL/src/render/opengl/SDL_shaders_gl.c
        SDL/src/render/opengles/SDL_render_gles.c
        SDL/src/render/opengles2/SDL_render_gles2.c
        SDL/src/render/opengles2/SDL_shaders_gles2.c
        SDL/src/render/psp/SDL_render_psp.c
        SDL/src/render/software/SDL_blendfillrect.c
        SDL/src/render/software/SDL_blendline.c
        SDL/src/render/software/SDL_blendpoint.c
        SDL/src/render/software/SDL_drawline.c
        SDL/src/render/software/SDL_drawpoint.c
        SDL/src/render/software/SDL_render_sw.c
        SDL/src/render/software/SDL_rotate.c

        SDL/src/stdlib/SDL_getenv.c
        SDL/src/stdlib/SDL_iconv.c
        SDL/src/stdlib/SDL_malloc.c
        SDL/src/stdlib/SDL_qsort.c
        SDL/src/stdlib/SDL_stdlib.c
        SDL/src/stdlib/SDL_string.c

        SDL/src/thread/SDL_thread.c
        SDL/src/thread/pthread/SDL_syscond.c
        SDL/src/thread/pthread/SDL_sysmutex.c
        SDL/src/thread/pthread/SDL_syssem.c
        SDL/src/thread/pthread/SDL_systhread.c
        SDL/src/thread/pthread/SDL_systls.c

        SDL/src/timer/SDL_timer.c
        SDL/src/timer/unix/SDL_systimer.c

        SDL/src/video/SDL_blit.c
        SDL/src/video/SDL_blit_0.c
        SDL/src/video/SDL_blit_1.c
        SDL/src/video/SDL_blit_A.c
        SDL/src/video/SDL_blit_auto.c
        SDL/src/video/SDL_blit_copy.c
        SDL/src/video/SDL_blit_N.c
        SDL/src/video/SDL_blit_slow.c
        SDL/src/video/SDL_bmp.c
        SDL/src/video/SDL_clipboard.c
        SDL/src/video/SDL_egl.c
        SDL/src/video/SDL_fillrect.c
        SDL/src/video/SDL_pixels.c
        SDL/src/video/SDL_rect.c
        SDL/src/video/SDL_RLEaccel.c
        SDL/src/video/SDL_shape.c
        SDL/src/video/SDL_stretch.c
        SDL/src/video/SDL_surface.c
        SDL/src/video/SDL_video.c
        SDL/src/video/SDL_vulkan_utils.c
        SDL/src/video/SDL_yuv.c

        SDL/src/video/android/SDL_androidclipboard.c
        SDL/src/video/android/SDL_androidevents.c
        SDL/src/video/android/SDL_androidgl.c
        SDL/src/video/android/SDL_androidkeyboard.c
        SDL/src/video/android/SDL_androidmessagebox.c
        SDL/src/video/android/SDL_androidmouse.c
        SDL/src/video/android/SDL_androidtouch.c
        SDL/src/video/android/SDL_androidvideo.c
        SDL/src/video/android/SDL_androidvulkan.c
        SDL/src/video/android/SDL_androidwindow.c

        SDL/src/video/yuv2rgb/yuv_rgb.c

        SDL/src/test/SDL_test_assert.c
        SDL/src/test/SDL_test_common.c
        SDL/src/test/SDL_test_compare.c
        SDL/src/test/SDL_test_crc32.c
        SDL/src/test/SDL_test_font.c
        SDL/src/test/SDL_test_fuzzer.c
        SDL/src/test/SDL_test_harness.c
        SDL/src/test/SDL_test_imageBlit.c
        SDL/src/test/SDL_test_imageBlitBlend.c
        SDL/src/test/SDL_test_imageFace.c
        SDL/src/test/SDL_test_imagePrimitives.c
        SDL/src/test/SDL_test_imagePrimitivesBlend.c
        SDL/src/test/SDL_test_log.c
        SDL/src/test/SDL_test_md5.c
        SDL/src/test/SDL_test_memory.c
        SDL/src/test/SDL_test_random.c


        )
add_library( # Sets the name of the library.
        sdl2

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        ${source_files})


add_library( # Sets the name of the library.
        kmedia

        # Sets the library as a shared library.
        SHARED

        # Provides a relative path to your source file(s).
        media/media_bridge.cpp)


# Searches for a specified prebuilt library and stores the path as a
# variable. Because CMake includes system libraries in the search path by
# default, you only need to specify the name of the public NDK library
# you want to add. CMake verifies that the library exists before
# completing its build.

find_library( # Sets the name of the path variable.
        log-lib

        # Specifies the name of the NDK library that
        # you want CMake to locate.
        log)

# Specifies libraries CMake should link to your target library. You
# can link multiple libraries, such as libraries you define in this
# build script, prebuilt third-party libraries, or system libraries.

#set(local_c_flags
#        -DGL_GLEXT_PROTOTYPES
#        -Wall -Wextra
#        -Wdocumentation
#        -Wdocumentation-unknown-command
#        -Wmissing-prototypes
#        -Wunreachable-code-break
#        -Wunneeded-internal-declaration
#        -Wmissing-variable-declarations
#        -Wfloat-conversion
#        -Wunreachable-code-return
#        -Wno-unused-parameter -Wno-sign-compare )
#set(CMAKE_C_FLAGS ${local_c_flags} )

target_link_libraries( # Specifies the target library.
        native-lib

#        sdl2

        kmedia

        GLESv1_CM
        GLESv2
        android
        jnigraphics
        # Links the target library to the log library
        # included in the NDK.
        ${log-lib})