package farom.astroid;

import java.util.ArrayList;

import laazotea.indi.Constants.SwitchStatus;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDILightElement;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDISwitchElement;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;

public class LightPropPref extends PropPref {

	public LightPropPref(Context context, INDIProperty prop) {
		super(context, prop);
		// TODO Auto-generated constructor stub
	}

	/**
	 * Create the summary rich-text string
	 * 
	 * @return the summary
	 */
	@Override
	protected Spannable createSummary() {
		String temp = "";
		ArrayList<INDIElement> elements = prop.getElementsAsList();

		int[] starts = new int[elements.size()];
		int[] ends = new int[elements.size()];
		starts[0] = 0;

		for (int i = 0; i < elements.size(); i++) {
			starts[i] = temp.length();
			temp = temp + elements.get(i).getLabel() + " ";
			ends[i] = temp.length();
		}

		Spannable summaryText = new SpannableString(temp);

		for (int i = 0; i < elements.size(); i++) {
			int color = Color.WHITE;
			switch (((INDILightElement) (elements.get(i))).getValue()) {
			case ALERT:
				color = Color.RED;
				break;
			case BUSY:
				color = Color.YELLOW;
				break;
			case IDLE:
				color = Color.WHITE;
				break;
			case OK:
				color = Color.GREEN;
				break;
			}
			summaryText.setSpan(new ForegroundColorSpan(color), starts[i], ends[i], 0);

		}

		return summaryText;
	}

}
