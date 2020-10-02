package ch.epfl.javass.net;

import static ch.epfl.javass.net.StringSerializer.combine;
import static ch.epfl.javass.net.StringSerializer.deserializeInt;
import static ch.epfl.javass.net.StringSerializer.serializeInt;
import static ch.epfl.javass.net.StringSerializer.serializeLong;
import static ch.epfl.javass.net.StringSerializer.serializeString;
import static java.nio.charset.StandardCharsets.UTF_8;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UncheckedIOException;
import java.net.Socket;
import java.util.Map;
import java.util.StringJoiner;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class representing a client ; it is a player that could play the game from
 * another machine
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class RemotePlayerClient implements Player, AutoCloseable {
    private BufferedWriter w;
    private BufferedReader r;
    private Socket s;

    /**
     * Constructs an instance of a PlayerClient
     * 
     * @param host
     *            the address of the game's host
     * @param port
     *            the port to which all parties are connected during the game to
     *            exchange the information
     * @throws IOException
     *             if an event between the client and the server occurs
     *             (connection interruption for example)
     */
    public RemotePlayerClient(String host, int port) throws IOException {
        Socket s = new Socket(host, port);
        BufferedReader r = new BufferedReader(
                new InputStreamReader(s.getInputStream(), UTF_8));
        BufferedWriter w = new BufferedWriter(
                new OutputStreamWriter(s.getOutputStream(), UTF_8));
        this.s = s;
        this.w = w;
        this.r = r;
    }

    private void send(String message) {
        try {
            w.write(message + "\n");
            w.flush();
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        StringJoiner s = new StringJoiner(" ");
        s.add(JassCommand.CARD.name());
        String[] turnState = { serializeLong(state.packedScore()),
                serializeLong(state.packedUnplayedCards()),
                serializeInt(state.packedTrick()) };

        s.add(combine(',', turnState));
        s.add(serializeLong(hand.packed()));
        send(s.toString());
        Card c = null;
        try {
            c = Card.ofPacked(deserializeInt(r.readLine()));
        } catch (IOException e) {
            throw new UncheckedIOException(e);
        }
        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        StringJoiner s = new StringJoiner(" ");
        s.add(JassCommand.PLRS.name());
        s.add(Integer.toString(ownId.ordinal()));
        String[] string = new String[PlayerId.COUNT];
        for (int i = 0; i < PlayerId.COUNT; ++i)
            string[i] = serializeString(playerNames.get(PlayerId.ALL.get(i)));
        s.add(combine(',', string));
        send(s.toString());
    }

    @Override
    public void updateHand(CardSet newHand) {
        send(JassCommand.HAND.name() + " " + serializeLong(newHand.packed()));
    }

    @Override
    public void setTrump(Card.Color trump) {
        send(JassCommand.TRMP.name() + " " + serializeInt(trump.ordinal()));
    }

    @Override
    public void updateTrick(Trick newTrick) {
        send(JassCommand.TRCK.name() + " " + serializeInt(newTrick.packed()));
    }

    @Override
    public void updateScore(Score score) {
        send(JassCommand.SCOR.name() + " " + serializeLong(score.packed()));
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        send(JassCommand.WINR.name() + " " + serializeInt(winningTeam.ordinal()));
    }

    @Override
    public void close() throws Exception {
        w.close();
        r.close();
        s.close();
    }
}
