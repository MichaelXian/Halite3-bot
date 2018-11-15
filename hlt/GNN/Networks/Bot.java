package hlt.GNN.Networks;

import org.neuroph.core.NeuralNetwork;

public class Bot {
    private ShipBot shipBot;
    private ControllerBot controllerBot;

    Bot(NeuralNetwork shipNetwork, NeuralNetwork controllerNetwork, String shipPath, String controllerPath) {
        this.shipBot = new ShipBot(shipNetwork, shipPath);
        this.controllerBot = new ControllerBot(controllerNetwork, controllerPath);
    }

    public ControllerBot getControllerBot() {
        return controllerBot;
    }

    public ShipBot getShipBot() {
        return shipBot;
    }
}
