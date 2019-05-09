package com.cna.mineru.cna.Fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.cna.mineru.cna.Adapter.CustomViewPager;
import com.cna.mineru.cna.Adapter.FragmentPagerAdapter;
import com.cna.mineru.cna.Fragment.ExamFragmentChild.DoExamFragment;
import com.cna.mineru.cna.Fragment.ExamFragmentChild.ListExamFragment;
import com.cna.mineru.cna.R;


public class ExamFragment extends Fragment {

    private CustomViewPager viewPager;
    private TabLayout tabLayout;
    public ExamFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_exam, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@Nullable View view, @Nullable Bundle savedInstanceState){
        super.onViewCreated(view, savedInstanceState);

        viewPager = (CustomViewPager) view.findViewById(R.id.view_pager);
        tabLayout = view.findViewById(R.id.tablayout);

        setupViewPager(viewPager);
        tabLayout.setupWithViewPager(viewPager);
        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setupViewPager(ViewPager viewPager) {
        FragmentPagerAdapter viewPagerAdapter = new FragmentPagerAdapter(getChildFragmentManager());
        viewPagerAdapter.addFragment(new DoExamFragment(), getString(R.string.title_test_1));
        viewPager.setAdapter(viewPagerAdapter);
        viewPagerAdapter.addFragment(new ListExamFragment(), getString(R.string.title_test_2));
        viewPager.setAdapter(viewPagerAdapter);
    }

}
