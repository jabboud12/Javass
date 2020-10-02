
package ch.epfl.javass.jass;

import static ch.epfl.javass.bits.Bits32.extract;
import static ch.epfl.javass.bits.Bits32.pack;
import static ch.epfl.javass.jass.PackedCardSet.subsetOfColor;
import static ch.epfl.javass.jass.Jass.TRICKS_PER_TURN;

import java.util.ArrayList;
import java.util.List;
import java.util.StringJoiner;

import ch.epfl.javass.jass.Card.Color;
import ch.epfl.javass.jass.Card.Rank;

/**
 * Methods to manipulate and give information about a Trick through its packed
 * version
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class PackedTrick {

    private PackedTrick() {
    }

    /**
     * Invalid version of the PackedTrick
     */
    public static final int INVALID = 0b11_11_1111_111111_111111_111111_111111;

    /**
     * PackedTrick where no card has been played yet
     */
    private static final int EMPTY = 0b111111_111111_111111_111111;

    /**
     * Allocated bits for one Card in the PackedTrick
     */
    private static final int CARD_SIZE = 6;

    /**
     * Maximum number of Cards per Trick
     */
    private static final int CARDS_PER_TRICK = 4;

    /**
     * Index of the first bit for the first Card
     */
    private static final int CARD_0_START = 0;

    /**
     * Index of the first bit for the second Card (bit 6)
     */
    private static final int CARD_1_START = CARD_0_START + CARD_SIZE;

    /**
     * Index of the first bit for the third Card (bit 12)
     */
    private static final int CARD_2_START = CARD_1_START + CARD_SIZE;

    /**
     * Index of the first bit for the fourth Card (bit 18)
     */
    private static final int CARD_3_START = CARD_2_START + CARD_SIZE;

    /**
     * Index of the first bit for the Trick's index in the Turn (bit 24)
     */
    private static final int INDEX_START = CARD_3_START + CARD_SIZE;

    /**
     * Allocated bits for the trick's index in the PackedTrick
     */
    private static final int INDEX_SIZE = 4;

    /**
     * Highest possible value for an index in a Turn (There are 9 tricks per
     * turn and we take 0 as the first trick)
     */
    private static final int MAX_INDEX = TRICKS_PER_TURN - 1; // TRICKS_PER_TURN
    // is a contant in
    // interface Jass

    /**
     * Index of the first bit for Player1 (first player to play a card) in the
     * Trick (bit 28)
     */
    private static final int PLAYER_1_START = INDEX_START + INDEX_SIZE;

    /**
     * Allocated bits for Player1 in the PackedTrick
     */
    private static final int PLAYER_1_SIZE = 2;

    /**
     * Index of the first bit for trump Color in the Trick (bit 30)
     */
    private static final int TRUMP_START = PLAYER_1_START + PLAYER_1_SIZE;

    /**
     * Allocated bits for trump Color in the PackedTrick
     */
    private static final int TRUMP_SIZE = 2;

    /**
     * Checks if a Trick is valid and returns a boolean depending whether it is
     * or not
     * 
     * @param (pkTrick)
     *            the packed version of a Trick to check
     * @return true if pkTrick verifies the conditions and false otherwise
     */
    public static boolean isValid(int pkTrick) {
        int card0 = extract(pkTrick, CARD_0_START, CARD_SIZE);
        int card1 = extract(pkTrick, CARD_1_START, CARD_SIZE);
        int card2 = extract(pkTrick, CARD_2_START, CARD_SIZE);
        int card3 = extract(pkTrick, CARD_3_START, CARD_SIZE);
        int index = extract(pkTrick, INDEX_START, INDEX_SIZE);

        return index <= MAX_INDEX && index >= 0 && ((PackedCard.isValid(card0)
                && PackedCard.isValid(card1) && PackedCard.isValid(card2))
                || (PackedCard.isValid(card0) && !PackedCard.isValid(card2)
                        && !PackedCard.isValid(card3))
                || !(PackedCard.isValid(card1) || PackedCard.isValid(card2)
                        || PackedCard.isValid(card3)));
    }

    /**
     * Returns the empty trick, except for the packed player1 and trump color at
     * their position in the bit string
     * 
     * @param (trump)
     *            the Color of the trump in the Trick
     * @param (firstPlayer)
     *            the first player to draw a Card
     * @return the packed version of the wanted Trick
     */
    public static int firstEmpty(Color trump, PlayerId firstPlayer) {
        return pack(EMPTY, PLAYER_1_START, firstPlayer.ordinal(), PLAYER_1_SIZE,
                trump.ordinal(), TRUMP_SIZE);
    }

    /**
     * Returns the empty trick, with an incremented index by 1, the winning
     * player, and an unchanged trump
     * 
     * @param (pkTrick)
     *            the packed version of the first Trick
     * @return the packed version of the next empty updated Trick
     */
    public static int nextEmpty(int pkTrick) {
        assert isValid(pkTrick);

        if (index(pkTrick) == MAX_INDEX)
            return INVALID;

        return pack(
                pack(EMPTY, INDEX_START, index(pkTrick) + 1, INDEX_SIZE,
                        winningPlayer(pkTrick).ordinal(), PLAYER_1_SIZE),
                TRUMP_START, trump(pkTrick).ordinal(), TRUMP_SIZE);
    }

    /**
     * Checks if a Trick in its packed version represents the last Trick of the
     * turn
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return true if the trick is the last of the game and false otherwise
     */
    public static boolean isLast(int pkTrick) {
        assert isValid(pkTrick);
        return index(pkTrick) == MAX_INDEX;
    }

    /**
     * Checks if a Trick in its packed version does not contain any Card.
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return true if no Card has been drawn yet in the trick and false
     *         otherwise
     */
    public static boolean isEmpty(int pkTrick) {
        assert isValid(pkTrick);
        // Check if the first card is not valid
        return card(pkTrick, 0) == PackedCard.INVALID;
    }

    /**
     * Checks if a Trick in its packed version contains all 4 cards.
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return true if all cards have been drawn in the trick and false
     *         otherwise
     */
    public static boolean isFull(int pkTrick) {
        assert isValid(pkTrick);
        // Check if the last card is valid
        return card(pkTrick, 3) != PackedCard.INVALID;
    }

    /**
     * Returns the number of cards contained in a Trick given its packed version
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return the number of cards in the Trick
     */
    public static int size(int pkTrick) {
        assert isValid(pkTrick);

        for (int i = 0; i <= CARDS_PER_TRICK; ++i)
            if (card(pkTrick, i) == PackedCard.INVALID)
                return i;

        // The case where the Trick is full
        return CARDS_PER_TRICK;
    }

    /**
     * Returns the Color of the trump in the current Trick given its packed
     * version
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return the Color of the trump in the Trick
     */
    public static Color trump(int pkTrick) {
        assert isValid(pkTrick);
        return Color.ALL.get(extract(pkTrick, TRUMP_START, TRUMP_SIZE));
    }

    /**
     * Return the Player at the given index, with a given Player1
     * 
     * @param (pkTrick)
     *            the packed version of the Trick containing the identity of the
     *            PLayer1
     * @param (index)
     *            the index of the player we want to get, depending on Player1
     * @return the Player at the given index
     */
    public static PlayerId player(int pkTrick, int index) {
        assert isValid(pkTrick);

        return PlayerId.ALL.get(
                (extract(pkTrick, PLAYER_1_START, PLAYER_1_SIZE) + index) % 4);

    }

    /**
     * Returns the index of the current Trick, given its packed version
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return the index of the Trick in the turn
     */
    public static int index(int pkTrick) {
        assert isValid(pkTrick);

        return extract(pkTrick, INDEX_START, INDEX_SIZE);
    }

    /**
     * Returns the n-th Card of a trick given an index n
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @param (index)
     *            the index of the Card we want to get
     * @return the Card at the given index
     */
    public static int card(int pkTrick, int index) {
        assert isValid(pkTrick);

        return extract(pkTrick, CARD_SIZE * index, CARD_SIZE);
    }

    /**
     * Returns a packed version of the non-full Trick to which we added a
     * certain Card
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @param (pkCard)
     *            the packed version of the Card
     * @return the packed version of the new updated Trick
     */
    public static int withAddedCard(int pkTrick, int pkCard) {
        assert isValid(pkTrick);
        assert PackedCard.isValid(pkCard);

        return pkTrick - (PackedCard.INVALID << (size(pkTrick) * CARD_SIZE))
                + (pkCard << (size(pkTrick) * CARD_SIZE));
    }

    /**
     * Returns the Color of the first played Card of the Trick
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return the Color of the first played Card
     */
    public static Color baseColor(int pkTrick) {
        assert isValid(pkTrick);

        return PackedCard.color(extract(pkTrick, CARD_0_START, CARD_SIZE));
    }

    /**
     * Returns all the possible playable cards depending on the current Trick
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @param (pkHand)
     *            the packed version of the player's hand
     * @return the CardSet of all the playable cards that the player possesses
     */
    public static long playableCards(int pkTrick, long pkHand) {
        assert isValid(pkTrick);
        assert PackedCardSet.isValid(pkHand);
        long temp = pkHand;

        /// The case where no card has been played
        if (isEmpty(pkTrick))
            return pkHand;

        /// The cases where only one card has been played
        if (size(pkTrick) == 1) {
            // Check if the hand contains cards from the base Color
            if (subsetOfColor(pkHand, baseColor(pkTrick)) == 0)
                return pkHand;

            // Check if base Color is trump
            if (baseColor(pkTrick).equals(trump(pkTrick)))
                // Check if the hand only has the jack as a trump card
                if (subsetOfColor(pkHand, trump(pkTrick)) == PackedCardSet
                        .singleton(PackedCard.pack(trump(pkTrick), Rank.JACK)))
                    return pkHand;

            return PackedCardSet.union(subsetOfColor(pkHand, trump(pkTrick)),
                    subsetOfColor(pkHand, baseColor(pkTrick)));
        }

        /// The cases where 2 cards have been played
        if (size(pkTrick) == 2) {
            // Check if base Color is trump
            if (baseColor(pkTrick).equals(trump(pkTrick))) {
                // Check if the hand only has the jack as a trump card or does
                // not have any trump Card
                if (subsetOfColor(pkHand, trump(pkTrick)) == PackedCardSet
                        .singleton(PackedCard.pack(trump(pkTrick), Rank.JACK))
                        || subsetOfColor(pkHand, trump(pkTrick)) == 0)
                    return pkHand;
                return subsetOfColor(pkHand, trump(pkTrick));
            }

            // Check if base Color is not trump
            if (subsetOfColor(pkHand, baseColor(pkTrick)) == 0) {
                // Check if the second Card's Color is trump
                if (PackedCard.color(card(pkTrick, 1)).equals(trump(pkTrick))) {
                    // Check if hand only has trump cards
                    if (subsetOfColor(pkHand, trump(pkTrick)) == pkHand)
                        // Check if hand only has trump cards lower than the
                        // second Card
                        if ((subsetOfColor(pkHand, trump(pkTrick))
                                & PackedCardSet
                                        .trumpAbove(card(pkTrick, 1))) == 0)
                            return pkHand;
                    pkHand -= subsetOfColor(temp, trump(pkTrick));
                    return PackedCardSet.union(pkHand,
                            (subsetOfColor(temp, trump(pkTrick)) & PackedCardSet
                                    .trumpAbove(card(pkTrick, 1))));
                }
                return pkHand;
            }
            if (PackedCard.color(card(pkTrick, 1)).equals(trump(pkTrick))) {
                pkHand -= subsetOfColor(temp, trump(pkTrick));
                pkHand = PackedCardSet.union(pkHand,
                        (subsetOfColor(temp, trump(pkTrick))
                                & PackedCardSet.trumpAbove(card(pkTrick, 1))));
            }
            return PackedCardSet.union(subsetOfColor(pkHand, trump(pkTrick)),
                    subsetOfColor(pkHand, baseColor(pkTrick)));
        }

        /// The cases where 3 cards have been played
        // Check if base Color is trump
        if (baseColor(pkTrick).equals(trump(pkTrick))) {
            // Check if hand only has jack as trump or does not have any trump
            // Card
            if (subsetOfColor(pkHand, trump(pkTrick)) == PackedCardSet
                    .singleton(PackedCard.pack(trump(pkTrick), Rank.JACK))
                    || subsetOfColor(pkHand, trump(pkTrick)) == 0)
                return pkHand;
            return subsetOfColor(pkHand, trump(pkTrick));
        }

        // Check if base Color is not trump
        if (subsetOfColor(pkHand, baseColor(pkTrick)) == 0) {
            // Check if both second and third cards' Color is trump
            if (PackedCard.color(card(pkTrick, 1)).equals(trump(pkTrick))
                    && PackedCard.color(card(pkTrick, 2))
                            .equals(trump(pkTrick))) {
                int cardIndex = 0;
                // Check which Card between second and third Card is better
                if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, 1),
                        card(pkTrick, 2))) {
                    cardIndex = 1;
                } else {
                    cardIndex = 2;
                }
                // Check if hand only has trump cards
                if (subsetOfColor(pkHand, trump(pkTrick)) == pkHand) {
                    // Check if hand only has trump cards lower than the played
                    // trump Card
                    if ((subsetOfColor(pkHand, trump(pkTrick)) & PackedCardSet
                            .trumpAbove(card(pkTrick, cardIndex))) == 0) {
                        return pkHand;
                    }
                }
                pkHand -= subsetOfColor(pkHand, trump(pkTrick));
                return PackedCardSet.union(pkHand,
                        (subsetOfColor(temp, trump(pkTrick)) & PackedCardSet
                                .trumpAbove(card(pkTrick, cardIndex))));
            }
            // Check if only second Card's Color is trump
            if (PackedCard.color(card(pkTrick, 1)).equals(trump(pkTrick))) {
                if (subsetOfColor(pkHand, trump(pkTrick)) != pkHand) {
                    pkHand -= subsetOfColor(pkHand, trump(pkTrick));
                    return PackedCardSet.union(pkHand,
                            (subsetOfColor(temp, trump(pkTrick)) & PackedCardSet
                                    .trumpAbove(card(pkTrick, 1))));
                }

            }
            // Check if only third Card's Color is trump
            if (PackedCard.color(card(pkTrick, 2)).equals(trump(pkTrick))) {
                if (subsetOfColor(pkHand, trump(pkTrick)) != pkHand) {
                    pkHand -= subsetOfColor(pkHand, trump(pkTrick));
                    return PackedCardSet.union(pkHand,
                            (subsetOfColor(temp, trump(pkTrick)) & PackedCardSet
                                    .trumpAbove(card(pkTrick, 2))));
                }
            }
            return pkHand;
        }

        int cardIndex = 0;
        // Check which Card between second and third Card is better
        if (PackedCard.isBetter(trump(pkTrick), card(pkTrick, 1),
                card(pkTrick, 2)))
            cardIndex = 1;
        else
            cardIndex = 2;
        // Check if the better Card's Color is trump
        if (PackedCard.color(card(pkTrick, cardIndex)).equals(trump(pkTrick))) {
            pkHand -= subsetOfColor(temp, trump(pkTrick));
            pkHand = PackedCardSet.union(pkHand,
                    (subsetOfColor(temp, trump(pkTrick)) & PackedCardSet
                            .trumpAbove(card(pkTrick, cardIndex))));
            return PackedCardSet.union(subsetOfColor(pkHand, trump(pkTrick)),
                    subsetOfColor(pkHand, baseColor(pkTrick)));

        }
        return PackedCardSet.union(subsetOfColor(pkHand, trump(pkTrick)),
                subsetOfColor(pkHand, baseColor(pkTrick)));
    }

    /**
     * Returns the number of points obtained during a Trick considering the 5
     * bonus points at the end of the turn
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return the number of points obtained
     */
    public static int points(int pkTrick) {
        assert isValid(pkTrick);
        int points = 0;

        if (isLast(pkTrick))
            points += Jass.LAST_TRICK_ADDITIONAL_POINTS;

        for (int i = 0; i < CARDS_PER_TRICK; ++i)
            points += PackedCard.points(trump(pkTrick), card(pkTrick, i));

        return points;
    }

    /**
     * Returns the winning Player of a Trick given its packed version
     * 
     * @param (pkTrick)
     *            the packed version of the Trick
     * @return the Player whose Card is the best
     */
    public static PlayerId winningPlayer(int pkTrick) {
        assert isValid(pkTrick);
        assert (!isEmpty(pkTrick));

        List<Integer> cards = new ArrayList<>();

        for (int i = 0; i < PlayerId.COUNT; ++i)
            if (PackedCard.isValid(card(pkTrick, i)))
                cards.add(card(pkTrick, i));

        int winningCard = cards.get(0);
        int index = 0;

        // Computes the winning card of the Trick and its index
        for (int i = 1; i < cards.size(); ++i)
            if (PackedCard.isBetter(trump(pkTrick), cards.get(i),
                    winningCard)) {
                index = i;
                winningCard = cards.get(i);
            }

        cards.clear();
        return PlayerId.ALL.get((index + player(pkTrick, 0).ordinal()) % 4);

    }

    /**
     * Hiding of Object.toString()
     * 
     * @param (pkTrick)
     *            the packed version of the Trick to print
     * @return the cards composing the current given trick even if it's not full
     */
    public static String toString(int pkTrick) {
        assert isValid(pkTrick);
        StringJoiner j = new StringJoiner(",");

        for (int i = 0; i < size(pkTrick); ++i)
            j.add(PackedCard.toString(card(pkTrick, i)));

        return j.toString();
    }
}
