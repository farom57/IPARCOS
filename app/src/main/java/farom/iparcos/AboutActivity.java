package farom.iparcos;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * @author SquareBoot
 */
public class AboutActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        getSupportActionBar().setHomeButtonEnabled(true);
    }
}
