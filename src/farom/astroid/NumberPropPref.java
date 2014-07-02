package farom.astroid;

import java.util.ArrayList;

import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDIProperty;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.StyleSpan;

public class NumberPropPref extends PropPref {

	public NumberPropPref(Context context, INDIProperty prop) {
		super(context, prop);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create the summary rich-text string  
	 * @return the summary
	 */
	@Override
	protected Spannable createSummary() {
		ArrayList<INDIElement> elements = prop.getElementsAsList();
		if (elements.size() > 0) {
			String temp = "";

			int i = 0;

			temp = temp + elements.get(0).getLabel() + ": ";

			for (i = 0; i < elements.size() - 1; i++) {
				temp = temp + elements.get(i).getValueAsString();
				temp = temp + ", ";

				temp = temp + elements.get(i + 1).getLabel() + ": ";
			}

			temp = temp + elements.get(i).getValueAsString();

			Spannable summaryText = new SpannableString(temp);

			return summaryText;
		} else {
			return new SpannableString("");
		}
	}
	


}
