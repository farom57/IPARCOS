package farom.iparcos;

import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;

/**
 * @author SquareBoot
 */
public class MainActivity extends AppCompatActivity {

    private Pages lastPage = Pages.CONNECTION;
    private Fragment[] fragments;
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
        fragments[Pages.SEARCH.getIndex()] = null;

        BottomNavigationView navigation = findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {

            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                for (Pages p : Pages.values()) {
                    int id = item.getItemId();
                    if (p.getItemId() == id) {
                        boolean beforeAfter;
                        Pages current = Pages.fromId(id);
                        try {
                            int c = current.getIndex(), l = lastPage.getIndex();
                            if (l == c) {
                                return false;
                            }
                            beforeAfter = (l > current.getIndex());

                        } catch (NullPointerException e) {
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
                                beforeAfter ? R.anim.slide_in_right : R.anim.slide_in_left,
                                beforeAfter ? R.anim.slide_out_right : R.anim.slide_out_left,
                                0, 0)
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
                getSupportFragmentManager().beginTransaction().setCustomAnimations(
                        R.anim.slide_in_left, R.anim.slide_out_left, 0, 0)
                        .replace(R.id.content_frame, fragments[Pages.CONNECTION.getIndex()]).commit();
            }
        });
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