package org.indilib.i4j.iparcos.prop;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

import androidx.preference.Preference;
import androidx.preference.PreferenceViewHolder;

import org.indilib.i4j.client.INDIBLOBProperty;
import org.indilib.i4j.client.INDIElement;
import org.indilib.i4j.client.INDILightProperty;
import org.indilib.i4j.client.INDINumberProperty;
import org.indilib.i4j.client.INDIProperty;
import org.indilib.i4j.client.INDIPropertyListener;
import org.indilib.i4j.client.INDISwitchProperty;
import org.indilib.i4j.client.INDITextProperty;

import org.indilib.i4j.iparcos.IPARCOSApp;
import org.indilib.i4j.iparcos.R;

public abstract class PropPref<Element extends INDIElement> extends Preference implements INDIPropertyListener {

    protected final INDIProperty<Element> prop;
    protected View title = null;

    protected PropPref(Context context, INDIProperty<Element> prop) {
        super(context);
        this.prop = prop;

        prop.addINDIPropertyListener(this);

        setTitle(createTitle());
        setSummary(createSummary());
    }

    public static PropPref<?> create(Context context, INDIProperty<?> prop) {
        if (prop instanceof INDISwitchProperty) {
            return new SwitchPropPref(context, (INDISwitchProperty) prop);
        } else if (prop instanceof INDILightProperty) {
            return new LightPropPref(context, (INDILightProperty) prop);
        } else if (prop instanceof INDITextProperty) {
            return new TextPropPref(context, (INDITextProperty) prop);
        } else if (prop instanceof INDINumberProperty) {
            return new NumberPropPref(context, (INDINumberProperty) prop);
        } else if (prop instanceof INDIBLOBProperty) {
            return new BLOBPropPref(context, (INDIBLOBProperty) prop);
        }
        return null;
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
                color = IPARCOSApp.getContext().getResources().getColor(R.color.light_red);
                break;
            }

            case BUSY: {
                color = IPARCOSApp.getContext().getResources().getColor(R.color.light_yellow);
                break;
            }

            case OK: {
                color = IPARCOSApp.getContext().getResources().getColor(R.color.light_green);
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
    public void propertyChanged(INDIProperty<?> property) {
        if (property != prop) {
            Log.w("PropPref", "wrong property");
            return;
        }
        if (title != null) {
            title.post(() -> {
                PropPref.this.setSummary(createSummary());
                PropPref.this.setTitle(createTitle());
            });
        } else {
            Log.w("PropPref", "null title, prop = " + prop.getLabel());
        }
    }

    /**
     * Send updates to the server.
     */
    protected void sendChanges() {
        new PropUpdater(prop).start();
    }
}