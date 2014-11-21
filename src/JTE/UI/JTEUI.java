package JTE.UI;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JEditorPane;

import application.Main.JTEPropertyType;

import properties_manager.PropertiesManager;

import JTE.file.JTEFileLoader;
import JTE.game.JTEGameData;
import JTE.game.JTEGameData.Card;
import JTE.game.JTEGameData.Player;
import JTE.game.JTEGameStateManager;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.util.Duration;
import javax.swing.BorderFactory;

public class JTEUI extends Pane {

    private Stage primaryStage;

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
    private GraphicsContext gc;
    private Button gameHistory;
    private Button gameAbout;
    private Button save;
    private FlowPane leftPane;
    private FlowPane rightPane;
    private FlowPane mapView;
    private int currentMap;

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
    }

    // Methods
    public void setStage(Stage stage) {
        primaryStage = stage;
    }

    public BorderPane getMainPane() {
        return mainPane;
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

    public ArrayList getPlayers() {
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
                eventHandler.respondToNewGameRequest();
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

    private void initGameHistoryScreen() {
        gameHistoryPane = new JEditorPane();
        gameHistoryPane.setEditable(false);
        gameHistoryPane.setContentType("text/html");
        gameHistoryPane.setPreferredSize(new Dimension(800, 800));
        SwingNode swingNode = new SwingNode();
        swingNode.setContent(gameHistoryPane);
        gameHistoryPane.setText("Game History");
        gameHistoryScrollPane = new ScrollPane();
        gameHistoryScrollPane.setPrefSize(800, 800);
        gameHistoryScrollPane.setContent(swingNode);
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
        currentMap = 1;
        gamePanel = new BorderPane();
        leftPane = new FlowPane(Orientation.VERTICAL);
        leftPane.resize(170, 640);
        leftPane.setStyle("-fx-background-color:orange");
        Text currentPlayer = new Text();
        currentPlayer.setText("Current Player");
        leftPane.getChildren().add(currentPlayer);
        rightPane = new FlowPane(Orientation.VERTICAL);
        rightPane.resize(170, 640);
        rightPane.setStyle("-fx-background-color:orange");
        map = new Canvas();
        map.setWidth(550);
        map.setHeight(640);
        gc = map.getGraphicsContext2D();
        gc.drawImage(mapQuadrant1, 0, 0);
        gamePanel.setCenter(map);
        map.setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                eventHandler.mouseClicked(event);
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
                    initGameHistoryScreen();
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
        mapView = new FlowPane();
        mapView.setPrefWrapLength(170);
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
            }
        });
        map2.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 2;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant2);
            }
        });
        map3.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 3;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant3);
            }
        });
        map4.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                currentMap = 4;
                eventHandler.respondToSwitchMapView(currentMap, gc, mapQuadrant4);

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
        mapView.getChildren().addAll(map1, map2, map3, map4);
        rightPane.getChildren().addAll(mapView, gameAbout, gameHistory, save, quitButton);
        gamePanel.setRight(rightPane);
        gamePanel.setLeft(leftPane);
    }

    public void repaint() {
        gc = map.getGraphicsContext2D();
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

}
