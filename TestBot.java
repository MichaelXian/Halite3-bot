// This Java API uses camelCase instead of the snake_case as documented in the API docs.
//     Otherwise the names of methods are consistent.

import hlt.*;

public class TestBot {
    public static void main(final String[] args) {

        Game game = new Game();
        // At this point "game" variable is populated with initial map data.
        // This is a good place to do computationally expensive start-up pre-processing.
        // As soon as you call "ready" function below, the 2 second per turn timer will start.
        game.ready("MyJavaBot");
        System.out.println("g");
        System.out.println("m 0 n");
        System.out.println("g");
        System.out.println();
        System.out.println();
        System.out.println("m 1 n m 0 n");

        for (;;) {
            game.updateFrame();
            final Player me = game.me;
            final GameMap gameMap = game.gameMap;
            System.out.println();
            //System.out.println("m 0 n m 1 n");
            //final ArrayList<Command> commandQueue = new ArrayList<>();

            //for (final Ship ship : me.ships.values()) {
            //}

            //game.endTurn(commandQueue);
        }
    }
}