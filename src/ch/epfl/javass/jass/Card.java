package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static ch.epfl.javass.Preconditions.checkArgument;;

/**
 * Class containing all methods and information (Enums Rank and Color) about a
 * Card
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class Card {

    private final int PK_CARD;

    private Card(int pkCard) {
        // checkArgument throws an IllegalArgumentException in all methods below
        // that call the private constructor if pkCard is not valid
        checkArgument(PackedCard.isValid(pkCard));
        PK_CARD = pkCard;
    }

    /**
     * Constructs an instance of Card given its Color and Rank
     *
     * @param (c)
     *            Color of the card
     * @param (r)
     *            Rank of the card
     * @return corresponding to the Color and Rank given
     */
    public static Card of(Color c, Rank r) {
        return new Card(PackedCard.pack(c, r));
    }

    /**
     * Constructs an instance of Card given its packed version
     *
     * @param (packed)
     *            packed version of the card
     * @return corresponding to packed
     */
    public static Card ofPacked(int packed) {
        return new Card(packed);
    }

    /**
     * Returns packed version of the card
     * 
     * @return packed version of the card
     */
    public int packed() {
        return PK_CARD;
    }

    /**
     * Returns the Color of the card
     * 
     * @return color of the card
     */
    public Color color() {
        return PackedCard.color(PK_CARD);
    }

    /**
     * Returns the Rank of the card
     * 
     * @return rank of the card
     */
    public Rank rank() {
        return PackedCard.rank(PK_CARD);
    }

    /**
     * Compares two cards
     *
     * @param (trump)
     *            the trump Color of the current game
     * @param (that)
     *            card to compare with
     * @return true if the Card in question is stronger than that
     */
    public boolean isBetter(Color trump, Card that) {
        return PackedCard.isBetter(trump, this.PK_CARD, that.packed());
    }

    /**
     * Returns the points value of the card
     *
     * @param (trump)
     *            the trump Color of the current game
     * @return value of the card in the game
     */
    public int points(Color trump) {
        return PackedCard.points(trump, PK_CARD);
    }

    @Override
    public boolean equals(Object thatO) {
        if (thatO != null)
            if (thatO instanceof Card)
                return ((Card) thatO).packed() == this.packed();

        return false;
    }

    @Override
    public int hashCode() {
        return PK_CARD;
    }

    @Override
    public String toString() {
        return PackedCard.toString(PK_CARD);
    }

    /**
     * Enumerator for the Color of the card
     */
    public enum Color {
        SPADE("\u2660"), HEART("\u2661"), DIAMOND("\u2662"), CLUB("\u2663");

        private final String color;

        private Color(String color) {
            this.color = color;
        }

        /**
         * List containing all Colors by order
         */
        public static final List<Color> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        
        /**
         * Number of Colors needed for a game
         */
        public static final int COUNT = ALL.size();

        @Override
        public String toString() {
            return color;
        }
    }

    /**
     * Enumerator for the Rank of the card
     */
    public enum Rank {
        SIX("6"), SEVEN("7"), EIGHT("8"), NINE("9"), TEN("10"), JACK(
                "J"), QUEEN("Q"), KING("K"), ACE("A");

        private final String rank;

        private Rank(String rank) {
            this.rank = rank;
        }

        /**
         * List containing all Ranks by order (from 6 to ace)
         */
        public static final List<Rank> ALL = Collections
                .unmodifiableList(Arrays.asList(values()));
        
        /**
         * Number of Ranks needed for a game (6 to ace included)
         */
        public static final int COUNT = ALL.size();

        /**
         * Same List as ALL but the order depends on the trump hierarchy
         */
        public static final Rank[] TRUMP_ALL = { SIX, SEVEN, EIGHT, TEN, QUEEN,
                KING, ACE, NINE, JACK };

        /**
         * Returns the position (between 0 and 8) of the trump card in the
         * correct order
         * 
         * @return the position (between 0 and 8) of the trump card in the
         *         correct order
         */
        public int trumpOrdinal() {
            switch (rank) {
            case ("10"):
                return 3;
            case ("Q"):
                return 4;
            case ("K"):
                return 5;
            case ("A"):
                return 6;
            case ("9"):
                return 7;
            case ("J"):
                return 8;
            default:
                return this.ordinal();
            }
        }

        @Override
        public String toString() {
            return rank;
        }
    }

}
