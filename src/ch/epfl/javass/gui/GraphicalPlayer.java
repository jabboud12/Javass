package ch.epfl.javass.gui;

import java.util.HashMap;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.ArrayBlockingQueue;

import ch.epfl.javass.jass.Card;
import ch.epfl.javass.jass.Jass;
import ch.epfl.javass.jass.PlayerId;
import ch.epfl.javass.jass.TeamId;
import javafx.beans.binding.Bindings;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableBooleanValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.effect.GaussianBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.stage.Stage;

/**
 * Class representing the graphical interface of the game; it is created here
 *
 * @author Joseph Abboud (296753)
 * @author Zad Abi Fadel (295734)
 */
public class GraphicalPlayer {

    private BorderPane mainPane, victoryPane1, victoryPane2;
    private StackPane mainStack;
    private Stage stage;

    private Map<PlayerId, String> playerNames;
    private PlayerId ownId;
    private ArrayBlockingQueue<Card> queue;

    // Map containing all Images (240p) associated to their respective Cards
    // (used for the trick)
    private static final ObservableMap<Card, Image> CARDS_IMAGES_240 = fillCardsMap(240);

    // Map containing all Images (160p) associated to their respective Cards
    // (used for the hand)
    private static final ObservableMap<Card, Image> CARDS_IMAGES_160 = fillCardsMap(160);

    // Map containing all Images associated to their Respective Color
    public static final ObservableMap<Card.Color, Image> TRUMP_IMAGES = fillTrumpMap();

    /**
     * Creates the graphical interface's primary instances
     * 
     * @param ownId
     *            The Id of the Player's interface
     * @param playerNames
     *            The map containing all four players' names
     * @param scoreBean
     *            The Bean related to the score of the game
     * @param trickBean
     *            The Bean related the Tricks during the game
     * @param handBean
     *            The Bean related to the hand of the Player (ownId)
     * @param queue
     *            Queue containing the Card to play, when needed (else it is
     *            empty)
     */
    public GraphicalPlayer(PlayerId ownId, Map<PlayerId, String> playerNames,
            ScoreBean scoreBean, TrickBean trickBean, HandBean handBean,
            ArrayBlockingQueue<Card> queue) {
        this.playerNames = playerNames;
        this.ownId = ownId;
        this.queue = queue;
        mainStack = new StackPane();

        mainPane = createMainPane(playerNames, scoreBean, trickBean, handBean);
        victoryPane1 = createVictoryPanes(scoreBean, TeamId.TEAM_1);
        victoryPane2 = createVictoryPanes(scoreBean, TeamId.TEAM_2);
    }

    /**
     * Creates the Stage containing the mainPane (where the game takes place)
     * and the victory panes (each appear depending on whether their respective
     * Team won the game or not)
     * 
     * @return The created Stage of the game
     */
    public Stage createStage() {
        System.out.println("exec");

        mainStack.getChildren().add(victoryPane1);
        mainStack.getChildren().add(victoryPane2);
        mainStack.getChildren().add(mainPane);

        stage = new Stage();
        stage.setScene(new Scene(mainStack));
        stage.setTitle("Javass " + playerNames.get(ownId));

        return stage;

    }

    /*
     * Creates the main Pane for the game. The bigger part of the game takes
     * place on this Pane
     */
    private BorderPane createMainPane(Map<PlayerId, String> playerNames,
            ScoreBean scoreBean, TrickBean trickBean, HandBean handBean) {
        BorderPane main = new BorderPane();

        // Places the Score Pane at the top
        main.setTop(createScorePane(scoreBean));

        // Places the Trick Pane in the middle
        main.setCenter(createTrickPane(trickBean, scoreBean));

        // Places the Hand Pane in the bottom
        main.setBottom(createHandPane(handBean, trickBean));

        // Binds the opacity of the Pane to the fact that the game is over or
        // not. When the game is over, the opacity is 0 and the Pane disappears
        BooleanProperty b = new SimpleBooleanProperty();
        b.bind(scoreBean.totalPointsProperty(TeamId.TEAM_1)
                .greaterThan(Jass.WINNING_POINTS)
                .or(scoreBean.totalPointsProperty(TeamId.TEAM_2)
                        .greaterThan(Jass.WINNING_POINTS)));

        main.opacityProperty()
                .bind((Bindings.when(b).then(0.0).otherwise(1.0)));
        return main;
    }

    /*
     * Creates the Pane containing the Scores of both teams during the game.
     * Score are updated through the given ScoreBean
     */
    private GridPane createScorePane(ScoreBean scoreBean) {

        GridPane scorePane = new GridPane();
        scorePane.setStyle(
                "-fx-font: 16 Optima; -fx-background-color: lightgray; -fx-padding: 5px; -fx-alignment: center;");

        for (TeamId team : TeamId.ALL) {

            // Creates the String associated to the team's players' names
            StringJoiner s = new StringJoiner(" et ", " ", " : ");
            for (int i = 0; i < PlayerId.COUNT; ++i)
                if (ownId.next(i).team() == team)
                    s.add(playerNames.get(ownId.next(i)));
            Text names = new Text(s.toString());

            IntegerProperty trickPoints = new SimpleIntegerProperty();

            // Text associated to the additional points in each trick
            Text addPoints = new Text();

            // Text associated to the Points during a Turn
            Text turnPoints = new Text();

            // Text associated to the Points during the Game
            Text gamePoints = new Text();

            scoreBean.turnPointsProperty(team).addListener((o, oV,
                    nV) -> trickPoints.set(nV.intValue() - oV.intValue()));
            addPoints.visibleProperty().bind(trickPoints.greaterThan(0));

            addPoints.disableProperty()
                    .bind(scoreBean.turnPointsProperty(team).isEqualTo(0));
            addPoints.textProperty()
                    .bind(Bindings.format(" (+%d) ", trickPoints));

            turnPoints.textProperty()
                    .bind(Bindings.convert(scoreBean.turnPointsProperty(team)));

            gamePoints.textProperty()
                    .bind(Bindings.convert(scoreBean.gamePointsProperty(team)));

            Text additionalPoints = new Text();
            additionalPoints.textProperty().bind(Bindings
                    .when(scoreBean.turnPointsProperty(team).isEqualTo(0))
                    .then(new Text().textProperty())
                    .otherwise(addPoints.textProperty()));

            // Creates the final text containing all previous elements and adds
            // it to the Pane
            scorePane.addRow(team.ordinal(), names, turnPoints,
                    additionalPoints, new Text("/ Total :"), gamePoints);
        }

        return scorePane;
    }

    /*
     * Creates the Pane where the Tricks will take place an be updated during
     * the game through the given TrickBean
     */
    private GridPane createTrickPane(TrickBean trickBean, ScoreBean sb) {

        GridPane trickPane = new GridPane();
        trickPane.setStyle(
                " -fx-padding: 5px;  -fx-border-width: 3px 0px; -fx-border-style: solid;"
                        + "-fx-border-color: gray; -fx-alignment: center;");

        int width = 120; // Width of all cards
        int height = 180; // Height of all Cards
        int trumpDim = 101; // Dimension of the image referring to the Trump
        // color

        String style = "-fx-font: 14 Optima; -fx-padding: 5px;\n; -fx-alignment: center;";
        String haloStyle = "-fx-arc-width: 20; -fx-arc-height: 20; -fx-fill: transparent; -fx-stroke: lightpink; "
                + "-fx-stroke-width: 5; -fx-opacity: 0.5;";

        // Map containing the four cards of a Trick, associated to the player
        // who played them
        Map<PlayerId, VBox> cards = new HashMap<>();

        for (int i = 0; i < PlayerId.COUNT; ++i) {
            ImageView imageView = new ImageView();
            imageView.imageProperty().bind(Bindings.valueAt(CARDS_IMAGES_240,
                    Bindings.valueAt(trickBean.trick(), ownId.next(i))));

            setDimensions(width, height, imageView);

            // Creates the halo surrounding the winning Card of a Trick
            Rectangle halo = new Rectangle(width, height);
            halo.setStyle(haloStyle);
            halo.setEffect(new GaussianBlur(4));

            BooleanProperty winningCard = new SimpleBooleanProperty();

            if (trickBean.winningPlayerProperty() != null) {
                winningCard.bind(trickBean.winningPlayerProperty()
                        .isEqualTo(ownId.next(i)));
            }
            halo.opacityProperty()
                    .bind(Bindings.when(winningCard).then(1d).otherwise(0d));

            StackPane sPane = new StackPane(halo, imageView);

            // Creates the final version of the image (card + name + halo if
            // needed) and puts it in the map
            VBox vBox = new VBox();
            if (i == 0)
                vBox = new VBox(sPane, new Text(playerNames.get(ownId)));
            else
                vBox = new VBox(new Text(playerNames.get(ownId.next(i))),
                        sPane);
            vBox.setStyle(style);
            cards.put(ownId.next(i), vBox);
        }

        // Creates the image of the Trick's trump color, placed in the center of
        // the Pane
        ImageView trump = new ImageView();
        trump.imageProperty().bind(
                Bindings.valueAt(TRUMP_IMAGES, trickBean.trumpProperty()));

        setDimensions(trumpDim, trumpDim, trump);

        // Places the cards in the Pane, starting from the bottom card which is
        // that of the ownId, and we continue placing the cards in an
        // anti-clockwise movement
        trickPane.add(cards.get(ownId), 1, 2);
        trickPane.add(cards.get(ownId.next(1)), 2, 1, 1, 1);
        trickPane.add(cards.get(ownId.next(2)), 1, 0);
        trickPane.add(cards.get(ownId.next(3)), 0, 1, 1, 1);

        trickPane.add(trump, 1, 1);

        GridPane.setHalignment(trump, HPos.CENTER);

        return trickPane;
    }

    /*
     * Method setting the dimension of one or multiple images. It is used to
     * simplify the code's visibility
     */
    private void setDimensions(int width, int height, ImageView... images) {
        for (ImageView image : images) {
            image.setFitHeight(height);
            image.setFitWidth(width);
        }
    }

    /*
     * Creates the Pane containing the Hand of the interface's Player owner,
     * which will be updated through the given HandBean and trickBean
     */
    private HBox createHandPane(HandBean handBean, TrickBean trickBean) {
        HBox handPane = new HBox();
        handPane.setStyle(
                "-fx-background-color: lightgray; -fx-spacing: 5px; -fx-padding: 5px; -fx-alignment: center;");
        int width = 80; // Cards' width
        int height = 120; // Cards' height

        for (int i = 0; i < Jass.HAND_SIZE; ++i) {
            int n = i;
            ImageView card = new ImageView();
            card.imageProperty().bind(Bindings.valueAt(CARDS_IMAGES_160,
                    Bindings.valueAt(handBean.hand(), i)));

            // Binds the image to the fact that the Card it represents is
            // playable or not
            ObservableBooleanValue isPlayable = Bindings
                    .createBooleanBinding(() -> {
                        return handBean.playableCards()
                                .contains(handBean.hand().get(n));
                    }, handBean.playableCards(), handBean.hand());

            // Put the clicked on Card in the queue so it will be played if
            // playable and if the queue is not full
            card.setOnMouseClicked(e -> {
                if (handBean.hand().get(n) != null) {
                    try {
                        queue.put(handBean.hand().get(n));
                    } catch (InterruptedException ie) {
                        throw new Error();
                    }
                }
            });

            // Binds the opacity and of the image to the fact that it is
            // playable or not
            card.opacityProperty()
                    .bind(Bindings.when(isPlayable).then(1.0).otherwise(0.2));

            // If the Card is not playable, clicking on it with the mouse won't
            // do anything
            card.disableProperty().bind(Bindings.not(isPlayable));

            setDimensions(width, height, card);
            handPane.getChildren().add(card);
        }

        return handPane;
    }

    /*
     * Creates the Panes showing that a given Team has won the game, with the
     * final Score reached by the given ScoreBean
     */
    private BorderPane createVictoryPanes(ScoreBean scoreBean, TeamId team) {

        BorderPane victoryPane = new BorderPane();
        String style = "-fx-font: 16 Optima; -fx-background-color: white;";

        StringJoiner winners = new StringJoiner(" et ");

        for (PlayerId player : PlayerId.ALL)
            if (player.team() == team)
                winners.add(playerNames.get(player));

        Text winningScore = new Text();
        winningScore.textProperty()
                .bind(Bindings.convert(scoreBean.totalPointsProperty(team)));

        Text losingScore = new Text();
        losingScore.textProperty().bind(
                Bindings.convert(scoreBean.totalPointsProperty(team.other())));

        Text message = new Text();
        message.textProperty()
                .bind(Bindings.concat(winners.toString() + " ont gagné avec ",
                        winningScore.textProperty(), " points contre ",
                        losingScore.textProperty()));

        BooleanProperty b = new SimpleBooleanProperty();
        b.bind(scoreBean.winningTeamProperty().isEqualTo(team));

        // The opacity of the Pane is 0 as long as the game is not over and the
        // owner Team of the Pane is not the winner. When both these events
        // happen, the opacity is 1 and the Pane appears
        victoryPane.opacityProperty()
                .bind((Bindings.when(b).then(1.0).otherwise(0.0)));
        victoryPane.setCenter(message);
        victoryPane.setStyle(style);

        return victoryPane;
    }

    // Fills the Map containing the Card Images
    private static ObservableMap<Card, Image> fillCardsMap(int pixelWidth) {
        ObservableMap<Card, Image> map = FXCollections.observableHashMap();

        for (int i = 0; i < Card.Color.COUNT; ++i) {
            for (int j = 0; j < Card.Rank.COUNT; ++j) {
                String url = String.format("/card_%d_%d_%d.png", i, j,
                        pixelWidth);
                map.put(Card.of(Card.Color.ALL.get(i), Card.Rank.ALL.get(j)),
                        new Image(url));
            }
        }
        return FXCollections.unmodifiableObservableMap(map);
    }

    // Fills the Map containing the Trump images
    private static ObservableMap<Card.Color, Image> fillTrumpMap() {
        ObservableMap<Card.Color, Image> map = FXCollections
                .observableHashMap();

        for (int i = 0; i < 4; ++i) {
            String url = String.format("/trump_%d.png", i);
            map.put(Card.Color.ALL.get(i), new Image(url));
        }
        return FXCollections.unmodifiableObservableMap(map);
    }

}