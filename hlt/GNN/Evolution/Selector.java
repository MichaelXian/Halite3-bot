package hlt.GNN.Evolution;

import hlt.GNN.Util.Matchup;
import hlt.GNN.Networks.Bot;
import org.json.JSONObject;

import java.util.*;

import static java.lang.Math.pow;

public class Selector {
    private Map<Bot, List<Integer>> grades; // The value is <Number of matchups the bot has played, total score>
    private Map<Bot, Double> averageGrades; // The average score over the bot's games
    private List<Bot> bots;

    public Selector(List<Bot> bots) {
        this.bots = new ArrayList<>(bots);
        grades = new HashMap<>();
        averageGrades = new HashMap<>();
        for (Bot bot: bots) {
            List<Integer> scores = new ArrayList<>();
            scores.add(0);
            scores.add(0);
            grades.put(bot, scores);
        }
    }

    /**
     * Grades the performance of the two neural networks by the outcome of the game
     * @param matchup the the matchup
     * @param stats The stats from the game
     */
    public void grade(Matchup matchup, JSONObject stats) {
        Bot bot1 = matchup.getBot1();
        List<Integer> bot1Grades = grades.get(bot1);
        JSONObject bot1Stats = stats.getJSONObject("0");
        int bot1Score = bot1Stats.getInt("score");
        bot1Grades.set(0, bot1Grades.get(0) + 1);
        bot1Grades.set(1, bot1Grades.get(1) + bot1Score + 1); // Add 1 so we don't div by 0 when normalizing
        grades.put(bot1, bot1Grades);
        Bot bot2 = matchup.getBot2();
        List<Integer> bot2Grades = grades.get(bot2);
        JSONObject bot2Stats = stats.getJSONObject("1");
        int bot2Score = bot2Stats.getInt("score");
        bot2Grades.set(0, bot2Grades.get(0) + 1);
        bot2Grades.set(1, bot2Grades.get(1) + bot2Score + 1);
    }


    /**
     * Selects given number of nets and returns them. Breaks getBestBot, getBestScore, getAverageScore, getStandardDeviation.
     * @param number
     * @return
     */
    public List<Bot> select(int number) {
        calcAverageGrades();
        List<Bot> ret = new ArrayList<>();
        for (int i = 0; i < number; i ++) { // Repeatedly select 1 bot until we've reached the desired number of bots
            Bot botToAdd = selectOne();
            grades.remove(botToAdd);
            bots.remove(botToAdd);
            ret.add(botToAdd);
        }
        return ret;
    }

    /**
     * Calculates average grades for each bot and initializes the averageGrades map, if it has not already been done.
     */
    private void calcAverageGrades() {
        if (averageGrades.isEmpty()) {
            for (Bot bot : bots) { // Calculate the averages
                List<Integer> botGrades = grades.get(bot);
                Double average = new Double(botGrades.get(1)) / new Double(botGrades.get(0));
                if (botGrades.get(0) == 0) {
                    average = 5000d;
                }
                averageGrades.put(bot, average);
            }
        }
    }

    /**
     * Returns the bot with the best grade. Should be run before select(n)
     * @return
     */
    public Bot getBestBot() {
        calcAverageGrades();
        Double maxGrade = 0d;
        Bot bot = null; // might not have been initialized
        for (Map.Entry<Bot, Double>  entry : averageGrades.entrySet()){
            if (entry.getValue() >= maxGrade) {
                maxGrade = entry.getValue();
                bot = entry.getKey();
            }
        }
        return bot;
    }

    /**
     * Gets the best score in the average grades. Should be run before select(n)
     * @return
     */
    public Double getBestScore() {
        calcAverageGrades();
        Double maxGrade = 0d;
        for (Map.Entry<Bot, Double>  entry : averageGrades.entrySet()){
            if (entry.getValue() >= maxGrade && entry.getValue() != 5001.0) {
                maxGrade = entry.getValue();
            }
        }
        return maxGrade;
    }

    /**
     * Gets the average score of all the bots. Should be run before select(n)
     * @return
     */
    public Double getAverageScore() {
        calcAverageGrades();
        Double sum = 0d;
        int num = 0;
        for (Map.Entry<Bot, Double>  entry : averageGrades.entrySet()){
            num ++;
            sum += entry.getValue();
        }
        return sum/num;
    }

    /**
     * Gets the standard deviation of all the bots. Should be run before select(n)
     * @return
     */
    public Double getStandardDeviation() {
        calcAverageGrades();
        Double variance = 0d;
        Double average = getAverageScore();
        int num = 0;
        for (Map.Entry<Bot, Double>  entry : averageGrades.entrySet()){
            num ++;
            variance += pow(entry.getValue() - average ,2);
        }
        variance /= num;
        return Math.sqrt(variance);
    }


    /**
     * Selects one bot to survive to the next generation randomly.
     */

    private Bot selectOne() {
        normalize();
        Double random = Math.random();
        Double sum = 0d;
        double grade;
        for (Bot bot : bots) {
            grade = averageGrades.get(bot);
            if (grade != 5001) {
                sum += grade;
            }
            if (random <= sum) {
                return bot;
            }
        }
        throw new NullPointerException("Somehow nothing was selected");
    }


    /**
     * Normalizes the grades
     */
    private void normalize() {
        Double sum = 0d;
        for (Bot bot : bots) { // Sum up all grades
            if (averageGrades.get(bot) == 5001) {
                averageGrades.put(bot, 0.1d); // Try to nudge it away from the relative safety of doing nothing
            }
            sum += averageGrades.get(bot);
        }
        for (Map.Entry<Bot, Double> entry: averageGrades.entrySet()) {
            entry.setValue(entry.getValue() / sum); // Divide every value by the sum
        }
    }

    /**
     * A function that is applied to all averages to allow for different weightings of averages
     * @param average
     * @return
     */
    private Double evalFunction(Double average) {
        return pow(average, 2);
    }


}
