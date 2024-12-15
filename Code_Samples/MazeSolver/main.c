/**
 * @author maisy dunlavy
 * date 11/26/2023
 * cs241L 
 */
#include <stdio.h>
#include <stdlib.h>
#include "header.h"

int main(void)
{
	/**call function to find size and read in input, takes in empty maze and returns maze*/
	/**calls solve with maze as input*/
	/**then prints*/
	findMazeSize();
	if(solve(0, 0))print();
	else printf("no path found.\n");	
	freeMem();
	return 0;
}
