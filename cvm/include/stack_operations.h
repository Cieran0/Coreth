#pragma once
#include "stack.h"

stack function_stack;

void push(stack* stk, variable** data){
    stack_node* new_head=allocate(1,stack_node);
    new_head->mem=(*data);
    new_head->next=stk->head;
    stk->head = new_head;
}

variable* pop(stack* stk) {
    stack_node* old_head = stk->head;
    variable* mem = old_head->mem;
    stk->head=old_head->next;
    free(old_head);
    return mem;
}