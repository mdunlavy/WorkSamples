SHELL := /bin/bash
CC=gcc
CFLAGS=-pedantic -ansi -Wall
TESTMAZE=maze1.in

all: maze

maze1: maze
	time ./maze < maze1.in > mypath1.out

maze2: maze
	time ./maze < maze2.in > mypath2.out

maze3: maze
	time ./maze < maze3.in > mypath3.out

maze4: maze
	time ./maze < maze4.in > mypath4.out

maze5: maze
	time ./maze < maze5.in > mypath5.out

maze: main.c path.c
	$(CC) $(CFLAGS) -g -o maze path.c main.c

valgrind: maze
	valgrind --tool=memcheck --leak-check=yes ./maze < $(TESTMAZE)

clean:
	rm maze
