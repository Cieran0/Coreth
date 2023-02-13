#include "stdio.h"
#include "cvm.h"
#include "variable.h"
#include "function.h"
#include "token_type.h"

#define MAX_FUNCTION_COUNT 0xFFF

u8* bytes;
u64 length; 
u8* ptr;

void read_file(char* path) {
    FILE *f = fopen(path, "rb+");
    if (f)
    {
        fseek(f, 0L, SEEK_END);
        long filesize = ftell(f); // get file size
        fseek(f, 0L ,SEEK_SET); //go back to the beginning
        u8* bytes = (u8*)malloc(filesize); // allocate the read buf
        fread(bytes, 1, filesize, f);
        fclose(f);
    }
}

int main(int argc, char const *argv[])
{
    variable* mem = NULL;
    function functions[MAX_FUNCTION_COUNT] = {0};
    printf("%li\n",sizeof(functions));
    return 0;
}
