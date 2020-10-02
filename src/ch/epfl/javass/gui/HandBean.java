package ch.epfl.javass.gui;

import java.util.stream.IntStream;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Jass;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;

/**
 * Class representing the hand of a graphical player, who will access the
 * information about its hand from these methods as the game goes on
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class HandBean {

    private final ObservableList<Card> handProperty;
    private final ObservableSet<Card> playableCardsProperty;

    /**
     * Constructs an instance of HandBean ; initializes the ObservableSet and
     * the ObservableList
     */
    public HandBean() {
        handProperty = FXCollections.observableArrayList();
        playableCardsProperty = FXCollections.observableSet();
        IntStream.range(0, Jass.HAND_SIZE).forEach(i -> handProperty.add(null));
    }

    /**
     * Returns an unmodifiable version of the hand in an ObservableList
     * 
     * @return the ObservableList containing the hand
     */
    public ObservableList<Card> hand() {
        return FXCollections.unmodifiableObservableList(handProperty);
    }

    /**
     * Sets the value of the ObservableList's elements, given a CardSet
     * 
     * @param newHand
     *            The CardSet representing the hand of a Player
     */
    public void setHand(CardSet newHand) {
        if (newHand.size() == Jass.HAND_SIZE)
            for (int i = 0; i < Jass.HAND_SIZE; ++i)
                handProperty.set(i, newHand.get(i));
        else
            for (int i = 0; i < handProperty.size(); ++i)
                if (handProperty.get(i) != null && !newHand.contains(handProperty.get(i)))
                    handProperty.set(i, null);
    }

    /**
     * Returns an unmodifiable version of the playableCards in an ObservableSet
     * 
     * @return the ObservableSet containing the playableCards
     */
    public ObservableSet<Card> playableCards() {
        return FXCollections.unmodifiableObservableSet(playableCardsProperty);
    }

    /**
     * Sets the value of the ObservableSet's elements, given a CardSet
     * 
     * @param newPlayableCards
     *            The CardSet representing the playableCards of a Player
     */
    public void setPlayableCards(CardSet newPlayableCards) {
        playableCardsProperty.clear();
        for (int i = 0; i < newPlayableCards.size(); ++i)
            playableCardsProperty.add(newPlayableCards.get(i));
    }
}
