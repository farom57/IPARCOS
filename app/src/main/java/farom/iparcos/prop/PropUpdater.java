package farom.iparcos.prop;

import android.os.AsyncTask;

import java.io.IOException;

import farom.iparcos.Application;
import farom.iparcos.R;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIValueException;

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
            Application.log(Application.getContext().getResources().getString(R.string.error) + e.getLocalizedMessage());
        }
        return null;
    }
}