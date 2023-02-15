#include "stdio.h"
#include "cvm.h"
#include "variable.h"
#include "function.h"
#include "token_type.h"
#include "stack.h"
#include "stack_operations.h"
#include "function_operations.h"

#define MAX_FUNCTION_COUNT 0xFFF

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
        f.instructions=ptr;
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
    printf("%i\n",functions[0].size);
    printf("%p\n",functions[0].instructions);
    //for (u64 i = 0; i < length; i++)
    //{
    //    printf("%i,",next_u8(&ptr));
    //}
    //printf("%s","\n");
    //printf("%s\n",(bytes+stringOffset));
    execute_function(functions[0],NULL,NULL,0);
    return 0;
}
