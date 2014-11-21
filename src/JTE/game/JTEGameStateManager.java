package JTE.game;

import java.util.ArrayList;
import java.util.Iterator;
import JTE.UI.JTEUI;

public class JTEGameStateManager {

    // THE GAME WILL ALWAYS BE IN
    // ONE OF THESE STATES
    public enum JTEGameState {
        GAME_NOT_STARTED, GAME_IN_PROGRESS, GAME_OVER,
    }

    // STORES THE CURRENT STATE OF THIS GAME
    public JTEGameState currentGameState;

    // WHEN THE STATE OF THE GAME CHANGES IT WILL NEED TO BE
    // REFLECTED IN THE USER INTERFACE, SO THIS CLASS NEEDS
    // A REFERENCE TO THE UI
    private JTEUI ui;

    // THIS IS THE GAME CURRENTLY BEING PLAYED
    private JTEGameData gameInProgress;

    // HOLDS ALL OF THE COMPLETED GAMES. NOTE THAT THE GAME
    // IN PROGRESS IS NOT ADDED UNTIL IT IS COMPLETED
    private ArrayList<JTEGameData> gamesHistory;

    private final String NEWLINE_DELIMITER = "\n";

    public JTEGameStateManager(JTEUI initUI) {
        ui = initUI;
        // WE HAVE NOT STARTED A GAME YET
        currentGameState = JTEGameState.GAME_NOT_STARTED;
        // NO GAMES HAVE BEEN PLAYED YET, BUT INITIALIZE
        // THE DATA STRCUTURE FOR PLACING COMPLETED GAMES
        gamesHistory = new ArrayList();
        // THE FIRST GAME HAS NOT BEEN STARTED YET
        gameInProgress = null;
    }

    // ACCESSOR METHODS

    /**
     * Accessor method for getting the game currently being played.
     *
     * @return The game currently being played.
     */
    public void setData(JTEGameData data) {
        gameInProgress = data;
    }

    public JTEGameData getGameInProgress() {
        return gameInProgress;
    }

    /**
     * Accessor method for getting the number of games that have been played.
     *
     * @return The total number of games that have been played during this game
     * session.
     */
    public int getGamesPlayed() {
        return gamesHistory.size();
    }

    /**
     * Accessor method for getting all the games that have been completed.
     *
     * @return An Iterator that allows one to go through all the games that have
     * been played so far.
     */
    public Iterator<JTEGameData> getGamesHistoryIterator() {
        return gamesHistory.iterator();
    }

    /**
     * Accessor method for testing to see if any games have been started yet.
     *
     * @return true if at least one game has already been started during this
     * session, false otherwise.
     */
    public boolean isGameNotStarted() {
        return currentGameState == JTEGameState.GAME_NOT_STARTED;
    }

    /**
     * Accessor method for testing to see if the current game is over.
     *
     * @return true if the game in progress has completed, false otherwise.
     */
    public boolean isGameOver() {
        return currentGameState == JTEGameState.GAME_OVER;
    }

    /**
     * Accessor method for testing to see if the current game is in progress.
     *
     * @return true if a game is in progress, false otherwise.
     */
    public boolean isGameInProgress() {
        return currentGameState == JTEGameState.GAME_IN_PROGRESS;
    }
}
