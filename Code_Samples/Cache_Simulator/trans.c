

/* Maisy Dunlavy
 * NetID: 101882330
 *
 * trans.c - Matrix transpose B = A^T
 *
 * Each transpose function must have a prototype of the form:
 * void trans(int M, int N, int A[N][M], int B[M][N]);
 *
 * A transpose function is evaluated by counting the number of misses
 * on a 1KB direct mapped cache with a block size of 32 bytes.
 */
#include <stdio.h>
#include "cachelab.h"

int is_transpose(int M, int N, int A[N][M], int B[M][N]);

/*
 * transpose_submit - This is the solution transpose function that you
 *     will be graded on for Part B of the assignment. Do not change
 *     the description string "Transpose submission", as the driver
 *     searches for that string to identify the transpose function to
 *     be graded.
 */
char transpose_submit_desc[] = "Transpose submission";
void transpose_submit(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, ii, jj, temp;
    int blockSize;
    //switch on unqiue M values for matrix sizes - we are allowed to hardcode these values and optimize only for these
    switch (M)
    {
    //case 32 is optimized for a 32x32 matrix
    case 32:
    {
        blockSize = 8;
        //8 x 8 blocks
        for (i = 0; i < N; i += blockSize)
        {
            for (j = 0; j < M; j += blockSize)
            {
                //in each block, go through rows and cols
                for (ii = i; ii < i + blockSize && ii < N; ++ii)
                {
                    for (jj = j; jj < j + blockSize && jj < M; ++jj)
                    {
                        //if not on the diagonal, transpose directly
                        if (ii != jj)
                        {
                            B[jj][ii] = A[ii][jj]; 
                        }
                        else //will be diagonal if it hits this, stores value in temp
                        {
                            temp = A[ii][jj];
                        }
                    }
                    if (i == j)
                    {
                        // then this writes diagonals after the block 
                        B[ii][ii] = temp; 
                    }
                }
            }
        }
        break;
    }

    case 64:
    {

//processing in 8x4 blocks 
        for (i = 0; i < 64; i += 8)
        {
            for (j = 0; j < 64; j += 4)
            {
                //non diagonal blocks 
                if (i != j)
                {
                    for (ii = i; ii < i + 8; ++ii)
                    {
                        for (jj = j; jj < j + 4; ++jj)
                        {
                            B[jj][ii] = A[ii][jj]; 
                        }
                    }
                }
                else
                {
                    // and than handle diagonal blocks here
                    for (ii = i; ii < i + 8; ++ii)
                    {
                        for (jj = j; jj < j + 4; ++jj)
                        {
                            if (ii != jj)
                            {
                                B[jj][ii] = A[ii][jj]; 
                            }
                            else
                            {
                                temp = A[ii][jj];
                            }
                        }
                        B[ii][ii] = temp;
                    }
                }
            }
        }
        break;
    }

    case 61:
    {
        int blockSize = 16;
        //16x16 blocks 
        for (i = 0; i < N; i += blockSize)
        {
            for (j = 0; j < M; j += blockSize)
            {
                //directly processing each element
                for (ii = i; ii < i + blockSize && ii < N; ++ii)
                {
                    for (jj = j; jj < j + blockSize && jj < M; ++jj)
                    {
                        B[jj][ii] = A[ii][jj];
                    }
                }
            }
        }

        break;
    }
    default:
        printf("Invalid matrix configuration\n");
        break;
    }
}

/*
 * You can define additional transpose functions below. We've defined
 * a simple one below to help you get started.
 */

/*
 * trans - A simple baseline transpose function, not optimized for the cache.
 */
char trans_desc[] = "Simple row-wise scan transpose";
void trans(int M, int N, int A[N][M], int B[M][N])
{
    int i, j, tmp;

    for (i = 0; i < N; i++)
    {
        for (j = 0; j < M; j++)
        {
            tmp = A[i][j];
            B[j][i] = tmp;
        }
    }
}

/*
 * registerFunctions - This function registers your transpose
 *     functions with the driver.  At runtime, the driver will
 *     evaluate each of the registered functions and summarize their
 *     performance. This is a handy way to experiment with different
 *     transpose strategies.
 */
void registerFunctions()
{
    /* Register your solution function */
    registerTransFunction(transpose_submit, transpose_submit_desc);

    /* Register any additional transpose functions */
    registerTransFunction(trans, trans_desc);
}

/*
 * is_transpose - This helper function checks if B is the transpose of
 *     A. You can check the correctness of your transpose by calling
 *     it before returning from the transpose function.
 */
int is_transpose(int M, int N, int A[N][M], int B[M][N])
{
    int i, j;

    for (i = 0; i < N; i++)
    {
        for (j = 0; j < M; ++j)
        {
            if (A[i][j] != B[j][i])
            {
                return 0;
            }
        }
    }
    return 1;
}
