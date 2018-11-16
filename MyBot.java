// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//     Otherwise the names of methods are consistent.

import hlt.*;
import hlt.GNN.Networks.Bot;
import hlt.GNN.Networks.ControllerBot;
import hlt.GNN.Networks.ShipBot;
import hlt.GNN.Util.NetworkFileManager;

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
            Map<EntityId, Ship> enemyShips = getEnemies(game, me);
            final ArrayList<Command> commandQueue = new ArrayList<>();
            curHalite = me.halite;
            data = new HashMap<>(); // Reset data
            setTurnData(me);
            controllerBot.setInput(data);
            handleOutput(controllerBot.getOutput(), commandQueue, me, gameMap); // Do we build a ship?
            for (final Ship ship : me.ships.values()) {
                Entity enemyShip = closest(ship, enemyShips);   // Find closest enemy
                Entity allyShip = closest(ship, me.ships);      // Find closest ally (hopefully it helps not crash)
                Entity drop = getClosestDrop(me, ship);
                setShipData(gameMap, ship, enemyShip, allyShip, drop);
                shipBot.setInput(data);
                handleOutput(shipBot.getOutput(), ship, commandQueue, gameMap);
            }
            game.endTurn(commandQueue);
        }
    }

    /**
     * Gets the closest drop-off point. It may be either a shipyard or a drop-off
     * @param me
     * @param ship
     * @return
     */
    private static Entity getClosestDrop(Player me, Ship ship) {
        Entity drop = closest(ship, me.dropoffs);       // Find closest dropoff
        if (drop == null) {
            drop = me.shipyard;
        } else {
            drop = dist(ship, drop) > dist(ship, me.shipyard) ? me.shipyard : drop;
        }
        return drop;
    }

    /**
     * Sets the data relevant to the given ship
     * @param gameMap
     * @param ship The ship
     * @param enemyShip The closest enemy ship
     * @param allyShip The closest allied ship
     * @param drop The closest dropoff point, either a dropoff or a shipyard
     */
    private static void setShipData(GameMap gameMap, Ship ship, Entity enemyShip, Entity allyShip, Entity drop) {
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
    }

    /**
     * Adds data relevant to the entire turn
     * @param me
     */
    private static void setTurnData(Player me) {
        data.put("totalHalite", me.halite);
        data.put("numShips", me.ships.size()); // Get data
    }

    /**
     * Returns a map of enemy ships
     * @param game The game
     * @param me The player (whose ships will not be included in the enemy ships)
     * @return
     */
    private static Map<EntityId, Ship> getEnemies(Game game, Player me) {
        Map<EntityId, Ship> enemyShips = new HashMap<>(); // Make a hashmap to put all enemy ships in
        for (Player player: game.players) {
            if (player != me) { // Only add to the map if it's not an ally
                enemyShips.putAll(player.ships);
            }
        }
        return enemyShips;
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

    /**
     * Returns the closest entity to the given one
     * @param entity Entity to compare to
     * @param entityMap List of entities
     * @param <T> The type of entity
     * @return
     */
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
