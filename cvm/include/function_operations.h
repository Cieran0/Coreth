#pragma once
#include "function.h"
#include "stack.h"
#include "stack_operations.h"

function new_function(u32 info)
{
    return *((function*)(&info));
}

variable_data execute_function(function func, variable* memory, variable_data* params, u16 param_count) {
    variable_data return_value = NULL;
    variable* local_memory = allocate(param_count,variable);
    for (u16 i = 0; i < param_count; i++)
    {
        local_memory[i].data=params[i];
    }
    u8 should_exit = 0;
    for (u8* current_instruction = func.instructions; current_instruction < func.instructions+func.size && !should_exit;)
    {
        execute_instruction(func,&current_instruction,&memory,&should_exit);
    }
    return return_value;
}

variable_data execute_instruction(function func, u8** current_instruction, variable** mem, u8* should_exit) {
    #define memory (*mem)
    u8 this_instruction = next_u8(current_instruction); 
    switch (this_instruction)
    {
        case FUNCTION_CALL:
            //TODO: function call stuff
            break;
        case CONSTANT_INTEGER:
        case INTEGER:
            return next_s64(current_instruction);
        case CONSTANT_STRING:
        case STRING:
            //TODO: return string from string offset;
            return next_s8(current_instruction);
        case VARIABLE_DECLARATION:
        case VARIABLE_REFRENCE:
            s16 variable_index = next_s16(current_instruction);
            if(func.instructions+func.size <= current_instruction+3) {
                return memory[next_s16(current_instruction)].data;
            }
            u8 next_token_type = next_u8(current_instruction);
            if(next_token_type == VARIABLE_ASSIGNMENT) {
                variable_data next_data = execute_instruction(func,current_instruction,mem,should_exit);
                memory[variable_index].data=next_data;
            } else {
                current_instruction--;
                return memory[variable_index].data;
            }
            return memory[variable_index].data;
        case NOT:
            variable_data next_data = execute_instruction(func,current_instruction,mem,should_exit);
            return !((s64)next_data);
        case REFERENCE:
            u8 next_token_type = next_u8(current_instruction);
            if(next_token_type!=VARIABLE_DECLARATION&&next_token_type!=VARIABLE_ASSIGNMENT) {
                printf("Trying to get refrence to something that is not a variable!");
                exit(-1);
            }
            return next_s16(current_instruction);
        case DEREFERENCE:
            variable_data next_data = execute_instruction(func,current_instruction,mem,should_exit);
            return memory[(s64)next_data].data;
        case PLUS:
        case MINUS:
        case DIVIDE:
        case MULTIPLY:
        case MODULUS:
        case AND:
        case OR:
        case IS_FACTOR:
        case EQUAL:
        case NOT_EQUAL:
        case GREATER:
        case LESSER:
        case NOT_LESSER:
        case NOT_GREATER:
            variable_data n1 = execute_instruction(func,current_instruction,mem,should_exit);
            variable_data n2 = execute_instruction(func,current_instruction,mem,should_exit);
            return do_maths(n1,n2,this_instruction);
        case RETURN:
            (*should_exit) = 1;
            if(func.instructions+func.size <= current_instruction+1)
                break;
            return execute_instruction(func,current_instruction,mem,should_exit);
        default:
            break;
    }
    return NULL;
    #undef memory
}

s64 do_maths(variable_data n1, variable_data n2, u8 maths_type) {
    s64 number_1 = (s64)n1;
    s64 number_2 = (s64)n2;
    switch (maths_type)
    {
        case PLUS:
            return number_1+number_2;
        case MINUS:
            return number_1-number_2;
        case DIVIDE:
            return number_1/number_2;
        case MULTIPLY:
            return number_1*number_2;
        case MODULUS:
            return number_1%number_2;
        case AND:
            return number_1&&number_2;
        case OR:
            return number_1||number_2;
        case IS_FACTOR:
            return (number_2%number_1==0);
        case EQUAL:
            return number_1==number_2;
        case NOT_EQUAL:
            return number_1!=number_2;
        case GREATER:
            return number_1>number_2;
        case LESSER:
            return number_1<number_2;
        case NOT_LESSER:
            return number_1>=number_2;
        case NOT_GREATER:
            return number_1<=number_2;
        default:
            break;
    }
    return 0;
}