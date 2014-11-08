package JTE.UI;

import java.io.IOException;
import java.util.ArrayList;

import javax.swing.JEditorPane;

import application.Main.JTEPropertyType;

import properties_manager.PropertiesManager;

import JTE.file.JTEFileLoader;
import JTE.game.JTEGameStateManager;
import java.awt.Dimension;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.File;
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
import javafx.embed.swing.SwingNode;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JTEUI extends Pane {

    private Stage primaryStage;

    public enum JTEUIState {

        SPLASH_SCREEN_STATE,
        PLAY_GAME_STATE,
        SELECT_PLAYERS_STATE,
        VIEW_ABOUT_STATE,
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

    // GamePane
    private StackPane gamePane;

    //AboutPane
    private JEditorPane aboutPane;
    private ScrollPane aboutScrollPane;
    private HBox aboutPaneNorthToolBar;
    private Button backButton;

    //SelectPlayersPane
    private FlowPane selectPlayersPane;
    private HBox selectPlayersNorthToolBar;

    // Padding
    private Insets marginlessInsets;

    // Image path
    private String ImgPath = "file:images/";

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

    public void initMainPane() {
        marginlessInsets = new Insets(5, 5, 5, 5);
        mainPane = new BorderPane();

        PropertiesManager props = PropertiesManager.getPropertiesManager();
        paneWidth = Integer.parseInt(props
                .getProperty(JTEPropertyType.WINDOW_WIDTH));
        paneHeigth = Integer.parseInt(props
                .getProperty(JTEPropertyType.WINDOW_HEIGHT));
        mainPane.resize(890, 535);
        mainPane.setPadding(marginlessInsets);
    }

    public void initSplashScreen() {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String splashScreenImagePath = props.getProperty(JTEPropertyType.SPLASH_SCREEN_IMAGE_NAME);

        splashScreenPane = new StackPane();

        Image splashScreenImage = loadImage(splashScreenImagePath);
        splashScreenImageView = new ImageView(splashScreenImage);

        splashScreenImageLabel = new Label();
        splashScreenImageLabel.setGraphic(splashScreenImageView);
        splashScreenPane.getChildren().add(splashScreenImageLabel);
        optionSelectionPane = new FlowPane();
        optionSelectionPane.setAlignment(Pos.BOTTOM_CENTER);
        //LOAD SPLASH SCREEN OPTION
        Button startButton = new Button();
        Image startImage = loadImage("start.png");
        ImageView startImageView = new ImageView(startImage);
        startButton.setGraphic(startImageView);
        optionSelectionPane.getChildren().add(startButton);
        Button loadButton = new Button();
        Image loadImage = loadImage("load.png");
        ImageView loadImageView = new ImageView(loadImage);
        loadButton.setGraphic(loadImageView);
        optionSelectionPane.getChildren().add(loadButton);
        Button aboutButton = new Button();
        Image aboutImage = loadImage("about.png");
        ImageView aboutImageView = new ImageView(aboutImage);
        aboutButton.setGraphic(aboutImageView);
        optionSelectionPane.getChildren().add(aboutButton);
        Button quitButton = new Button();
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
        selectPlayersNorthToolBar = new HBox();
        selectPlayersNorthToolBar.setStyle("-fx-background-color:brown");
        selectPlayersNorthToolBar.setAlignment(Pos.CENTER);
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
        for (int i = 1; i < 7; i++) {
            FlowPane playerPane = new FlowPane();
            String imageString = "" + i + ".png";
            Image playerImage = loadImage(imageString);
            ImageView playerImageView = new ImageView(playerImage);
            playerPane.getChildren().add(playerImageView);
            playerPane.setStyle("-fx-background-color:orange");
            selectPlayersPane.getChildren().add(playerPane);
        }
    }

    private void initAboutPane() {
        // WE'LL DISPLAY ALL STATS IN A JEditorPane
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
        aboutPaneNorthToolBar.setAlignment(Pos.CENTER);
        aboutPaneNorthToolBar.setPadding(marginlessInsets);
        aboutPaneNorthToolBar.setSpacing(10.0);

        // MAKE AND INIT THE BACK BUTTON
        backButton = initToolbarButton(aboutPaneNorthToolBar,
                JTEPropertyType.BACK_IMG_NAME);
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
            case SELECT_PLAYERS_STATE:
                mainPane.getChildren().clear();
                mainPane.setCenter(selectPlayersPane);
                mainPane.setTop(selectPlayersNorthToolBar);
                primaryStage.setTitle("Select Players");
                break;
            case VIEW_ABOUT_STATE:
                mainPane.setCenter(aboutScrollPane);
                mainPane.setTop(aboutPaneNorthToolBar);
                primaryStage.setTitle("Journey Through Europe Wiki");
                break;
            default:
        }
    }

}
