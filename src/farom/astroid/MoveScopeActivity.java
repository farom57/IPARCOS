package farom.astroid;

import android.os.Bundle;
import android.app.Activity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.Button;
import android.support.v4.app.NavUtils;

public class MoveScopeActivity extends Activity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initComponents();
    }

    public void initComponents(){
    	setContentView(R.layout.activity_move_scope);
    	
    	findViewById(R.id.btn_up).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	Scope.getLastInstance().moveNorth();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	Scope.getLastInstance().stopMove();
		        }
		        return false;
			}
		});
    	findViewById(R.id.btn_left).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	Scope.getLastInstance().moveWest();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	Scope.getLastInstance().stopMove();
		        }
		        return false;
			}
		});
    	findViewById(R.id.btn_right).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	Scope.getLastInstance().moveEast();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	Scope.getLastInstance().stopMove();
		        }
		        return false;
			}
		});
    	findViewById(R.id.btn_down).setOnTouchListener(new OnTouchListener(){
			@Override
			public boolean onTouch(View v, MotionEvent event) {
		        if(event.getAction() == MotionEvent.ACTION_DOWN) {
		        	Scope.getLastInstance().moveSouth();
		        } else if (event.getAction() == MotionEvent.ACTION_UP) {
		        	Scope.getLastInstance().stopMove();
		        }
		        return false;
			}
		});
    	findViewById(R.id.btn_center).setOnClickListener(new OnClickListener(){
			@Override
			public void onClick(View v) {
				Scope.getLastInstance().stopMove();				
			}
		});
    	
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_move_scope, menu);
        return true;
    }
    


}
