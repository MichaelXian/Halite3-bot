package hlt.GNN.Evolution;

import hlt.GNN.Networks.Bot;
import org.neuroph.core.Connection;
import org.neuroph.core.Layer;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import static java.lang.Math.min;

public class Crossover {
    public static final String CLONE_PATH = "hlt/NeuralNets/temp/clone.nnet";
    private static Random random = new Random();

    public static Bot breed(List<Bot> bots) {
        // Choose 2 random parents
        List<Bot> bots2 = new ArrayList<>(bots);  // Create a copy of the passed argument to mutate
        Bot bot = randomList(bots2);
        bots2.remove(bot);
        Bot bot2 = randomList(bots2);
        NeuralNetwork newShipNetwork = createChild(bot.getShipBot().getNeuralNetwork(), bot2.getShipBot().getNeuralNetwork());
        NeuralNetwork newControllerNetwork = createChild(bot.getControllerBot().getNeuralNetwork(), bot2.getControllerBot().getNeuralNetwork());
        return new Bot(newShipNetwork, null, newControllerNetwork, null, 0);
    }

    /**
     * Creates a new child from two random networks from the given list of networks
     * @param networks
     * @return the child
     */
    public static NeuralNetwork breed(List<NeuralNetwork> networks, int sameErasure) {
        // Choose 2 random parents

        List<NeuralNetwork> networks2 = new ArrayList<>();
        networks2.addAll(networks);
        NeuralNetwork network = networks.get(random.nextInt(networks.size()));
        networks2.remove(network);
        NeuralNetwork network2 = networks2.get(random.nextInt(networks2.size()));

        return createChild(network, network2);
    }

    /**
     * Creates a child from the given 2 networks
     * @param network
     * @param network2
     */
    private static NeuralNetwork createChild(NeuralNetwork network, NeuralNetwork network2) {
        NeuralNetwork child;
        NeuralNetwork other;
        int numLayers = min(network.getLayersCount(), network2.getLayersCount());
        if (random.nextBoolean()) {
            child = clone(network);
            other = network2;
        } else {
            child = clone(network2);
            other = network;
        }
        for (int i = 0; i < numLayers - 1; i++) { // don't cross the last layer
            crossLayers(child.getLayerAt(i), other.getLayerAt(i));
        }
        return child;
    }

    /**
     * Crosses the given layers, changing the first given one
     * @param childLayer
     * @param otherLayer
     */
    private static void crossLayers(Layer childLayer, Layer otherLayer) {
        List<Neuron> childNeurons = childLayer.getNeurons();
        List<Neuron> otherNeurons = otherLayer.getNeurons();
        int smallerSize = smallerSize(childNeurons, otherNeurons);
        for (int i = 0; i < smallerSize; i++) {
            crossNeurons(childNeurons.get(i), otherNeurons.get(i));
        }
    }

    /**
     * Crosses the given neurons, changing the first given one
     * @param childNeuron
     * @param otherNeuron
     */
    private static void crossNeurons(Neuron childNeuron, Neuron otherNeuron) {
        List<Connection> childConnections = childNeuron.getOutConnections();
        List<Connection> otherConnections = otherNeuron.getOutConnections();
        int smallerSize = smallerSize(childConnections, otherConnections);
        for (int i = 0; i < smallerSize; i++) {
            if (random.nextBoolean()) {
                Connection childConnection = childConnections.get(i);
                Connection otherConnection = otherConnections.get(i);
                childConnection.setWeight(otherConnection.getWeight());
            }
        }
    }

    /**
     * returns the smaller size between two lists
     * @param list1
     * @param list2
     * @return
     */
    private static <T> int smallerSize(List<T> list1, List<T> list2) {
        return list1.size() < list2.size() ? list1.size() : list2.size();
    }

    /**
     * Creates a clone of the given network, and returns it
     * @param network
     * @return
     */
    private static NeuralNetwork clone(NeuralNetwork network) {
        network.save(CLONE_PATH);
        return NeuralNetwork.createFromFile(CLONE_PATH);
    }


    private static <T> T randomList(List<T> list) {
        return list.get(random.nextInt(list.size()));
    }

}
