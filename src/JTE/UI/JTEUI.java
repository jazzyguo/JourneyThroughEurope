package JTE.UI;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JEditorPane;

import application.Main.JTEPropertyType;

import properties_manager.PropertiesManager;

import JTE.file.JTEFileLoader;
import JTE.game.JTEGameData;
import JTE.game.JTEGameData.Card;
import JTE.game.JTEGameData.City;
import JTE.game.JTEGameData.Coordinates;
import JTE.game.JTEGameData.Die;
import JTE.game.JTEGameData.Player;
import JTE.game.JTEGameStateManager;
import static JTE.game.JTEGameStateManager.JTEGameState.GAME_IN_PROGRESS;
import static JTE.game.JTEGameStateManager.JTEGameState.GAME_OVER;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Array;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import static javafx.scene.paint.Color.color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.BorderFactory;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;

public class JTEUI extends Pane {

    private Stage primaryStage;
    private Button diceButton;
    private Text diceValue;

    public enum JTEUIState {

        SPLASH_SCREEN_STATE,
        PLAY_GAME_STATE,
        SELECT_PLAYERS_STATE,
        VIEW_ABOUT_STATE, VIEW_GAME_ABOUT_STATE,
        VIEW_HISTORY_STATE,
        MAP1_STATE, MAP2_STATE, MAP3_STATE, MAP4_STATE,
    }

    private BorderPane mainPane;

    // mainPane weight && height
    private int paneWidth;
    private int paneHeigth;

    // SplashScreen
    private ImageView splashScreenImageView;
    private StackPane splashScreenPane;
    private Label splashScreenImageLabel;
    private FlowPane optionSelectionPane;
    //MapImage
    Image mapQuadrant1 = new Image("file:images/map1.jpg");
    Image mapQuadrant2 = new Image("file:images/map2.jpg");
    Image mapQuadrant3 = new Image("file:images/map3.jpg");
    Image mapQuadrant4 = new Image("file:images/map4.jpg");

    // GamePane
    private BorderPane gamePanel;
    private Canvas map;
    private Canvas flightMap;
    private GraphicsContext gc;
    private Button flight;
    private Button gameHistory;
    private Button gameAbout;
    private Button save;
    private FlowPane leftPane;
    private VBox rightPane;
    private FlowPane mapView;
    private int currentMap;
    private Button endTurn;
    Player currentPlayer;
    private Text currentPlayerName;
    Die die;
    int currentRoll = -2;
    int rolled;
    //GameHistory
    private JEditorPane gameHistoryPane;
    private ScrollPane gameHistoryScrollPane;
    private HBox historyNorthToolBar;

    //PlayerPane
    private FlowPane playerPane;

    //AboutPane
    private JEditorPane aboutPane;
    private ScrollPane aboutScrollPane;
    private HBox aboutPaneNorthToolBar;
    private Button backButton;

    //SelectPlayersPane
    private FlowPane selectPlayersPane;
    private HBox selectPlayersNorthToolBar;
    private ComboBox numPlayers;
    private Button go;
    ArrayList<Player> players;
    int numOfPlayers;

    // Padding
    private Insets marginlessInsets;

    // Image path
    private String ImgPath = "file:images/";

    //cards
    private ArrayList<Card> cards = new ArrayList();
    private Button[][] cardButtons;

    // THIS CLASS WILL HANDLE ALL ACTION EVENTS FOR THIS PROGRAM
    private JTEEventHandler eventHandler;
    private JTEErrorHandler errorHandler;
    private JTEDocumentManager docManager;
    private JTEGameStateManager gsm;
    private JTEFileLoader fileLoader;

    public JTEUI() {
        gsm = new JTEGameStateManager(this);
        eventHandler = new JTEEventHandler(this);
        errorHandler = new JTEErrorHandler(getPrimaryStage());
        docManager = new JTEDocumentManager(this);
        fileLoader = new JTEFileLoader(this);
        initMainPane();
        initSplashScreen();
        initAboutPane();
        initGameHistoryScreen();
    }

    // Methods
    public void setPlayers(ArrayList<Player> players) {
        this.players = players;
    }

    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public BorderPane getMainPane() {
        return mainPane;
    }

    public Player getCurrentPlayer() {
        return currentPlayer;
    }

    public int getpaneWidth() {
        return paneWidth;
    }

    public int getpaneHeight() {
        return paneHeigth;
    }

    public JTEGameStateManager getGSM() {
        return gsm;
    }

    public JTEDocumentManager getDocManager() {
        return docManager;
    }

    public JTEErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public JTEFileLoader getFileLoader() {
        return fileLoader;
    }

    public Stage getPrimaryStage() {
        return this.primaryStage;
    }

    public void setPrimaryStage(Stage primaryStage) {
        this.primaryStage = primaryStage;
    }

    public int getMap() {
        return currentMap;
    }

    public int getNumOfPlayers() {
        return numOfPlayers;
    }

    public ArrayList<Player> getPlayers() {
        return players;
    }

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(JTEPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(JTEPropertyType.WINDOW_HEIGHT));
        mainPane.resize(890, 640);
        mainPane.setPadding(marginlessInsets);
    }

    public void loadGameDataFromFile(File file) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String str;
            str = in.readLine();
            int numPlayers = Integer.parseInt(str);
            players = new ArrayList<>();
            System.out.println(numPlayers);
            while ((str = in.readLine()) != null) {
                Player player = new Player();
                players.add(player);
                player.setName(str);
                str = in.readLine();
                player.setCurrentCity(str);
                str = in.readLine();
                int x = Integer.parseInt(str);
                str = in.readLine();
                int y = Integer.parseInt(str);
                str = in.readLine();
                player.setQuadrant(Integer.parseInt(str));
                str = in.readLine();
                player.setHome(str);
                str = in.readLine();
                if (str.equals("true")) {
                    player.setHuman();
                }
                str = in.readLine();
                player.setNum(Integer.parseInt(str));
                str = in.readLine();
                player.setHomeQ(Integer.parseInt(str));
                str = in.readLine();
                int hX = Integer.parseInt(str);
                str = in.readLine();
                int hY = Integer.parseInt(str);
                player.setHomeLocation(new Coordinates(hX, hY));
                str = in.readLine();
                player.setCurrentLocation(new Coordinates(x, y));
                int numCards = Integer.parseInt(str);
                for (int c = 0; c < numCards; c++) {
                    str = in.readLine();
                    String[] cards = str.split(",");
                    String cardName = cards[0];
                    String cardColor = cards[1];
                    String frontString, backString;
                    Image front, back;
                    Card card;
                    File f, p;
                    switch (cardColor) {
                        case "red":
                            frontString = "red/" + cardName + ".jpg";
                            backString = "";
                            System.out.println(frontString);
                            f = new File("images/red/" + cardName + "_I.jpg");
                            p = new File("images/red/" + cardName + "_I.png");
                            if (f.exists()) {
                                backString = "red/" + cardName + "_I.jpg";
                                System.out.println(backString);
                                back = loadImage(backString);
                            }
                            if (p.exists()) {
                                backString = "red/" + cardName + "_I.png";
                                System.out.println(backString);
                                back = loadImage(backString);
                            }
                            if (!p.exists() && !f.exists()) {
                                backString = frontString;
                            }
                            front = loadImage(frontString);
                            back = loadImage(backString);
                            card = new Card(cardColor, cardName, front, back);
                            player.getCardsOnHand().add(card);
                            break;
                        case "green":
                            frontString = "green/" + cardName + ".jpg";
                            backString = "";
                            System.out.println(frontString);
                            f = new File("images/green/" + cardName + "_I.jpg");
                            p = new File("images/green/" + cardName + "_I.png");
                            if (f.exists()) {
                                backString = "green/" + cardName + "_I.jpg";
                                System.out.println(backString);
                                back = loadImage(backString);
                            }
                            if (p.exists()) {
                                backString = "green/" + cardName + "_I.png";
                                System.out.println(backString);
                                back = loadImage(backString);
                            }
                            if (!p.exists() && !f.exists()) {
                                backString = frontString;
                            }
                            front = loadImage(frontString);
                            back = loadImage(backString);
                            card = new Card(cardColor, cardName, front, back);
                            player.getCardsOnHand().add(card);
                            break;
                        case "yellow":
                            frontString = "yellow/" + cardName + ".jpg";
                            backString = "";
                            System.out.println(frontString);
                            f = new File("images/yellow/" + cardName + "_I.jpg");
                            p = new File("images/yellow/" + cardName + "_I.png");
                            if (f.exists()) {
                                backString = "yellow/" + cardName + "_I.jpg";
                                System.out.println(backString);
                                back = loadImage(backString);
                            }
                            if (p.exists()) {
                                backString = "yellow/" + cardName + "_I.png";
                                System.out.println(backString);
                                back = loadImage(backString);
                            }
                            if (!p.exists() && !f.exists()) {
                                backString = frontString;
                            }
                            front = loadImage(frontString);
                            back = loadImage(backString);
                            card = new Card(cardColor, cardName, front, back);
                            player.getCardsOnHand().add(card);
                            break;
                    }
                }
            }
            JTEGameData data = new JTEGameData();
            data.setPlayers(players);
            eventHandler.respondToLoadGame(data);
            getRoute();
            in.close();
        } catch (IOException e) {
        }
    }

    private void getRoute() {
        for (Player player : players) {
            int numCards = player.getCardsOnHand().size();
            switch (numCards) {
                case 1:
                    if (!player.isHuman()) {
                        ArrayList<String> route = new ArrayList();
                        route.addAll(eventHandler.data.shortestPath(player.getCurrentCity(), player.getHome()));
                        for (String s : route) {
                            player.getRoute().add(s);
                        }
                    }
                    break;
                case 2:
                    if (!player.isHuman()) {
                        ArrayList<String> route = new ArrayList();
                        route.addAll(eventHandler.data.shortestPath(player.getCurrentCity(), player.getCardsOnHand().get(1).getName()));
                        route.addAll(eventHandler.data.shortestPath(player.getCurrentCity(), player.getHome()));
                        for (String s : route) {
                            player.getRoute().add(s);
                        }
                    }
                    break;
                case 3:
                    if (!player.isHuman()) {
                        ArrayList<String> route = new ArrayList();
                        route.addAll(eventHandler.data.shortestPath(player.getCurrentCity(), player.getCardsOnHand().get(1).getName()));
                        route.addAll(eventHandler.data.shortestPath(player.getCardsOnHand().get(1).getName(), player.getCardsOnHand().get(2).getName()));
                        route.addAll(eventHandler.data.shortestPath(player.getCardsOnHand().get(2).getName(), player.getCurrentCity()));
                        for (String s : route) {
                            player.getRoute().add(s);
                        }
                    }
                    break;
            }
        }
    }

    public void initSplashScreen() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props.getProperty(JTEPropertyType.SPLASH_SCREEN_IMAGE_NAME);
        splashScreenPane = new StackPane();
        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);
        splashScreenImageView.setFitWidth(880);
        splashScreenImageView.setFitHeight(630);
        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        splashScreenPane.getChildren().add(splashScreenImageLabel);
        optionSelectionPane = new FlowPane();
        optionSelectionPane.setAlignment(Pos.BOTTOM_CENTER);
        //LOAD SPLASH SCREEN OPTION
        Button startButton = new Button();
        startButton.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image startImage = loadImage("start.png");
        ImageView startImageView = new ImageView(startImage);
        startButton.setGraphic(startImageView);
        optionSelectionPane.getChildren().add(startButton);
        Button loadButton = new Button();
        loadButton.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image loadImage = loadImage("load.png");
        ImageView loadImageView = new ImageView(loadImage);
        loadButton.setGraphic(loadImageView);
        optionSelectionPane.getChildren().add(loadButton);
        Button aboutButton = new Button();
        aboutButton.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image aboutImage = loadImage("about.png");
        ImageView aboutImageView = new ImageView(aboutImage);
        aboutButton.setGraphic(aboutImageView);
        optionSelectionPane.getChildren().add(aboutButton);
        Button quitButton = new Button();
        quitButton.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image quitImage = loadImage("Exit.png");
        ImageView quitImageView = new ImageView(quitImage);
        quitButton.setGraphic(quitImageView);
        optionSelectionPane.getChildren().add(quitButton);
        // add key listener
        startButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    initSelectPlayersScreen();
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.SELECT_PLAYERS_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        loadButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                File file = new File("save.txt");
                if (file.exists()) {
                    System.out.println("loading");
                    loadGameDataFromFile(file);
                    initPlayer();
                    gsm.currentGameState = GAME_IN_PROGRESS;
                    playGame();
                    loadGameHistory();
                }
            }

            private void loadGameHistory() {
                try {
                    BufferedReader in = new BufferedReader(new FileReader("gameHistory.txt"));
                    String str;
                    str = in.readLine();
                    str = in.readLine();
                    while ((str = in.readLine()) != null) {
                        append(str);
                    }
                    in.close();
                } catch (IOException e) {
                }
            }
        });
        aboutButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.VIEW_ABOUT_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        quitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                eventHandler.respondToExitRequest(primaryStage);
            }
        });

        mainPane.setCenter(splashScreenPane);
        splashScreenPane.getChildren().add(optionSelectionPane);
    }

    public void initPlayer() {
        for (Player player : players) {
            int num = player.getNum();
            switch (num) {
                case 1:
                    player.setImage(loadImage("p1.png"));
                    player.setFlag(loadImage("1.png"));
                    break;
                case 2:
                    player.setImage(loadImage("p2.png"));
                    player.setFlag(loadImage("2.png"));
                    break;
                case 3:
                    player.setImage(loadImage("p3.png"));
                    player.setFlag(loadImage("3.png"));
                    break;
                case 4:
                    player.setImage(loadImage("p4.png"));
                    player.setFlag(loadImage("4.png"));
                    break;
                case 5:
                    player.setImage(loadImage("p5.png"));
                    player.setFlag(loadImage("5.png"));
                    break;
                case 6:
                    player.setImage(loadImage("p6.png"));
                    player.setFlag(loadImage("6.png"));
                    break;
            }

        }
    }

    /**
     * This method initializes the language-specific game controls, which
     * includes the three primary game screens.
     *
     * @throws IOException
     */
    public void initJTEUI() throws IOException {
        // FIRST REMOVE THE SPLASH SCREEN
        mainPane.getChildren().clear();
        // GET THE UPDATED TITLE
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String title = props.getProperty(JTEPropertyType.GAME_TITLE_TEXT);
        getPrimaryStage().setTitle(title);
        initGameScreen();
        // WE'LL START OUT WITH THE GAME SCREEN
        eventHandler.respondToSwitchScreenRequest(JTEUIState.PLAY_GAME_STATE);
    }

    private void initSelectPlayersScreen() {
        selectPlayersPane = new FlowPane();
        selectPlayersPane.setStyle("-fx-background-color:orange");
        selectPlayersNorthToolBar = new HBox();
        selectPlayersNorthToolBar.setStyle("-fx-background-color:brown");
        selectPlayersNorthToolBar.setAlignment(Pos.BASELINE_LEFT);
        selectPlayersNorthToolBar.setPadding(marginlessInsets);
        selectPlayersNorthToolBar.setSpacing(10.0);
        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(selectPlayersNorthToolBar,
                JTEPropertyType.BACK2_IMG_NAME);
        //setTooltip(backButton, SokobanPropertyType.GAME_TOOLTIP);
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    // TODO Auto-generated method stub
                    eventHandler
                            .respondToSwitchScreenRequest(JTEUIState.SPLASH_SCREEN_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        numPlayers = new ComboBox();
        numPlayers.getItems().addAll("1", "2", "3", "4", "5", "6");
        Text numPlayersText = new Text();
        numPlayersText.setText("Number of Players");
        numPlayers.setValue("Select");
        numPlayers.getSelectionModel().selectedItemProperty().addListener(new ChangeListener() {
            public void changed(ObservableValue<? extends String> ov, String old_val, String new_val) {
            }

            @Override
            public void changed(ObservableValue observable, Object oldValue, Object newValue) {
                int n = Integer.parseInt((String) newValue);
                numOfPlayers = n;
                selectPlayersPane.getChildren().clear();
                players = new ArrayList<>();
                for (int i = 1; i < n + 1; i++) {
                    Player player = new Player();
                    playerPane = new FlowPane();
                    playerPane.setHgap(7);
                    String imageString = "" + i + ".png";
                    Image playerImage = loadImage(imageString);
                    ImageView playerImageView = new ImageView(playerImage);
                    playerImageView.setFitHeight(160);
                    playerImageView.setFitWidth(160);
                    playerPane.getChildren().add(playerImageView);
                    playerPane.setStyle("-fx-background-color:orange");
                    playerPane.setStyle("-fx-border-color: black");
                    RadioButton playerButton = new RadioButton();
                    playerButton.setText("Player");
                    RadioButton computerButton = new RadioButton();
                    computerButton.setText("Computer");
                    TextField playerName = new TextField();
                    playerName.setText("Player " + i);
                    ToggleGroup group = new ToggleGroup();
                    playerButton.setToggleGroup(group);
                    computerButton.setToggleGroup(group);
                    playerButton.setOnAction(e -> player.setHuman());
                    computerButton.setOnAction(e -> player.setComputer());
                    player.setName(playerName.getText());
                    playerName.setOnKeyTyped(e -> player.setName(playerName.getText()));
                    players.add(player);
                    playerPane.getChildren().addAll(playerButton, computerButton, playerName);
                    selectPlayersPane.getChildren().add(playerPane);
                }
            }
        });
        go = new Button();
        go.setText("GO");
        go.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                gsm.currentGameState = GAME_IN_PROGRESS;
                eventHandler.respondToNewGameRequest();
                playGame();
            }
        });
        selectPlayersNorthToolBar.getChildren().addAll(numPlayersText, numPlayers, go);

    }

    private void initGameAboutPane() {
        aboutPane = new JEditorPane();
        aboutPane.setEditable(false);
        aboutPane.setContentType("text/html");
        aboutPane.setPreferredSize(new Dimension(800, 800));
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(aboutPane);
        aboutPane.setText("Journey Through Europe Wiki");
        aboutScrollPane = new ScrollPane();
        aboutScrollPane.setPrefSize(800, 800);
        aboutScrollPane.setContent(swingNode);
        aboutPaneNorthToolBar = new HBox();
        aboutPaneNorthToolBar.setStyle("-fx-background-color:orange");
        aboutPaneNorthToolBar.setAlignment(Pos.BASELINE_LEFT);
        aboutPaneNorthToolBar.setPadding(marginlessInsets);
        aboutPaneNorthToolBar.setSpacing(10.0);

        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(aboutPaneNorthToolBar,
                JTEPropertyType.BACK2_IMG_NAME);
        //setTooltip(backButton, SokobanPropertyType.GAME_TOOLTIP);
        backButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    // TODO Auto-generated method stub
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.PLAY_GAME_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        URL url;
        try {
            url = new URL("http://en.wikipedia.org/wiki/Journey_Through_Europe");
            URLConnection con = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String text = "";
            while ((inputLine = br.readLine()) != null) {
                text += inputLine;
            }
            aboutPane.setText(text);
            br.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    private void initAboutPane() {
        aboutPane = new JEditorPane();
        aboutPane.setEditable(false);
        aboutPane.setContentType("text/html");
        aboutPane.setPreferredSize(new Dimension(800, 800));
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(aboutPane);
        aboutPane.setText("Journey Through Europe Wiki");
        aboutScrollPane = new ScrollPane();
        aboutScrollPane.setPrefSize(800, 800);
        aboutScrollPane.setContent(swingNode);
        aboutPaneNorthToolBar = new HBox();
        aboutPaneNorthToolBar.setStyle("-fx-background-color:orange");
        aboutPaneNorthToolBar.setAlignment(Pos.BASELINE_LEFT);
        aboutPaneNorthToolBar.setPadding(marginlessInsets);
        aboutPaneNorthToolBar.setSpacing(10.0);

        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(aboutPaneNorthToolBar,
                JTEPropertyType.BACK2_IMG_NAME);
        //setTooltip(backButton, SokobanPropertyType.GAME_TOOLTIP);
        backButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    // TODO Auto-generated method stub
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.SPLASH_SCREEN_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        URL url;
        try {
            url = new URL("http://en.wikipedia.org/wiki/Journey_Through_Europe");
            URLConnection con = url.openConnection();
            BufferedReader br = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String inputLine;
            String text = "";
            while ((inputLine = br.readLine()) != null) {
                text += inputLine;
            }
            aboutPane.setText(text);
            br.close();
        } catch (MalformedURLException ex) {
            Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            System.out.println(ex);
        }
    }

    public void append(String s) {
        try {
            Document doc = gameHistoryPane.getDocument();
            doc.insertString(doc.getLength(), s, null);
        } catch (BadLocationException exc) {
            exc.printStackTrace();
        }
    }

    private void initGameHistoryScreen() {
        gameHistoryPane = new JEditorPane();
        gameHistoryPane.setEditable(false);
        gameHistoryPane.setContentType("text/html");
        gameHistoryPane.setPreferredSize(new Dimension(800, 800));
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(gameHistoryPane);
        gameHistoryPane.setText("Game History");
        Document doc = gameHistoryPane.getDocument();
        gameHistoryScrollPane = new ScrollPane();
        gameHistoryScrollPane.setPrefSize(800, 800);
        gameHistoryScrollPane.setContent(swingNode);
        gameHistoryScrollPane.setHbarPolicy(ScrollBarPolicy.NEVER);
        gameHistoryScrollPane.setVbarPolicy(ScrollBarPolicy.ALWAYS);
        historyNorthToolBar = new HBox();
        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(historyNorthToolBar,
                JTEPropertyType.BACK2_IMG_NAME);
        //setTooltip(backButton, SokobanPropertyType.GAME_TOOLTIP);
        backButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    // TODO Auto-generated method stub
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.PLAY_GAME_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
    }

    private Button initToolbarButton(HBox toolbar, JTEPropertyType prop) {
        // GET THE NAME OF THE IMAGE, WE DO THIS BECAUSE THE
        // IMAGES WILL BE NAMED DIFFERENT THINGS FOR DIFFERENT LANGUAGES
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imageName = props.getProperty(prop);

        // LOAD THE IMAGE
        Image image = loadImage(imageName);
        ImageView imageIcon = new ImageView(image);

        // MAKE THE BUTTON
        Button button = new Button();
        button.setGraphic(imageIcon);
        button.setPadding(marginlessInsets);

        // PUT IT IN THE TOOLBAR
        toolbar.getChildren().add(button);

        // AND SEND BACK THE BUTTON
        return button;
    }

    /**
     * The workspace is a panel that will show different screens depending on
     * the user's requests.
     */
    public void initGameScreen() {
        die = eventHandler.data.getDie();
        gamePanel = new BorderPane();
        leftPane = new FlowPane(Orientation.VERTICAL);
        leftPane.resize(170, 640);
        leftPane.setStyle("-fx-background-color:orange");
        rightPane = new VBox();
        rightPane.resize(170, 640);
        rightPane.setStyle("-fx-background-color:orange");
        map = new Canvas();
        map.setWidth(500);
        map.setHeight(640);
        gc = map.getGraphicsContext2D();
        //rightPane.getChildren().add(dice);
        gamePanel.setCenter(map);
        //map.setOnMouseClicked(new EventHandler<MouseEvent>() {
        //  @Override
        // public void handle(MouseEvent event) {
        //     eventHandler.mouseClicked(event);
        // }
        // });
        flight = new Button();
        flight.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image flightImage = loadImage("Flight Plan.jpg");
        ImageView flightImageView = new ImageView(flightImage);
        flightImageView.setFitWidth(110);
        flightImageView.setPreserveRatio(true);
        flightImageView.setSmooth(true);
        flightImageView.setCache(true);
        flight.setGraphic(flightImageView);
        flightMap = new Canvas();
        flightMap.setHeight(550);
        flightMap.setWidth(500);
        GraphicsContext gc2 = flightMap.getGraphicsContext2D();
        gc2.drawImage(flightImage, 0, 0);
        flightMap.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                ArrayList<City> flightCities = eventHandler.data.getFlightCities();
                ArrayList<City> cities = eventHandler.data.getCities();
                double mouseX = event.getX();
                double mouseY = event.getY();
                System.out.println(mouseX + "," + mouseY);
                int n = flightCities.size();
                for (int x = 0; x < n; x++) {
                    if (currentPlayer.getCurrentCity().equals(flightCities.get(x).getName())) {
                        for (int i = 0; i < n; i++) {
                            double citiesX = flightCities.get(i).getCoordinates().getX();
                            double citiesY = flightCities.get(i).getCoordinates().getY();
                            if ((mouseX > citiesX - 5 && mouseX < citiesX + 5)
                                    && (mouseY > citiesY - 5 && mouseY < citiesY + 5)) {
                                City clickedCity = flightCities.get(i);
                                for (int c = 0; c < cities.size(); c++) {
                                    if (clickedCity.getName().equals(cities.get(c).getName())) {
                                        City moveCity = cities.get(c);
                                        if (!currentPlayer.getCurrentCity().equals(moveCity.getName())) {
                                            if (currentRoll >= 2) {
                                                if (clickedCity.getQuadrant() == currentPlayer.getQuadrant()) {
                                                    currentRoll--;
                                                    moveAnimation(currentPlayer.getCurrentLocation(), moveCity);
                                                    diceValue.setText("Current Points: " + currentRoll);
                                                }
                                                if (clickedCity.getQuadrant() != currentPlayer.getQuadrant()
                                                        && currentRoll >= 4) {
                                                    currentRoll = currentRoll - 3;
                                                    moveToQuadrant(currentPlayer.getCurrentLocation(), moveCity);
                                                    diceValue.setText("Current Points: " + currentRoll);
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        });
        flight.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                Stage dialogStage = new Stage();
                Pane pane = new Pane();
                dialogStage.initModality(Modality.WINDOW_MODAL);
                dialogStage.initOwner(primaryStage);
                pane.getChildren().add(flightMap);
                Scene scene = new Scene(pane, 500, 550);
                dialogStage.setScene(scene);
                dialogStage.show();
            }
        });
        gameAbout = new Button();
        gameAbout.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image aboutImage = loadImage("gameabout.png");
        ImageView aboutImageView = new ImageView(aboutImage);
        gameAbout.setGraphic(aboutImageView);
        gameAbout.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                try {
                    // TODO Auto-generated method stub
                    initGameAboutPane();
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.VIEW_GAME_ABOUT_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        gameHistory = new Button();
        gameHistory.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image historyImage = loadImage("history.png");
        ImageView historyImageView = new ImageView(historyImage);
        gameHistory.setGraphic(historyImageView);
        gameHistory.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                try {
                    // TODO Auto-generated method stub                   
                    eventHandler.respondToSwitchScreenRequest(JTEUIState.VIEW_HISTORY_STATE);
                } catch (IOException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        save = new Button();
        save.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image saveImage = loadImage("save.png");
        ImageView saveImageView = new ImageView(saveImage);
        save.setGraphic(saveImageView);
        save.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                eventHandler.data.saveGameDataToFile(new File("save.txt"));
                Document doc = gameHistoryPane.getDocument();
                saveGameHistory(doc);
            }

            private void saveGameHistory(Document doc) {
                PrintWriter writer = null;
                try {
                    writer = new PrintWriter(new File("gameHistory.txt"), "UTF-8");
                    try {
                        writer.println(doc.getText(0, doc.getLength()));
                    } catch (BadLocationException ex) {
                        Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    writer.close();
                } catch (FileNotFoundException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                } catch (UnsupportedEncodingException ex) {
                    Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        });
        mapView = new FlowPane();
        mapView.setPrefWrapLength(160);
        diceButton = new Button();
        die.setRoll(6);
        die.setImage();
        ImageView diceImage = new ImageView(die.getImg());
        diceButton.setGraphic(diceImage);
        diceButton.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                diceAnimation();
            }
        });
        Button map1 = new Button();
        Image map1Image = loadImage("mapb1.jpg");
        ImageView map1ImageView = new ImageView(map1Image);
        map1.setGraphic(map1ImageView);
        Button map2 = new Button();
        Image map2Image = loadImage("mapb2.jpg");
        ImageView map2ImageView = new ImageView(map2Image);
        map2.setGraphic(map2ImageView);
        Button map3 = new Button();
        Image map3Image = loadImage("mapb3.jpg");
        ImageView map3ImageView = new ImageView(map3Image);
        map3.setGraphic(map3ImageView);
        Button map4 = new Button();
        Image map4Image = loadImage("mapb4.jpg");
        ImageView map4ImageView = new ImageView(map4Image);
        map4.setGraphic(map4ImageView);
        map1.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 1;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant1);
                if (currentPlayer.getQuadrant() == currentMap) {
                    showRedLines();
                }
            }
        });
        map2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 2;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant2);
                if (currentPlayer.getQuadrant() == currentMap) {
                    showRedLines();
                }
            }
        });
        map3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 3;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant3);
                if (currentPlayer.getQuadrant() == currentMap) {
                    showRedLines();
                }
            }
        });
        map4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 4;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant4);
                if (currentPlayer.getQuadrant() == currentMap) {
                    showRedLines();
                }
            }
        });
        Button quitButton = new Button();
        quitButton.setStyle("-fx-background-color:orange;-fx-border-color: brown");
        Image quitImage = loadImage("quit.png");
        ImageView quitImageView = new ImageView(quitImage);
        quitButton.setGraphic(quitImageView);
        quitButton.setOnAction(new EventHandler<ActionEvent>() {

            @Override
            public void handle(ActionEvent event) {
                eventHandler.respondToExitRequest(primaryStage);
            }
        });
        diceValue = new Text();
        mapView.getChildren().addAll(map1, map2, map3, map4);
        rightPane.getChildren().addAll(diceButton, diceValue, mapView, flight, gameAbout, gameHistory, save, quitButton);
        gamePanel.setRight(rightPane);
        gamePanel.setLeft(leftPane);
    }

    public Image loadImage(String imageName) {
        Image img = new Image(ImgPath + imageName);
        return img;
    }

    /**
     * This function selects the UI screen to display based on the uiScreen
     * argument. Note that we have 3 such screens: game, stats, and help.
     *
     * @param uiScreen The screen to be switched to.
     * @throws IOException
     */
    public void changeWorkspace(JTEUIState uiScreen) throws IOException {
        switch (uiScreen) {
            case SPLASH_SCREEN_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(splashScreenPane);
                primaryStage.setTitle("Journey Through Europe");
                break;
            case VIEW_HISTORY_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(gameHistoryScrollPane);
                mainPane.setTop(historyNorthToolBar);
                primaryStage.setTitle("Current Game History");
                break;
            case SELECT_PLAYERS_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(selectPlayersPane);
                mainPane.setTop(selectPlayersNorthToolBar);
                primaryStage.setTitle("Select Players");
                break;
            case PLAY_GAME_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(gamePanel);
                primaryStage.setTitle("Journey Through Europe Game");
                break;
            case VIEW_GAME_ABOUT_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(aboutScrollPane);
                mainPane.setTop(aboutPaneNorthToolBar);
                primaryStage.setTitle("Journey Through Europe Wiki");
                break;
            case VIEW_ABOUT_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(aboutScrollPane);
                mainPane.setTop(aboutPaneNorthToolBar);
                primaryStage.setTitle("Journey Through Europe Wiki");
                break;
            default:
        }
    }

    public void moveAnimation(Coordinates currentLocation, City city) {
        append("\n" + currentPlayer.getName() + " moved from " + currentPlayer.getCurrentCity() + " to " + city.getName() + ".");
        currentRoll--;
        AnimationTimer timer = new AnimationTimer() {
            int i = 0;
            long previous = -1;
            double currentX = currentLocation.getX();
            double currentY = currentLocation.getY();
            double destX = city.getCoordinates().getX();
            double destY = city.getCoordinates().getY();
            double differenceX = (city.getCoordinates().getX() - currentLocation.getX()) / 50;
            double differenceY = (city.getCoordinates().getY() - currentLocation.getY()) / 50;

            @Override
            public void handle(long now) {

                if (i == 50) {
                    stop();
                    if (!currentPlayer.isHuman() && currentRoll > 0) {
                        showPlayerQuadrant();
                        computerMove();
                    }
                    if (!currentPlayer.isHuman() && currentRoll == 0) {
                        diceValue.setText("End of Turn");
                    }
                    for (int x = 0; x < currentPlayer.getCardsOnHand().size(); x++) {
                        if (currentPlayer.getCurrentCity().equals(currentPlayer.getCardsOnHand().get(x).getName())
                                && !currentPlayer.getHome().equals(currentPlayer.getCardsOnHand().get(x).getName())) {
                            currentPlayer.getCardsOnHand().remove(x);
                        }
                    }
                    if (currentPlayer.getCardsOnHand().size() == 1 && currentPlayer.getCurrentCity().equals(currentPlayer.getHome())) {
                        winGame();
                    }
                }
                if (previous == -1) {
                    previous = now;
                }
                if (now - previous > 100000000) {
                    gc.clearRect(0, 0, 550, 640);
                    currentX = currentX + differenceX;
                    currentY = currentY + differenceY;
                    currentPlayer.setCurrentLocation(new Coordinates(currentX, currentY));
                    currentPlayer.setCurrentCity(city.getName());
                    showPlayerQuadrant();
                    showRedLines();
                    i++;
                }
            }
        };
        timer.start();
    }

    public void moveToQuadrant(Coordinates currentLocation, City city) {
        append("\n" + currentPlayer.getName() + " moved from " + currentPlayer.getCurrentCity() + " to " + city.getName() + ".");
        currentRoll--;
        currentPlayer.setQuadrant(city.getQuadrant());
        currentPlayer.setCurrentLocation(city.getCoordinates());
        currentPlayer.setCurrentCity(city.getName());
        for (int i = 0; i < currentPlayer.getCardsOnHand().size(); i++) {
            if (currentPlayer.getCurrentCity().equals(currentPlayer.getCardsOnHand().get(i).getName())
                    && !currentPlayer.getHome().equals(currentPlayer.getCardsOnHand().get(i).getName())) {
                currentPlayer.getCardsOnHand().remove(i);
            }
        }
        if (currentPlayer.getCardsOnHand().size() == 1 && currentPlayer.getCurrentCity().equals(currentPlayer.getHome())) {
            winGame();
        }
        if (!currentPlayer.isHuman() && currentRoll > 0) {
            showPlayerQuadrant();
            computerMove();
        }
        if (!currentPlayer.isHuman() && currentRoll == 0) {
            diceValue.setText("End of Turn");
        }
        showPlayerQuadrant();
        showRedLines();
    }

    public void winGame() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        BorderPane exitPane = new BorderPane();
        Button yesButton = new Button();
        yesButton.setText("" + currentPlayer.getName() + " has won the game!");
        exitPane.setCenter(yesButton);
        Scene scene = new Scene(exitPane, 250, 100);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        yesButton.setOnAction(e -> {
            try {
                eventHandler.respondToSwitchScreenRequest(JTEUIState.SPLASH_SCREEN_STATE);
                dialogStage.close();
            } catch (IOException ex) {
                Logger.getLogger(JTEUI.class.getName()).log(Level.SEVERE, null, ex);
            }
        });
    }

    public void drawTempPlayers() {
        if (currentPlayer.getQuadrant() == currentMap) {
            gc.drawImage(currentPlayer.getImage(), currentPlayer.getTempLocation().getX() - 25,
                    currentPlayer.getTempLocation().getY() - 45, 50, 50);
            if (currentPlayer.getHomeQ() == currentMap) {
                gc.drawImage(currentPlayer.getFlag(), currentPlayer.getHomeLocation().getX() - 21,
                        currentPlayer.getHomeLocation().getY() - 45, 50, 50);
            }
        }
    }

    public void drawPlayers() {
        for (JTEGameData.Player player : eventHandler.data.getPlayers()) {
            if (player.getQuadrant() == currentMap) {
                gc.drawImage(player.getImage(), player.getCurrentLocation().getX() - 25,
                        player.getCurrentLocation().getY() - 45, 50, 50);
                if (player.getHomeQ() == currentMap) {
                    gc.drawImage(player.getFlag(), player.getHomeLocation().getX() - 21,
                            player.getHomeLocation().getY() - 45, 50, 50);
                }
            }
        }
    }

    public void diceAnimation() {
        AnimationTimer timer = new AnimationTimer() {
            int i = 0;

            @Override
            public void handle(long now) {
                if (i < 20) {
                    die.roll();
                    die.setImage();
                    ImageView diceImage = new ImageView(die.getImg());
                    diceButton.setGraphic(diceImage);
                    i++;
                }
                if (i == 20) {
                    stop();
                    currentRoll = die.getRoll();
                    rolled = currentRoll;
                    append("\n" + currentPlayer.getName() + " rolled " + rolled + ".");
                    diceValue.setText("Current Points: " + currentRoll);
                    diceButton.setDisable(true);
                    if (!currentPlayer.isHuman()) {
                        computerMove();
                    }
                }
            }
        };
        timer.start();
    }

    public void computerMove() {
        String cityName = currentPlayer.getRoute().poll();
        for (City city : eventHandler.data.getCities()) {
            if (city.getName().equals(cityName)) {
                City moveCity = city;
                if (currentPlayer.getQuadrant() == moveCity.getQuadrant()) {
                    moveAnimation(currentPlayer.getCurrentLocation(), moveCity);
                } else {
                    moveToQuadrant(currentPlayer.getCurrentLocation(), moveCity);
                }
                diceValue.setText("Current Points: " + currentRoll);
            }
        }
    }

    public void playGame() {
        cardButtons = new Button[players.size()][10];
        currentPlayer = players.get(0);
        endTurn = new Button();
        endTurn.setText("End Turn");
        leftPane.getChildren().add(endTurn);
        currentPlayerName = new Text();
        currentPlayerName.setText(currentPlayer.getName());
        leftPane.getChildren().add(currentPlayerName);
        showPlayerQuadrant();
        showCards();
        showRedLines();
        map.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (currentRoll > 0) {
                    eventHandler.moveTo(event, currentPlayer, gc);
                    diceValue.setText("Current Points: " + currentRoll);
                    showCards();
                }
                if (currentRoll == 0) {
                    diceValue.setText("End of Turn");
                }
                if (currentRoll == -2) {
                    diceValue.setText("Roll the dice");
                }
                eventHandler.mouseClicked(event);
            }
        });
        map.setOnMouseDragged(new EventHandler<MouseEvent>() {
            public void handle(MouseEvent event) {
                Coordinates temp = new Coordinates(event.getX(), event.getY());
                currentPlayer.setTempLocation(temp);
                gc.clearRect(0, 0, 550, 640);
                drawMapQuadrant();
                drawTempPlayers();
                if (currentMap == currentPlayer.getQuadrant()) {
                    showRedLines();
                }
                if (currentRoll == 0) {
                    diceValue.setText("End of Turn");
                }
                if (currentRoll == -2) {
                    diceValue.setText("Roll the dice");
                }
                map.setOnMouseReleased(new EventHandler<MouseEvent>() {
                    @Override
                    public void handle(MouseEvent event) {
                        gc.clearRect(0, 0, 550, 640);
                        drawMapQuadrant();
                        drawPlayers();
                        if (currentMap == currentPlayer.getQuadrant()) {
                            showRedLines();
                        }
                    }
                });
            }
        });

        map.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                if (currentRoll > 0) {
                    eventHandler.dragged(event, currentPlayer, gc);
                    diceValue.setText("Current Points: " + currentRoll);
                    showCards();
                }
                if (currentRoll == 0) {
                    diceValue.setText("End of Turn.");
                }
                if (currentRoll == -2) {
                    diceValue.setText("Roll the dice.");
                }
            }
        });
        endTurn.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                int playerTurn = currentPlayer.getNum();
                if (eventHandler.data.getPlayers().size() == 1) {
                    switch (playerTurn) {
                        case 1:
                            currentPlayer = eventHandler.data.getPlayers().get(0);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                    }
                }
                if (eventHandler.data.getPlayers().size() == 2) {
                    switch (playerTurn) {
                        case 1:
                            currentPlayer = eventHandler.data.getPlayers().get(1);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 2:
                            currentPlayer = eventHandler.data.getPlayers().get(0);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                    }
                }
                if (eventHandler.data.getPlayers().size() == 3) {
                    switch (playerTurn) {
                        case 1:
                            currentPlayer = eventHandler.data.getPlayers().get(1);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 2:
                            currentPlayer = eventHandler.data.getPlayers().get(2);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 3:
                            currentPlayer = eventHandler.data.getPlayers().get(0);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                    }
                }
                if (eventHandler.data.getPlayers().size() == 4) {
                    switch (playerTurn) {
                        case 1:
                            currentPlayer = eventHandler.data.getPlayers().get(1);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 2:
                            currentPlayer = eventHandler.data.getPlayers().get(2);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 3:
                            currentPlayer = eventHandler.data.getPlayers().get(3);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 4:
                            currentPlayer = eventHandler.data.getPlayers().get(0);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                    }
                }
                if (eventHandler.data.getPlayers().size() == 5) {
                    switch (playerTurn) {
                        case 1:
                            currentPlayer = eventHandler.data.getPlayers().get(1);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 2:
                            currentPlayer = eventHandler.data.getPlayers().get(2);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 3:
                            currentPlayer = eventHandler.data.getPlayers().get(3);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 4:
                            currentPlayer = eventHandler.data.getPlayers().get(4);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 5:
                            currentPlayer = eventHandler.data.getPlayers().get(0);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                    }
                }
                if (eventHandler.data.getPlayers().size() == 6) {
                    switch (playerTurn) {
                        case 1:
                            currentPlayer = eventHandler.data.getPlayers().get(1);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 2:
                            currentPlayer = eventHandler.data.getPlayers().get(2);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 3:
                            currentPlayer = eventHandler.data.getPlayers().get(3);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 4:
                            currentPlayer = eventHandler.data.getPlayers().get(4);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 5:
                            currentPlayer = eventHandler.data.getPlayers().get(5);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                        case 6:
                            currentPlayer = eventHandler.data.getPlayers().get(0);
                            currentPlayerName.setText(currentPlayer.getName());
                            showCards();
                            enableDice();
                            showPlayerQuadrant();
                            showRedLines();
                            if (!currentPlayer.isHuman()) {
                                diceButton.fire();
                            }
                            break;
                    }
                }
            }
        });
        if (!currentPlayer.isHuman()) {
            diceButton.fire();
        }
    }

    public void showRedLines() {
        ArrayList<String> availableCities = new ArrayList();
        if (eventHandler.data.getCityLandNeighbors().get(currentPlayer.getCurrentCity()) != null) {
            availableCities.addAll(Arrays.asList(eventHandler.data.getCityLandNeighbors().get(currentPlayer.getCurrentCity())));
        }
        if (eventHandler.data.getCitySeaNeighbors().get(currentPlayer.getCurrentCity()) != null) {
            availableCities.addAll(Arrays.asList(eventHandler.data.getCitySeaNeighbors().get(currentPlayer.getCurrentCity())));
        }
        for (String string : availableCities) {
            for (City city : eventHandler.data.getCities()) {
                if (city.getName().equals(string) && currentPlayer.getQuadrant() == city.getQuadrant()) {
                    gc.setStroke(Color.RED);
                    gc.setLineWidth(5);
                    gc.strokeLine(currentPlayer.getCurrentLocation().getX(), currentPlayer.getCurrentLocation().getY(),
                            city.getCoordinates().getX(), city.getCoordinates().getY());
                }
            }
        }
    }

    public void drawMapQuadrant() {
        switch (currentMap) {
            case 1:
                gc.drawImage(mapQuadrant1, 0, 0);
                drawPlayers();
                break;
            case 2:
                gc.drawImage(mapQuadrant2, 0, 0);
                drawPlayers();
                break;
            case 3:
                gc.drawImage(mapQuadrant3, 0, 0);
                drawPlayers();
                break;
            case 4:
                gc.drawImage(mapQuadrant4, 0, 0);
                drawPlayers();
                break;
        }
    }

    public void showPlayerQuadrant() {
        int currentPlayerQ = currentPlayer.getQuadrant();
        currentMap = currentPlayerQ;
        switch (currentPlayerQ) {
            case 1:
                currentMap = 1;
                gc.drawImage(mapQuadrant1, 0, 0);
                drawPlayers();
                break;
            case 2:
                currentMap = 2;
                gc.drawImage(mapQuadrant2, 0, 0);
                drawPlayers();
                break;
            case 3:
                currentMap = 3;
                gc.drawImage(mapQuadrant3, 0, 0);
                drawPlayers();
                break;
            case 4:
                currentMap = 4;
                gc.drawImage(mapQuadrant4, 0, 0);
                drawPlayers();
                break;
        }
    }

    public void enableDice() {
        currentRoll = -2;
        diceButton.setDisable(false);
    }

    public void showCards() {
        leftPane.getChildren().clear();
        leftPane.getChildren().add(currentPlayerName);
        leftPane.getChildren().add(endTurn);
        for (int i = 0; i < currentPlayer.getCardsOnHand().size(); i++) {
            Button button = new Button();
            button.setId("" + i + "");
            ImageView iv2 = new ImageView();
            button.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent event) {
                    Stage dialogStage = new Stage();
                    dialogStage.initModality(Modality.WINDOW_MODAL);
                    dialogStage.initOwner(primaryStage);
                    Pane pane = new Pane();
                    Button back = new Button();
                    ImageView iv = new ImageView();
                    iv.setImage(currentPlayer.getCardsOnHand().get(Integer.parseInt(button.getId())).getBack());
                    iv.setFitWidth(250);
                    iv.setPreserveRatio(true);
                    iv.setSmooth(true);
                    iv.setCache(true);
                    back.setGraphic(iv);
                    pane.getChildren().add(back);
                    Scene scene = new Scene(pane, 250, 365);
                    dialogStage.setScene(scene);
                    dialogStage.show();
                }
            });
            iv2.setImage(currentPlayer.getCardsOnHand().get(i).getFront());
            iv2.setFitWidth(110);
            iv2.setPreserveRatio(true);
            iv2.setSmooth(true);
            iv2.setCache(true);
            button.setGraphic(iv2);
            if (currentPlayer.getCurrentCity().equals(currentPlayer.getCardsOnHand().get(i).getName())
                    && !currentPlayer.getHome().equals(currentPlayer.getCardsOnHand().get(i).getName())) {
                currentPlayer.getCardsOnHand().remove(i);
            }
            leftPane.getChildren().add(button);
        }
    }
}
