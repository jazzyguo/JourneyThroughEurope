package JTE.UI;

import java.io.DataInputStream;
import java.io.IOException;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Stack;

import application.Main.JTEPropertyType;

import properties_manager.PropertiesManager;

import JTE.file.JTEFileLoader;
import JTE.game.JTEGameData;
import JTE.game.JTEGameData.City;
import JTE.game.JTEGameData.Die;
import JTE.game.JTEGameData.Player;
import JTE.game.JTEGameStateManager;
import static JTE.game.JTEGameStateManager.JTEGameState.GAME_IN_PROGRESS;
import static JTE.game.JTEGameStateManager.JTEGameState.GAME_OVER;
import java.util.Arrays;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.input.DragEvent;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Duration;

public class JTEEventHandler {

    private JTEUI ui;
    public JTEGameData data;

    /**
     * Constructor that simply saves the ui for later.
     *
     * @param initUI
     */
    public JTEEventHandler(JTEUI initUI) {
        ui = initUI;
    }

    /**
     * This method responds to when the user wishes to switch between the Game,
     * Stats, and Help screens.
     *
     * @param uiState The ui state, or screen, that the user wishes to switch
     * to.
     * @throws IOException
     */
    public void respondToSwitchScreenRequest(JTEUI.JTEUIState uiState) throws IOException {
        ui.changeWorkspace(uiState);
    }

    /**
     * This method responds to when the user presses the new game method.
     */
    public void respondToNewGameRequest() {
        JTEGameStateManager gsm = ui.getGSM();
        data = new JTEGameData(ui);
        ui.initGameScreen();
        gsm.setData(data);
        try {
            respondToSwitchScreenRequest(JTEUI.JTEUIState.PLAY_GAME_STATE);
        } catch (IOException ex) {
            Logger.getLogger(JTEEventHandler.class.getName()).log(Level.SEVERE, null, ex);
        }
        for (int i = 0; i < data.getPlayers().size(); i++) {
            System.out.println(data.getPlayers().get(i).getName());
            System.out.println(data.getPlayers().get(i).isHuman());
        }
    }

    public void respondToSwitchMapView(int currentMap, GraphicsContext gc, Image mapQuadrant) {
        switch (currentMap) {
            case 1:
                gc.clearRect(0, 0, 550, 640);
                gc.drawImage(mapQuadrant, 0, 0);
                for (JTEGameData.Player player : data.getPlayers()) {
                    if (player.getQuadrant() == currentMap) {
                        gc.drawImage(player.getImage(), player.getCurrentLocation().getX() - 25,
                                player.getCurrentLocation().getY() - 45, 50, 50);
                        if (player.getHomeQ() == currentMap) {
                            gc.drawImage(player.getFlag(), player.getHomeLocation().getX() - 21,
                                    player.getHomeLocation().getY() - 45, 50, 50);
                        }
                    }
                }
                break;
            case 2:
                gc.clearRect(0, 0, 550, 640);
                gc.drawImage(mapQuadrant, 0, 0);
                for (JTEGameData.Player player : data.getPlayers()) {
                    if (player.getQuadrant() == currentMap) {
                        gc.drawImage(player.getImage(), player.getCurrentLocation().getX() - 25,
                                player.getCurrentLocation().getY() - 45, 50, 50);
                        if (player.getHomeQ() == currentMap) {
                            gc.drawImage(player.getFlag(), player.getHomeLocation().getX() - 21,
                                    player.getHomeLocation().getY() - 45, 50, 50);
                        }
                    }
                }
                break;
            case 3:
                gc.clearRect(0, 0, 550, 640);
                gc.drawImage(mapQuadrant, 0, 0);
                for (JTEGameData.Player player : data.getPlayers()) {
                    if (player.getQuadrant() == currentMap) {
                        gc.drawImage(player.getImage(), player.getCurrentLocation().getX() - 25,
                                player.getCurrentLocation().getY() - 45, 50, 50);
                        if (player.getHomeQ() == currentMap) {
                            gc.drawImage(player.getFlag(), player.getHomeLocation().getX() - 21,
                                    player.getHomeLocation().getY() - 45, 50, 50);
                        }
                    }
                }
                break;
            case 4:
                gc.clearRect(0, 0, 550, 640);
                gc.drawImage(mapQuadrant, 0, 0);
                for (JTEGameData.Player player : data.getPlayers()) {
                    if (player.getQuadrant() == currentMap) {
                        gc.drawImage(player.getImage(), player.getCurrentLocation().getX() - 25,
                                player.getCurrentLocation().getY() - 45, 50, 50);
                        if (player.getHomeQ() == currentMap) {
                            gc.drawImage(player.getFlag(), player.getHomeLocation().getX() - 21,
                                    player.getHomeLocation().getY() - 45, 50, 50);
                        }
                    }
                }
                break;
        }
    }

    /**
     * This method responds to when the user requests to exit the application.
     *
     * @param window The window that the user has requested to close.
     */
    public void respondToExitRequest(Stage primaryStage) {
        // ENGLIS IS THE DEFAULT
        String options[] = new String[]{"Yes", "No"};
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        options[0] = props.getProperty(JTEPropertyType.DEFAULT_YES_TEXT);
        options[1] = props.getProperty(JTEPropertyType.DEFAULT_NO_TEXT);
        String verifyExit = props.getProperty(JTEPropertyType.DEFAULT_EXIT_TEXT);

        // NOW WE'LL CHECK TO SEE IF LANGUAGE SPECIFIC VALUES HAVE BEEN SET
        if (props.getProperty(JTEPropertyType.YES_TEXT) != null) {
            options[0] = props.getProperty(JTEPropertyType.YES_TEXT);
            options[1] = props.getProperty(JTEPropertyType.NO_TEXT);
            verifyExit = props.getProperty(JTEPropertyType.EXIT_REQUEST_TEXT);
        }

        // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
        /*int selection = JOptionPane.showOptionDialog(   window, 
         verifyExit, 
         verifyExit, 
         JOptionPane.YES_NO_OPTION, 
         JOptionPane.ERROR_MESSAGE,
         null,
         options,
         null);
         // WHAT'S THE USER'S DECISION?
         if (selection == JOptionPane.YES_NO_OPTION)
         {
         // YES, LET'S EXIT
         System.exit(0);
         }*/
        // FIRST MAKE SURE THE USER REALLY WANTS TO EXIT
        Stage dialogStage = new Stage();
        dialogStage.initModality(Modality.WINDOW_MODAL);
        dialogStage.initOwner(primaryStage);
        BorderPane exitPane = new BorderPane();
        HBox optionPane = new HBox();
        Button yesButton = new Button(options[0]);
        Button noButton = new Button(options[1]);
        optionPane.setSpacing(10.0);
        optionPane.getChildren().addAll(yesButton, noButton);
        Label exitLabel = new Label(verifyExit);
        exitPane.setCenter(exitLabel);
        exitPane.setBottom(optionPane);
        Scene scene = new Scene(exitPane, 250, 100);
        dialogStage.setScene(scene);
        dialogStage.show();
        // WHAT'S THE USER'S DECISION?
        yesButton.setOnAction(e -> {
            // YES, LET'S EXIT
            System.exit(0);
        });
        noButton.setOnAction(e -> {
            dialogStage.close();
        });
    }

    public void mouseClicked(MouseEvent event) {
        ArrayList<City> cities = data.getCities();
        double mouseX = event.getX();
        double mouseY = event.getY();
        System.out.println(mouseX + "," + mouseY);
        int n = cities.size();
        for (int i = 0; i < n; i++) {
            double citiesX = cities.get(i).getCoordinates().getX();
            double citiesY = cities.get(i).getCoordinates().getY();
            if ((mouseX > citiesX - 5 && mouseX < citiesX + 5)
                    && (mouseY > citiesY - 5 && mouseY < citiesY + 5)
                    && (ui.getMap() == cities.get(i).getQuadrant())) {
                City clickedCity = cities.get(i);
                HashMap<String, String[]> land = data.getCityLandNeighbors();
                String[] lands = land.get(cities.get(i).getName());
                for (int c = 0; c < lands.length; c++) {
                    System.out.println(lands[c]);
                }
                HashMap<String, String[]> sea = data.getCitySeaNeighbors();
                String[] seas = sea.get(cities.get(i).getName());
                for (int c = 0; c < seas.length; c++) {
                    System.out.println(seas[c]);
                }
            }
        }
    }

    public void moveTo(MouseEvent event, Player currentPlayer, GraphicsContext gc) {
        ArrayList<String> availableCities = new ArrayList();
        if (data.getCityLandNeighbors().get(currentPlayer.getCurrentCity()).length != 0) {
            availableCities.addAll(Arrays.asList(data.getCityLandNeighbors().get(currentPlayer.getCurrentCity())));
        }
        if (data.getCitySeaNeighbors().get(currentPlayer.getCurrentCity()).length != 0) {
            availableCities.addAll(Arrays.asList(data.getCitySeaNeighbors().get(currentPlayer.getCurrentCity())));
        }
        ArrayList<City> cities = data.getCities();
        double mouseX = event.getX();
        double mouseY = event.getY();
        System.out.println(mouseX + "," + mouseY);
        int n = cities.size();
        for (int i = 0; i < n; i++) {
            double citiesX = cities.get(i).getCoordinates().getX();
            double citiesY = cities.get(i).getCoordinates().getY();
            if ((mouseX > citiesX - 10 && mouseX < citiesX + 10)
                    && (mouseY > citiesY - 10 && mouseY < citiesY + 10)
                    && (ui.getMap() == cities.get(i).getQuadrant())) {
                City clickedCity = cities.get(i);
                if (availableCities.contains(clickedCity.getName())) {
                    System.out.println("valid city");
                    if (currentPlayer.getQuadrant() == clickedCity.getQuadrant()) {
                        ui.moveAnimation(currentPlayer.getCurrentLocation(), clickedCity);
                    } else {
                        ui.moveToQuadrant(currentPlayer.getCurrentLocation(), clickedCity);
                    }
                }
            }
        }
    }

    public void dragged(DragEvent event, Player currentPlayer, GraphicsContext gc) {
        ArrayList<String> availableCities = new ArrayList();
        if (data.getCityLandNeighbors().get(currentPlayer.getCurrentCity()).length != 0) {
            availableCities.addAll(Arrays.asList(data.getCityLandNeighbors().get(currentPlayer.getCurrentCity())));
        }
        if (data.getCitySeaNeighbors().get(currentPlayer.getCurrentCity()).length != 0) {
            availableCities.addAll(Arrays.asList(data.getCitySeaNeighbors().get(currentPlayer.getCurrentCity())));
        }
        ArrayList<City> cities = data.getCities();
        double mouseX = event.getX();
        double mouseY = event.getY();
        System.out.println(mouseX + "," + mouseY);
        int n = cities.size();
        for (int i = 0; i < n; i++) {
            double citiesX = cities.get(i).getCoordinates().getX();
            double citiesY = cities.get(i).getCoordinates().getY();
            if ((mouseX > citiesX - 10 && mouseX < citiesX + 10)
                    && (mouseY > citiesY - 10 && mouseY < citiesY + 10)
                    && (ui.getMap() == cities.get(i).getQuadrant())) {
                City clickedCity = cities.get(i);
                if (availableCities.contains(clickedCity.getName())) {
                    System.out.println("valid city");
                    if (currentPlayer.getQuadrant() == clickedCity.getQuadrant()) {
                        ui.moveToQuadrant(currentPlayer.getCurrentLocation(), clickedCity);
                    }
                }
            }
        }
    }
}
