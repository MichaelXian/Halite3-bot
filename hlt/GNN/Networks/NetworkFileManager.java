package hlt.GNN.Networks;


import hlt.GNN.Matchup;
import org.neuroph.core.NeuralNetwork;

public class NetworkFileManager {
    public static final String SHIPS_PATH = "hlt/NeuralNets/ships/ship";
    public static final String CONTROLLERS_PATH = "hlt/NeuralNets/controllers/controller";

    /**
     * Loads a bot from the files and returns it
     * @param botNum The identifier for the bot
     * @return
     */
    public static Bot loadBot(int botNum) {
        String shipPath = SHIPS_PATH + botNum + ".nnet";
        String controllerPath = CONTROLLERS_PATH  + botNum + ".nnet";
        NeuralNetwork shipNetwork = NeuralNetwork.createFromFile(shipPath);
        NeuralNetwork controllerNetwork = NeuralNetwork.createFromFile(controllerPath);
        return new Bot(shipNetwork, shipPath, controllerNetwork, controllerPath, botNum);
    }

    /**
     * Loads the best bot
     * @return The best bot
     */
    public static Bot loadBest() {
        String shipPath = SHIPS_PATH + "best.nnet";
        String controllerPath = CONTROLLERS_PATH  + "best.nnet";
        NeuralNetwork shipNetwork = NeuralNetwork.createFromFile(shipPath);
        NeuralNetwork controllerNetwork = NeuralNetwork.createFromFile(controllerPath);
        return new Bot(shipNetwork, shipPath, controllerNetwork, controllerPath, 0);
    }

    /**
     * Saves a bot into the neural networks directory
     * @param bot The bot to save
     */
    public static void saveBot(Bot bot) {
        String shipPath = SHIPS_PATH + bot.getBotNum() + ".nnet";
        String controllerPath = CONTROLLERS_PATH  + bot.getBotNum() + ".nnet";
        saveBotToFile(bot, shipPath, controllerPath);
    }

    /**
     * Saves the bot as the best bot
     * @param bot The bot to save
     */
    public static void saveBest(Bot bot) {
        String shipPath = SHIPS_PATH + "best.nnet";
        String controllerPath = CONTROLLERS_PATH  + "best.nnet";
        saveBotToFile(bot, shipPath, controllerPath);
    }


    /**
     * Saves a bot to the given filepaths
     * @param bot The bot to save
     * @param shipPath Where to save the ship bot
     * @param controllerPath Where to save the controller bot
     */
    private static void saveBotToFile(Bot bot, String shipPath, String controllerPath) {
        ShipBot shipBot = bot.getShipBot();
        ControllerBot controllerBot = bot.getControllerBot();
        NeuralNetwork shipNetwork = shipBot.getNeuralNetwork();
        shipNetwork.save(shipPath);
        NeuralNetwork controllerNetwork = controllerBot.getNeuralNetwork();
        controllerNetwork.save(controllerPath);
    }


    /**
     * Sets the files the bot will use to be the ones in the current matchup. Very slow cuz lots of file writing,
     * decided to instead pass in filepaths as arguments for the bot.
     * @param matchup
     */
    @Deprecated
    public static void setMatchup(Matchup matchup) {
        NeuralNetwork shipNetwork1 = matchup.getBot1().getShipBot().getNeuralNetwork();
        NeuralNetwork controllerNetwork1 = matchup.getBot1().getControllerBot().getNeuralNetwork();
        NeuralNetwork shipNetwork2 = matchup.getBot2().getShipBot().getNeuralNetwork();
        NeuralNetwork controllerNetwork2 = matchup.getBot2().getControllerBot().getNeuralNetwork();
        shipNetwork1.save(SHIPS_PATH + "Matchup1.nnet");
        controllerNetwork1.save(CONTROLLERS_PATH + "Matchup1.nnet");
        shipNetwork2.save("Matchup2.nnet");
        controllerNetwork2.save("Matchup2.nnet");
    }
}
