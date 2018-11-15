package hlt.GNN.Networks;

import org.neuroph.core.NeuralNetwork;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface AI {


    /**
     * Sets the input with the given data
     * @param data A map of the data relevant to the bot
     */
    void setInput(Map<String, Integer> data);

    /**
     * Gets the output as a list in descending order, from the "best" action determined by the network to the worst
     * @return ^
     */
    List<String> getOutput();

    /**
     *
     * @return The neural net associated with this AI
     */
    NeuralNetwork getNeuralNetwork();

    String getFilePath();


}
