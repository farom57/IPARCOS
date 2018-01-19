package farom.iparcos;

import laazotea.indi.client.INDIProperty;
import android.content.Context;
import android.text.Spannable;
import android.text.SpannableString;
import farom.iparcos.R;

public class BLOBPropPref extends PropPref {

	public BLOBPropPref(Context context, INDIProperty prop) {
		super(context, prop);
	}

	/**
	 * Create the summary rich-text string  
	 * @return the summary
	 */
	@Override
	protected Spannable createSummary() {
		Spannable summaryText = new SpannableString(getContext().getString(R.string.BLOB_not_supported));
		return summaryText;
	}

}
