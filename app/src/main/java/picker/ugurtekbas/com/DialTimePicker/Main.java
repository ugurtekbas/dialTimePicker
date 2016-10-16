package picker.ugurtekbas.com.DialTimePicker;

import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.view.Menu;
import android.view.MenuItem;



public class Main extends ActionBarActivity {

    private ActionBar actionBar;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        actionBar  = getSupportActionBar();
        actionBar.setTitle(getResources().getString(R.string.appTitle));
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        viewPager.setAdapter(new ActivityAdapter(getSupportFragmentManager()));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return super.onOptionsItemSelected(item);
    }
}
