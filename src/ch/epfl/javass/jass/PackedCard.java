
package ch.epfl.javass.jass;

import static ch.epfl.javass.bits.Bits32.extract;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Methods to manipulate and give information about a Card through its packed
 * version
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class PackedCard {

    private PackedCard() {
    }

    /**
     * Invalid version of a PackedCard
     */
    public static final int INVALID = 0b11_1111;

    /**
     * Card's Rank starting index in the packed bit string
     */
    private static final int RANK_START = 0;

    /**
     * Card's packed Rank's size in the packed bit string
     */
    private static final int RANK_SIZE = 4;

    /**
     * Card's Color starting index in the packed bit string
     */
    private static final int COLOR_START = RANK_START + RANK_SIZE;

    /**
     * Card's packed Color's size in the packed bit string
     */
    private static final int COLOR_SIZE = 2;

    /**
     * Checks if a Card is valid and returns a boolean depending whether it is
     * or not
     * 
     * @param (pkCard)
     *            the packed version of a Card to check
     * @return true if pkCard verifies the conditions and false otherwise
     */
    public static boolean isValid(int pkCard) {
        return (pkCard < INVALID
                && (extract(pkCard, RANK_START, RANK_SIZE)) < Card.Rank.COUNT
                && pkCard >= 0);
    }

    /**
     * Returns the packed version of a Card given its Color and Rank
     * 
     * @param (c)
     *            the Color of the Card.
     * @param (r)
     *            the rank of the Card.
     * @return pkCard the packed version of the Card
     */
    public static int pack(Color c, Rank r) {
        return Bits32.pack(r.ordinal(), RANK_SIZE, c.ordinal(), COLOR_SIZE);
    }

    /**
     * Returns the Color of a given Card in its packed version
     * 
     * @param (pkCard)
     *            the packed version of the card
     * @return the Color associated to the Card
     */
    public static Color color(int pkCard) {
        assert isValid(pkCard);
        return Color.ALL.get(extract(pkCard, COLOR_START, COLOR_SIZE));
    }

    /**
     * Returns the Rank of a given Card in its packed version
     * 
     * @param (pkCard)
     *            the packed version of the card
     * @return the Rank associated to the Card
     */
    public static Rank rank(int pkCard) {
        assert isValid(pkCard);
        return Rank.ALL.get(extract(pkCard, RANK_START, RANK_SIZE));
    }

    /**
     * Tests which of two given Cards is better than the other
     * 
     * @param (trump)
     *            the trump Color of the current game
     * @param (pkCardL)
     *            the packed version of the first Card considered
     * @param (pkCardR)
     *            the packed version of the second Card considered
     * @return true if CardL is better than CardR and false otherwise
     */
    public static boolean isBetter(Color trump, int pkCardL, int pkCardR) {
        assert isValid(pkCardL);
        assert isValid(pkCardR);

        if (color(pkCardL).equals(trump) && color(pkCardR) != trump)
            return true;

        if (color(pkCardR).equals(color(pkCardL))) {
            if (color(pkCardR) != trump)
                return rank(pkCardL).ordinal() > rank(pkCardR).ordinal();

            return rank(pkCardL).trumpOrdinal() > rank(pkCardR).trumpOrdinal();
        }

        return false;
    }

    /**
     * Computes the number of points allocated to each Card
     * 
     * @param (trump)
     *            the trump Color of the current game
     * @param (pkCard)
     *            the packed version of the Card considered
     * @return the points allocated to the given Card
     */
    public static int points(Color trump, int pkCard) {
        assert isValid(pkCard);
        // Checks if Card's Color is the trump of the Turn
        boolean colorCondition = color(pkCard).equals(trump);

        switch (rank(pkCard).ordinal()) {
        case (3):// Nine
            return colorCondition ? 14 : 0;
        case (4):// Ten
            return 10;
        case (5):// Jack
            return colorCondition ? 20 : 2;
        case (6):// Queen
            return 3;
        case (7):// King
            return 4;
        case (8):// Ace
            return 11;
        default: // These cases don't give points
            return 0;
        }
    }

    /**
     * Overloading of Object.toString()
     * 
     * @param (pkCard)
     *            the packed version of the Card to print
     * @return the Color and Rank of the Card
     */
    public static String toString(int pkCard) {
        assert isValid(pkCard);
        return color(pkCard).toString() + rank(pkCard).toString();
    }
}
