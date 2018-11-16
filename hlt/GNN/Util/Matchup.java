package hlt.GNN.Util;

import hlt.GNN.Networks.Bot;

public class Matchup {
    private Bot bot1;
    private Bot bot2;

    Matchup(Bot bot1, Bot bot2) {
        this.bot1 = bot1;
        this.bot2 = bot2;

    }

    public Bot getBot1() {
        return bot1;
    }

    public Bot getBot2() {
        return bot2;
    }
}
