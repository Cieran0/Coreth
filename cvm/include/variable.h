#pragma once
#include "cvm.h"

#define INT 0
#define STR 1
#define PTR 2
#define UNDEFINED 3

#define cast(v,x) (x)v.data 

typedef void* variable_data;
typedef u8 variable_type;

struct variable
{
    variable_type type;
    variable_data data;

} typedef variable;

char* variable_as_string(variable v)
{
    return (char*)(v.data);
}
s64 variable_as_integer(variable v)
{
    return (s64)(v.data);
}
void* variable_as_pointer(variable v)
{
    return v.data;
}

variable declare_variable() {
    return (variable){UNDEFINED,0};
}
variable declare_variable(variable_type type) {
    return (variable){type,0};
}
variable declare_variable(variable_type type, variable_data data) {
    return (variable){type,data};
}

