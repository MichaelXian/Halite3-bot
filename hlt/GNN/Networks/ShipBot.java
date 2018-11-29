package hlt.GNN.Networks;

import hlt.Constants;
import hlt.GNN.Util.InsertionSort;
import hlt.Log;
import org.neuroph.core.NeuralNetwork;

import java.util.*;

public class ShipBot implements AI{
    public static final int INPUTS = 14;
    public static final int OUTPUTS = 6;
    private NeuralNetwork network;
    private String filePath;
    private final double DIST_MULTIPLIER = 1.0/32.0;
    private final double SHIP_HALITE_MULTIPLIER = 1.0/1000.0;
    private final double TILE_HALITE_MULTIPLIER = 1.0/1000.0;
    private final double TOTAL_HALITE_MULTIPLIER = 1.0/10000.0;


    public ShipBot(NeuralNetwork network) {
        this.network = network;
        this.filePath = null;
    }


    public ShipBot(NeuralNetwork network, String filePath) {
        this.network = network;
        this.filePath = filePath;
    }

    @Override
    public void setInput(Map<String, Integer> data) {
        double TURNS_LEFT_MULTIPLIER = 1.0/ (double) Constants.MAX_TURNS;
        network.setInput(
                data.get("enemyDistX") * DIST_MULTIPLIER, // Make inputs *roughly* in [0,1]
                data.get("enemyDistY") * DIST_MULTIPLIER,
                data.get("allyDistX") * DIST_MULTIPLIER,
                data.get("allyDistY") * DIST_MULTIPLIER,
                data.get("dropDistX") * DIST_MULTIPLIER,
                data.get("dropDistY") * DIST_MULTIPLIER,
                data.get("totalHalite") * TOTAL_HALITE_MULTIPLIER,
                data.get("shipHalite") * SHIP_HALITE_MULTIPLIER,
                data.get("tileHalite") * TILE_HALITE_MULTIPLIER,
                data.get("northHalite") * TILE_HALITE_MULTIPLIER,
                data.get("southHalite") * TILE_HALITE_MULTIPLIER,
                data.get("eastHalite") * TILE_HALITE_MULTIPLIER,
                data.get("westHalite") * TILE_HALITE_MULTIPLIER,
                data.get("turnsLeft") * TURNS_LEFT_MULTIPLIER
        );

        // Set the input with given data
        network.calculate(); // Calculateify
    }

    @Override
    public List<String> getOutput() {
        double[] outputPrimitive = network.getOutput(); // Get output
        Double[] output = new Double[outputPrimitive.length];
        for (int i = 0; i < outputPrimitive.length; i++) {
            output[i] = new Double(outputPrimitive[i]); // Wrap the output
        }
        ArrayList<Double> outputValues = new ArrayList<>(Arrays.asList(output));
        ArrayList<String> outputKeys = new ArrayList<>(Arrays.asList("convert", "north", "south", "east", "west", "stay"));
        InsertionSort.sortKeyValue(outputKeys, outputValues);
        return outputKeys;
    }

    public List<Double> getOutputValues() {
        double[] outputPrimitive = network.getOutput(); // Get output
        Double[] output = new Double[outputPrimitive.length];
        for (int i = 0; i < outputPrimitive.length; i++) {
            output[i] = new Double(outputPrimitive[i]); // Wrap the output
        }
        ArrayList<Double> outputValues = new ArrayList<>(Arrays.asList(output));
        return outputValues;
    }


    @Override
    public NeuralNetwork getNeuralNetwork() {
        return network;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }
}
