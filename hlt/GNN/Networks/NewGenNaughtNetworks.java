package hlt.GNN.Networks;

import hlt.GNN.Util.NetworkFileManager;
import hlt.GNN.Util.StatFileManager;
import org.neuroph.core.NeuralNetwork;


public class NewGenNaughtNetworks {

    /**
     * Creates 100 new gen0 neural networks, and resets the generation to 0
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
