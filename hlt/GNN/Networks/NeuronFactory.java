package hlt.GNN.Networks;

import org.neuroph.core.Neuron;
import org.neuroph.core.transfer.Sigmoid;

public class NeuronFactory {

    public static Neuron randomNeuron() {
        Neuron ret = new Neuron();
        ret.setTransferFunction(new Sigmoid());
        return ret;
    }


}
