#!/bin/sh

set -e
NEUROPH="/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/neuroph-core-2.94.jar"
SLF="/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/libs/slf4j-api-1.7.5.jar"
NOP="/home/michael/Documents/Github/Halite/hlt/neuroph-2.94/libs/slf4j-nop-1.7.6.jar"
CP=".:${NEUROPH}:${SLF}:${NOP}"
BOT1="java -cp ${CP} MyBot ${1}"
BOT2="java -cp ${CP} MyBot ${2}"

javac -cp "${CP}" MyBot.java