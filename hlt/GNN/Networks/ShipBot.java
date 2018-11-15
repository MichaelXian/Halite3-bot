package hlt.GNN.Networks;

import hlt.GNN.InsertionSort;
import org.neuroph.core.NeuralNetwork;

import java.util.*;

public class ShipBot implements AI{
    private NeuralNetwork network;
    private String filePath;

    ShipBot(NeuralNetwork network, String filePath) {
        this.network = network;
        this.filePath = filePath;
    }

    @Override
    public void setInput(Map<String, Integer> data) {
        network.setInput(
                data.get("enemyDistX"),
                data.get("enemyDistY"),
                data.get("allyDistX"),
                data.get("allyDistY"),
                data.get("dropDistX"),
                data.get("dropDistY"),
                data.get("totalHalite"),
                data.get("shipHalite"),
                data.get("tileHalite"),
                data.get("northHalite"),
                data.get("southHalite"),
                data.get("eastHalite"),
                data.get("westHalite")
        ); // Set the input with given data
        network.calculate(); // Calculateify
    }

    @Override
    public List<String> getOutput() {
        double[] outputPrimitive = network.getOutput(); // Get output
        Double[] output = {};
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
