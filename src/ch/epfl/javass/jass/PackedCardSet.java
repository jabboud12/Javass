package ch.epfl.javass.jass;

import static ch.epfl.javass.bits.Bits64.mask;
import static ch.epfl.javass.jass.Jass.HAND_SIZE;

import java.util.StringJoiner;

import ch.epfl.javass.jass.Card.Color;

/**
 * Methods to manipulate and give information about a CardSet through its packed
 * version
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class PackedCardSet {
    
    /**
     * Empty PackedCardSet
     */
    public static final long EMPTY = 0L;
    
    /**
     * Number of bits reserved for each Color in the PackedCardSet (4 colors *
     * 16 bits per color == 64 == Long.SIZE)
     */
    private static final int SUIT_SIZE = 16;

    /**
     * Maximum number of consecutive Cards in a PackedCardSet (the disposition
     * of the Cards in a PackedCardSet don't allow for 10 consecutive bits in
     * the packed version to be 1
     */
    private static final long VALID_MAX = 0b1_1111_1111;

    /**
     * PackedCardSet containing all 36 Cards
     */
    public static final long ALL_CARDS = mask(0, HAND_SIZE)
            | mask(SUIT_SIZE, HAND_SIZE) | mask(2 * SUIT_SIZE, HAND_SIZE)
            | mask(3 * SUIT_SIZE, HAND_SIZE);// HAND_SIZE is a constant in
                                             // interface Jass

    // This table is filled with a private method whose behavior is explained
    // below
    private static final long[][] TRUMP_ABOVE_TABLE = fillTrumpAboveTable();

    // This table contains 4 PackedCardSets, each of them representing all cards
    // of a certain color
    private static final long[] SUBSET_OF_COLOR_TABLE = { VALID_MAX,
            VALID_MAX << SUIT_SIZE, VALID_MAX << (2 * SUIT_SIZE),
            VALID_MAX << (3 * SUIT_SIZE) };

    private PackedCardSet() {
    }

    /**
     * Checks if a CardSet is valid and returns a boolean depending whether it
     * is or not
     * 
     * @param (pkCardSet)
     *            the packed version of a CardSet to check
     * @return true if pkCardSet verifies the conditions and false otherwise
     */
    public static boolean isValid(long pkCardSet) {
        return (~ALL_CARDS & pkCardSet) == EMPTY;
    }

    /**
     * Returns the PackedCardSet containing all cards better than the given
     * Card, with the trump priorities
     * 
     * @param (pkCard)
     *            the packed version of the Card to compare
     * @return the packed version of the subset of better cards
     */
    public static long trumpAbove(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return TRUMP_ABOVE_TABLE[PackedCard.color(pkCard).ordinal()][PackedCard
                .rank(pkCard).trumpOrdinal()];
    }

    /**
     * Returns a PackedCardSet containing a single Card given its packed version
     * 
     * @param (pkCard)
     *            the packed version of the Card to extract
     * @return the packed version of the singleton
     */
    public static long singleton(int pkCard) {
        assert PackedCard.isValid(pkCard);
        return 1L << pkCard;
    }

    /**
     * Returns whether the CardSet does not contain any element given its packed
     * version
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @return true if the CardSet is empty and false otherwise
     */
    public static boolean isEmpty(long pkCardSet) {
        assert isValid(pkCardSet);
        return pkCardSet == EMPTY;
    }

    /**
     * Returns the number of cards in a CardSet given its packed version
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @return size of the CardSet
     */
    public static int size(long pkCardSet) {
        assert isValid(pkCardSet);
        return Long.bitCount(pkCardSet);
    }

    /**
     * Gets an element from the CardSet depending on an index
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @param (index)
     *            the position of the wanted element between existing elements
     *            in the CardSet
     * @return the packed version of the Card at the given index
     */
    public static int get(long pkCardSet, int index) {
        assert isValid(pkCardSet);

        long l = pkCardSet;
        for (int i = 0; i < index; ++i)
            l &= ~Long.lowestOneBit(l);

        return Long.numberOfTrailingZeros(l);
    }

    /**
     * Returns a updated CardSet containing a given Card if it was not in the
     * set
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @param (pkCard)
     *            the packed version of the Card
     * @return packed version of the computed CardSet
     */
    public static long add(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);

        return pkCardSet | singleton(pkCard);
    }

    /**
     * Returns an updated CardSet not containing a given Card if it was in the
     * set
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @param (pkCard)
     *            the packed version of the Card
     * @return (Long) packed version of the computed CardSet
     */
    public static long remove(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);

        return pkCardSet & ~singleton(pkCard) & ALL_CARDS;
    }

    /**
     * Checks if a CardSet contains a certain Card given their packed versions
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @param (pkCard)
     *            the packed version of the Card
     * @return true if the Card is inside the CardSet and false otherwise
     */
    public static boolean contains(long pkCardSet, int pkCard) {
        assert isValid(pkCardSet);
        assert PackedCard.isValid(pkCard);

        return pkCardSet == add(pkCardSet, pkCard);
    }

    /**
     * Returns the complement of a CardSet given its packed version
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @return packed version of the computed CardSet
     */
    public static long complement(long pkCardSet) {
        assert isValid(pkCardSet);

        return ~pkCardSet & ALL_CARDS;
    }

    /**
     * Returns the union of two card sets given their respective packed versions
     * 
     * @param (pkCardSet1)
     *            the packed version of the first CardSet
     * @param (pkCardSet2)
     *            the packed version of the second CardSet
     * @return packed version of the computed CardSet
     */
    public static long union(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);

        return pkCardSet1 | pkCardSet2;
    }

    /**
     * Returns the intersection of two card sets given their respective packed
     * versions
     * 
     * @param (pkCardSet1)
     *            the packed version of the first CardSet
     * @param (pkCardSet2)
     *            the packed version of the second CardSet
     * @return packed version of the computed CardSet
     */
    public static long intersection(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);

        return pkCardSet1 & pkCardSet2;
    }

    /**
     * Returns the difference between two card sets given their respective
     * packed versions
     * 
     * @param (pkCardSet1)
     *            the packed version of the first CardSet
     * @param (pkCardSet2)
     *            the packed version of the second CardSet
     * @return packed version of the computed CardSet
     */
    public static long difference(long pkCardSet1, long pkCardSet2) {
        assert isValid(pkCardSet1);
        assert isValid(pkCardSet2);

        return ~pkCardSet2 & pkCardSet1;
    }

    /**
     * Returns a subset of the CardSet containing only one given Color, given
     * its packed version
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @param (color)
     *            the color that we want to isolate
     * @return a packed version of the new CardSet containing only the given
     *         color
     */
    public static long subsetOfColor(long pkCardSet, Card.Color color) {
        assert isValid(pkCardSet);
        return pkCardSet & SUBSET_OF_COLOR_TABLE[color.ordinal()];
    }

    /**
     * Overloading of Object.toString()
     * 
     * @param (pkCardSet)
     *            the packed version of the CardSet
     * @return prints all components of the given CardSet
     */
    public static String toString(long pkCardSet) {
        assert isValid(pkCardSet);

        StringJoiner j = new StringJoiner(",", "{", "}");

        for (int i = 0; i < Long.bitCount(pkCardSet); ++i)
            j.add(PackedCard.toString(
                    PackedCard.pack(PackedCard.color(get(pkCardSet, i)),
                            PackedCard.rank(get(pkCardSet, i)))));

        return j.toString();
    }

    /*
     * This method is used to fill the TRUMP_ABOVE_TABLE, which gives for each
     * Card, all the other Cards that are better than it in a PackedCardSet (e.g
     * trumpAboveTable[0][0] gives the PackedCardSet representing all Cards
     * better than the 6 of SPADE, giving than the trump is SPADE)
     */
    private static long[][] fillTrumpAboveTable() {
        long[][] table = new long[Color.COUNT][Card.Rank.COUNT];
        for (int i = 0; i < Color.ALL.size(); ++i) {
            for (int j = 0; j < Card.Rank.ALL.size(); ++j) {
                for (int k = j + 1; k < table[0].length; ++k) {
                    table[i][j] |= singleton(PackedCard.pack(
                            Card.Color.ALL.get(i), Card.Rank.TRUMP_ALL[k]));
                }
            }
        }
        return table;
    }
}
