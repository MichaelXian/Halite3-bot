package hlt.GNN;//package hlt.GNN;

import hlt.GNN.Evolution.Crossover;
import hlt.GNN.Evolution.MatchMaker;
import hlt.GNN.Evolution.Mutator;
import hlt.GNN.Evolution.Selector;
import hlt.GNN.Networks.Bot;
import hlt.GNN.Networks.NeuralNetworkFactory;
import hlt.GNN.Util.Matchup;
import hlt.GNN.Util.NetworkFileManager;
import hlt.GNN.Util.StatFileManager;
import org.json.JSONException;
import org.json.JSONObject;
import org.neuroph.core.NeuralNetwork;
import org.neuroph.core.Neuron;
import org.neuroph.nnet.comp.neuron.BiasNeuron;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
// Can't resolve org.json.* but can resolve org.json.specificClass... ok then...

public class NeuralNetworkInspector {
    static final int NUM_SURVIVORS = 50;
    static final int NUM_BOTS = 100;
    static List<Bot> bots;
    static List<Matchup> matchups;
    static MatchMaker matchMaker;
    static Selector selector;
    static int generation;

    public static void main(String[] args) {
        NeuralNetwork shipNetwork = NeuralNetworkFactory.emptyNeuralNet(13,6);
        Neuron biasNeuron = new Neuron();
        for (Neuron neuron: shipNetwork.getLayerAt(0).getNeurons()) {
            if (neuron instanceof BiasNeuron) {
                biasNeuron = neuron;
            }
        }
        if (biasNeuron instanceof BiasNeuron) {
            Neuron north = shipNetwork.getLayerAt(6).getNeuronAt(1);
            north.addInputConnection(biasNeuron);
        }
    }
}