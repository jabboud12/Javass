
package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.Preconditions;

/**
 * Contains the methods necessary for a PacedPlayer to wait a certain amount of
 * time before playing a Card
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class PacedPlayer implements Player {

    private final Player underlyingPlayer;
    private final double minTime;

    /**
     * Public constructor for a PacedPlayer
     * 
     * @param (underlyingPlayer)
     *            the Player who gives its basic behaviors to this Player
     * @param (minTime)
     *            the minimum time (in seconds) required for the Player to play
     *            a Card
     */
    public PacedPlayer(Player underlyingPlayer, double minTime) {
        Preconditions.checkArgument(minTime >= 0);
        this.underlyingPlayer = underlyingPlayer;
        this.minTime = minTime * 1000;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        long time = System.currentTimeMillis();
        Card temp = underlyingPlayer.cardToPlay(state, hand);
        long timeAfter = System.currentTimeMillis();

        // Checks if the minimum time has passed
        if (timeAfter - time < (long) minTime) {
            try {
                Thread.sleep((long) minTime - (timeAfter - time));
            } catch (InterruptedException e) {}
        }

        return temp;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        underlyingPlayer.setPlayers(ownId, playerNames);
    }

    @Override
    public void updateHand(CardSet newHand) {
        underlyingPlayer.updateHand(newHand);
    }

    @Override
    public void setTrump(Card.Color trump) {
        underlyingPlayer.setTrump(trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        underlyingPlayer.updateTrick(newTrick);
    }

    @Override
    public void updateScore(Score score) {
        underlyingPlayer.updateScore(score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        underlyingPlayer.setWinningTeam(winningTeam);
    }
}
