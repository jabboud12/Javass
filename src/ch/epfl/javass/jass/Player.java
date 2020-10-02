
package ch.epfl.javass.jass;

import java.util.Map;

import ch.epfl.javass.jass.Card.Color;

public interface Player {

    Card cardToPlay(TurnState state, CardSet hand);

    /**
     * Sets the players at the beginning of the Turn
     * 
     * @param (ownId)
     *            the PlayerId corresponding to the player calling the method
     * @param (playerNames)
     *            the map containing all names associated to each PlayerId
     */
    default void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
    }

    /**
     * Returns the updated given hand corresponding to the player calling the
     * method
     * 
     * @param (newHand)
     *            the CardSet containing the player's hand
     */
    default void updateHand(CardSet newHand) {
    }

    /**
     * Sets the trump Color at the beginning of the Turn
     * 
     * @param (trump)
     *            the Color to use for the Turn's trump
     */
    default void setTrump(Color trump) {
    }

    /**
     * Returns the updated given Trick after the player calling the method plays
     * a Card
     * 
     * @param (newTrick)
     *            the Trick containing the played Card
     */
    default void updateTrick(Trick newTrick) {
    }

    /**
     * Returns the updated given Score of the Game
     * 
     * @param (score)
     *            the game's Score that was given
     */
    default void updateScore(Score score) {
    }

    /**
     * Sets the given TeamId as the winning TeamId
     * 
     * @param (winningTeam)
     *            the given winning TeamId
     */
    default void setWinningTeam(TeamId winningTeam) {
    }

}