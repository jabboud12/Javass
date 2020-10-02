
package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumerator for players inside a game of Jass.
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public enum PlayerId {
    PLAYER_1, PLAYER_2, PLAYER_3, PLAYER_4;

    /**
     * List containing all players in order (1 to 4)
     */
    public static final List<PlayerId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    
    /**
     * Number of players in a game
     */
    public static final int COUNT = ALL.size();

    /**
     * Returns the player's team
     * 
     * @return the team assigned to the player
     */
    public TeamId team() {
        return this.equals(PLAYER_1) || this.equals(PLAYER_3) ? TeamId.TEAM_1
                : TeamId.TEAM_2;
    }

    /**
     * Returns the next n-th player in order
     * 
     * @param (n)
     *            the difference between this player and the player we want to
     *            get (modulo 4)
     * @return the Player that follows in order
     */
    public PlayerId next(int n) {
        return ALL.get((this.ordinal() + n) % COUNT);
    }

}
