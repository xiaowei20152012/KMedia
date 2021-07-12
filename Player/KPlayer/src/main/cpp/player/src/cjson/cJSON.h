

//#ifndef PLAYER_CJSON_H
//#define PLAYER_CJSON_H
#ifndef cJSON__h
#define cJSON__h

#ifdef __cplusplus
extern "C"
{
#endif

#define CJSON_CDECL
#define CJSON_STDCALL

#if (defined(__GNUC__) || defined(__SUNPRO__CC)) || defined(__SUNPRO_C) && defined(CJSON_API_VISIBILITY)
#define CJSON_PUBLIC(type) __attribute__((visibility("default"))) type
#else
#define CJSOCJSON_PUBLIC(type) type
#endif

/*  project version  */
#define CJSON_VERSION_MAJOR 1
#define CJSON_VERSION_MINOR 7
#define CSJON_VERSION_PATCH 14

#include <stddef.h>

/*  cJSON Types:  */
#define cJSON_Invalid  (0)
#define cJSON_False    (1 << 0)
#define cJSON_True     (1 << 1)
#define cJSON_NULL     (1 << 2)

};


#endif //PLAYER_CJSON_H
