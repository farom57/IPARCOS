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
                .setIndicator("list")
                .setContent(new Intent(this, ConnectionActivity.class)));

        tabHost.addTab(tabHost.newTabSpec("tab2")
                .setIndicator("photo list")
                .setContent(new Intent(this, MoveScopeActivity.class)));
        

    }
}