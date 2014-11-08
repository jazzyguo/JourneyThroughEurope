package application;

import java.io.IOException;
import properties_manager.PropertiesManager;
import JTE.UI.JTEUI;
import javafx.application.Application;
import static javafx.application.Application.launch;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import xml_utilities.InvalidXMLFileFormatException;

public class Main extends Application {

    static String PROPERTY_TYPES_LIST = "property_types.txt";
    static String UI_PROPERTIES_FILE_NAME = "properties.xml";
    static String PROPERTIES_SCHEMA_FILE_NAME = "properties_schema.xsd";
    static String DATA_PATH = "./data/";

    @Override
    public void start(Stage primaryStage) {
        try {
            PropertiesManager props = PropertiesManager.getPropertiesManager();
            props.addProperty(JTEPropertyType.UI_PROPERTIES_FILE_NAME,
                    UI_PROPERTIES_FILE_NAME);
            props.addProperty(JTEPropertyType.PROPERTIES_SCHEMA_FILE_NAME,
                    PROPERTIES_SCHEMA_FILE_NAME);
            props.addProperty(JTEPropertyType.DATA_PATH.toString(),
                    DATA_PATH);
            props.loadProperties(UI_PROPERTIES_FILE_NAME,
                    PROPERTIES_SCHEMA_FILE_NAME);
            String title = props.getProperty(JTEPropertyType.SPLASH_SCREEN_TITLE_TEXT);
            primaryStage.setTitle(title);

            JTEUI root = new JTEUI();
            BorderPane mainPane = root.getMainPane();
            root.setStage(primaryStage);

            Scene scene = new Scene(mainPane, mainPane.getWidth(), mainPane.getHeight());
            primaryStage.setScene(scene);
            primaryStage.show();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }

    public enum JTEPropertyType {
        /* SETUP FILE NAMES */

        UI_PROPERTIES_FILE_NAME,
        PROPERTIES_SCHEMA_FILE_NAME,
        /* DIRECTORIES FOR FILE LOADING */
        DATA_PATH,
        IMG_PATH,
        /* WINDOW DIMENSIONS */
        WINDOW_WIDTH,
        WINDOW_HEIGHT,
        /* GAME TEXT */
        SPLASH_SCREEN_TITLE_TEXT,
        GAME_TITLE_TEXT,
        GAME_SUBHEADER_TEXT,
        WIN_DISPLAY_TEXT,
        LOSE_DISPLAY_TEXT,
        GAME_RESULTS_TEXT,
        EXIT_REQUEST_TEXT,
        YES_TEXT,
        NO_TEXT,
        DEFAULT_YES_TEXT,
        DEFAULT_NO_TEXT,
        DEFAULT_EXIT_TEXT,
        /* IMAGE FILE NAMES */
        WINDOW_ICON,
        SPLASH_SCREEN_IMAGE_NAME,
        GAME_IMG_NAME,
        STATS_IMG_NAME,
        HELP_IMG_NAME,
        EXIT_IMG_NAME,
        NEW_GAME_IMG_NAME,
        HOME_IMG_NAME,
        /* DATA FILE STUFF */
        GAME_FILE_NAME,
        STATS_FILE_NAME,
        HELP_FILE_NAME,
        /* TOOLTIPS */
        GAME_TOOLTIP,
        STATS_TOOLTIP,
        HELP_TOOLTIP,
        EXIT_TOOLTIP,
        NEW_GAME_TOOLTIP,
        HOME_TOOLTIP,
        /*
         * THESE ARE FOR LANGUAGE-DEPENDENT ERROR HANDLING, LIKE FOR TEXT PUT
         * INTO DIALOG BOXES TO NOTIFY THE USER WHEN AN ERROR HAS OCCURED
         */
        ERROR_DIALOG_TITLE_TEXT,
        IMAGE_LOADING_ERROR_TEXT,
        INVALID_URL_ERROR_TEXT,
        INVALID_DOC_ERROR_TEXT,
        INVALID_XML_FILE_ERROR_TEXT,
        INSETS,
        BACK_IMG_NAME,
        TIME_IMG_NAME, BACK2_IMG_NAME
    }
}
