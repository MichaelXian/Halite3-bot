// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//     Otherwise the names of methods are consistent.

import hlt.*;
import hlt.GNN.Networks.Bot;
import hlt.GNN.Networks.ControllerBot;
import hlt.GNN.Networks.ShipBot;
import hlt.GNN.Util.NetworkFileManager;
import org.neuroph.core.NeuralNetwork;
import java.util.*;

import static hlt.Constants.SHIP_COST;

public class MyBot {
    static HashMap<String, Integer> data;
    static int curHalite;
    public static void main(final String[] args) {
        Bot bot;
        if (args.length > 0) {
            bot = NetworkFileManager.loadBot(Integer.parseInt(args[0]));
        } else {
            bot = NetworkFileManager.loadBest();
        }

        ControllerBot controllerBot = bot.getControllerBot();
        ShipBot shipBot = bot.getShipBot();
        Game game = new Game();
        game.ready("MyJavaBot");
        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            final GameMap gameMap = game.gameMap;
            ArrayList<Player> players = game.players;
            Player enemy = players.get(0) == me ? players.get(1) : players.get(0);
            final ArrayList<Command> commandQueue = new ArrayList<>();
            curHalite = me.halite;
            data = new HashMap<>();
            data.put("totalHalite", me.halite);
            data.put("numShips", me.ships.size());
            controllerBot.setInput(data);
            handleOutput(controllerBot.getOutput(), commandQueue, me, gameMap);
            for (final Ship ship : me.ships.values()) {
                Entity enemyShip = closest(ship, enemy.ships);
                Entity allyShip = closest(ship, me.ships);
                Entity drop = closest(ship, me.dropoffs);
                if (drop == null) {
                    drop = me.shipyard;
                } else {
                    drop = dist(ship, drop) > dist(ship, me.shipyard) ? me.shipyard : drop;
                }
                data.put("enemyDistX", xDist(ship, enemyShip));
                data.put("enemyDistY", yDist(ship, enemyShip));
                data.put("allyDistX", xDist(ship, allyShip));
                data.put("allyDistY", yDist(ship, allyShip));
                data.put("dropDistX", xDist(ship, drop));
                data.put("dropDistY", yDist(ship, drop));
                data.put("shipHalite", ship.halite);
                data.put("tileHalite", gameMap.at(ship).halite);
                data.put("northHalite", gameMap.at(ship.position.directionalOffset(Direction.NORTH)).halite);
                data.put("southHalite", gameMap.at(ship.position.directionalOffset(Direction.SOUTH)).halite);
                data.put("eastHalite", gameMap.at(ship.position.directionalOffset(Direction.EAST)).halite);
                data.put("westHalite", gameMap.at(ship.position.directionalOffset(Direction.WEST)).halite);
                shipBot.setInput(data);
                handleOutput(shipBot.getOutput(), ship, commandQueue, gameMap);
            }
            game.endTurn(commandQueue);
        }
    }

    /**
     * Returns the x displacement
     * @param entity
     * @param entity2
     * @return
     */
    private static int xDist(Entity entity, Entity entity2) {
        if (entity2 == null) {
            return 0;
        }
        return entity2.position.x - entity.position.x;
    }

    /**
     * Returns the y displacement
     * @param entity
     * @param entity2
     * @return
     */
    private static int yDist(Entity entity, Entity entity2) {
        if (entity2 == null) {
            return 0;
        }
        return entity2.position.y - entity.position.y;
    }

    /**
     * Returns the distance between two entities
     * @param entity
     * @param entity2
     * @return
     */
    private static int dist(Entity entity, Entity entity2) {
        return Math.abs(xDist(entity, entity2)) + Math.abs(yDist(entity,entity2));
    }

    private static <T> Entity closest(Entity entity, Map<EntityId, T> entityMap) {
        if (entityMap.isEmpty()) {
            return null;
        } else{
            Entity closest = (Entity) entityMap.get(0);
            int closestDist = dist(entity, closest);
            for (Map.Entry<EntityId, T> entry : entityMap.entrySet()) {
                Entity newEntity = (Entity) entry.getValue();
                int newDist = dist(entity, newEntity);
                if (newDist < closestDist && newDist != 0) {
                    closest = newEntity;
                }
            }
            return closest;
        }
    }



    /**
     * Handles controller's output
     * @param output
     * @param commandQueue
     * @param me
     */
    private static void handleOutput(List<String> output, ArrayList<Command> commandQueue, Player me, GameMap gameMap) {
        String str = output.get(0);
        if (str == "create") {
            if (curHalite >= SHIP_COST && !gameMap.at(me.shipyard).isOccupied()) {
                commandQueue.add(me.shipyard.spawn());
                curHalite -= SHIP_COST;
            }
        }
    }

    /**
     * Handle's ships's output
     * @param output
     * @param ship
     * @param commandQueue
     * @param gameMap
     */
    private static void handleOutput(List<String> output, Ship ship, ArrayList<Command> commandQueue, GameMap gameMap) {
        for (String str: output) {
            if (validCommand(str, ship, gameMap)) {
                doCommand(str, ship, commandQueue, gameMap);
                break;
            }
        }
    }

    /**
     * Adds the command to the command queue
     * @param str
     * @param ship
     * @param commandQueue
     * @param gameMap
     */

    private static void doCommand(String str, Ship ship, ArrayList<Command> commandQueue, GameMap gameMap) {
        if (str == "convert") {
            curHalite -= (4000 - ship.halite - gameMap.at(ship.position).halite);
            commandQueue.add(Command.transformShipIntoDropoffSite(ship.id));
            return;
        }
        if (str == "stay") {
            commandQueue.add(ship.stayStill());
            return;
        }
        //int cost = gameMap.at(ship.position).halite/10;
        //curHalite -= cost;
        switch (str) {
            case "north":
                commandQueue.add(ship.move(Direction.NORTH));
                return;
            case "south":
                commandQueue.add(ship.move(Direction.NORTH));
                return;
            case "east":
                commandQueue.add(ship.move(Direction.NORTH));
                return;
            case "west":
                commandQueue.add(ship.move(Direction.NORTH));
                return;
        }
    }


    /**
     * Checks if the command would kill the player
     * @param str
     * @param ship
     * @param gameMap
     * @return
     */
    private static boolean validCommand(String str, Ship ship, GameMap gameMap) {
        if (str == "stay") {
            return true;
        }
        if (str == "convert") {
            boolean enoughHalite = curHalite >= (4000 - ship.halite - gameMap.at(ship.position).halite);
            boolean notOwned = !gameMap.at(ship).hasStructure();
            return enoughHalite && notOwned;

        }
        int cost = (gameMap.at(ship.position).halite)/10;
        return ship.halite >= cost;
    }
}
