
package ch.epfl.javass.jass;

/**
 * Contains the main constants of the game
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public interface Jass {

    /**
     * The starting number of Cards in a hand in the game
     */
    public static final int HAND_SIZE = 9;

    /**
     * The number of tricks in every normal Turn in the game (sometimes the last
     * Turn is stopped in the middle when a Team has reached the score limit)
     */
    public static final int TRICKS_PER_TURN = 9;

    /**
     * The number of points required for a Team to win the game
     */
    public static final int WINNING_POINTS = 1000;

    /**
     * Points added to a Team when they get all 157 points in a Turn
     */
    public static final int MATCH_ADDITIONAL_POINTS = 100;

    /**
     * Points added to the Team which wins the last Trick of a Turn
     */
    public static final int LAST_TRICK_ADDITIONAL_POINTS = 5;

}
