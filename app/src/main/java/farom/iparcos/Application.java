package farom.iparcos;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author SquareBoot
 */
public class Application extends android.app.Application {

    /**
     * The context of the whole app.
     */
    private static Context context;

    /**
     * @return the context of the whole app.
     */
    public static Context getAppContext() {
        return Application.context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
    }

    /**
     * Add the given message to the logs.
     *
     * @param message
     */
    public void appendLog(String message) {
        final String msg = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date()) + ": " + message + "\n";
        Log.i("GLOBALLOG", message);
        logView.post(new Runnable() {
            public void run() {
                logView.append(msg);
            }
        });
    }
}