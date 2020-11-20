package marcocipriani01.iparcos.prop;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

import marcocipriani01.iparcos.Application;
import marcocipriani01.iparcos.R;
import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDILightElement;
import laazotea.indi.client.INDIProperty;

@SuppressWarnings({"WeakerAccess"})
public class LightPropPref extends PropPref {

    public LightPropPref(Context context, INDIProperty prop) {
        super(context, prop);
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    @Override
    protected Spannable createSummary() {
        ArrayList<INDIElement> elements = prop.getElementsAsList();
        if (elements.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            int[] starts = new int[elements.size()];
            int[] ends = new int[elements.size()];
            starts[0] = 0;
            for (int i = 0; i < elements.size(); i++) {
                starts[i] = stringBuilder.length();
                stringBuilder.append(elements.get(i).getLabel()).append(" ");
                ends[i] = stringBuilder.length();
            }
            Spannable summaryText = new SpannableString(stringBuilder.toString());
            Resources resources = Application.getContext().getResources();
            for (int i = 0; i < elements.size(); i++) {
                int color = Color.WHITE;
                switch (((INDILightElement) (elements.get(i))).getValue()) {
                    case ALERT: {
                        color = resources.getColor(R.color.light_red);
                        break;
                    }

                    case BUSY: {
                        color = resources.getColor(R.color.light_yellow);
                        break;
                    }

                    case IDLE: {
                        color = Color.WHITE;
                        break;
                    }

                    case OK: {
                        color = resources.getColor(R.color.light_green);
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