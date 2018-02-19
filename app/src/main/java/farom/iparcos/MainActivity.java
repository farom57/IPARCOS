package farom.iparcos;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

/**
 * The main activity of the application, that contains all the fragments.
 *
 * @author SquareBoot
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Last open page.
     */
    private Pages lastPage = Pages.CONNECTION;
    private Fragment[] fragments;
    /**
     * The activity's toolbar.
     */
    private Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.app_toolbar);
        setSupportActionBar(toolbar);

        getSupportFragmentManager().beginTransaction()
                .replace(R.id.content_frame, new ConnectionFragment()).commit();

        fragments = new Fragment[4];
        fragments[Pages.CONNECTION.getIndex()] = new ConnectionFragment();
        fragments[Pages.MOTION.getIndex()] = new MotionFragment();
        fragments[Pages.GENERIC.getIndex()] = new GenericFragment();
        fragments[Pages.SEARCH.getIndex()] = new SearchFragment();

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                for (Pages p : Pages.values()) {
                    int id = item.getItemId();
                    if (p.getItemId() == id) {
                        Pages current = Pages.fromId(id);
                        if ((current == null) || (current.getIndex() == lastPage.getIndex())) {
                            return false;
                        }
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            if (p == Pages.GENERIC) {
                                toolbar.setElevation(0);

                            } else {
                                toolbar.setElevation(4);
                            }
                        }
                        getSupportFragmentManager().beginTransaction().setCustomAnimations(
                                R.anim.fade_in, R.anim.fade_out, 0, 0)
                                .replace(R.id.content_frame, fragments[p.getIndex()]).commit();
                        lastPage = current;
                        return true;
                    }
                }
                return false;
            }
        });

        Application.setGoToConnectionTab(new Runnable() {
            @Override
            public void run() {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setElevation(4);
                }
                getSupportFragmentManager().beginTransaction()
                        .setCustomAnimations(R.anim.fade_in, R.anim.fade_out, 0, 0)
                        .replace(R.id.content_frame, fragments[Pages.CONNECTION.getIndex()]).commit();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.global, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_about:
                startActivity(new Intent(this, AboutActivity.class));
                return true;
        }
        return false;
    }

    /**
     * @author SquareBoot
     */
    private enum Pages {
        CONNECTION(0, R.id.menu_connection),
        MOTION(1, R.id.menu_move),
        GENERIC(2, R.id.menu_generic),
        SEARCH(3, R.id.menu_search);

        private final int index;
        private int itemId;

        Pages(int index, int itemId) {
            this.index = index;
            this.itemId = itemId;
        }

        static Pages fromId(int id) {
            for (Pages p : Pages.values()) {
                if (p.getItemId() == id) {
                    return p;
                }
            }
            return null;
        }

        static Pages fromIndex(int index) {
            for (Pages p : Pages.values()) {
                if (p.getIndex() == index) {
                    return p;
                }
            }
            return null;
        }

        int getItemId() {
            return itemId;
        }

        int getIndex() {
            return index;
        }
    }
}