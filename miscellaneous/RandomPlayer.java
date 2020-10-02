/*
 * Author :   Joseph E. Abboud.
 * Date   :   18 Mar 2019
 */

import java.util.Random;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.CardSet;
import ch.epfl.javass.jass.Player;
import ch.epfl.javass.jass.TurnState;

public final class RandomPlayer implements Player {
    private final Random rng;

    public RandomPlayer(long rngSeed) {
      this.rng = new Random(rngSeed);
    }

    @Override
    public Card cardToPlay(TurnState state, CardSet hand) {
      CardSet playable = state.trick().playableCards(hand);
      return playable.get(rng.nextInt(playable.size()));
    }
  }
