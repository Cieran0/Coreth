#include "stdio.h"
#include "cvm.h"
#include "variable.h"
#include "function.h"
#include "token_type.h"
#include "stack.h"
#include "stack_operations.h"
#include "function_operations.h"

#define MAX_FUNCTION_COUNT 0xFFF

u8 test[]= {205,0,0,0,0,0,0,0,177,0,3,0,1,1,1,6,0,0,8,2,1,0,0,0,6,1,0,8,2,100,0,0,0,6,2,0,18,7,0,27,7,255,255,7,255,255,133,0,7,255,255,8,2,0,0,0,0,17,13,0,28,2,3,0,0,0,7,255,255,21,0,1,1,0,3,0,4,0,0,7,255,255,8,2,1,0,0,0,17,13,0,28,2,5,0,0,0,7,255,255,21,0,1,1,0,3,0,4,1,0,7,255,255,8,2,1,0,0,0,17,4,0,19,7,255,255,8,0,1,255,255,3,0,7,255,255,1,1,0,3,0,4,2,0,7,255,255,8,12,7,255,255,2,1,0,0,0,52,0,3,0,2,1,1,6,1,0,8,1,253,255,3,0,7,0,0,6,2,0,8,9,7,0,0,1,250,255,24,0,2,1,0,0,0,2,0,0,0,0,7,2,0,7,1,0,70,105,122,122,0,66,117,122,122,0,10,0};
u8* bytes;
u64 length; 
u8* ptr;
s64 stringOffset;
u64 fCount = 0;

inline void reset_ptr() {
    ptr=bytes;
}

void read_file(char* path) {
    FILE *f = fopen(path, "rb+");
    if (f)
    {
        fseek(f, 0L, SEEK_END);
        length = ftell(f); // get file size
        fseek(f, 0L ,SEEK_SET); //go back to the beginning
        bytes = (u8*)malloc(length); // allocate the read buf
        fread(bytes, 1, length, f);
        fclose(f);
        reset_ptr();
    }
}

void import_functions(function* buff) {
    stringOffset=next_s64(&ptr);
    while (ptr < bytes+stringOffset)
    {
        function f = new_function(next_s32(&ptr));
        buff[fCount] = f;
        ptr+=f.size;
        fCount++;
        printf("Size = %i, Variable Count = %i\n",f.size,f.variable_count);
    }
    reset_ptr();
    printf("Function Count: %lli\n",fCount);
}

int main(int argc, char const *argv[])
{
    variable* mem;
    function functions[MAX_FUNCTION_COUNT] = {0};
    read_file((char*)"test.cvm");
    import_functions(functions);
    stringOffset = next_s32(&ptr);
    reset_ptr();
    for (u64 i = 0; i < length; i++)
    {
        printf("%i,",next_u8(&ptr));
    }
    printf("%s","\n");
    printf("%s\n",(bytes+stringOffset));

    enter_function(functions[0],NULL,0,&mem);
    exit_function(&mem);
    return 0;
}
