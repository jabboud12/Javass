
package ch.epfl.javass.jass;

import ch.epfl.javass.jass.Card.Color;
import static ch.epfl.javass.Preconditions.checkArgument;
import static ch.epfl.javass.Preconditions.checkState;

/**
 * Represents the state of a current Turn
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class TurnState {

    private final long pkScore;
    private final long pkUnplayedCards;
    private final int pkTrick;

    private TurnState(long pkScore, long pkUnplayedCards, int pkTrick) {
        this.pkScore = pkScore;
        this.pkUnplayedCards = pkUnplayedCards;
        this.pkTrick = pkTrick;
    }

    /**
     * Constructs the initial instance of TurnState
     * 
     * @param (trump)
     *            the trump used during the Turn
     * @param (score)
     *            the Score of the game preceding this Turn
     * @param (firstPlayer)
     *            the first player to play a Card in this Turn
     * @return the initial TurnState of a Turn
     */
    public static TurnState initial(Color trump, Score score,
            PlayerId firstPlayer) {
        return new TurnState(score.packed(), PackedCardSet.ALL_CARDS,
                PackedTrick.firstEmpty(trump, firstPlayer));
    }

    /**
     * Constructs an instance of TurnState given the packed versions of its
     * arguments
     * 
     * @param (pkScore)
     *            the packed version of the current Score
     * @param (pkUnplayedCards)
     *            the packed version of the available CardSet
     * @param (pkTrick)
     *            the packed version of the Trick
     * @throws IllegalArgumentException
     *             if any of the packed parameters is not valid
     * @return the TurnState associated with the given packed parameters
     */
    public static TurnState ofPackedComponents(long pkScore,
            long pkUnplayedCards, int pkTrick) {

        checkArgument(PackedCardSet.isValid(pkUnplayedCards)
                && PackedTrick.isValid(pkTrick)
                && PackedScore.isValid(pkScore));

        return new TurnState(pkScore, pkUnplayedCards, pkTrick);
    }

    /**
     * Getter for the PackedScore of the TurnState
     * 
     * @return the PackedScore of the TrunState
     */
    public long packedScore() {
        return pkScore;
    }

    /**
     * Getter for the PackedCardSet associated to the unplayed cards of the
     * TurnState
     * 
     * @return the packed unplayed cards of the TurnState
     */
    public long packedUnplayedCards() {
        return pkUnplayedCards;
    }

    /**
     * Getter for the current PackedTrick of the TurnState
     * 
     * @return the current PackedTrick of the TurnState
     */
    public int packedTrick() {
        return pkTrick;
    }

    /**
     * Getter for the Score of the TurnState
     * 
     * @return the Score of the TrunState
     */
    public Score score() {
        return Score.ofPacked(pkScore);
    }

    /**
     * Getter for the CardSet associated to the unplayed cards of the TurnState
     * 
     * @return the unplayed cards of the TurnState
     */
    public CardSet unplayedCards() {
        return CardSet.ofPacked(pkUnplayedCards);
    }

    /**
     * Getter for the current Trick of the TurnState
     * 
     * @return the current Trick of the TurnState
     */
    public Trick trick() {
        return Trick.ofPacked(pkTrick);
    }

    /**
     * Checks if the last Trick of the turn has been played
     * 
     * @return true the last Trick has been played and false otherwise
     */
    public boolean isTerminal() {
        return pkTrick == PackedTrick.INVALID;
    }

    /**
     * Returns the Player supposed to play the next Card
     * 
     * @throws IllegalStateException
     *             if the TurnState's Trick is full
     * @return the Player supposed to play the next Card
     */
    public PlayerId nextPlayer() {
        checkState(!PackedTrick.isFull(pkTrick));

        return PlayerId.ALL.get((PackedTrick
                .player(pkTrick, PackedTrick.size(pkTrick)).ordinal() % 4));
    }

    /**
     * Returns a new TurnState where we are in the situation of a new Card being
     * played
     * 
     * @param (card)
     *            the Card to be played
     * @throws IllegalStateException
     *             if the TurnState's Trick is full
     * @return the new updated TurnState
     */
    public TurnState withNewCardPlayed(Card card) {
        checkState(!PackedTrick.isFull(pkTrick));

        return new TurnState(pkScore,
                PackedCardSet.remove(pkUnplayedCards, card.packed()),
                PackedTrick.withAddedCard(pkTrick, card.packed()));
    }

    /**
     * Returns a new TurnState where the current trick has been collected
     * 
     * @throws IllegalStateException
     *             if the TurnState's Trick is not full
     * @return the new updated TurnState
     */
    public TurnState withTrickCollected() {
        checkState(PackedTrick.isFull(pkTrick));

        return new TurnState(
                PackedScore.withAdditionalTrick(pkScore,
                        PackedTrick.winningPlayer(pkTrick).team(),
                        PackedTrick.points(pkTrick)),
                pkUnplayedCards, PackedTrick.nextEmpty(pkTrick));

    }

    /**
     * Returns a new TurnState where a new Card has been played in the Trick and
     * where the current Trick has been collected if it contains 4 cards.
     * 
     * @param (card)
     *            the Card which will be played
     * @throws IllegalStateException
     *             if the TurnState's Trick is full
     * @return the new updated TurnState
     */
    public TurnState withNewCardPlayedAndTrickCollected(Card card) {
        checkState(!PackedTrick.isFull(pkTrick));

        TurnState s = withNewCardPlayed(card);
        if (PackedTrick.isFull(s.packedTrick()))
            return s.withTrickCollected();

        return s;
    }

}
