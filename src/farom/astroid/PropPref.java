package farom.astroid;

import laazotea.indi.client.*;
import android.content.Context;
import android.graphics.Color;
import android.preference.Preference;
import android.view.View;
import android.widget.TextView;

public class PropPref extends Preference implements INDIPropertyListener {
	INDIProperty prop;
	private TextView title = null;
	private int changed = 0;
	private String titleText;
	private String summaryText;

	public PropPref(Context context, INDIProperty prop) {
		super(context);
		this.prop = prop;
		titleText = prop.getLabel();
		
		prop.addINDIPropertyListener(this);
		
		setTitle(prop.getLabel());
		
	}

	@Override
	protected void onBindView(View view) {
		super.onBindView(view);
		//INDIAdapter.getInstance().log(view.toString());
		title = (TextView) view.findViewById(android.R.id.title);
		//propertyChanged(prop);
		//changed ++;
	}
	

	@Override
	public void propertyChanged(INDIProperty arg0) {
		if(arg0!=prop){
			INDIAdapter.getInstance().log("wrong property");
			return;
		}
		
		changed ++;
		summaryText = "changed " + changed + " times, " + prop.getState();
		
		if(title==null){
			INDIAdapter.getInstance().log("null title, prop = " + prop.getLabel());
			return;
		}
		
		final PropPref thisPref = this;
		title.post(new Runnable() {
			public void run() {
				thisPref.setSummary(summaryText);
			}
		});

		
		
//		setSummary("changed "+changed+" times");
//		
//
//		if(title==null){
//			INDIAdapter.getInstance().log("null title, prop = " + prop.getLabel());
//			return;
//		}
//		
//		switch (prop.getState()) {
//		case ALERT:
//			title.post(new Runnable() {
//				@Override
//				public void run() {
//					title.setTextColor(Color.RED);
//				}
//			});
//			break;
//		case BUSY:
//			title.post(new Runnable() {
//				@Override
//				public void run() {
//					title.setTextColor(Color.YELLOW);
//				}
//			});
//			break;
//		case IDLE:
//			title.post(new Runnable() {
//				@Override
//				public void run() {
//					title.setTextColor(Color.WHITE);
//				}
//			});
//			break;
//		case OK:
//			title.post(new Runnable() {
//				@Override
//				public void run() {
//					title.setTextColor(Color.GREEN);
//				}
//			});
//			break;
//		}

	}
}
