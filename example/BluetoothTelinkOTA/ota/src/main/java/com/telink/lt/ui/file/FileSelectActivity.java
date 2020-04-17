package com.telink.lt.ui.file;

import android.app.ActionBar;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.telink.lt.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 2016/10/8.
 */
public class FileSelectActivity extends FragmentActivity implements View.OnClickListener {

    private LocalFileSelectFragment localFileSelectFragment;
    private WebFileSelectFragment webFileSelectFragment;
    private ViewPager pager;
    private List<Fragment> mFragments = new ArrayList<>();
    private TextView tv_attention;
    private TextView tv_no_attention;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_file_select);
        initView();
        initViewPager();
    }

    private void initView() {
        ActionBar actionBar = getActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Select Bin");
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeButtonEnabled(true);
            actionBar.setDisplayShowHomeEnabled(true);
        }
    }

    private void initViewPager() {
        localFileSelectFragment = new LocalFileSelectFragment();
        webFileSelectFragment = new WebFileSelectFragment();
        mFragments.add(localFileSelectFragment);
        mFragments.add(webFileSelectFragment);
        pager = (ViewPager) findViewById(R.id.pager);
        MyFragmentPageAdapter pageAdapter = new MyFragmentPageAdapter(getSupportFragmentManager());
        pager.setAdapter(pageAdapter);
        pager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                if (position == 0) {
                    tv_attention.setSelected(true);
                    tv_no_attention.setSelected(false);
                } else {
                    tv_attention.setSelected(false);
                    tv_no_attention.setSelected(true);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tv_attention = (TextView) findViewById(R.id.tv_attention);
        tv_attention.setOnClickListener(this);
        tv_attention.setSelected(true);
        tv_no_attention = (TextView) findViewById(R.id.tv_no_attention);
        tv_no_attention.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.tv_attention) {
            tv_attention.setSelected(true);
            tv_no_attention.setSelected(false);
            pager.setCurrentItem(0);
        } else if (v.getId() == R.id.tv_no_attention) {
            tv_attention.setSelected(false);
            tv_no_attention.setSelected(true);
            pager.setCurrentItem(1);
        }
    }

    private class MyFragmentPageAdapter extends FragmentPagerAdapter {

        public MyFragmentPageAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }

        @Override
        public int getCount() {
            return mFragments.size();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}
