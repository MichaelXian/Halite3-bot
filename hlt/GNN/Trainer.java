package hlt.GNN;//package hlt.GNN;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicInteger;

import hlt.GNN.Evolution.Crossover;
import hlt.GNN.Evolution.MatchMaker;
import hlt.GNN.Evolution.Mutator;
import hlt.GNN.Evolution.Selector;
import hlt.GNN.Networks.Bot;
import hlt.GNN.Util.Matchup;
import hlt.GNN.Util.NetworkFileManager;
import hlt.GNN.Util.StatFileManager;
import org.json.JSONObject;
import org.json.JSONException;
import org.neuroph.core.NeuralNetwork;
// Can't resolve org.json.* but can resolve org.json.specificClass... ok then...

public class Trainer {
    static final int NUM_SURVIVORS = 50;
    static final int NUM_BOTS = 100;
    static List<Bot> bots;
    static CopyOnWriteArrayList<Matchup> matchups;
    static List<Thread> threads;
    static MatchMaker matchMaker;
    static Selector selector;
    static int generation;
    static AtomicInteger matchNum;

    public static void main(String[] args) {
        matchups = new CopyOnWriteArrayList<>();
        matchNum = new AtomicInteger();
        int numTimes = -1;
        boolean remote = (args.length == 1);
        if (args.length == 1) {
            numTimes = Integer.parseInt(args[0]);
        }
        generation = StatFileManager.getGeneration();
        for (; numTimes != 0; numTimes--) {
            System.out.println("Generation: " + generation);
            bots = new ArrayList<>();
            // Load botsException in thread "main"
            loadBots();
            // Initialize matchmaker and selector
            matchMaker = new MatchMaker(bots);
            selector = new Selector(bots);
            matchups.clear();                          // Clear matchups
            matchups.addAll(matchMaker.getMatchups()); // Get the matchups
            matchMaker = null; // Deallocate some memory, we no longer need matchmaker since we have the matchups
            matchNum.set(0);                           // Reset match counter
            threads = new ArrayList<>();
            for (Matchup matchup : matchups) {
                Thread matchupThread = new Thread(()->doMatchup(remote, matchup));
                threads.add(matchupThread);
            }
            for (Thread thread : threads) {
                try {
                    thread.join();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // Record some stats
            StatFileManager.updateScores(generation, selector.getBestScore(), selector.getAverageScore(), selector.getStandardDeviation());
            Bot bestBot = selector.getBestBot(); // Save the best bot for the actual competition
            NetworkFileManager.saveBest(bestBot);
            // Select bots to survive to next generation
            bots = selector.select(NUM_SURVIVORS);
            // Breed back to 100 neural nets
            List<Bot> originalBots = new ArrayList<>(bots);
            List<Bot> newBots = new ArrayList<>();      // List of newly created bots, that will be mutated later
            while (newBots.size() < NUM_BOTS/2) {       // Create new bots until we have enough to reach NUM_BOTS in the next generation
                Bot newBot = Crossover.breed(originalBots);
                newBots.add(newBot);
            }
            // Mutate the new bots, and add them to the list of bots
            for (Bot bot: newBots) {
                Mutator.mutate(bot);
                bots.add(bot);
            }
            saveBots();
            generation ++;
            StatFileManager.setGeneration(generation);
        }
    }

    private static void doMatchup(boolean remote, Matchup matchup) {
        JSONObject matchResults = playMatchup(matchup, remote);
        analyzeMatchup(matchup, matchResults);
    }

    // Grades the bots and prints out information accordingly
    private static synchronized void analyzeMatchup(Matchup matchup, JSONObject matchResults) throws JSONException {
        matchNum.getAndIncrement();
        System.out.print("Matchup " + matchNum + "/" + matchups.size());
        // Play each matchup, and grade the bots accordingly.
        selector.grade(matchup, matchResults);
        System.out.print(" Bot" + matchup.getBot1().getBotNum() + ": " + matchResults.getJSONObject("0").getInt("score") + " Bot" + matchup.getBot2().getBotNum() +": " + matchResults.getJSONObject("1").getInt("score") +  "\n");
    }

    private static void saveBots() {
        for (int i = 0; i < NUM_BOTS; i++) {
            NetworkFileManager.saveBot(bots.get(i), i);
        }
    }

    private static org.json.JSONObject playMatchup(Matchup matchup, boolean remote) {
        String botNum1 = new Integer(matchup.getBot1().getBotNum()).toString();
        String botNum2 = new Integer(matchup.getBot2().getBotNum()).toString();
        return playMatch(botNum1, botNum2, remote);
    }

    private static void loadBots() {
        for (int i = 0; i < NUM_BOTS; i++) {
            bots.add(NetworkFileManager.loadBot(i));
        }
    }



    private static JSONObject playMatch(String botNum1, String botNum2, boolean remote) throws JSONException {
        String filePath =  new File("").getAbsolutePath(); // Get path of root dir
        String fileName = remote ? "/run_game_remote.sh" : "/run_game.sh";
        filePath = filePath.concat(fileName); // Add the bash script
        System.out.println(filePath);
        String[] cmd = new String[]{"/bin/sh", filePath, botNum1, botNum2}; // Command to run
        String result = ""; // Initialize result & ret value
        JSONObject stats = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd); // Run the command
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream())); // Read output
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }
            in.close();
            JSONObject matchResults = new JSONObject(result); // Turn output into JSONObject
            stats = matchResults.getJSONObject("stats"); // Set return value
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stats;
    }

}
