package farom.iparcos;

import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;

/**
 * @author SquareBoot
 */
public class SearchActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentById(android.R.id.content) == null) {
            SearchFragment fragment = new SearchFragment();
            fragmentManager.beginTransaction().add(android.R.id.content, fragment).commit();
        }
    }
}