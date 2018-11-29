package hlt.GNN.Networks;

import hlt.GNN.Util.InsertionSort;
import org.neuroph.core.NeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ControllerBot implements AI{
    public static final int INPUTS = 2;
    public static final int OUTPUTS = 2;
    private NeuralNetwork network;
    private String filePath;
    private final double TOTAL_HALITE_MULTIPLIER = 1.0/10000.0;
    private final double NUM_SHIPS_MULTIPLIER = 1.0/10000.0;

    public ControllerBot(NeuralNetwork network) {
        this.network = network;
    }


    public ControllerBot(NeuralNetwork network, String filePath) {
        this.network = network;
        this.filePath = filePath;
    }

    @Override
    public void setInput(Map<String, Integer> data) {
        network.setInput(
                data.get("totalHalite") * TOTAL_HALITE_MULTIPLIER,
                data.get("numShips") * NUM_SHIPS_MULTIPLIER
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
        ArrayList<String> outputKeys = new ArrayList<>(Arrays.asList("create","dont"));
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
