package ch.epfl.javass.gui;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Trick;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

/**
 * Class representing a Trick in a game, that will be accessed by the graphical
 * player from these methods as the game goes on
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class TrickBean {

    private final ObjectProperty<Card.Color> trumpProperty;
    private final ObservableMap<PlayerId, Card> trickProperty;
    private final ObjectProperty<PlayerId> winningPlayerProperty;

    /**
     * Constructs an instance of TrickBean ; initializes the ObservableMap and
     * the ObjectProperties
     */
    public TrickBean() {
        trumpProperty = new SimpleObjectProperty<>();
        trickProperty = FXCollections.observableHashMap();
        winningPlayerProperty = new SimpleObjectProperty<>();
    }

    /**
     * Returns an unmodifiable version of the trick in an ObservableMap
     * 
     * @return the ObservableMap containing the Trick
     */
    public ObservableMap<PlayerId, Card> trick() {
        return FXCollections.unmodifiableObservableMap(trickProperty);
    }

    /**
     * Sets the value of the ObservableMap's elements, given a Trick
     * 
     * @param newTrick
     *            The Trick given to update inside the ObservableMap
     */
    public void setTrick(Trick newTrick) {

        // First we fill the map with the played Cards and their respective
        // players
        for (int i = 0; i < newTrick.size(); ++i)
            trickProperty.put(newTrick.player(i), newTrick.card(i));

        // Then if needed, we fill the rest of the map with null (which mean
        // that the player has not played their Card yet)
        for (int i = newTrick.size(); i < PlayerId.COUNT; ++i)
            trickProperty.put(newTrick.player(i), null);

        // Finally, we set the winning Player of the Trick
        winningPlayerProperty.set(newTrick.isEmpty() ? null : newTrick.winningPlayer());
    }

    /**
     * Returns the trump Color contained in the Property
     * 
     * @return ReadOnlyObjectProperty containing the trump Color
     */
    public ReadOnlyObjectProperty<Card.Color> trumpProperty() {
        return trumpProperty;
    }

    /**
     * Sets the value of the trump inside the Property
     * 
     * @param trump
     *            The Color to change
     */
    public void setTrump(Card.Color trump) {
        this.trumpProperty.set(trump);
    }

    /**
     * Returns the PlayerId of the winningPlayer of the Trick. The player's Id
     * is set in the method setTrick() above
     * 
     * @return ReadOnlyObjectProperty containing the PlayerId of the winning
     *         Player of the current Trick
     */
    public ReadOnlyObjectProperty<PlayerId> winningPlayerProperty() {
        return winningPlayerProperty;
    }

}
