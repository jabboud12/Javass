/*
 * Author :   Joseph E. Abboud.
 * Date   :   24 Mar 2019
 */

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.net.RemotePlayerClient;

public class MctsJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
        Random rng = new Random();
        long seed = rng.nextLong();
        for (PlayerId pId : PlayerId.ALL) {
            Player player = null;
            if (pId.team() == TeamId.TEAM_1)
                player = new MctsPlayer(pId, seed, 10_000);
            else
                player = new RandomPlayer(seed);

            if (pId == PlayerId.PLAYER_1)
                player = new PrintingPlayer(player);
            if (pId == PlayerId.PLAYER_2) {
                try {
                player = new RemotePlayerClient("localhost", 5108);
                } catch(IOException e) {}
            }
            //player = new PacedPlayer(player, 1);
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(seed, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
        }
        //128.179.186.25
        //128.179.181.155
    }
}
