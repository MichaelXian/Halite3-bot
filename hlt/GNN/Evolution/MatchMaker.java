package hlt.GNN.Evolution;

import hlt.GNN.Networks.Bot;
import hlt.GNN.Util.Matchup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Random;

public class MatchMaker {
    private final int MAX_MATCHUPS = 1;
    private List<Matchup> matchups;
    private HashMap<Bot, Integer> numMatches; // Keeps track of the number of matches each bot has had.
    private HashMap<Bot, List<Bot>> prevMatches; // Keeps track of the matches each bot has played.
    private List<Bot> activeBots; // Bots that still need matchups
    Random rand = new Random();

    public MatchMaker(List<Bot> bots) {
        numMatches = new HashMap<>();
        prevMatches = new HashMap<>();
        matchups = new ArrayList<>();
        for (Bot bot: bots) {
            numMatches.put(bot, 0);
            prevMatches.put(bot, new ArrayList<>());
        }
        activeBots = new ArrayList<>(bots);
        createMatchups();
    }

    private void createMatchups() {
        while (activeBots.size() > 1) {
            createMatchup();
        }
    }

    private void createMatchup() {
        Bot firstBot = getRandom(activeBots); // Select a random bot
        List<Bot> possibleOpponents = new ArrayList<>(activeBots); // Figure out who it can play against
        possibleOpponents.remove(firstBot); // Can't play against itself
        for (Bot bot: prevMatches.get(firstBot)) {
            possibleOpponents.remove(bot); // Can't play against previous opponents
        }
        if (possibleOpponents.isEmpty()) {
            activeBots.remove(firstBot); // If nobody is left, deactivate the bot
            return;
        }
        Bot secondBot = getRandom(possibleOpponents); // Select a random opponent
        matchups.add(new Matchup(firstBot, secondBot)); // Add matchup
        incrementMatchups(firstBot); // Update matchup count
        incrementMatchups(secondBot);
    }

    private void incrementMatchups(Bot bot) {
        int matches = numMatches.get(bot);
        if (matches == MAX_MATCHUPS - 1) {
            activeBots.remove(bot);
        }
        numMatches.put(bot, matches + 1);
    }

    private Bot getRandom(List<Bot> list) {
        return list.get(rand.nextInt(list.size()));
    }

    public List<Matchup> getMatchups() {
        return matchups;
    }


}
