
package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.*;

import ch.epfl.javass.jass.Card.Color;

/**
 * Class containing all methods and information about a Trick in a game of Jass
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class Trick {

    /**
     * Invalid version of a Trick
     */
    public final static Trick INVALID = new Trick(PackedTrick.INVALID);

    private int packedTrick;

    private Trick(int pkTrick) {
        packedTrick = pkTrick;
    }

    /**
     * Returns the empty trick, except for the packed player1 and trump color at
     * their position in the bit string
     * 
     * @param (trump)
     *            the Color of the trump in the Trick
     * @param (firstPlayer)
     *            the first player to draw a Card
     * @return the wanted Trick
     */
    public static Trick firstEmpty(Color trump, PlayerId firstPlayer) {
        return new Trick(PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Constructs an instance of Trick given its packed version
     * 
     * @param (packed)
     *            the packed version of the Trick
     * @throws IllegalArgumentException
     *             if the packed version given is not valid
     * @return the Trick corresponding to the packedTrick given
     */
    public static Trick ofPacked(int packed) {
        checkArgument(PackedTrick.isValid(packed));
        return new Trick(packed);
    }

    /**
     * Returns packed version of the Trick
     * 
     * @return packed version of the Trick
     */
    public int packed() {
        return packedTrick;
    }

    /**
     * Returns the empty trick, with an incremented index by 1, the winning
     * player, and an unchanged trump
     * 
     * @throws IllegalStateException
     *             if the Trick is not full
     * @return the next empty updated Trick
     */
    public Trick nextEmpty() {
        checkState(isFull());
        return new Trick(PackedTrick.nextEmpty(packedTrick));
    }

    /**
     * Checks if a Trick does not contain any Card.
     * 
     * @return true if no Card has been drawn yet in the Trick and false
     *         otherwise
     */
    public boolean isEmpty() {
        return PackedTrick.isEmpty(packedTrick);
    }

    /**
     * Checks if a Trick contains all 4 cards.
     * 
     * @return true if all cards have been played in the Trick and false
     *         otherwise
     */
    public boolean isFull() {
        return PackedTrick.isFull(packedTrick);
    }

    /**
     * Checks if a Trick represents the last Trick of the turn
     * 
     * @return true if the Trick is the last of the game and false otherwise
     */
    public boolean isLast() {
        return PackedTrick.isLast(packedTrick);
    }

    /**
     * Returns the number of cards contained in a Trick
     * 
     * @return the number of cards in the Trick
     */
    public int size() {
        return PackedTrick.size(packedTrick);
    }

    /**
     * Returns the Color of the trump in the current Trick
     * 
     * @return the Color of the trump in the Trick
     */
    public Color trump() {
        return PackedTrick.trump(packedTrick);
    }

    /**
     * Returns the index of the current Trick
     * 
     * @return the index of the Trick in the turn
     */
    public int index() {
        return PackedTrick.index(packedTrick);
    }

    /**
     * Return the Player at the given index, with a given Player1
     * 
     * @param (index)
     *            the index of the player we want to get, depending on Player1
     * @throws IndexOutOfBoundsException
     *             if index does not respect the number of players (in PlayerId)
     * @return the Player at the given index
     */
    public PlayerId player(int index) {
        checkIndex(index, PlayerId.COUNT);
        return PackedTrick.player(packedTrick, index);
    }

    /**
     * Returns the n-th Card of a trick given an index n
     * 
     * @param (index)
     *            the index of the Card we want to get
     * @throws IndexOutOfBoundsException
     *             if index does not respect the size of the Trick
     * @return the Card at the given index
     */
    public Card card(int index) {
        checkIndex(index, size());
        return Card.ofPacked(PackedTrick.card(packedTrick, index));
    }

    /**
     * Returns a non-full Trick to which we added a certain Card
     * 
     * @param (c)the
     *            Card we want to add
     * @throws IllegalStateException
     *             if the Trick is full
     * @return the new updated Trick
     */
    public Trick withAddedCard(Card c) {
        checkState(!isFull());
        return new Trick(PackedTrick.withAddedCard(packedTrick, c.packed()));
    }

    /**
     * Returns the Color of the first played Card of the Trick
     * 
     * @throws IllegalStateException
     *             if the Trick is empty
     * @return the Color of the first played Card
     */
    public Color baseColor() {
        checkState(!isEmpty());
        return PackedTrick.baseColor(packedTrick);
    }

    /**
     * Returns all the possible playable cards depending on the current Trick
     * 
     * @param (hand)
     *            the CardSet representing the player's hand
     * @throws IllegalStateException
     *             if the Trick is full
     * @return the CardSet of all the playable cards that the player possesses
     */
    public CardSet playableCards(CardSet hand) {
        checkState(!isFull());
        return CardSet.ofPacked(
                PackedTrick.playableCards(packedTrick, hand.packed()));
    }

    /**
     * Returns the number of points obtained during a Trick considering the 5
     * bonus points at the end of the turn
     * 
     * @return the number of points obtained
     */
    public int points() {
        return PackedTrick.points(packedTrick);
    }

    /**
     * Returns the winning Player of a Trick
     * 
     * @throws IllegalStateException
     *             if the Trick is empty
     * @return the Player whose Card is the best
     */
    public PlayerId winningPlayer() {
        checkState(!isEmpty());
        return PackedTrick.winningPlayer(packedTrick);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null)
            if (obj.getClass().equals(this.getClass()))
                return ((Trick) obj).packed() == this.packed();

        return false;
    }

    @Override
    public int hashCode() {
        return packedTrick;
    }

    @Override
    public String toString() {
        return PackedTrick.toString(packedTrick);
    }

}
