# Ships

Each ship takes in 13 inputs

Note "distance" is actually displacement, negative if it is west/south, positive if east/north.

* horizontal distance to nearest enemy
* vertical distance to nearest enemy
* horizontal distance to nearest ally
* vertical distance to nearest ally
* horizontal distance to nearest drop-off
* vertical distance to nearest drop-off
* total halite
* halite in ship
* halite on tile ship is at
* halite north of ship
* halite south of ship
* halite east of ship
* halite west of ship



And has 6 outputs

* Convert to drop-off
* Move North
* Move South
* Move East
* Move West
* Stay still

The highest of the outputs will be the output that is taken. If that output would kill the bot, it is discarded and the second highest output is taken, then third highest, etc (This is done in the bot itself, rather than in the neural network).



javac -cp ".:/home/<username>/Documents/test/jars/slf4j.jar:/home/<username>/Documents/test/jars/neuroph.jar" Test.java

javac -cp ".:/home/<username>/Documents/test/slf4j.jar:/home/<username>/Documents/test/neuroph.jar" Test.java

javac -cp ".:/home/<username>/Documents/test/neuroph.jar" Test.java

javac -cp "/home/<username>/Documents/test/slf4j.jar:/home/<username>/Documents/test/neuroph.jar" Test.java

javac -cp "/home/<username>/Documents/test/neuroph.jar" Test.java



javac -cp ".:/home/michael/Documents/test/jars/slf4j.jar:/home/michael/Documents/test/jars/neuroph.jar" Test.java

javac -cp ".:/home/michael/Documents/test/slf4j.jar:/home/michael/Documents/test/neuroph.jar" Test.java

javac -cp ".:/home/<username>/Documents/test/neuroph.jar" Test.java

javac -cp "/home/<username>/Documents/test/slf4j.jar:/home/<username>/Documents/test/neuroph.jar" Test.java

javac -cp "/home/<username>/Documents/test/neuroph.jar" Test.java

# Controller

The controller takes in 2 inputs

* Amount of halite
* Number of ships



And has 2 outputs

* Create ship
* Do nothing

Note: I just kinda wanted to keep it in the same format, since for ships, only 1 action can be taken or the bot dies.



```
javac -cp ".:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/neuroph-core-2.94.jar" MyBot.java
```

```
./hlt/neuroph-2.94/neuroph-core-2.94.jar
```