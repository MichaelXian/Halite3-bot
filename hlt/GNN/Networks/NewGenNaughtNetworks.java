package hlt.GNN.Networks;

import hlt.GNN.StatFileManager;
import hlt.GNN.Trainer;
import org.neuroph.core.NeuralNetwork;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;

public class NewGenNaughtNetworks {

    /**
     * Creates new gen1 neural networks, and resets the generation to 1
     * @param args
     */
    public static void main(String[] args) {
        NeuralNetwork shipNetwork;
        NeuralNetwork controllerNetwork;
        for (int i = 0; i < 100; i ++) {
            shipNetwork = NeuralNetworkFactory.randomNeuralNet(13,6);
            controllerNetwork = NeuralNetworkFactory.randomNeuralNet(2, 2);
            NetworkFileManager.saveBot(shipNetwork, controllerNetwork, i);
        }
        StatFileManager.setGeneration(0);
        //neuralNetwork = NeuralNetworkFactory.emptyNeuralNet();
        //neuralNetwork.save("NeuralNets/net2.nnet");
    }


}
