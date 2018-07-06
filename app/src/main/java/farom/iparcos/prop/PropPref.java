package farom.iparcos.prop;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.preference.Preference;
import android.support.v7.preference.PreferenceViewHolder;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import farom.iparcos.Application;
import farom.iparcos.R;
import laazotea.indi.client.INDIBLOBProperty;
import laazotea.indi.client.INDILightProperty;
import laazotea.indi.client.INDINumberProperty;
import laazotea.indi.client.INDIProperty;
import laazotea.indi.client.INDIPropertyListener;
import laazotea.indi.client.INDISwitchProperty;
import laazotea.indi.client.INDITextProperty;

@SuppressWarnings({"unused", "WeakerAccess"})
public abstract class PropPref extends Preference implements INDIPropertyListener {

    protected INDIProperty prop;
    protected View title = null;

    protected PropPref(Context context, INDIProperty prop) {
        super(context);
        this.prop = prop;

        prop.addINDIPropertyListener(this);

        setTitle(createTitle());
        setSummary(createSummary());
    }

    public static PropPref create(Context context, INDIProperty prop) {
        if (prop instanceof INDISwitchProperty) {
            return new SwitchPropPref(context, prop);

        } else if (prop instanceof INDILightProperty) {
            return new LightPropPref(context, prop);

        } else if (prop instanceof INDITextProperty) {
            return new TextPropPref(context, prop);

        } else if (prop instanceof INDINumberProperty) {
            return new NumberPropPref(context, prop);

        } else if (prop instanceof INDIBLOBProperty) {
            return new BLOBPropPref(context, prop);

        } else {
            return new LightPropPref(context, prop);
        }
    }

    @Override
    public void onBindViewHolder(PreferenceViewHolder holder) {
        super.onBindViewHolder(holder);
        title = holder.itemView;
    }

    /**
     * Create the title rich-text string with the color corresponding to the
     * Property state
     *
     * @return the title
     */
    protected Spannable createTitle() {
        Spannable titleText = new SpannableString(prop.getLabel());
        int color;
        switch (prop.getState()) {
            case ALERT: {
                color = Application.getContext().getResources().getColor(R.color.light_red);
                break;
            }

            case BUSY: {
                color = Application.getContext().getResources().getColor(R.color.light_yellow);
                break;
            }

            case OK: {
                color = Application.getContext().getResources().getColor(R.color.light_green);
                break;
            }

            default: {
                color = Color.WHITE;
                break;
            }
        }
        titleText.setSpan(new ForegroundColorSpan(color), 0, titleText.length(), 0);
        return titleText;
    }

    /**
     * Create the summary rich-text string
     *
     * @return the summary
     */
    protected abstract Spannable createSummary();

    @Override
    public void propertyChanged(INDIProperty property) {
        if (property != prop) {
            Log.w("PropPref", "wrong property");
            return;
        }
        if (title != null) {
            title.post(new Runnable() {
                public void run() {
                    PropPref.this.setSummary(createSummary());
                    PropPref.this.setTitle(createTitle());
                }
            });

        } else {
            Log.w("PropPref", "null title, prop = " + prop.getLabel());
        }
    }

    /**
     * Send updates to the server (async task)
     */
    protected void sendChanges() {
        new PropUpdater().execute(prop);
    }
}