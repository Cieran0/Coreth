#include "stdio.h"
#include "cvm.h"
#include "variable.h"
#include "function.h"
#include <unistd.h>

#define MAX_FUNCTION_COUNT 0xFFF

u8 bytes[] = {8,0,16,0,0,0,0,0,16,0,0,0,0,0,0,0};
u8* ptr = bytes;

int main(int argc, char const *argv[])
{
    variable* mem = NULL;
    function functions[MAX_FUNCTION_COUNT] = {0};
    functions[0] = new_function(next_u32(&ptr));
    printf("%li\n",sizeof(functions));
    return 0;
}
