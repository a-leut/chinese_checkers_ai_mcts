# chinese_checkers_ai_mcts

## About

This program was for an “Artificial Intelligence (AI) for Traditional Games” class I took Spring 2015. I chose to use a Monte-Carlo Tree Search based agent with some modifications. After playing three rounds against all the other undergraduate programs, my program tied for third based on overall win / losses. I used a selection function that is an extension of UCB1 with the addition of a progressive bias:
![alt text](http://i.imgur.com/aM78hN6.png "Logo Title Text 1")

I also changed the tree expansion and simulation playout policy based on Prof. Sturtevant’s paper. A node will only be expanded if it has previously been reached 32 times and is a forward move across the board. During simulation, the program will only consider forward moves.

## Building / Running

You will need "GameMaster.jar" and a binary of "ChineseCheckersModerator." I have included GameMaster and an .exe of the moderator program, see [this repo](https://github.com/wtmitchell/tradgames) for source of both programs.

1. Export runable jar of project
2. Run GameMaster program
3. Select jar of my program as Player1 or Player2
4. Select "Human" for other player
5. Select "ChineseCheckersModerator" program for moderator
6. Begin game

## Sources

[Enhancements for Multi-Player Monte-Carlo Tree Search](http://bnaic2010.uni.lu/Papers/Category%20B/Nijssen.pdf)

[UCT Enhancements in Chinese Checkers Using an Endgame Database](http://www.cs.du.edu/~sturtevant/papers/UCT-endgame.pdf)

