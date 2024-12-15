/**
 * @author maisy dunlavy
 * date 11/26/2023
 * cs241L 
 */
#include <stdio.h>
#include <stdlib.h>
#include "header.h"

/**2D dynamically allocated arrays to store maze information*/
int **maze;
int **solution;
int **trackPaths;
int *line;
int i, j, c, SIZE;

/**findMazeSize allocates size for a max 200x200 maze, then reallocates based on the real maze size. It then feeds in the values for 
 * the maze, solution, and trackPaths mazes*/
void findMazeSize(void)
{
	SIZE = 0;
	line = (int*)malloc(150 * sizeof(int));
	c = getchar();
	while(c != '\n')
	{
		SIZE++;
		line[SIZE-1] = c;
		c = getchar();
	}

	line = (int*)realloc(line, SIZE*sizeof(int));
	/**create size for maze*/
	
	maze = (int**)malloc(SIZE*sizeof(int*));
	solution = (int**)malloc(SIZE*sizeof(int*));
	trackPaths = (int**)malloc(SIZE*sizeof(int*));
	
	for(i = 0; i < SIZE; i++)
	{
		maze[i] = (int*)malloc(SIZE * sizeof(int));
	}
	maze[0] = line;
	/**fill in values for maze*/
	for(i = 1; i < SIZE; i++)
	{
		for(j = 0; j < SIZE; j++)
		{
			c = getchar();	
			
			if (c != '\n')
			{
				maze[i][j] = c;
			}else 
			{
				c = getchar();
				maze[i][j] = c;
			}	
			
		}
	}

	/**create size for solution and trackPaths*/
	for(i = 0; i < SIZE; i++)
	{
		solution[i] = (int*)malloc(SIZE*sizeof(int));
		trackPaths[i] = (int*)malloc(SIZE*sizeof(int));
	}

	/**all values for solution and trackpaths to 0*/
	 for(i = 0; i < SIZE; i++)
        {
                for(j = 0; j < SIZE; j++)
                {
			solution[i][j] = 48;
			trackPaths[i][j] = 48;
		}
        }


}
/**solve uses recursion to find a valid path through the maze. It uses the trackPaths to make sure that once a path fails, it isn't tried again.*/
int solve(int row, int col)
{
	if(row == SIZE-1 && col == SIZE -1)
	{
		solution[row][col] = 49;
		return 1;
	}

	if(row < SIZE && col < SIZE&& maze[row][col] == 49  && solution[row][col] == 48 && trackPaths[row][col] == 48)
	{
		solution[row][col] = 49;
		trackPaths[row][col] = 49;
		
		/**right*/
		if(solve(row, col+1))
		{
			return 1;
		}
		/**down*/
		else if(solve(row+1, col))
		{
			return 1;
		}
		/**backtracking*/
		else
		{
			solution[row][col] = 48;
			return 0;	
		}
	}

	return 0;
}

/**print correctly formats and prints the found path of the maze*/
void print(void)
{
	printf("PATH FOUND!\n");
	for(i = 0; i < SIZE; i++)
	{
		for(j = 0; j < SIZE; j++)
		{
			printf("%c", solution[i][j]);
		}
		printf("\n");
	}
}



void printMaze(void)
{

	printf("  MAZE    SOLUTION    TRACe\n");	
	for(i = 0; i < SIZE; i++)
        {
                for(j = 0; j < SIZE; j++)
                {
                        printf("%c",maze[i][j]);
                }
		printf("        ");
		for(j = 0; j < SIZE; j++)
                {
                        printf("%c", solution[i][j]);
                }
		printf("        ");
                for(j = 0; j < SIZE; j++)
                {
                        printf("%c", trackPaths[i][j]);
                }

                printf("\n");
        }
}

void freeMem(void)
{
	for(i = 0; i < SIZE; i++)
	{
		free(maze[i]);
		free(solution[i]);
		free(trackPaths[i]);
	}

	free(maze);
	free(solution);
	free(trackPaths);

}


