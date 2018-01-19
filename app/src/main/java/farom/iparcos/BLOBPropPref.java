package farom.iparcos;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import laazotea.indi.client.INDIProperty;

public class BLOBPropPref extends PropPref {

    public BLOBPropPref(Context context, INDIProperty prop) {
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