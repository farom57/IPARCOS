package marcocipriani01.iparcos.prop;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import org.indilib.i4j.client.INDIBLOBElement;
import org.indilib.i4j.client.INDIProperty;

import marcocipriani01.iparcos.R;

public class BLOBPropPref extends PropPref<INDIBLOBElement> {

    public BLOBPropPref(Context context, INDIProperty<INDIBLOBElement> prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        return new SpannableString(getContext().getString(R.string.BLOB_not_supported));
    }
}