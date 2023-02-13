#pragma once
#include "stdlib.h"
#define allocate(count,type) (type*)malloc(sizeof(type)*count)

typedef char s8;
typedef long long s64;
typedef int s32;
typedef short s16;
typedef unsigned char u8;
typedef unsigned long long u64;
typedef unsigned int u32;
typedef unsigned short u16;
typedef unsigned char u8;

#define Next(type) \
type next_##type(u8** ptrPtr) \
{ \
    *(ptrPtr)+=sizeof(type);\
    return *((type*)(*(ptrPtr)-sizeof(type)));\
}
Next(s64)
Next(s32)
Next(s16)
Next(s8)
Next(u64)
Next(u32)
Next(u16)
Next(u8)