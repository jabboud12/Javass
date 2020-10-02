package ch.epfl.javass;

import static ch.epfl.javass.jass.PlayerId.ALL;
import static ch.epfl.javass.jass.PlayerId.COUNT;
import static ch.epfl.javass.jass.PlayerId.PLAYER_1;
import static ch.epfl.javass.jass.PlayerId.PLAYER_2;
import static ch.epfl.javass.jass.PlayerId.PLAYER_3;
import static ch.epfl.javass.jass.PlayerId.PLAYER_4;

import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.gui.GraphicalPlayerAdapter;
import ch.epfl.javass.jass.JassGame;
import ch.epfl.javass.jass.MctsPlayer;
import ch.epfl.javass.jass.PacedPlayer;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.net.RemotePlayerClient;
import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Main Class of the Project. The game starts form here and it is the most
 * superficial Class in the Project.
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class LocalMain extends Application {

    // Map which will contain the players' names
    private static Map<PlayerId, String> names = new EnumMap<>(PlayerId.class);

    // The default number of iterations given to an MctsPlayer
    private static final int ITERATIONS = 100_000;

    // The default IP address of the server (local machine)
    private static final String IPADRESS = "localhost";

    // The port to which we connect in order to play the game in distance
    private static final int PORT = 5108;

    // The default seed used in the game
    private static long seed = 2019;

    // The minimum time it takes for a PacedPlayer to play a Card
    private static final int WAITING_TIME = 1;

    /**
     * Main method that launches the game. All instances of the games are
     * initialized through the arguments given
     * 
     * @param args
     *            The String table containing the arguments in order to start
     *            and play the game
     */
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    /*
     * This method is used to create an instance of a game, initialize the
     * primary elements of the game (players, names, location...) and it follows
     * these steps in order :
     */
    public void start(Stage primaryStage) throws Exception {
        // Here we put the default player names in the map (will change if
        // specified)
        names.put(PLAYER_1, "Alice");
        names.put(PLAYER_2, "Bob");
        names.put(PLAYER_3, "Eve");
        names.put(PLAYER_4, "Denis");

        List<String> param = getParameters().getRaw();

        if (!isValid(param)) {
            // The method above isValid() is invoked and checks for eventual
            // errors. If the method returns false (it means there are errors),
            // the game will stop and the program exits and shows where the
            // first error occurred
            System.exit(1);
        } else {

            // Changes the value of the seed if specified in args
            if (param.size() == 5)
                seed = Long.parseLong(param.get(4));

            Random r = new Random(seed);

            // Creates the Map where the Players will be placed and linked to
            // their IDs
            Map<PlayerId, Player> players = new EnumMap<>(PlayerId.class);

            for (int i = 0; i < COUNT; ++i) {
                // The Player in question that we want to place in the Map
                PlayerId play = ALL.get(i);

                String[] arg = param.get(i).split(":");

                // All four Players are Paced Players with the same waiting time
                // specified above in the attributes for the class
                switch (arg[0]) {

                // If it is a human local Player, we create a
                // GraphicalPlayerAdapter and change the name from the default
                // value if specified
                case ("h"):
                    players.put(play, new GraphicalPlayerAdapter());
                    if (arg.length == 2)
                        names.replace(play, arg[1]);
                    break;

                // If it is a simulated Player, we replace the name in the Map
                // if specified, and change the number of iterations in the MCTS
                // algorithm if specified also
                case ("s"):
                    int it = ITERATIONS;
                    if (arg.length > 1 && !arg[1].isEmpty())
                        names.replace(play, arg[1]);

                    if (arg.length == 3)
                        it = Integer.parseInt(arg[2]);

                    players.put(play,
                            new PacedPlayer(
                                    new MctsPlayer(play, r.nextLong(), it),
                                    WAITING_TIME));
                    break;

                // If it is a human distant Player, we create a
                // GraphicalPlayerAdapter, then initialize the created Player as
                // a RemotePlayerClient with "localhost" a default address, or
                // other if specified. Then we change the name of the Player in
                // the Map if specified
                case ("r"):
                    String IpAdress = IPADRESS;
                    Player player = new GraphicalPlayerAdapter();

                    if (arg.length == 3)
                        IpAdress = arg[2];
                    player = new RemotePlayerClient(IpAdress, PORT);
                    players.put(play, player);

                    if (arg.length > 1 && !arg[1].isEmpty())
                        names.replace(play, arg[1]);

                    break;
                default: // Should never happen
                    System.out.println("!!!");
                }
            }
            // The game starts here after being initialized above
            new Thread(() -> {
                JassGame g = new JassGame(r.nextInt(1000), players, names);
                while (!g.isGameOver()) {
                    g.advanceToEndOfNextTrick();

                    try {
                        Thread.sleep(1000);
                    } catch (Exception e) {
                    }
                }
            }).start();
        }
    }

    /*
     * Private method used to validate the given arguments with these rules in
     * the order they are written in the method:
     */
    private boolean isValid(List<String> param) {

        // 1) The length of the args table is different from 4 or 5
        if (param.size() < 4 || param.size() > 5) {
            System.err.println(
                    "Erreur : Mauvais nombre d'arguments : il faut qu'il y ait 4 ou 5 arguments, mais ici il y en a "
                            + param.size());
            return false;
        }

        // 2) The givenLong for the Seed is not valid (< 0, or String does not
        // represent a number)
        if (param.size() == 5)
            try {
                if (Long.parseLong(param.get(4)) < 0) {
                    System.err
                            .println("Erreur : La graine donnée est négative ("
                                    + param.get(4)
                                    + ") elle doit être positive");
                    return false;
                }
            } catch (NumberFormatException e) {
                System.err.println(
                        "Erreur : L'argument donné pour la graine utilisée durant le jeu n'est pas de type Long ("
                                + param.get(4) + ")");
                return false;
            }

        for (int i = 0; i < param.size() - 1; ++i) {
            String[] components = param.get(i).split(":");

            switch (components[0]) {
            case ("h"):
                // 3) The number of arguments for a human player is different
                // from 1 or 2
                if (components.length != 1 && components.length != 2) {
                    System.err.println(
                            "Erreur : Nombre d'arguments non valide : un joueur humain prend 1 ou 2 arguments séparés par des \":\","
                                    + " mais ici il en possède : "
                                    + components.length);
                    return false;
                }
                break;
            case ("s"):
                // 4) The number of arguments for a simulated player is not
                // between 1 and 3 (included)
                if (components.length < 1 || components.length > 3) {
                    System.err.println(
                            "Erreur : Nombre d'arguments non valide : un joueur simulé prend 1, 2 ou 3 arguments séparés par des \":\", "
                                    + "mais ici il en possède : "
                                    + components.length);
                    return false;
                }
                try {
                    // 5) The number of iterations given for the MCTS simulation
                    // is not valid (< 9, or String does not represent an
                    // Integer)
                    if (components.length == 3)
                        if (Integer.parseInt(components[2]) <= 9) {
                            System.err.println(
                                    "Erreur : Nombre d'itérations donné n'est pas valide ("
                                            + components[2]
                                            + ") doit être positif supérieur à 9");
                            return false;
                        }
                } catch (NumberFormatException e) {
                    System.err.println(
                            "Erreur : L'argument donné représentant le nombre d'itérations n'est pas de type Integer ("
                                    + components[2] + ")");
                    return false;
                }
                break;
            case ("r"):
                // 6) The number of arguments for a distant player is not
                // between 1 and 3 (included)
                if (components.length < 1 || components.length > 3) {
                    System.err.println(
                            "Erreur : Nombre d'arguments non valide : un joueur distant prend 1, 2 ou 3 arguments séparés par des \":\", "
                                    + "mais ici il en possède : "
                                    + components.length);
                    return false;
                }
                break;
            default:
                // 7) First part of each argument is neither "h" nor "s" nor "r"
                System.err.println(
                        "Erreur : La spécification des joueurs n'est pas valide. La première partie de chaque partie doit contenir soit "
                                + "\"h\" pour un joueur humain local, \nsoit \"s\" pour un joueur simulé, "
                                + "ou bien \"r\" pour un joueur humain distant, mais dans ce cas on a : "
                                + components[0]);
                return false;
            }
        }
        return true;
    }
}