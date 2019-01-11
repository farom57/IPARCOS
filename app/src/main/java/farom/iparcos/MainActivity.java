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
 * The main activity of the application, that manages all the fragments.
 *
 * @author marcocipriani01
 */
public class MainActivity extends AppCompatActivity {

    /**
     * Last open page.
     */
    private Pages currentPage = Pages.CONNECTION;
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

        final BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                Pages newPage = Pages.fromId(item.getItemId());
                if ((newPage != null) && (newPage != currentPage)) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        if (newPage == Pages.GENERIC) {
                            toolbar.setElevation(0);

                        } else {
                            toolbar.setElevation(4);
                        }
                    }
                    getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out,
                            R.animator.fade_in, R.animator.fade_out).replace(R.id.content_frame, Pages.values()[newPage.index].instance).commit();
                    currentPage = newPage;
                    return true;
                }
                return false;
            }
        });

        Application.setGoToConnectionTab(new Runnable() {
            @Override
            public void run() {
                currentPage = Pages.CONNECTION;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    toolbar.setElevation(4);
                }
                getSupportFragmentManager().beginTransaction().setCustomAnimations(R.animator.fade_in, R.animator.fade_out,
                        R.animator.fade_in, R.animator.fade_out).replace(R.id.content_frame, Pages.CONNECTION.instance).commit();
                navigation.setSelectedItemId(currentPage.itemId);
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
     * @author marcocipriani01
     */
    private enum Pages {
        CONNECTION(0, R.id.menu_connection, new ConnectionFragment()),
        MOTION(1, R.id.menu_move, new MotionFragment()),
        GENERIC(2, R.id.menu_generic, new GenericFragment()),
        SEARCH(3, R.id.menu_search, new SearchFragment()),
        FOCUSER(4, R.id.menu_focuser, new FocuserFragment());

        private final int index;
        private final int itemId;
        private final Fragment instance;

        Pages(int index, int itemId, Fragment instance) {
            this.index = index;
            this.itemId = itemId;
            this.instance = instance;
        }

        static Pages fromId(int id) {
            for (Pages p : Pages.values()) {
                if (p.itemId == id) {
                    return p;
                }
            }
            return null;
        }
    }
}