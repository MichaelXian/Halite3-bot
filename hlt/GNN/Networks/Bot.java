package hlt.GNN.Networks;

import org.neuroph.core.NeuralNetwork;

public class Bot {
    private ShipBot shipBot;
    private ControllerBot controllerBot;
    private int botNum;

    Bot(NeuralNetwork shipNetwork, String shipPath, NeuralNetwork controllerNetwork, String controllerPath, int botNum) {
        this.shipBot = new ShipBot(shipNetwork, shipPath);
        this.controllerBot = new ControllerBot(controllerNetwork, controllerPath);
        this.botNum = botNum;
    }

    public ControllerBot getControllerBot() {
        return controllerBot;
    }

    public ShipBot getShipBot() {
        return shipBot;
    }

    public int getBotNum() {
        return botNum;
    }

    public void setBotNum(int botNum) {
        this. botNum = botNum;
    }
}
