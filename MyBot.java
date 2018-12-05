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
    static HashMap<Ship, Position> newLocations;
    static int curHalite;
    static boolean timeToReturn;
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
        game.ready("GNNBot");
        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            timeToReturn = shouldReturn(game, me, me.ships, me.dropoffs, me.shipyard);
            int turnsLeft = Constants.MAX_TURNS - game.turnNumber;
            final GameMap gameMap = game.gameMap;
            Map<EntityId, Ship> enemyShips = getEnemies(game, me);
            final ArrayList<Command> commandQueue = new ArrayList<>();
            initializeNewLocations(me.ships);
            curHalite = me.halite;
            data = new HashMap<>(); // Reset data
            setTurnData(me);
            controllerBot.setInput(data);
            handleOutput(controllerBot.getOutput(), commandQueue, me, gameMap); // Do we build a ship?
            for (final Ship ship : me.ships.values()) {
                Position enemyShipPos = closest(ship, enemyShips);   // Find closest enemy
                Position allyShipPos = closestAlly(ship, newLocations);      // Find closest ally (hopefully it helps not crash)
                Position dropPos = getClosestDrop(me, ship);
                if (timeToReturn) {
                    goTo(gameMap, commandQueue, ship, dropPos);
                    continue;
                }
                setShipData(turnsLeft, gameMap, ship, enemyShipPos, allyShipPos, dropPos);
                shipBot.setInput(data);
                handleOutput(shipBot.getOutput(), ship, commandQueue, gameMap);
            }
            game.endTurn(commandQueue);
        }
    }

    private static void goTo(GameMap gameMap, ArrayList<Command> commandQueue, Ship ship, Position position) {
        int xDistance = xDist(ship.position, position);
        int yDistance = yDist(ship.position, position);
        if (xDistance > 0) {
            doCommand("west", ship, commandQueue, gameMap);
        } else if (xDistance < 0) {
            doCommand("east", ship, commandQueue, gameMap);
        } else if (yDistance > 0) {
            doCommand("south", ship, commandQueue, gameMap);
        } else if (yDistance < 0) {
            doCommand("north", ship, commandQueue, gameMap);
        }
    }

    private static boolean shouldReturn(Game game, Player me, Map<EntityId, Ship> ships, Map<EntityId, Dropoff> dropoffs, Shipyard shipyard) {
        int turnsLeft = Constants.MAX_TURNS - game.turnNumber;
        int maxDist = 0;
        for (Map.Entry<EntityId, Ship> entry : ships.entrySet()) {
            Ship ship = entry.getValue();
            Position closestDrop = getClosestDrop(me, ship);
            int dist = dist(ship.position, closestDrop);
            maxDist = maxDist > dist ? maxDist : dist;
        }
        return maxDist >= turnsLeft;
    }

    /**
     * Goes through the new positions of the ships, and returns the one closest to the given ship
     * @param ship
     * @param newLocations
     * @return
     */
    private static Position closestAlly(Ship ship, HashMap<Ship, Position> newLocations) {
        Position closest = null;
        for (Map.Entry<Ship, Position> entry:  newLocations.entrySet()) {
            Position newPos = entry.getValue();
            if (closest == null) {
                closest = newPos;
            } else {
                closest = dist(ship.position, closest) > dist(ship.position, newPos) ? newPos : closest;
            }
        }
        return closest;
    }

    /**
     * Initializes newLocations hashmap with the current locations
     * @param ships
     */
    private static void initializeNewLocations(Map<EntityId, Ship> ships) {
        newLocations = new HashMap<>();
        for (Map.Entry<EntityId, Ship> entry : ships.entrySet()) {
            Ship ship = entry.getValue();
            newLocations.put(ship, ship.position);
        }
    }

    /**
     * Gets the closest drop-off point. It may be either a shipyard or a drop-off
     * @param me
     * @param ship
     * @return
     */
    private static Position getClosestDrop(Player me, Ship ship) {
        Position drop = closest(ship, me.dropoffs);       // Find closest dropoff
        if (drop == null) {
            drop = me.shipyard.position;
        } else {
            drop = dist(ship.position, drop) > dist(ship.position, me.shipyard.position) ? me.shipyard.position : drop;
        }
        return drop;
    }

    /**
     * Sets the data relevant to the given ship
     * @param turnsLeft
     * @param gameMap
     * @param ship The ship
     * @param enemyShipPos The closest enemy ship's position
     * @param allyShipPos The closest allied ship's position
     * @param dropPos The closest dropoff's position point, either a dropoff or a shipyard
     */
    private static void setShipData(int turnsLeft, GameMap gameMap, Ship ship, Position enemyShipPos, Position allyShipPos, Position dropPos) {
        data.put("enemyDistX", xDist(ship.position, enemyShipPos));
        data.put("enemyDistY", yDist(ship.position, enemyShipPos));
        data.put("allyDistX", xDist(ship.position, allyShipPos));
        data.put("allyDistY", yDist(ship.position, allyShipPos));
        data.put("dropDistX", xDist(ship.position, dropPos));
        data.put("dropDistY", yDist(ship.position, dropPos));
        data.put("shipHalite", ship.halite);
        data.put("tileHalite", gameMap.at(ship).halite);
        data.put("northHalite", gameMap.at(ship.position.directionalOffset(Direction.NORTH)).halite);
        data.put("southHalite", gameMap.at(ship.position.directionalOffset(Direction.SOUTH)).halite);
        data.put("eastHalite", gameMap.at(ship.position.directionalOffset(Direction.EAST)).halite);
        data.put("westHalite", gameMap.at(ship.position.directionalOffset(Direction.WEST)).halite);
        data.put("turnsLeft", turnsLeft);
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
     * @param position
     * @param position1
     * @return
     */
    private static int xDist(Position position, Position position1) {
        if (position1 == null) {
            return 0;
        }
        return position1.x - position.x;
    }

    /**
     * Returns the y displacement
     * @param position
     * @param position1
     * @return
     */
    private static int yDist(Position position, Position position1) {
        if (position1 == null) {
            return 0;
        }
        return position1.y - position.y;
    }

    /**
     * Returns the distance between two entities
     * @param position
     * @param position1
     * @return
     */
    private static int dist(Position position, Position position1) {
        return Math.abs(xDist(position, position1)) + Math.abs(yDist(position,position1));
    }

    /**
     * Returns the closest entity to the given one
     * @param entity Entity to compare to
     * @param entityMap List of entities
     * @param <T> The type of entity
     * @return
     */
    private static <T> Position closest(Entity entity, Map<EntityId, T> entityMap) {
        if (entityMap.isEmpty()) {
            return null;
        } else{
            Position closest = null;
            int closestDist = 0;
            for (Map.Entry<EntityId, T> entry : entityMap.entrySet()) {
                Entity newEntity = (Entity) entry.getValue();
                int newDist = dist(entity.position, newEntity.position);
                if (newDist < closestDist && newDist != 0 || closestDist == 0) {
                    closest = newEntity.position;
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
                newLocations.put(new Ship(me.id,  new EntityId(999), me.shipyard.position, 1), me.shipyard.position);
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
        switch (str) {
            case "north":
                commandQueue.add(ship.move(Direction.NORTH));
                newLocations.put(ship, ship.position.directionalOffset(Direction.NORTH));
                return;
            case "south":
                commandQueue.add(ship.move(Direction.SOUTH));
                newLocations.put(ship, ship.position.directionalOffset(Direction.SOUTH));
                return;
            case "east":
                commandQueue.add(ship.move(Direction.EAST));
                newLocations.put(ship, ship.position.directionalOffset(Direction.EAST));
                return;
            case "west":
                commandQueue.add(ship.move(Direction.WEST));
                newLocations.put(ship, ship.position.directionalOffset(Direction.WEST));
                return;
        }
    }


    /**
     * Checks if the command would kill the player (or if not enough halite for ship to move)
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
