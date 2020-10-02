/*
 * Author :   Joseph E. Abboud.
 * Date   :   5 May 2019
 */

import static ch.epfl.javass.jass.PlayerId.PLAYER_1;
import static ch.epfl.javass.jass.PlayerId.PLAYER_2;
import static ch.epfl.javass.jass.PlayerId.PLAYER_3;
import static ch.epfl.javass.jass.PlayerId.PLAYER_4;

import java.util.EnumMap;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import javafx.application.Application;
import javafx.stage.Stage;

public final class TestMain extends Application {
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        Random r = new Random();
        Map<PlayerId, Player> ps = new EnumMap<>(PlayerId.class);

        //         Player player = new RemotePlayerClient("128.179.177.171", 5108);
        //         player = new GraphicalPlayerAdapter();
        //         ps.put(PLAYER_1, player);

        ps.put(PLAYER_1, new GraphicalPlayerAdapter());
        //         ps.put(PLAYER_2, new GraphicalPlayerAdapter());
        //         ps.put(PLAYER_3, new GraphicalPlayerAdapter());
        //         ps.put(PLAYER_4, new GraphicalPlayerAdapter());
        
        ps.put(PLAYER_2, new PacedPlayer(new MctsPlayer(PLAYER_2, r.nextLong(), 10_000), 0));
        ps.put(PLAYER_3, new PacedPlayer(new MctsPlayer(PLAYER_3, r.nextLong(), 10_000), 0));
        ps.put(PLAYER_4, new PacedPlayer(new MctsPlayer(PLAYER_4, r.nextLong(), 10_000), 0));

        Map<PlayerId, String> ns = new EnumMap<>(PlayerId.class);
        PlayerId.ALL.forEach(i -> ns.put(i, i.name()));

        new Thread(() -> {
            JassGame g = new JassGame(r.nextLong(), ps, ns);
            while (!g.isGameOver()) {
                g.advanceToEndOfNextTrick();

//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                }
            }
        }).start();
    }
}