#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include <stdnoreturn.h>
#include <threads.h>
#include <stdatomic.h>

// define macro
#define PI 3.1415

// typedef usage
typedef struct {
    int x;
    int y;
} Point;

// enum
enum Color { RED, GREEN, BLUE };

// union
union Data {
    int i;
    float f;
};

// struct with bit fields
struct Flags {
    unsigned int flag1 : 1;
    unsigned int flag2 : 1;
};

// _Atomic variable
_Atomic int counter = 0;

// inline function
inline int square(int x) {
    return x * x;
}

// restrict pointer
void add_array(int* restrict a, int* restrict b, int* restrict result, int size) {
    for (int i = 0; i < size; ++i)
        result[i] = a[i] + b[i];
}

// _Generic demonstration
void print_type(int x) {
    printf("%s\n", _Generic((x), int: "int", float: "float", default: "unknown"));
}

// _Static_assert
_Static_assert(sizeof(int) == 4, "Int must be 4 bytes");

// noreturn function
noreturn void fatal_error(const char* msg) {
    fprintf(stderr, "Fatal error: %s\n", msg);
    exit(1);
}

int main(void) {
    // auto storage
    auto int x = 5;

    // register (deprecated)
    register int r = 3;

    // volatile usage
    volatile int flag = 1;

    // const usage
    const double ratio = PI;

    // goto and label
    goto skip;

skip:
    puts("Jumped here using goto");

    // switch-case-default
    int color = RED;
    switch (color) {
        case RED:
            puts("Red color");
            break;
        case GREEN:
            puts("Green color");
            break;
        default:
            puts("Unknown color");
            break;
    }

    // if-else
    if (x > 0) {
        puts("x is positive");
    } else {
        puts("x is non-positive");
    }

    // while
    while (r > 0) {
        --r;
    }

    // do-while
    int count = 0;
    do {
        ++count;
    } while (count < 3);

    // for loop
    for (int i = 0; i < 5; ++i) {
        continue;
        break;
    }

    // create struct
    Point p = {1, 2};
    printf("Point: (%d, %d)\n", p.x, p.y);

    // union usage
    union Data d;
    d.i = 42;
    printf("Union int: %d\n", d.i);

    // use inline function
    printf("Square: %d\n", square(6));

    // call _Generic demo
    print_type(100);

    // restrict demo
    int a[3] = {1, 2, 3};
    int b[3] = {4, 5, 6};
    int result[3];
    add_array(a, b, result, 3);
    printf("Result: %d %d %d\n", result[0], result[1], result[2]);

    // return
    return 0;
}
