
package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.checkArgument;

/**
 * Represents the scores of a game of Jass
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class Score {

    /**
     * Initial Score of a game (every component is 0)
     */
    public final static Score INITIAL = new Score(PackedScore.INITIAL);

    private long packedScore;

    private Score(long pkScore) {
        // checkArgument throws an IllegalArgumentException in all methods below
        // that call the private constructor if pkScore is not valid
        checkArgument(PackedScore.isValid(pkScore));
        packedScore = pkScore;
    }

    /**
     * Constructs an instance of Score given its packed version
     * 
     * @param (packed)
     *            packed version of the Score to construct
     * @return the score associated the given PackedScore
     */
    public static Score ofPacked(long packed) {
        return new Score(packed);
    }

    /**
     * Returns the packed version of the scores
     * 
     * @return the packed version of the scores
     */
    public long packed() {
        return packedScore;
    }

    /**
     * Returns this round's turn wins
     * 
     * @param (t)
     *            team to consider
     * @return tricks won in this round
     */
    public int turnTricks(TeamId t) {
        return PackedScore.turnTricks(packedScore, t);
    }

    /**
     * Returns points won in this round
     * 
     * @param (t)
     *            team to consider
     * @return points won in this round
     */
    public int turnPoints(TeamId t) {
        return PackedScore.turnPoints(packedScore, t);
    }

    /**
     * Returns points won in previous rounds
     * 
     * @param (t)
     *            team to consider
     * @return accumulated points won in previous rounds
     */
    public int gamePoints(TeamId t) {
        return PackedScore.gamePoints(packedScore, t);
    }

    /**
     * Returns total points won
     * 
     * @param (t)
     *            team to consider
     * @return total points won
     */
    public int totalPoints(TeamId t) {
        return PackedScore.totalPoints(packedScore, t);
    }

    /**
     * Returns updated scores after a trick
     *
     * @param (winningTeam)
     *            team that has most points
     * @param (trickPoints)
     *            points won after the trick
     * @throws IllegalArgumentException
     *             if the trickPoints to add are negative
     * @return updated scores after a turn
     */
    public Score withAdditionalTrick(TeamId winningTeam, int trickPoints) {
        checkArgument(trickPoints >= 0);
        return new Score(PackedScore.withAdditionalTrick(packedScore,
                winningTeam, trickPoints));
    }

    /**
     * Returns the updated Score after the turn
     * 
     * @return the updated score after the turn
     */
    public Score nextTurn() {
        return new Score(PackedScore.nextTurn(packedScore));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Score)
            return ((Score) obj).packed() == this.packed();

        return false;
    }

    @Override
    public int hashCode() {
        return Long.hashCode(packedScore);
    }

    @Override
    public String toString() {
        return PackedScore.toString(packedScore);
    }

}