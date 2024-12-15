#include <stdio.h>
#include <stdlib.h>
#include <string.h>
#include "cachelab.h"

/*------------------------------GLOBALS-----------------------------*/

/* Cacheline struct will contain a valid bit, tag, and lru value. */
typedef struct
{
    unsigned int valid : 1;
    unsigned long tag;
    int lru;
    
} CacheLine;

/* Cache is logically a 2D array of CacheLine structs, but must be dynamically allocated. */
CacheLine **cache;

/* Accumulators initialized globally to 0 */
int hit_count = 0;
int miss_count = 0;
int eviction_count = 0;

/*-------------------------------------------------------------------*/

/**
 * Initializes the cache with the given number of set index bits and lines per set.
 * Cache should start "cold".
 *
 * @param s The number of set index bits
 * @param E The number of lines per set
 */

void initializeCache(int s, int E)
{
    int S = 1 << s;
    cache = (CacheLine **)malloc(S * sizeof(CacheLine *));
    for (int i = 0; i < S; i++)
    {
        cache[i] = (CacheLine *)malloc(E * sizeof(CacheLine));
        for (int j = 0; j < E; j++)
        {
            cache[i][j].valid = 0;
            cache[i][j].lru = 0;
        }
    }
}

/*-------------------------------------------------------------------*/

/**
 * Accesses the cache at the given address. If the address is already in the cache,
 * increments hit_count. If the address is not in the cache, increments miss_count.
 * If the cache is full, increments eviction_count.
 *
 * @param address The address to access
 * @param s The number of set index bits
 * @param b The number of block bits
 * @param E The number of lines per set
 *
 */
void accessCache(unsigned long address, int s, int b, int E)
{

    int S = 1 << s;

    unsigned long set_index = (address >> b) & (S - 1);
    unsigned long tag = address >> (b + s);

    CacheLine *set = cache[set_index];

    int lru_min = set[0].lru, lru_min_index = 0;

    for (int i = 0; i < E; i++)
    {

        if (set[i].valid && set[i].tag == tag)
        {
            hit_count++;
            set[i].lru = miss_count + hit_count + eviction_count;
            return;
        }

        if (set[i].lru < lru_min)
        {
            lru_min = set[i].lru;
            lru_min_index = i;
        }
    }

    miss_count++;

    for (int i = 0; i < E; i++)
    {
        if (!set[i].valid)
        {
            set[i].valid = 1;
            set[i].tag = tag;
            set[i].lru = miss_count + hit_count + eviction_count;
            return;
        }
    }

    eviction_count++;

    set[lru_min_index].tag = tag;

    set[lru_min_index].lru = miss_count + hit_count + eviction_count;
}

/*-------------------------------------------------------------------*/

/**
 * Main function parses command line input, initializes the cache,
 * reads the trace file, and simulates the cache. Then calls required
 * printSummary function to match output of csim-ref.
 *
 * @param argc Number of command line args
 * @param argv Command line args to be parsed
 */
int main(int argc, char *argv[])
{

    int s, E, b;
    char operation;
    unsigned long address;
    int size;
    char *trace_file;

    for (int i = 1; i < argc; i++)
    {

        if (strcmp(argv[i], "-s") == 0)
        {
            char *endptr;
            s = strtol(argv[++i], &endptr, 10);
            if (*endptr != '\0')
            {
                fprintf(stderr, "Invalid value for -s: %s\n", argv[i]);
                exit(1);
            }
        }
        else if (strcmp(argv[i], "-E") == 0)
        {
            char *endptr;
            E = strtol(argv[++i], &endptr, 10);
            if (*endptr != '\0')
            {
                fprintf(stderr, "Invalid value for -E: %s\n", argv[i]);
                exit(1);
            }
        }
        else if (strcmp(argv[i], "-b") == 0)
        {
            char *endptr;
            b = strtol(argv[++i], &endptr, 10);
            if (*endptr != '\0')
            {
                fprintf(stderr, "Invalid value for -b: %s\n", argv[i]);
                exit(1);
            }
        }
        else if (strcmp(argv[i], "-t") == 0)
        {
            trace_file = argv[++i];
        }
    }

    initializeCache(s, E);

    FILE *trace = fopen(trace_file, "r");

    while (fscanf(trace, " %c %lx,%d", &operation, &address, &size) == 3)
    {

        if (operation == 'L' || operation == 'S' || operation == 'M')
        {

            accessCache(address, s, b, E);

            if (operation == 'M')
            {
                hit_count++;
            }
        }
    }

    fclose(trace);

    printSummary(hit_count, miss_count, eviction_count);

    return 0;
}
