# TacTicToe
A strategic variation of tic-tac-toe with nested boards. Also includes a monte-carlo AI. 

The Rules:

Each move on the little board (Hash) sends your opponent to the same square on the big board.
```
###  
### - Big Board 
### 
```
```
# - Hash
```
 So, selecting the top left square of the bottom right hash would require you to put your next move in the top left hash. 
```
(X) 
###        X | _ | _ 
###    @:  _ | _ | _ 
##^        _ | _ | _

 --->
 
(O) 
^##        _ | _ | _ 
###    @:  _ | 0 | _ 
###        _ | _ | _
```

Selecting an unavailable square allows the opponent to move anywhere on the board.


Winning a hash claims that square on the big board, and winning the big board wins the game.  
 
 

To play, download and run these commands:

javac src/tactictoe/\*.java src/tactictoe/tactful/\*.java 

java src/tactictoe.TacTicToe


