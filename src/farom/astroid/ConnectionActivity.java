package farom.astroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.View;

public class ConnectionActivity extends Activity {
	
	Scope scope;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);
        scope = new Scope();
        scope.setLogBox((LogTextBox) findViewById(R.id.text));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_connection, menu);
        return true;
    }
    
	public void btnOnClick(View view) {
	    scope.connect();
	}

    
}
