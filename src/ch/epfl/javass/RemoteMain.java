package ch.epfl.javass;

import java.io.IOException;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.net.RemotePlayerServer;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main class representing the server where a game of Jass is launched, in case
 * we want to play on multiple platforms and locations
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class RemoteMain extends Application {

    /**
     * Main method of the class, where the arguments (@param args) are given as
     * parameters to the method launch().
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Creating a Thread where the server for the distant game is launched.
        // The game starts when all 4 Players connect to the server
        Thread serverGame = new Thread(() -> {
            try {
                RemotePlayerServer serverPlayer = new RemotePlayerServer(
                        new GraphicalPlayerAdapter());
                System.out.println(
                        "La partie commencera à la connexion du client...");
                serverPlayer.run();
            } catch (IOException e) {
                throw new Error(e);
            }
        });
        serverGame.setDaemon(true);
        serverGame.start();
    }

}
