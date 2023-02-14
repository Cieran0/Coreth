#pragma once
#include "function.h"
#include "stack.h"
#include "stack_operations.h"

function new_function(u32 info)
{
    return *((function*)(&info));
}

void enter_function(function func, variable* params, s64 count, variable** memory)
{
    push(&function_stack,memory);
    (*memory)=allocate(func.variable_count,variable);
    (*memory)[0] = (variable){.type=INT,.data=(void*)100};
}

void exit_function(variable** memory)
{
    variable* old_mem = (*memory);
    free(old_mem);
    (*memory) = pop(&function_stack); 
}
