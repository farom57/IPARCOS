package org.indilib.i4j.iparcos.prop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import org.indilib.i4j.client.INDILightElement;
import org.indilib.i4j.client.INDIProperty;

import java.util.List;

import org.indilib.i4j.iparcos.IPARCOSApp;
import org.indilib.i4j.iparcos.R;

public class LightPropPref extends PropPref<INDILightElement> {

    public LightPropPref(Context context, INDIProperty<INDILightElement> prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        List<INDILightElement> elements = prop.getElementsAsList();
        int count = elements.size();
        if (count > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int[] starts = new int[count];
            int[] ends = new int[count];
            starts[0] = 0;
            for (int i = 0; i < count; i++) {
                starts[i] = stringBuilder.length();
                stringBuilder.append(elements.get(i).getLabel()).append(" ");
                ends[i] = stringBuilder.length();
            }
            Spannable summaryText = new SpannableString(stringBuilder.toString());
            Resources resources = IPARCOSApp.getContext().getResources();
            for (int i = 0; i < count; i++) {
                int color;
                switch (elements.get(i).getValue()) {
                    case ALERT: {
                        color = resources.getColor(R.color.light_red);
                        break;
                    }
                    case BUSY: {
                        color = resources.getColor(R.color.light_yellow);
                        break;
                    }
                    case OK: {
                        color = resources.getColor(R.color.light_green);
                        break;
                    }
                    default:
                    case IDLE: {
                        color = Color.WHITE;
                        break;
                    }
                }
                summaryText.setSpan(new ForegroundColorSpan(color), starts[i], ends[i], 0);
            }
            return summaryText;
        } else {
            return new SpannableString(getContext().getString(R.string.no_indi_elements));
        }
    }
}