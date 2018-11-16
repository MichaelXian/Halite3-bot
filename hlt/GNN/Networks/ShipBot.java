package hlt.GNN.Networks;

import hlt.GNN.Util.InsertionSort;
import org.neuroph.core.NeuralNetwork;

import java.util.*;

public class ShipBot implements AI{
    private NeuralNetwork network;
    private String filePath;
    private final double DIST_MULTIPLIER = 1/32;
    private final double SHIP_HALITE_MULTIPLIER = 1/1000;
    private final double TITLE_HALITE_MULTIPLIER = 1/1000;
    private final double TOTAL_HALITE_MULTIPLIER = 1/10000;


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
        network.setInput(
                data.get("enemyDistX") * DIST_MULTIPLIER, // Make inputs *roughly* in [0,1]
                data.get("enemyDistY") * DIST_MULTIPLIER,
                data.get("allyDistX") * DIST_MULTIPLIER,
                data.get("allyDistY") * DIST_MULTIPLIER,
                data.get("dropDistX") * DIST_MULTIPLIER,
                data.get("dropDistY") * DIST_MULTIPLIER,
                data.get("totalHalite") * TOTAL_HALITE_MULTIPLIER,
                data.get("shipHalite") * SHIP_HALITE_MULTIPLIER,
                data.get("tileHalite") * TITLE_HALITE_MULTIPLIER,
                data.get("northHalite") * TITLE_HALITE_MULTIPLIER,
                data.get("southHalite") * TITLE_HALITE_MULTIPLIER,
                data.get("eastHalite") * TITLE_HALITE_MULTIPLIER,
                data.get("westHalite") * TITLE_HALITE_MULTIPLIER
        ); // Set the input with given data
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


    @Override
    public NeuralNetwork getNeuralNetwork() {
        return network;
    }

    @Override
    public String getFilePath() {
        return filePath;
    }
}
