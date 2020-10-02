/*
 * Author :   Joseph E. Abboud.
 * Date   :   19 Mar 2019
 */

import java.util.HashMap;
import java.util.Map;

import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;

public final class RandomJassGame {
    public static void main(String[] args) {
        Map<PlayerId, Player> players = new HashMap<>();
        Map<PlayerId, String> playerNames = new HashMap<>();
//        Random rng = new Random();
        for (PlayerId pId: PlayerId.ALL) {
            Player player = new RandomPlayer(2019);
            if (pId == PlayerId.PLAYER_1) {
                player = new PrintingPlayer(player);
            }
//            player = new PacedPlayer(player, 1);
            players.put(pId, player);
            playerNames.put(pId, pId.name());
        }

        JassGame g = new JassGame(2019, players, playerNames);
        while (!g.isGameOver()) {
            g.advanceToEndOfNextTrick();
            System.out.println("----");
        }
    }
}