package org.indilib.i4j.iparcos.prop;

import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIValueException;

import java.io.IOException;

import org.indilib.i4j.iparcos.IPARCOSApp;
import org.indilib.i4j.iparcos.R;

/**
 * Thread to send updates to the server
 */
public class PropUpdater extends Thread {

    public PropUpdater(INDIProperty<?> prop) {
        super(() -> {
            try {
                prop.sendChangesToDriver();
            } catch (INDIValueException | IOException e) {
                IPARCOSApp.log(IPARCOSApp.getContext().getResources().getString(R.string.error) + e.getLocalizedMessage());
            }
        }, "INDI propriety updater");
    }
}