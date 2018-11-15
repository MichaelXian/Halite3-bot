# Ships

Each ship takes in inputs

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



And has outputs

* Convert to drop-off
* Move North
* Move South
* Move East
* Move West
* Stay still

The highest of the outputs will be the output that is taken. If that output would kill the bot, it is discarded and the second highest output is taken, then third highest, etc (This is done in the bot itself, rather than in the neural network).



# Controller

The controller takes in inputs

* Amount of halite
* Number of ships



And has the outputs

* Create ship
* Do nothing

Note: I just kinda wanted to keep it in the same format, since for ships, only 1 action can be taken or the bot dies.