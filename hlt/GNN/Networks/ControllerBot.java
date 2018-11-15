package hlt.GNN.Networks;

import hlt.GNN.InsertionSort;
import org.neuroph.core.NeuralNetwork;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ControllerBot implements AI{
    private NeuralNetwork network;
    private String filePath;

    ControllerBot(NeuralNetwork network, String filePath) {
        this.network = network;
        this.filePath = filePath;
    }

    @Override
    public void setInput(Map<String, Integer> data) {
        network.setInput(
                data.get("totalHalite"),
                data.get("numShips")
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
