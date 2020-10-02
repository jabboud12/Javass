package ch.epfl.javass.gui;

import static javafx.application.Platform.runLater;

import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

/**
 * Class representing the Player handling a graphical interface in the game
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class GraphicalPlayerAdapter implements Player {

    private final TrickBean trickBean;
    private final ScoreBean scoreBean;
    private final HandBean handBean;

    private GraphicalPlayer graphicalPlayer;

    private ArrayBlockingQueue<Card> queue;

    /**
     * Constructs the Player and initializes its components (all Beans and the
     * queue for the Card to play)
     */
    public GraphicalPlayerAdapter() {
        this.trickBean = new TrickBean();
        this.scoreBean = new ScoreBean();
        this.handBean = new HandBean();
        queue = new ArrayBlockingQueue<>(1);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {

        // We initialize card to a random value
        Card card = null;

        handBean.setPlayableCards(state.trick().playableCards(hand));
        try {
            card = queue.take();
        } catch (InterruptedException e) {
            throw new Error(e);
        }

        handBean.setPlayableCards(CardSet.EMPTY);
        return card;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        graphicalPlayer = new GraphicalPlayer(ownId, playerNames, scoreBean,
                trickBean, handBean, queue);
        runLater(() -> {
            // The graphical interface is created when the Player is given the
            // Names of the Players in the game
            graphicalPlayer.createStage().show();
        });
    }

    @Override
    public void updateHand(CardSet newHand) {
        handBean.setHand(newHand);
    }

    @Override
    public void setTrump(Color trump) {
        trickBean.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        trickBean.setTrick(newTrick);
    }

    @Override
    public void updateScore(Score score) {
        for (TeamId team : TeamId.ALL) {
            scoreBean.setGamePoints(team, score.gamePoints(team));
            scoreBean.setTurnPoints(team, score.turnPoints(team));
            scoreBean.setTotalPoints(team, score.totalPoints(team));
        }
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        scoreBean.setWinningTeam(winningTeam);
    }
}
