package com.cna.mineru.cna.Fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewpager.widget.ViewPager
import com.google.android.material.tabs.TabLayout

import com.cna.mineru.cna.Adapter.CustomViewPager
import com.cna.mineru.cna.Adapter.FragmentPagerAdapter
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragment
import com.cna.mineru.cna.Fragment.ExamFragmentChild.ListExamFragment
import com.cna.mineru.cna.R
import javax.annotation.Nullable
import kotlinx.android.synthetic.main.fragment_exam.*

class ExamFragment : Fragment() {

    private var viewPager: CustomViewPager? = null
    private var tabLayout: TabLayout? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_exam, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setupViewPager(viewPager!!)
        tabLayout!!.setupWithViewPager(viewPager)
        tabLayout!!.setOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab) {

            }

            override fun onTabUnselected(tab: TabLayout.Tab) {

            }

            override fun onTabReselected(tab: TabLayout.Tab) {

            }
        })
    }

    private fun setupViewPager(viewPager: ViewPager) {
        val viewPagerAdapter = FragmentPagerAdapter(getChildFragmentManager())
        viewPagerAdapter.addFragment(DoExamFragment(), getString(R.string.title_test_1))
        viewPager.setAdapter(viewPagerAdapter)
        viewPagerAdapter.addFragment(ListExamFragment(), getString(R.string.title_test_2))
        viewPager.setAdapter(viewPagerAdapter)
    }

}
