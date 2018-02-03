package farom.iparcos;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

import java.util.ArrayList;

import laazotea.indi.client.INDIElement;
import laazotea.indi.client.INDILightElement;
import laazotea.indi.client.INDIProperty;

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
        StringBuilder temp = new StringBuilder();
        ArrayList<INDIElement> elements = prop.getElementsAsList();

        int[] starts = new int[elements.size()];
        int[] ends = new int[elements.size()];
        starts[0] = 0;

        for (int i = 0; i < elements.size(); i++) {
            starts[i] = temp.length();
            temp.append(elements.get(i).getLabel()).append(" ");
            ends[i] = temp.length();
        }

        Spannable summaryText = new SpannableString(temp.toString());

        for (int i = 0; i < elements.size(); i++) {
            int color = Color.WHITE;
            switch (((INDILightElement) (elements.get(i))).getValue()) {
                case ALERT: {
                    color = Application.getAppContext().getResources().getColor(R.color.light_red);
                    break;
                }

                case BUSY: {
                    color = Application.getAppContext().getResources().getColor(R.color.light_yellow);
                    break;
                }

                case IDLE: {
                    color = Color.WHITE;
                    break;
                }

                case OK: {
                    color = Application.getAppContext().getResources().getColor(R.color.light_green);
                    break;
                }
            }
            summaryText.setSpan(new ForegroundColorSpan(color), starts[i], ends[i], 0);

        }

        return summaryText;
    }
}