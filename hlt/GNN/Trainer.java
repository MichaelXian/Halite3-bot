package hlt.GNN;//package hlt.GNN;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.JSONException;
// Can't resolve org.json.* but can resolve org.json.specificClass... ok then...

public class Trainer {
    public static void main(String[] args) {
        JSONObject matchResults = playMatch(); // Play the matchup
        JSONObject bot0Stats = getBotStats(matchResults, "0"); // Get the performance of bot0
        JSONObject bot1Stats = getBotStats(matchResults, "1"); // Get the performance of bot1



    }

    private static JSONObject getBotStats(JSONObject matchResults, String botIdentifier) {
        return matchResults.getJSONObject(botIdentifier);
    }

    private static JSONObject playMatch() throws JSONException {
        String filePath =  new File("").getAbsolutePath(); // Get path of root dir
        filePath = filePath.concat("/run_game.sh"); // Add the bash script
        String[] cmd = new String[]{"/bin/sh", filePath, "4", "5"}; // Command to run
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
