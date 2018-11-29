package hlt.GNN.Util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.Scanner;

public class StatFileManager {
    public static final String GENERATION_FILE_PATH = "hlt/Stats/generation.txt";
    public static final String SCORE_FILE_PATH = "hlt/Stats/score.txt";

    /**
     * Sets the generation stored in generation.txt
     * @param generation
     */
    public static void setGeneration(int generation) {
        try {
            File generationFile = new File(GENERATION_FILE_PATH);
            PrintWriter writer = new PrintWriter(generationFile);
            writer.print(generation);
            writer.close();
        } catch (FileNotFoundException e) {
            throw new NullPointerException("File not found");
        }
    }

    /**
     * Gets the generation in generation.txt
     * @return
     */
    public static int getGeneration() {
        int generation = 0;
        try {
            File generationFile = new File(GENERATION_FILE_PATH);
            Scanner scanner = new Scanner(generationFile);
            generation = scanner.nextInt();
        } catch (FileNotFoundException e) {
            throw new NullPointerException("File not found");
        }
        return generation;
    }

    /**
     * Appends a line to score.txt, showing the current generation, high score, and average score.
     * @param generation
     * @param highScore
     * @param average
     */
    public static void updateScores(int generation, double highScore, double average, double standradDeviation) {
        String scores = "Generation: " + generation + " High: " + highScore + " Mean: " + average + " Standard Deviation: " + standradDeviation;
        try(PrintWriter output = new PrintWriter(new FileWriter(SCORE_FILE_PATH,true)))
        {
            output.printf("%s\r\n", scores);
        }
        catch (Exception e) {}
    }
}
