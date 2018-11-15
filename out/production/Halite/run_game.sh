#!/bin/sh

set -e


javac MyBot.java
#./halite --replay-directory replays/ -vvv --width 32 --height 32 "java MyBot" "java MyBot"
./halite --no-replay --no-logs --results-as-json -vvv --width 32 --height 32 "java MyBot ${1}" "java MyBot ${2}"
