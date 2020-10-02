
package ch.epfl.javass.jass;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * Enumerator used to identify the two different teams
 * 
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public enum TeamId {
    TEAM_1, TEAM_2;

    /**
     * List conatining both teams in order(1 then 2)
     */
    public static final List<TeamId> ALL = Collections
            .unmodifiableList(Arrays.asList(values()));
    
    /**
     * Number of teams in a game
     */
    public static final int COUNT = ALL.size();

    /**
     * Returns the team that is not the receptor
     * 
     * @return the team that is not the receptor
     */
    public TeamId other() {
        return this.equals(TEAM_1) ? TEAM_2 : TEAM_1;
    }
}
