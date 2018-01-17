cmake_minimum_required(VERSION 3.4.1)
set(CMAKE_VERBOSE_MAKEFILE on)

include_directories( /home/jaycee/workspace/BristolTester/app/src/main/jniLibs${ANDROID_ABI}/include
                     src/main/cpp/include )

add_library( openal SHARED IMPORTED )
set_target_properties( openal PROPERTIES IMPORTED_LOCATION /home/jaycee/workspace/BristolTester/app/src/main/jniLibs/${ANDROID_ABI}/libopenal.so )

set( CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -std=c++11 -v" )

# Link OpenAL to SoundLib

add_library( SoundLib
             SHARED
             src/main/cpp/SoundLib.cpp )

target_link_libraries( SoundLib
                       openal
                       log
                       m )


# Link SoundLib to Limit source
add_library( limit
             SHARED
             src/main/cpp/limit.cpp )

target_link_libraries( limit
                       SoundLib )

# Link SoundLib to Tonal source
add_library( tonal
             SHARED
             src/main/cpp/tonal.cpp )

target_link_libraries( tonal
                       SoundLib )

# Link SoundLib to Spatial source
add_library( spatial
             SHARED
             src/main/cpp/spatial.cpp )

target_link_libraries( spatial
                       SoundLib )