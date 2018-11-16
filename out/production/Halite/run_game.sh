#!/bin/sh

set -e


javac -cp ".:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/neuroph-core-2.94.jar:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/libs/slf4j-api-1.7.5.jar" MyBot.java

#javac MyBot.java
#./halite --replay-directory replays/ -vvv --width 32 --height 32 "java MyBot" "java MyBot"
./halite --no-replay --no-logs --results-as-json -vvv --width 32 --height 32 "java -cp '.:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/neuroph-core-2.94.jar:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/libs/slf4j-api-1.7.5.jar' MyBot ${1}" "java -cp '.:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/neuroph-core-2.94.jar:/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/libs/slf4j-api-1.7.5.jar' MyBot ${2}"
