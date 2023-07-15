package com.ekumfi.admin.pagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ekumfi.admin.fragment.AgentAccountFragment1;
import com.ekumfi.admin.fragment.AgentAccountFragment2;

public class AgentAccountPageAdapter extends FragmentPagerAdapter {

    public AgentAccountPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AgentAccountFragment1 agentAccountFragment1 = new AgentAccountFragment1();
                return agentAccountFragment1;
            case 1:
                AgentAccountFragment2 agentAccountFragment2 = new AgentAccountFragment2();
                return agentAccountFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}