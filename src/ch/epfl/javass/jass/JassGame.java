package ch.epfl.javass.jass;

import static java.util.Collections.unmodifiableMap;
import static ch.epfl.javass.jass.TeamId.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import ch.epfl.javass.jass.Card.Color;

/**
 * Contains the method necessary to simulate a Trick, Turn and Game of Jass
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class JassGame {

    private TurnState turnState;

    private final Random shuffleRng;
    private final Random trumpRng;

    private PlayerId startingPlayer;

    private List<Card> deck;

    private final Map<PlayerId, Player> players;
    private final Map<PlayerId, String> playerNames;
    private Map<PlayerId, CardSet> hands;

    /**
     * Public constructor used to initialize a Jass game
     * 
     * @param (rngSeed)
     *            the seed used to generate all random events in the game
     * @param (players)
     *            the Map containing all Players, associated with their
     *            respective IDs
     * @param (playerNames)
     *            the Map containing all Players' names, associated with their
     *            respective Id's
     */
    public JassGame(long rngSeed, Map<PlayerId, Player> players,
            Map<PlayerId, String> playerNames) {
        Random rng = new Random(rngSeed);
        this.shuffleRng = new Random(rng.nextLong());
        this.trumpRng = new Random(rng.nextLong());

        this.players = unmodifiableMap(new HashMap<>(players));
        this.playerNames = unmodifiableMap(new HashMap<>(playerNames));

        turnState = TurnState.initial(Color.SPADE, Score.INITIAL,
                PlayerId.PLAYER_1);
        hands = new HashMap<>();
        deck = new ArrayList<>();
        for (int i = 0; i < CardSet.ALL_CARDS.size(); ++i)
            deck.add(CardSet.ALL_CARDS.get(i));
    }

    /**
     * Checks if a game that has started is over (One of the teams got more than
     * 1000 points)
     * 
     * @return true if one of the teams have won and false otherwise
     */
    public boolean isGameOver() {
        return turnState.score().totalPoints(TEAM_1) >= Jass.WINNING_POINTS
                || turnState.score().totalPoints(TEAM_2) >= Jass.WINNING_POINTS;
    }

    /**
     * Simulates a full Trick of Jass, and initializes the Turn when it needs to
     */
    public void advanceToEndOfNextTrick() {
        // Initializes a new Trick at the end of the Turn (the values of trump
        // Color and first player do not matter) as they will change later
        if (turnState.isTerminal())
            turnState = TurnState.initial(Color.SPADE,
                    turnState.score().nextTurn(), PlayerId.PLAYER_1);

        if (turnState.trick().index() == 0)
            initializeTurn();

        simulateTrick();
        collectTrick();

        if (isGameOver())
            declareWinner();

    }

    /*
     * This method is just esthetic, just to make advanceToNextTrick clearer
     */
    private void collectTrick() {
        turnState = turnState.withTrickCollected();
    }

    private void simulateTrick() {
        int ord = turnState.trick().player(0).ordinal();

        for (PlayerId player : PlayerId.ALL) {
            // Informs players about updated Trick and Score
            players.get(player).updateScore(turnState.score());
            players.get(player).updateTrick(turnState.trick());
        }

        for (int i = ord; i < ord + 4; ++i) {
            PlayerId player = PlayerId.ALL.get(i % 4);

            Card card = players.get(player).cardToPlay(turnState,
                    hands.get(player));
            CardSet newHand = CardSet.of(Arrays.asList(card));
            players.get(player)
            .updateHand(hands.get(player).difference(newHand));
            turnState = turnState.withNewCardPlayed(card);
            for (PlayerId plyr : PlayerId.ALL)
                players.get(plyr).updateTrick(turnState.trick());

            hands.replace(player, hands.get(player),
                    hands.get(player).difference(newHand));
        }

    }

    private void initializeTurn() {
        shuffleDeck();
        distributeCards();

        // Choose trump Color
        Card.Color trump = Card.Color.ALL
                .get(trumpRng.nextInt(Card.Color.COUNT));

        // Informs each player about the other players in the Trick and about
        // the new Turn
        for (PlayerId player : PlayerId.ALL) {

            // Checks if the Turn is the first of the game so the first player
            // is the one who has the seven of diamonds
            if (turnState.packedScore() == 0) {
                players.get(player).setPlayers(player, playerNames);
                if (hands.get(player).contains(
                        Card.of(Card.Color.DIAMOND, Card.Rank.SEVEN))) {
                    turnState = TurnState.initial(trump, turnState.score(),
                            player);
                    startingPlayer = player;
                }
            }
        }

        // Checks if the Turn is not the first of the game, so the starting
        // player is not the one who has the seven of diamonds
        if (turnState.packedScore() != 0) {
            turnState = TurnState.initial(trump, turnState.score(),
                    PlayerId.ALL.get((startingPlayer.ordinal() + 1) % 4));
            startingPlayer = turnState.trick().player(0);
        }

        for (PlayerId player : PlayerId.ALL) {
            // Informs players about their respective hands
            players.get(player).updateHand(hands.get(player));

            // Checks if the game is not over and informs players about trump
            // Color
            if (!isGameOver())
                players.get(player).setTrump(turnState.trick().trump());
        }
    }

    private void declareWinner() {
        assert (isGameOver());

        // Stops the Turn where it is and adds the current Turn points to the
        // Game points
        Score score = turnState.score().nextTurn();

        // Informs players about the winning team
        for (PlayerId player : PlayerId.ALL) {
            players.get(player).updateScore(score);
            players.get(player).setWinningTeam(
                    score.totalPoints(TEAM_1) >= Jass.WINNING_POINTS ? TEAM_1
                            : TEAM_2);
        }
    }

    private void shuffleDeck() {
        deck.clear();
        // Places Cards in order inside the deck
        for (int i = 0; i < CardSet.ALL_CARDS.size(); ++i)
            deck.add(CardSet.ALL_CARDS.get(i));
        // Shuffles the deck
        Collections.shuffle(deck, shuffleRng);

    }

    /*
     * Gives to each player their respective hand (Player_1 gets first 9 cards,
     * Player_2 gets next 9 cards...)
     */
    private void distributeCards() {
        hands.clear();
        for (int i = 0; i < players.size(); ++i) {
            PlayerId pId = PlayerId.ALL.get(i);
            hands.put(pId, CardSet.of(deck.subList(i * 9, (i + 1) * 9)));
        }

        /* This is just to print the hands of the players
         * for (PlayerId player : PlayerId.ALL)
         * System.out.println(playerNames.get(player) + " " +
         * hands.get(player));
         */

    }
}
