package io.flyingmongoose.EtzioDemo

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter

class ActivityAdapter(fm: FragmentManager) : FragmentStatePagerAdapter(fm) {

    var layoutIDs = intArrayOf(R.layout.normal_picker, R.layout.ampm_picker)

    override fun getItem(position: Int): Fragment {
        return MainFragment.newInstance(layoutIDs[position])
    }

    override fun getCount(): Int {
        return layoutIDs.size
    }
}
