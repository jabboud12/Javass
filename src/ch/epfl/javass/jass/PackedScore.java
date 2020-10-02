package ch.epfl.javass.jass;

import static ch.epfl.javass.bits.Bits64.extract;
import static ch.epfl.javass.bits.Bits64.mask;
import static ch.epfl.javass.jass.Jass.MATCH_ADDITIONAL_POINTS;
import static ch.epfl.javass.jass.Jass.TRICKS_PER_TURN;
import static ch.epfl.javass.jass.TeamId.TEAM_1;
import static ch.epfl.javass.jass.TeamId.TEAM_2;

import ch.epfl.javass.bits.Bits32;
import ch.epfl.javass.bits.Bits64;

/**
 * Methods to manipulate and give information about a Score through its packed
 * version
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class PackedScore {

    private PackedScore() {
    }

    /**
     * Initial PackedScore (all score are equal to 0)
     */
    public static final long INITIAL = 0L;

    /**
     * Allocated bits for each Team in the Long bit string
     */
    private static final int TEAMS_SIZE = 32;

    /**
     * First bit for Team1 (bit 0)
     */
    private static final int TEAM_1_START = 0;

    /**
     * First bit for Team2 (bit 32)
     */
    private static final int TEAM_2_START = TEAM_1_START + TEAMS_SIZE;

    /**
     * First bit for the Trick PackedScore representation (bit 0)
     */
    private static final int TRICK_START = TEAM_1_START;

    /**
     * ALlocated bits for the Trick's PackedScore
     */
    private static final int TRICK_SIZE = 4;

    /**
     * First bit for the Turn PackedScore representation (bit 4)
     */
    private static final int TURN_POINTS_START = TRICK_START + TRICK_SIZE;

    /**
     * ALlocated bits for the Turn's PackedScore
     */
    private static final int TURN_POINTS_SIZE = 9;

    /**
     * Highest value possible for a Turn's PackedScore (Over this would not be
     * valid)
     */
    private static final int TURN_POINTS_MAX_VALUE = 257;

    /**
     * First bit for the Game PackedScore representation (bit 13)
     */
    private static final int GAME_POINTS_START = TURN_POINTS_START
            + TURN_POINTS_SIZE;

    /**
     * ALlocated bits for the Game's PackedScore
     */
    private static final int GAME_POINTS_SIZE = 11;

    /**
     * Highest value possible for a Game's Score (Over this would not be valid)
     */
    private static final int GAME_POINTS_MAX_VALUE = 2000;

    /**
     * Invalid version of a PackedScore
     */
    private static final int INVALID = 0b1111_1111_1111_1111_1111_1111;

    /**
     * Checks if a Score is valid and returns a boolean depending whether it is
     * or not
     * 
     * @param (pkScore)
     *            the packed version of a Score to check
     * @return true if pkScore verifies the conditions and false otherwise
     */
    public static boolean isValid(long pkScore) {
        return extract(pkScore, TEAM_1_START, TEAMS_SIZE) < INVALID
                && extract(pkScore, TEAM_2_START, TEAMS_SIZE) < INVALID
                && extract(pkScore, TRICK_START,
                        TRICK_SIZE) <= TRICKS_PER_TURN
                && extract(pkScore, TURN_POINTS_START,
                        TURN_POINTS_SIZE) <= TURN_POINTS_MAX_VALUE
                && extract(pkScore, GAME_POINTS_START,
                        GAME_POINTS_SIZE) <= GAME_POINTS_MAX_VALUE
                && extract(team2Bits(pkScore), TRICK_START,
                        TRICK_SIZE) <= TRICKS_PER_TURN
                && extract(team2Bits(pkScore), TURN_POINTS_START,
                        TURN_POINTS_SIZE) <= TURN_POINTS_MAX_VALUE
                && extract(team2Bits(pkScore), GAME_POINTS_START,
                        GAME_POINTS_SIZE) <= GAME_POINTS_MAX_VALUE
                && pkScore >= 0;
    }

    private static long team2Bits(long pkScore) {
        return pkScore >> TEAMS_SIZE;
    }

    /**
     * Returns the packed version of a Score
     * 
     * @param (turnTricks1)
     *            turn's tricks won for team1
     * @param (turnPoints1)
     *            turn's points won for team1
     * @param (gamePoints1)
     *            game's points won for team1
     * @param (turnTricks2)
     *            turn's points won for team2
     * @param (turnPoints2)
     *            turn's points won for team2
     * @param (gamePoints2)
     *            game's points won for team2
     * @return the packed version of the given parameters
     */
    public static long pack(int turnTricks1, int turnPoints1, int gamePoints1,
            int turnTricks2, int turnPoints2, int gamePoints2) {

        int pkScore1 = Bits32.pack(turnTricks1, TRICK_SIZE, turnPoints1,
                TURN_POINTS_SIZE, gamePoints1, GAME_POINTS_SIZE);
        int pkScore2 = Bits32.pack(turnTricks2, TRICK_SIZE, turnPoints2,
                TURN_POINTS_SIZE, gamePoints2, GAME_POINTS_SIZE);

        return Bits64.pack(pkScore1, TEAMS_SIZE, pkScore2, TEAMS_SIZE);
    }

    /**
     * Returns this round's turn wins
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @param (t)
     *            team to consider
     * @return tricks won in this round
     */
    public static int turnTricks(long pkScore, TeamId t) {
        assert isValid(pkScore);

        return t.equals(TEAM_1)
                ? (int) extract(pkScore, TRICK_START, TRICK_SIZE)
                : (int) extract(team2Bits(pkScore), TRICK_START, TRICK_SIZE);
    }

    /**
     * Returns points won in this round
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @param (t)
     *            team to consider
     * @return points won in this round
     */
    public static int turnPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);

        return t.equals(TEAM_1)
                ? (int) extract(pkScore, TURN_POINTS_START, TURN_POINTS_SIZE)
                : (int) extract(team2Bits(pkScore), TURN_POINTS_START,
                        TURN_POINTS_SIZE);
    }

    /**
     * Returns points won in previous rounds
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @param (t)
     *            team to consider
     * @return points won in previous rounds
     */
    public static int gamePoints(long pkScore, TeamId t) {
        assert isValid(pkScore);

        return t.equals(TEAM_1)
                ? (int) extract(pkScore, GAME_POINTS_START, GAME_POINTS_SIZE)
                : (int) extract(team2Bits(pkScore), GAME_POINTS_START,
                        GAME_POINTS_SIZE);
    }

    /**
     * Returns total points won
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @param (t)
     *            team to consider
     * @return total points won
     */
    public static int totalPoints(long pkScore, TeamId t) {
        assert isValid(pkScore);
        return turnPoints(pkScore, t) + gamePoints(pkScore, t);
    }

    /**
     * Returns the updated score after the turn
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @param (winningTeam)
     *            team that has most points
     * @param (trickPoints)
     *            points won after the trick
     * @return updated scores after a turn
     */
    public static long withAdditionalTrick(long pkScore, TeamId winningTeam,
            int trickPoints) {
        assert isValid(pkScore);
        assert (trickPoints >= 0);

        if (turnTricks(pkScore, TEAM_1) == TRICKS_PER_TURN - 1
                || turnTricks(pkScore, TEAM_2) == TRICKS_PER_TURN - 1)
            trickPoints += MATCH_ADDITIONAL_POINTS; // MATCH_ADDITIONAL_POINTS
                                                    // is a constant in
                                                    // interface Jass

        return winningTeam.equals(TEAM_1)
                ? ++pkScore + (trickPoints << TRICK_SIZE)
                : (pkScore + (1L << TEAM_2_START))
                        + ((long) trickPoints << (TRICK_SIZE + TEAM_2_START));

    }

    /**
     * Returns the updated Score after the turn
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @return the updated score after the turn
     */
    public static long nextTurn(long pkScore) {
        assert isValid(pkScore);
        
        pkScore += (extract(pkScore, TURN_POINTS_START,
                TURN_POINTS_SIZE) << GAME_POINTS_START)
                + (extract(pkScore, TURN_POINTS_START + TEAM_2_START,
                        TURN_POINTS_SIZE) << GAME_POINTS_START + TEAM_2_START);

        return (((pkScore & ~mask(TRICK_START, TRICK_SIZE))
                & ~mask(TURN_POINTS_START, TURN_POINTS_SIZE))
                & ~mask(TEAM_2_START, TRICK_SIZE))
                & ~mask(TURN_POINTS_START + TEAM_2_START, TURN_POINTS_SIZE);
    }

    /**
     * Overloading of Object.toString()
     * 
     * @param (pkScore)
     *            packed version of the Score
     * @return prints all 6 components of the packedScore in order
     */
    public static String toString(long pkScore) {
        assert isValid(pkScore);
        return "(" + turnTricks(pkScore, TEAM_1) + ","
                + turnPoints(pkScore, TEAM_1) + ","
                + gamePoints(pkScore, TEAM_1) + ")/("
                + turnTricks(pkScore, TEAM_2) + ","
                + turnPoints(pkScore, TEAM_2) + ","
                + gamePoints(pkScore, TEAM_2) + ")";
    }

}
