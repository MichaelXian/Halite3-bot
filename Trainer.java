import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import org.json.JSONObject;
import org.json.JSONException;
// Can't resolve org.json.* but can resolve org.json.specificClass... ok then...

public class Trainer {
    public static void main(String[] args) {
        JSONObject matchResults = playMatch();
        System.out.println(matchResults);


    }

    private static JSONObject playMatch() throws JSONException {
        String[] cmd = new String[]{"/bin/sh", "./run_game.sh"};
        String result = "";
        JSONObject stats = null;
        try {
            Process p = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String inputLine;
            while ((inputLine = in.readLine()) != null) {
                result += inputLine;
            }
            in.close();
            JSONObject matchResults = new JSONObject(result);
            stats = matchResults.getJSONObject("stats");
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stats;
    }

}
