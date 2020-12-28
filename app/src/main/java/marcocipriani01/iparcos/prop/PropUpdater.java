package marcocipriani01.iparcos.prop;

import android.os.AsyncTask;

import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIValueException;

import java.io.IOException;

import marcocipriani01.iparcos.IPARCOSApp;
import marcocipriani01.iparcos.R;

/**
 * Async task to send updates to the server
 */
public class PropUpdater extends AsyncTask<INDIProperty, Void, Void> {

    @Override
    protected Void doInBackground(INDIProperty... param) {
        try {
            if (param.length != 1) {
                return null;
            }
            param[0].sendChangesToDriver();

        } catch (INDIValueException | IOException e) {
            IPARCOSApp.log(IPARCOSApp.getContext().getResources().getString(R.string.error) + e.getLocalizedMessage());
        }
        return null;
    }
}