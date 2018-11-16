package hlt.GNN;//package hlt.GNN;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

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
    static List<Bot> bots;
    static List<Matchup> matchups;
    static MatchMaker matchMaker;
    static Selector selector;
    static int generation;

    public static void main(String[] args) {
        bots = new ArrayList<>();
        generation = StatFileManager.getGeneration();
        while (true) {
            System.out.println("Generation: " + generation);
            // Load botsException in thread "main" java.lang.NoClassDefFoundError: org/neuroph/core/NeuralNetwork
            loadBots();
            // Initialize matchmaker and selector
            matchMaker = new MatchMaker(bots);
            selector = new Selector(bots);
            matchups = matchMaker.getMatchups(); // Get the matchups
            matchMaker = null; // Deallocate some memory, we no longer need matchmaker since we have the matchups
            int i = 0;
            for (Matchup matchup : matchups) {
                i++;
                System.out.print("Matchup " + i + "/" + matchups.size());
                // Play each matchup, and grade the bots accordingly.
                JSONObject matchResults = playMatchup(matchup);
                selector.grade(matchup, matchResults);
                System.out.print(" Bot1: " + matchResults.getJSONObject("0").getInt("score") + " Bot2: " + matchResults.getJSONObject("1").getInt("score") +  "\n");
            }
            // Record some stats
            StatFileManager.updateScores(generation, selector.getBestScore(), selector.getAverageScore());
            // Select bots to survive to next generation
            bots = selector.select(NUM_SURVIVORS);
            // Breed back to 100 neural nets
            List<Bot> originalBots = new ArrayList<>(bots);
            while (bots.size() < 100) {
                bots.add(Crossover.breed(originalBots));
            }
            // Mutate bots
            for (Bot bot: bots) {
                Mutator.mutate(bot);
            }
            saveBots();
            generation ++;
        }
    }

    private static void saveBots() {
        for (int i = 0; i < 100; i++) {
            NetworkFileManager.saveBot(bots.get(i), i);
        }
    }

    private static org.json.JSONObject playMatchup(Matchup matchup) {
        String botNum1 = new Integer(matchup.getBot1().getBotNum()).toString();
        String botNum2 = new Integer(matchup.getBot2().getBotNum()).toString();
        return playMatch(botNum1, botNum2);
    }

    private static void loadBots() {
        for (int i = 0; i < 100; i++) {
            bots.add(NetworkFileManager.loadBot(i));
        }
    }



    private static JSONObject playMatch(String botNum1, String botNum2) throws JSONException {
        String filePath =  new File("").getAbsolutePath(); // Get path of root dir
        filePath = filePath.concat("/run_game.sh"); // Add the bash script
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
