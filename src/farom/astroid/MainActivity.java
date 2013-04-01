package farom.astroid;

import android.app.TabActivity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TabHost;
import android.widget.TextView;
import android.view.LayoutInflater;
import android.view.View;


@SuppressWarnings("deprecation")
public class MainActivity extends TabActivity {


	private TabHost tabHost;
	 
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        initComponents();
        
    }
    
    private void initComponents(){
    	setContentView(R.layout.activity_main);
    	 
        this.tabHost = getTabHost();
 
        setupTab(getResources().getDrawable(R.drawable.icon_connect), "tab1", new Intent().setClass(this, ConnectionActivity.class));
        setupTab(getResources().getDrawable(R.drawable.icon_move), "tab2", new Intent().setClass(this, MoveScopeActivity.class));
    }
 
    private void setupTab(Drawable icon, String tag, Intent intent) {
		tabHost.addTab(tabHost.newTabSpec(tag).setIndicator(createTabView(tabHost.getContext(), icon)).setContent(intent));
	}
 
	private static View createTabView(final Context context, final Drawable icon) {
		View view = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
		ImageView iv = (ImageView) view.findViewById(R.id.tab_icon);
		iv.setImageDrawable(icon);

 
		return view;
	}
}

