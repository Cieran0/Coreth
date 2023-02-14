#pragma once
#include "variable.h"
#include "cvm.h"
#include "function.h"

struct stack_node
{
    stack_node* next;
    variable* mem;
}typedef stack_node;


struct stack
{
    stack_node* head;
}typedef stack;