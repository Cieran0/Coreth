#pragma once
#include "variable.h"
#include "stdio.h"
#include <stdlib.h>

struct __attribute__((packed)) function
{
    u16 size;
    u16 variable_count;
    variable* vars;
    variable* copyOfMem;
} typedef function;

function new_function(u32 info)
{
    function f = *((function*)(&info));
    f.vars = (variable*)malloc(f.variable_count*sizeof(variable));
    return f;
}

void enter_function(function* func, variable* params, s64 count, variable** memory)
{
    #define f (*func)
    f.vars[0]= (variable){.type=INT, .data=((void*)100)};
    f.copyOfMem=(*memory);
    (*memory)=f.vars;
    #undef f
}

void exit_function(function* current_function, variable** memory)
{
    #define f (*current_function)
    (*memory)=f.copyOfMem;
    free(f.vars);
    #undef f
}
