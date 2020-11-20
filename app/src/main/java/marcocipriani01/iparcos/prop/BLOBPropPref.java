package marcocipriani01.iparcos.prop;

import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;

import marcocipriani01.iparcos.R;
import laazotea.indi.client.INDIProperty;

@SuppressWarnings({"WeakerAccess"})
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