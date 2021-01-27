package com.example.testapp.adapter

import android.content.Context
import android.util.SparseArray
import android.view.ViewGroup
import androidx.core.util.isEmpty
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.testapp.R
import com.example.testapp.ui.EnterFieldFragment

class PagerAdapter(private val context: Context, private val fragmentManager: FragmentManager) : FragmentPagerAdapter(fragmentManager) {

    override fun getCount(): Int {
        return context.resources.getStringArray(R.array.stepLabels).size
    }
    private val fragments = SparseArray<Fragment>()

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val fragment = super.instantiateItem(container, position) as Fragment
        fragments.put(position, fragment)
        return fragment
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        fragments.remove(position)
        super.destroyItem(container, position, `object`)
    }

    override fun getItem(position: Int): Fragment {
        val fragment = getFragment(position)
        return fragment ?: EnterFieldFragment.instance(position)
    }

    private fun getFragment(position: Int): Fragment? {
        return if (fragments.isEmpty())
            null
        else
            fragments[position]
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return context.resources.getStringArray(R.array.stepLabels)[position]
    }
}