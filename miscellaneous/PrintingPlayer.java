/*
 * Author :   Joseph E. Abboud.
 * Date   :   18 Mar 2019
 */

import java.util.Map;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.Score;
import ch.epfl.javass.jass.TeamId;
import ch.epfl.javass.jass.Trick;
import ch.epfl.javass.jass.TurnState;

public final class PrintingPlayer implements Player {
    private final Player underlyingPlayer;

    public PrintingPlayer(Player underlyingPlayer) {
        this.underlyingPlayer = underlyingPlayer;
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
        System.out.print("C'est à moi de jouer... Je joue : ");
        Card c = underlyingPlayer.cardToPlay(state, hand);
        System.out.println(c);        
        return c;
    }

    @Override
    public void setPlayers(PlayerId ownId, Map<PlayerId, String> playerNames) {
        System.out.println("Les joueurs sont : ");
        for (PlayerId pId : PlayerId.ALL) {
            if (pId.equals(ownId)) {
                System.out.println(ownId + " (moi)");
            } else {
                System.out.println(pId);
            }
        }
    }

    @Override
    public void updateHand(CardSet newHand) {
        System.out.println("Ma nouvelle main : " + newHand);
    }

    @Override
    public void setTrump(Card.Color trump) {
        System.out.println("Atout : " + trump);
    }

    @Override
    public void updateTrick(Trick newTrick) {
        System.out.println("Pli " + newTrick.index() + ", commencé par "
                + newTrick.player(0) + " : " + newTrick);
    }

    @Override
    public void updateScore(Score score) {
        System.out.println("Scores: " + score);
    }

    @Override
    public void setWinningTeam(TeamId winningTeam) {
        System.out.println("L'équipe gagnante est : " + winningTeam);
    }
}
