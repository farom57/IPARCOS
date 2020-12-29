package marcocipriani01.iparcos.prop;

import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIValueException;

import java.io.IOException;

import marcocipriani01.iparcos.IPARCOSApp;
import marcocipriani01.iparcos.R;

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