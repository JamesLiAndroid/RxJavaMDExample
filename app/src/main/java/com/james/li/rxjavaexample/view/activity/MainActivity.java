package com.james.li.rxjavaexample.view.activity;

import android.os.Bundle;
import android.support.annotation.IdRes;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.james.li.rxjavaexample.R;
import com.james.li.rxjavaexample.adapter.MyFragmentAdapter;
import com.james.li.rxjavaexample.view.fragment.CombinationFragment;
import com.james.li.rxjavaexample.view.fragment.FilterFragment;
import com.james.li.rxjavaexample.view.fragment.HelloWorldFragment;
import com.james.li.rxjavaexample.view.fragment.ThreadFragment;
import com.james.li.rxjavaexample.view.fragment.TransformFragment;
import com.roughike.bottombar.BottomBar;
import com.roughike.bottombar.OnTabSelectListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.app_bar_layout)
    AppBarLayout appBarLayout;
    @BindView(R.id.viewPager)
    ViewPager viewPager;
    @BindView(R.id.fab)
    FloatingActionButton fab;
    @BindView(R.id.bottomBar)
    BottomBar bottomBar;

    private List<Fragment> fragmentList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(toolbar);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        initViewPager();

        bottomBar.setOnTabSelectListener(new OnTabSelectListener() {
            @Override
            public void onTabSelected(@IdRes int tabId) {
                switch (tabId) {
                    case R.id.hello_world:
                        viewPager.setCurrentItem(0);
                        break;
                    case R.id.filter:
                        viewPager.setCurrentItem(1);
                        break;
                    case R.id.transform:
                        viewPager.setCurrentItem(2);
                        break;
                    case R.id.combination:
                        viewPager.setCurrentItem(3);
                        break;
                    case R.id.thread:
                        viewPager.setCurrentItem(4);
                        break;
                    default:
                        break;
                }
            }
        });
    }

    private void initViewPager() {
        // 添加Fragment
        fragmentList.add(new HelloWorldFragment());
        fragmentList.add(new FilterFragment());
        fragmentList.add(new CombinationFragment());
        fragmentList.add(new TransformFragment());
        fragmentList.add(new ThreadFragment());

        // 设置Adapter
        FragmentManager manager = this.getSupportFragmentManager();
        viewPager.setAdapter(new MyFragmentAdapter(fragmentList, manager));

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                bottomBar.selectTabAtPosition(position);
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        // Necessary to restore the BottomBar's state, otherwise we would
        // lose the current tab on orientation change.
        bottomBar.onSaveInstanceState();
    }
}
