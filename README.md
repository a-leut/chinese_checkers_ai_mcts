# chinese_checkers_ai_mcts

## About

This project was for an “Artificial Intelligence (AI) for Traditional Games” class I took in 2015. There was a tournament between the student programs at the end of the class and this program tied for third place based on overall wins / losses. I chose to use a [Monte-Carlo Tree Search](http://mcts.ai/about/index.html) based agent with modifications for Chinese Checkers to encourage good lines of play, influenced by techniques from two AI papers.

From Nijssen's paper, I took a selection function that is an extension of UCB1 with the addition of a progressive bias:

![alt text](http://i.imgur.com/aM78hN6.png "Logo Title Text 1")

I also changed the tree expansion and simulation playout policy based Sturtevant’s experience. Instead of expanding every node that is reached during selection, a node will only be expanded if it has previously been reached a fixed number times and is a forward move across the board. During simulation, the program will only consider forward moves.

## Sources

[Enhancements for Multi-Player Monte-Carlo Tree Search](http://bnaic2010.uni.lu/Papers/Category%20B/Nijssen.pdf)

[UCT Enhancements in Chinese Checkers Using an Endgame Database](http://www.cs.du.edu/~sturtevant/papers/UCT-endgame.pdf)


## Building / Running

You will need "GameMaster.jar" and a binary of "ChineseCheckersModerator." I have included GameMaster.jar and a Windows executable of the moderator program. See [this repo](https://github.com/wtmitchell/tradgames) to build these programs from source.

1. Export runable jar of project
2. Run GameMaster program
3. Select jar of my program as Player1 or Player2
4. Select "Human" for other player
5. Select "ChineseCheckersModerator" program for moderator
6. Begin game
