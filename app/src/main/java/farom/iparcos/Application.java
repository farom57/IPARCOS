package farom.iparcos;

import android.content.Context;

/**
 * @author SquareBoot
 */
public class Application extends android.app.Application {

    private static Context context;

    public static Context getAppContext() {
        return Application.context;
    }

    public void onCreate() {
        super.onCreate();
        Application.context = getApplicationContext();
    }
}