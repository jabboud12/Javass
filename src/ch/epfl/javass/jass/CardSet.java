
package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.checkArgument;
import static ch.epfl.javass.Preconditions.checkIndex;

import java.util.List;

/**
 * Represents the Set of Cards of a game of Jass
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class CardSet {

    /**
     * CardSet representing the Empty CardSet
     */
    public final static CardSet EMPTY = new CardSet(PackedCardSet.EMPTY);

    /**
     * CardSet representing the full CardSet (all 36 Cards)
     */
    public final static CardSet ALL_CARDS = new CardSet(
            PackedCardSet.ALL_CARDS);

    private long packedCardSet;

    private CardSet(long pkCardSet) {
        // checkArgument throws an IllegalArgumentException in all methods below
        // that call the private constructor if pkCardSet is not valid
        checkArgument(PackedCardSet.isValid(pkCardSet));
        packedCardSet = pkCardSet;
    }

    /**
     * Constructs an instance of CardSet given its elements
     * 
     * @param (cards)
     *            the list containing all cards to add in the CardSet
     * @return corresponding to the List of cards given
     */
    public static CardSet of(List<Card> cards) {
        long pkCardSet = PackedCardSet.EMPTY;
        for (Card card : cards) {
            if (card != null)
                pkCardSet = PackedCardSet.add(pkCardSet, card.packed());
        }

        return new CardSet(pkCardSet);
    }

    /**
     * Constructs an instance of CardSet given its packed version
     * 
     * @param (packed)
     *            the packed version of the CardSet
     * @return corresponding to the packedCardSet given
     */
    public static CardSet ofPacked(long packed) {
        return new CardSet(packed);
    }

    /**
     * Returns packed version of the CardSet
     * 
     * @return packed version of the CardSet
     */
    public long packed() {
        return packedCardSet;
    }

    /**
     * Returns whether the CardSet does not contain any element
     * 
     * @return true if the CardSet is empty and false otherwise
     */
    public boolean isEmpty() {
        return PackedCardSet.isEmpty(packedCardSet);
    }

    /**
     * Returns the number of cards in the CardSet considered
     * 
     * @return size of the CardSet
     */
    public int size() {
        return PackedCardSet.size(packedCardSet);
    }

    /**
     * Gets an element from the CardSet depending on an index
     * 
     * @param (index)
     *            the position of the wanted element between existing elements
     *            in the CardSet
     * @throws IndexOutOfBoundsException
     *             if the index given does not respect the CardSet's size
     * @return the Card positioned at the given index
     */
    public Card get(int index) {
        checkIndex(index, size());
        return Card.ofPacked(PackedCardSet.get(packedCardSet, index));
    }

    /**
     * Returns an updated CardSet containing a given Card if it was not in the
     * set
     * 
     * @param (card)
     *            the Card to add to the CardSet
     * @return the new CardSet after the addition
     */
    public CardSet add(Card card) {
        return new CardSet(PackedCardSet.add(packedCardSet, card.packed()));
    }

    /**
     * Returns an updated CardSet not containing a given Card if it was in the
     * set
     * 
     * @param (card)
     *            the Card to remove from the CardSet
     * @return the new CardSet after the removal
     */
    public CardSet remove(Card card) {
        return new CardSet(PackedCardSet.remove(packedCardSet, card.packed()));
    }

    /**
     * Checks if a Card is part of the CardSet
     * 
     * @param (card)
     *            the Card to conduct the test on
     * @return true if the CardSet contains the card and false otherwise
     */
    public boolean contains(Card card) {
        return PackedCardSet.contains(packedCardSet, card.packed());
    }

    /**
     * Returns the complement of the cardSet
     * 
     * @return the complement of the CardSet
     */
    public CardSet complement() {
        return new CardSet(PackedCardSet.complement(packedCardSet));
    }

    /**
     * Returns the union of the CardSet and a given one
     * 
     * @param (that)
     *            the other CardSet of the union
     * @return the union of the two sets of cards
     */
    public CardSet union(CardSet that) {
        return new CardSet(PackedCardSet.union(packedCardSet, that.packed()));
    }

    /**
     * Returns the intersection of the CardSet and a given one
     * 
     * @param (that)
     *            the other CardSet of the intersection
     * @return the intersection of the two sets of cards
     */
    public CardSet intersection(CardSet that) {
        return new CardSet(
                PackedCardSet.intersection(packedCardSet, that.packed()));
    }

    /**
     * Returns the difference between the CardSet and a given one
     * 
     * @param (that)
     *            the other CardSet of the difference
     * @return the difference between the two sets of cards
     */
    public CardSet difference(CardSet that) {
        return new CardSet(
                PackedCardSet.difference(packedCardSet, that.packed()));
    }

    /**
     * Returns a subset of the CardSet containing only one given Color
     * 
     * @param (color)
     *            the color of the cards in the subset
     * @return the resulting subset after the operation
     */
    public CardSet subsetOfColor(Card.Color color) {
        return new CardSet(PackedCardSet.subsetOfColor(packedCardSet, color));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null)
            if (obj.getClass().equals(this.getClass()))
                return ((CardSet) obj).packed() == this.packed();
        
        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(packedCardSet);
    }

    @Override
    public String toString() {
        return PackedCardSet.toString(packedCardSet);
    }
}