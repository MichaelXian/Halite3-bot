package hlt.GNN.Networks;

import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.MultiLayerPerceptron;
import org.neuroph.nnet.comp.neuron.BiasNeuron;

import static java.lang.Math.random;

public class NeuralNetworkFactory {

    private static final Double NEW_NEURON_CHANCE = 0.3;
    private static final Double NEW_CONNECTION_CHANCE = 0.1;
    private static final int NUM_LAYERS = 3;


    /**
     * Creates a neural net with no hidden layers
     * @return
     */
    public static NeuralNetwork emptyNeuralNet(int inputs, int outputs) {
        NeuralNetwork neuralNetwork = new MultiLayerPerceptron(inputs, outputs);
        pruneAllConnections(neuralNetwork);
        return neuralNetwork;
    }

    /**
     * Creates a fully connected network with NUM_LAYERS-2 hidden layers
     */
    public static NeuralNetwork connectedNeuralNetwork(int inputs, int outputs) {
        // 7 hidden layers
        NeuralNetwork neuralNetwork = new MultiLayerPerceptron(inputs, inputs, inputs, inputs, inputs, inputs, inputs, inputs, outputs);
        return neuralNetwork;
    }

    /**
     * Creates a random neural net with NUM_LAYERS-2 hidden layers
     * @return
     */
    public static NeuralNetwork randomNeuralNet(int inputs, int outputs) {
        NeuralNetwork neuralNetwork = new MultiLayerPerceptron(inputs, outputs);
        //Remove Bias neuron
        //removeBiasNeuron(neuralNetwork);
        //Create layers in-between
        for (int i = 0; i < (NUM_LAYERS - 2); i ++) {
            Layer layer = new Layer();
            while (random() < neuronChance(layer)) {
                layer.addNeuron(NeuronFactory.randomNeuron());
            }
            neuralNetwork.addLayer(1, layer);
        }
        pruneAllConnections(neuralNetwork);
        //make random connections
        for (int i = 0; i < NUM_LAYERS - 1; i++) { // for each layer, excluding output layer
            Layer layer = neuralNetwork.getLayerAt(i);
            for (Neuron neuron: layer.getNeurons()) { // for each neuron
                for (int j = i + 1; j < NUM_LAYERS; j++) { // iterate through layers in front of that neuron
                    Layer connectLayer = neuralNetwork.getLayerAt(j);
                    for (Neuron connectNeuron: connectLayer.getNeurons()) { // for each neuron, connect if (random)
                        while (random() < connectChance(neuron, connectNeuron)) {
                            // this makes random go from -1 to 1, instead of 0 to 1
                            connectNeuron.addInputConnection(neuron, 2*(random() - 0.5));
                        }
                    }
                }
            }
        }
        return neuralNetwork;
    }

    /**
     * Removes the bias neuron from the given network
     * @param neuralNetwork
     */
    private static void removeBiasNeuron(NeuralNetwork neuralNetwork) {
        Neuron biasNeuron = new Neuron();
        for (Neuron neuron: neuralNetwork.getLayerAt(0).getNeurons()) {
            if (neuron instanceof BiasNeuron) {
                biasNeuron = neuron;
            }
        }
        neuralNetwork.getLayerAt(0).removeNeuron(biasNeuron);
    }


    /**
     * returns the chance of creating a new connection, chance decreases dramatically as number of neurons increase
     * This is to ensure my computer can handle the neuralNets
     * @param neuron
     * @param connectNeuron
     * @return
     */
    private static double connectChance(Neuron neuron, Neuron connectNeuron) {
        return NEW_CONNECTION_CHANCE /
                (neuron.getOutConnections().size() + connectNeuron.getInputConnections().size() + 0.2);
    }

    /**
     * Gets rid of all connections on the neuralNet
     * @param neuralNetwork
     */
    private static void pruneAllConnections(NeuralNetwork neuralNetwork) {
        for (Object layer: neuralNetwork.getLayers()) {
            for (Neuron neuron: ((Layer) layer).getNeurons()) {
                neuron.removeAllConnections();
            }
        }
    }

    /**
     * returns the chance of creating a new neuron, chance decreases dramatically as number of neurons increase
     * This is to ensure my computer can handle the neuralNets
     * @param layer
     * @return
     */
    private static Double neuronChance(Layer layer) {
        return NEW_NEURON_CHANCE/(layer.getNeuronsCount() + 0.2);
    }


}
