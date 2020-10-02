package ch.epfl.javass.gui;

import ch.epfl.javass.jass.TeamId;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.ReadOnlyIntegerProperty;
import javafx.beans.property.ReadOnlyObjectProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * Class representing the Score of a game, that will be accessed by the
 * graphical player from these methods as the game goes on
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class ScoreBean {

    private final IntegerProperty turnPointsProperty1;
    private final IntegerProperty turnPointsProperty2;
    private final IntegerProperty gamePointsProperty1;
    private final IntegerProperty gamePointsProperty2;
    private final IntegerProperty totalPointsProperty1;
    private final IntegerProperty totalPointsProperty2;
    private final ObjectProperty<TeamId> winningTeamProperty;

    /**
     * Constructs an instance of a ScoreBean, and initializes the
     * IntegerProperties and the winningTeam's Property
     */
    public ScoreBean() {
        turnPointsProperty1 = new SimpleIntegerProperty();
        turnPointsProperty2 = new SimpleIntegerProperty();
        gamePointsProperty1 = new SimpleIntegerProperty();
        gamePointsProperty2 = new SimpleIntegerProperty();
        totalPointsProperty1 = new SimpleIntegerProperty();
        totalPointsProperty2 = new SimpleIntegerProperty();
        winningTeamProperty = new SimpleObjectProperty<>();
    }

    /**
     * Returns the IntegerProperty representing the turnPoints of the given Team
     * 
     * @param team
     *            The team we want to access
     * @return ReadOnlyIntegerProperty containing the turn score of the given
     *         Team
     */
    public ReadOnlyIntegerProperty turnPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? turnPointsProperty1 : turnPointsProperty2;
    }

    /**
     * Changes the turnPoints of the given Team
     * 
     * @param team
     *            The Team which we want to change the score
     * @param newTurnPoints
     *            The turn points to put inside the Property
     */
    public void setTurnPoints(TeamId team, int newTurnPoints) {
        if (team == TeamId.TEAM_1)
            turnPointsProperty1.set(newTurnPoints);
        else
            turnPointsProperty2.set(newTurnPoints);
    }

    /**
     * Returns the IntegerProperty representing the gamePoints of the given Team
     * 
     * @param team
     *            The team we want to access
     * @return ReadOnlyIntegerProperty containing the game score of the given
     *         Team
     */
    public ReadOnlyIntegerProperty gamePointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? gamePointsProperty1 : gamePointsProperty2;
    }

    /**
     * Changes the gamePoints of the given Team
     * 
     * @param team
     *            The Team which we want to change the score
     * @param newGamePoints
     *            The game points to put inside the Property
     */
    public void setGamePoints(TeamId team, int newGamePoints) {
        if (team == TeamId.TEAM_1)
            gamePointsProperty1.set(newGamePoints);
        else
            gamePointsProperty2.set(newGamePoints);
    }

    /**
     * Returns the IntegerProperty representing the totalPoints of the given
     * Team
     * 
     * @param team
     *            The team we want to access
     * @return ReadOnlyIntegerProperty containing the total score of the given
     *         Team
     */
    public ReadOnlyIntegerProperty totalPointsProperty(TeamId team) {
        return team == TeamId.TEAM_1 ? totalPointsProperty1 : totalPointsProperty2;
    }

    /**
     * Changes the totalPoints of the given Team
     * 
     * @param team
     *            The Team which we want to change the score
     * @param newTotalPoints
     *            The total points to put inside the Property
     */
    public void setTotalPoints(TeamId team, int newTotalPoints) {
        if (team == TeamId.TEAM_1)
            totalPointsProperty1.set(newTotalPoints);
        else
            totalPointsProperty2.set(newTotalPoints);
    }

    /**
     * Returns the ObjectProperty representing the winning Team of the game
     * 
     * @return ReadOnlyObjectProperty containing the winning Team of the game
     */
    public ReadOnlyObjectProperty<TeamId> winningTeamProperty() {
        return winningTeamProperty;
    }

    /**
     * Sets the value of the winning Team of the game
     * 
     * @param winningTeam
     *            The identity of the Team that has won the game
     */
    public void setWinningTeam(TeamId winningTeam) {
        this.winningTeamProperty.set(winningTeam);
    }
}
