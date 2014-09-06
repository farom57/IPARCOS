package farom.iparcos;

import laazotea.indi.client.*;
import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.View;

public abstract class PropPref extends Preference implements INDIPropertyListener {
	protected INDIProperty prop;
	protected View title = null;
	
	
	public static PropPref create(Context context, INDIProperty prop){
		if(prop instanceof INDISwitchProperty){
			return new SwitchPropPref(context, prop);
		}else if(prop instanceof INDILightProperty){
			return new LightPropPref(context, prop);
		}else if(prop instanceof INDITextProperty){
			return new TextPropPref(context, prop);
		}else if(prop instanceof INDINumberProperty){
			return new NumberPropPref(context, prop);
		}else if(prop instanceof INDIBLOBProperty){
			return new BLOBPropPref(context, prop);
		}else{
			return new LightPropPref(context, prop);
		}
	}

	protected PropPref(Context context, INDIProperty prop) {
		super(context);
		this.prop = prop;

		prop.addINDIPropertyListener(this);

		setTitle(createTitle());
		setSummary(createSummary());
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		// INDIAdapter.getInstance().log(view.toString());
		title = view;
		// propertyChanged(prop);
		// changed ++;
	}

	/**
	 * Create the title rich-text string with the color corresponding to the
	 * Property state
	 * 
	 * @return the title
	 */
	protected Spannable createTitle() {
		Spannable titleText = new SpannableString(prop.getLabel());
		int color = 0;
		switch (prop.getState()) {
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
		titleText.setSpan(new ForegroundColorSpan(color), 0, titleText.length(), 0);
		return titleText;
	}
	
	/**
	 * Create the summary rich-text string  
	 * @return the summary
	 */
	protected abstract Spannable createSummary();
	
	@Override
	public void propertyChanged(INDIProperty arg0) {
		if (arg0 != prop) {
			Log.w("PropPref","wrong property");
			return;
		}

		if (title == null) {
			Log.w("PropPref","null title, prop = " + prop.getLabel());
			return;
		}

		final PropPref thisPref = this;
		title.post(new Runnable() {
			public void run() {
				thisPref.setSummary(createSummary());
				thisPref.setTitle(createTitle());
			}
		});

	}
}
