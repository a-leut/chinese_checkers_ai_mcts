# chinese_checkers_ai_mcts

## About

This is an artificial intelligence program that plays Chinese Checkers. I think that my program, "PudgeBot," is a strong player and would beat the average human oppponent. It placed third in a tournament of fifteen other programs. It beats me every time when I play it. I'm bad at Chinese Checkers so I would be interested to know how it does against a strong player.

I chose to use a [Monte-Carlo Tree Search](https://en.wikipedia.org/wiki/Monte_Carlo_tree_search) based agent with domain specific modifications for Chinese Checkers to encourage good lines of play. My approach was influenced by two AI papers that used similiar techniques. 

The selection function was chosen to be an extension of UCB1 with the addition of a progressive bias from Nijssen's paper. Progressive bias introduces the idea of learned "good moves." These are more likely to be chosen over other moves as the game progresses (as child nodes during selection.)

The tree expansion and simulation playout policy is based Sturtevant’s paper. Instead of expanding every node that is reached during selection, a node will only be expanded if it has previously been reached a fixed constant number of times *and* is a forward move across the board. During simulation the program will only consider forward moves.

## References

[Enhancements for Multi-Player Monte-Carlo Tree Search - J. (Pim) A. M. Nijssen and Mark H.M. Winands](http://bnaic2010.uni.lu/Papers/Category%20B/Nijssen.pdf)

[UCT Enhancements in Chinese Checkers Using an Endgame Database - Max Roschke and Nathan R. Sturtevant](http://www.cs.du.edu/~sturtevant/papers/UCT-endgame.pdf)

## Building / Running

You will need "GameMaster.jar" and a binary of "ChineseCheckersModerator." I have included GameMaster.jar and a Windows executable of the moderator program. See [this repo](https://github.com/wtmitchell/tradgames) to build these programs from source.

1. Export runable jar of project
2. Run GameMaster program
3. Select jar of my program as Player1 or Player2
4. Select "Human" for other player
5. Select "ChineseCheckersModerator" program for moderator
6. Begin game
