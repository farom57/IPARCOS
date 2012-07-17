package farom.astroid;

import android.app.TabActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TabHost;


public class MainActivity extends TabActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final TabHost tabHost = getTabHost();

        tabHost.addTab(tabHost.newTabSpec("tab1")
                .setIndicator("",getResources().getDrawable(R.drawable.icon_connect))
                .setContent(new Intent(this, ConnectionActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("",getResources().getDrawable(R.drawable.icon_move))
                .setContent(new Intent(this, MoveScopeActivity.class)));
        
        tabHost.setBackgroundDrawable(getResources().getDrawable(R.drawable.btn_red));
        

    }
}