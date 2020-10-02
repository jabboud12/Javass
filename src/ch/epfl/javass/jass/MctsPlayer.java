package ch.epfl.javass.jass;

import static ch.epfl.javass.Preconditions.checkArgument;

import java.util.ArrayList;
import java.util.List;
import java.util.SplittableRandom;

/**
 * Contains the methods necessary for the algorithm of MonteCarlor Tree Search,
 * implemented into a Player. We are using the packed versions of the classes
 * Card, Trick, CardSet, Score in order for the algorithm to be faster
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public final class MctsPlayer implements Player {

    private Node node = null;
    private final int c = 40;

    private final PlayerId ownId;
    private final int iterations;
    private final long rngSeed;

    /**
     * Public constructor
     * 
     * @param (ownId)
     *            The Player's Id
     * @param (rngSeed)
     *            the seed used to go through all random events in the class
     * @param (iterations)
     *            the number of iterations used in the MonteCarlo Tree Search
     *            algorithm
     */
    public MctsPlayer(PlayerId ownId, long rngSeed, int iterations) {
        checkArgument(iterations >= Jass.TRICKS_PER_TURN);
        this.ownId = ownId;
        this.iterations = iterations;
        this.rngSeed = rngSeed;
    }

    @Override
    public Card cardToPlay(TurnState turnState, CardSet hand) {
        long pkHand = hand.packed();

        // If there is only 1 possible playable card, there is no need to
        // conduct the algorithm since there is only one choice
        if (turnState.trick().playableCards(hand).size() == 1)
            return turnState.trick().playableCards(hand).get(0);

        // Create the first parent Node
        node = new Node(turnState, pkHand, ownId, rngSeed);

        for (int i = 0; i < iterations; ++i) {
            List<Node> nodes = addNode(pkHand);
            distributePoints(nodes);
        }

        return turnState.trick().playableCards(hand)
                .get(node.extractBestChildIndex(0));
    }

    /*
     * Goes through all nodes of a list (bottom to top) and updates the total
     * points of each Node and the turn simulated that originated from this Node
     */
    private void distributePoints(List<Node> nodes) {
        for (int i = nodes.size() - 1; i >= 0; --i) {
            nodes.get(i).totalPoints();
            nodes.get(i).randomTurnsNumber();
        }
    }

    private List<Node> addNode(long hand) {
        boolean added = false;
        List<Node> nodes = new ArrayList<>();
        Node n = node;

        while (!added) {
            // Necessary condition so the List nodes don't get filled with null
            // components
            if (n != null)
                nodes.add(n);
            else {
                added = true;
                break;
            }

            // Check if the Node n has all its children so we can extract the
            // best child
            if (PackedCardSet.isEmpty(n.nonExistingChildren))
                n = n.children[n.extractBestChildIndex(c)];
            else {
                for (int i = 0; i < n.children.length; ++i) {
                    if (n.children[i] == null) {

                        // Gets the first card occurring in the
                        // nonExistingChildren set
                        int card = PackedCardSet.get(n.nonExistingChildren, 0);
                        TurnState state = n.turnState
                                .withNewCardPlayedAndTrickCollected(
                                        Card.ofPacked(card));
                        if (!state.isTerminal()) {
                            n.children[i] = new Node(state,
                                    PackedCardSet.remove(hand, card), ownId,
                                    rngSeed);
                            if (n != null)
                                nodes.add(n.children[i]);
                            added = true;
                        }

                        // Updates the nonExistingChildren set for the node n
                        // (removes the new Node added to children[])
                        n.nonExistingChildren = PackedCardSet
                                .remove(n.nonExistingChildren, card);
                        break;
                    }
                }
            }
        }
        return nodes;
    }

    private static class Node {

        private TurnState turnState;
        private Node[] children;
        private long nonExistingChildren;
        private int totalPoints;
        private int turnPoints;
        // otherPoints is considered as a variable because it is not always
        // 157-turnPoints (it could be 257-0)
        private int otherPoints;
        private int randomTurnsNumber;
        private long hand;
        private final PlayerId playerId;

        private Node(TurnState turnState, long hand, PlayerId id,
                long rngSeed) {
            this.turnState = turnState;
            playerId = id;
            nonExistingChildren = playableCards(turnState, hand);
            children = new Node[PackedCardSet.size(nonExistingChildren)];
            this.hand = hand;
            turnPoints = turnPoints(rngSeed);
        }

        /*
         * Simulates a full Turn going from this Node's Trick in order to see
         * what score it expects to get if the Card of the Node is played
         */
        private int turnPoints(long rngSeed) {
            long hnd = hand;
            SplittableRandom rng = new SplittableRandom(rngSeed);

            TurnState trnStt = TurnState.ofPackedComponents(
                    turnState.packedScore(), turnState.packedUnplayedCards(),
                    turnState.packedTrick());

            while (!trnStt.isTerminal()) {
                turnPoints = trnStt.score().turnPoints(playerId.team());
                otherPoints = trnStt.score()
                        .turnPoints(playerId.team().other());

                long set = playableCards(trnStt, hnd);
                int c = PackedCardSet.get(set,
                        rng.nextInt(PackedCardSet.size(set)));
                hnd = PackedCardSet.remove(hnd, c);
                trnStt = trnStt
                        .withNewCardPlayedAndTrickCollected(Card.ofPacked(c));
            }
            return turnPoints;
        }

        private int extractBestChildIndex(int c) {
            int index = 0;
            Node child = children[0];
            for (int i = 1; i < children.length; ++i) {
                if (children[i] != null) {
                    int pts = children[i].totalPoints;
                    int nbr = children[i].randomTurnsNumber;
                    if (children[i].calculateV(c, pts, nbr,
                            randomTurnsNumber) > child.calculateV(c,
                                    child.totalPoints, child.randomTurnsNumber,
                                    randomTurnsNumber)) {
                        child = children[i];
                        index = i;
                    }
                }
            }
            return index;
        }

        private double calculateV(int c, int totalPoints, int randomTurnsNumber,
                int parentNumber) {
            return totalPoints / (double) randomTurnsNumber + c * Math
                    .sqrt((2 * Math.log(parentNumber)) / randomTurnsNumber);
        }

        /*
         * Computes the number of turns simulated from a node depending on its
         * children (this method is used in distributePoints() and works from
         * bottom to top, so each node only has to know the turns of its
         * children only)
         */
        private int randomTurnsNumber() {
            randomTurnsNumber = 1;
            for (Node child : children)
                if (child != null) {
                    randomTurnsNumber += child.randomTurnsNumber;
                }
            return randomTurnsNumber;
        }

        /*
         * Computes the total points gotten from a node depending on its
         * children (this method is used in distributePoints() and works from
         * bottom to top, so each node only has to know the turns of its
         * children only)
         */
        private int totalPoints() {
            totalPoints = turnPoints;
            for (Node child : children) {
                if (child != null) {
                    if (child.playerId.team() == playerId.team())
                        totalPoints += child.totalPoints;
                    else
                        totalPoints += (child.randomTurnsNumber * otherPoints)
                                - child.totalPoints;
                }
            }
            return totalPoints;
        }

        /*
         * Returns the playable cards in a packed version, depending on the hand
         * of the player if it's his turn to play in the simulated Turn, or
         * depending on the unplayedCards of the Trick if it is not the node's
         * owner turn
         */
        private long playableCards(TurnState turnState, long hand) {

            if (turnState.nextPlayer().equals(playerId))
                return PackedTrick.playableCards(turnState.packedTrick(), hand);

            return PackedTrick.playableCards(turnState.packedTrick(),
                    PackedCardSet.difference(turnState.packedUnplayedCards(),
                            hand));
        }
    }
}