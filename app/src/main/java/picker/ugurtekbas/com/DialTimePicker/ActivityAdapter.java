package picker.ugurtekbas.com.DialTimePicker;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

/**
 * Adapter for view pager.
 * @author Ugur Tekbas
 */
public class ActivityAdapter extends FragmentStatePagerAdapter{

    int[] layoutIDs = {R.layout.normal_picker, R.layout.ampm_picker};

    public ActivityAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {
        return MainFragment.newInstance(layoutIDs[position]);
    }

    @Override
    public int getCount() {
        return layoutIDs.length;
    }
}
