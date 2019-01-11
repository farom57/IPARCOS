package farom.iparcos;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import java.util.Date;

/**
 * @author marcocipriani01
 */
public class Application extends android.app.Application {

    /**
     * The context of the whole app.
     */
    @SuppressLint("StaticFieldLeak")
    private static Context context;
    /**
     * UI updater
     *
     * @see UIUpdater
     */
    private static UIUpdater uiUpdater = null;
    /**
     * Global connection manager.
     */
    private static ConnectionManager connectionManager;
    private static Runnable goToConnection;

    /**
     * @return the Connection Manager for this application.
     */
    public static ConnectionManager getConnectionManager() {
        return connectionManager;
    }

    /**
     * @param u a new {@link UIUpdater}
     */
    public static void setUiUpdater(UIUpdater u) {
        uiUpdater = u;
    }

    /**
     * Sets the action to fire to go back to the connection tab.
     *
     * @param runnable the action.
     */
    public static void setGoToConnectionTab(Runnable runnable) {
        goToConnection = runnable;
    }

    /**
     * @return the context of the whole app.
     */
    public static Context getContext() {
        return context;
    }

    /**
     * Add the given message to the logs.
     *
     * @param message a new log.
     */
    public static void log(String message) {
        Log.i("GlobalLog", message);
        if (uiUpdater != null) {
            Date now = new Date();
            uiUpdater.appendLog(message, DateFormat.getDateFormat(context).format(now) + " " +
                    DateFormat.getTimeFormat(context).format(now));
        }
    }

    /**
     * @param state the new state of the Connection button.
     */
    public static void setState(String state) {
        if (uiUpdater != null) {
            uiUpdater.setConnectionState(state);
        }
    }

    /**
     * Makes {@link MainActivity} change the current fragment to {@link ConnectionFragment}.
     */
    public static void goToConnectionTab() {
        if (goToConnection != null) {
            goToConnection.run();
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
        if (connectionManager == null) {
            connectionManager = new ConnectionManager();
        }
    }

    /**
     * This class offers a safe way to update the UI statically instead of keeping in memory Android Widgets,
     * which implement the class {@link Context}.
     *
     * @author marcocipriani01
     */
    public interface UIUpdater {
        /**
         * Appends a log to the Log TextView.
         */
        void appendLog(final String msg, final String timestamp);

        /**
         * @param state a new state for the Connection button.
         */
        void setConnectionState(final String state);
    }
}