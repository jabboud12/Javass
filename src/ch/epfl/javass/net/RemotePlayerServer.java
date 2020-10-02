package ch.epfl.javass.net;

import static ch.epfl.javass.net.StringSerializer.deserializeInt;
import static ch.epfl.javass.net.StringSerializer.deserializeLong;
import static ch.epfl.javass.net.StringSerializer.deserializeString;
import static ch.epfl.javass.net.StringSerializer.serializeInt;
import static ch.epfl.javass.net.StringSerializer.split;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.EnumMap;
import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class representing a server ; it is a moderator between a client from another
 * machine and the game on the local server
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class RemotePlayerServer {
    
    /**
     * The port on which we will establish the communication line
     */
    public static final int PORT = 5108;
    
    private final Player player;

    /**
     * Constructs an instance of this Player
     * 
     * @param player
     *            the Player representing the server
     */
    public RemotePlayerServer(Player player) {
        this.player = player;
    }

    /**
     * Runs the server and constantly waits for eventual instructions from the
     * Client, as the game goes on
     * 
     * @throws IOException
     *             if an event between the client and the server occurs
     *             (connection interruption for example)
     */
    public void run() throws IOException {

        try (ServerSocket s0 = new ServerSocket(5108);
                Socket s = s0.accept();
                BufferedReader r = new BufferedReader(
                        new InputStreamReader(s.getInputStream(), UTF_8));
                BufferedWriter w = new BufferedWriter(
                        new OutputStreamWriter(s.getOutputStream(), UTF_8))) {
            while (true) {
                String command = r.readLine();
                String[] a = split(' ', command);

                switch (JassCommand.valueOf(a[0])) {
                case PLRS:
                    String[] names = split(',', a[2]);
                    Map<PlayerId, String> playerNames = new EnumMap<>(PlayerId.class);
                    for (int i = 0; i < names.length; ++i)
                        playerNames.put(PlayerId.ALL.get(i),
                                deserializeString(names[i]));
                    player.setPlayers(PlayerId.ALL.get(deserializeInt(a[1])),
                            playerNames);
                    break;
                case TRMP:
                    player.setTrump(Card.Color.ALL.get(deserializeInt(a[1])));
                    break;
                case HAND:
                    player.updateHand(CardSet.ofPacked(deserializeLong(a[1])));
                    break;
                case TRCK:
                    player.updateTrick(Trick.ofPacked(deserializeInt(a[1])));
                    break;
                case CARD:
                    String[] state = split(',', a[1]);
                    Card c = player.cardToPlay(
                            TurnState.ofPackedComponents(
                                    deserializeLong(state[0]),
                                    deserializeLong(state[1]),
                                    deserializeInt(state[2])),
                            CardSet.ofPacked(deserializeLong(a[2])));
                    w.write(serializeInt(c.packed()) + "\n");
                    w.flush();
                    break;
                case SCOR:
                    player.updateScore(Score.ofPacked(deserializeLong(a[1])));
                    break;
                case WINR:
                    player.setWinningTeam(TeamId.ALL.get(deserializeInt(a[1])));
                    break;
                default:
                    throw new IOException();
                }
            }
        } catch (IOException e) {
            throw new IOException(e);
        }
    }

}
