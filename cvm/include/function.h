#pragma once
#include "variable.h"
#include "stdio.h"
#include <stdlib.h>

struct __attribute__((packed)) function
{
    u16 size;
    u16 variable_count;
    u8* instructions;
} typedef function;