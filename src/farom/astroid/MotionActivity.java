package farom.astroid;

import java.util.Date;

import laazotea.indi.client.INDIDevice;
import laazotea.indi.client.INDIServerConnection;
import laazotea.indi.client.INDIServerConnectionListener;
import android.app.Activity;
import android.app.ActionBar;
import android.app.Fragment;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.os.Build;

public class MotionActivity extends Activity implements INDIServerConnectionListener {
	
	private INDIAdapter indiAdapter;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_motion);

		indiAdapter = INDIAdapter.getInstance();
		indiAdapter.registerPermanentConnectionListener(this);
		indiAdapter.setBtnMoveE((Button)findViewById(R.id.buttonE));
		indiAdapter.setBtnMoveW((Button)findViewById(R.id.buttonW));
		indiAdapter.setBtnMoveN((Button)findViewById(R.id.buttonN));
		indiAdapter.setBtnMoveS((Button)findViewById(R.id.buttonS));
		indiAdapter.setBtnMoveNE((Button)findViewById(R.id.buttonNE));
		indiAdapter.setBtnMoveNW((Button)findViewById(R.id.buttonNW));
		indiAdapter.setBtnMoveSE((Button)findViewById(R.id.buttonSE));
		indiAdapter.setBtnMoveSW((Button)findViewById(R.id.buttonSW));
		indiAdapter.setBtnStop((Button)findViewById(R.id.buttonStop));
		indiAdapter.setBtnSpeedUp((Button)findViewById(R.id.buttonSpeedUp));
		indiAdapter.setBtnSpeedDown((Button)findViewById(R.id.buttonSpeedDown));
		indiAdapter.updateBtnState();
		indiAdapter.setSpeedText((TextView)findViewById(R.id.speedText));
		indiAdapter.updateSpeedText();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.global, menu);
		
		// hide the menu item for the current activity
		MenuItem motionItem = menu.findItem(R.id.menu_move);
		motionItem.setVisible(false);
		return true;
	}

	/**
	 * open the motion activity, 
	 * @param v 
	 */
	public boolean openMotionActivity(MenuItem v){
		// nothing to do, already the current activity
		return false;
	}

	/**
	 * open the settings activity
	 * @param v
	 * @return 
	 */
	public boolean openSettingsActivity(MenuItem v){
		// TODO
		return false;
	}
	
	/**
	 * open the generic activity
	 * @param v
	 * @return 
	 */
	public boolean openGenericActivity(MenuItem v){
		Intent intent = new Intent(this, GenericActivity.class);
		startActivity(intent);
		return true;
	}
	
	/**
	 * open the connection activity
	 * @param v
	 * @return 
	 */
	public boolean openConnectionActivity(MenuItem v){
		Intent intent = new Intent(this, ConnectionActivity.class);
		startActivity(intent);
		return true;
	}
	
	@Override
	public void connectionLost(INDIServerConnection arg0) {
		openConnectionActivity(null);
	}

	@Override
	public void newDevice(INDIServerConnection arg0, INDIDevice arg1) {

	}

	@Override
	public void newMessage(INDIServerConnection arg0, Date arg1, String arg2) {

	}

	@Override
	public void removeDevice(INDIServerConnection arg0, INDIDevice arg1) {
		openConnectionActivity(null);
	}
}
