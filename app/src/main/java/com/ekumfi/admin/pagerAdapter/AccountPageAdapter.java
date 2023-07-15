package com.ekumfi.admin.pagerAdapter;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;

import com.ekumfi.admin.fragment.AccountFragment1;
import com.ekumfi.admin.fragment.AccountFragment2;
import com.ekumfi.admin.fragment.SellerAccountFragment1;
import com.ekumfi.admin.fragment.SellerAccountFragment2;

public class AccountPageAdapter extends FragmentPagerAdapter {

    public AccountPageAdapter(FragmentManager fm) {
        super(fm);
    }

    @Override
    public Fragment getItem(int position) {

        switch (position) {
            case 0:
                AccountFragment1 accountFragment1 = new AccountFragment1();
                return accountFragment1;
            case 1:
                AccountFragment2 accountFragment2 = new AccountFragment2();
                return accountFragment2;
            default:
                return null;
        }
    }

    @Override
    public int getCount() {
        return 2;
    }
}