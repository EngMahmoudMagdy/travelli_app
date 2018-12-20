package com.magdy.travelli.UI;

import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.magdy.travelli.Adapters.SearchAdapter;
import com.magdy.travelli.Adapters.SharedPreference;
import com.magdy.travelli.Adapters.Utils;
import com.magdy.travelli.R;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawer;
    private Toolbar toolbar;
    private ActionBarDrawerToggle drawerToggle;
    TextView navName , navEmail;
    private ArrayList<String> mCountries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar =  findViewById(R.id.toolbar);
        setSupportActionBar(toolbar); // for toolbar_home

        ViewPager tabPager =  findViewById(R.id.tappager);
        setupTapPager(tabPager);

        TabLayout tabLayout =  findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(tabPager);
        mDrawer = findViewById(R.id.drawer_layout);//layout itself
        NavigationView nvDrawer = findViewById(R.id.nvView);
        View header = nvDrawer.inflateHeaderView(R.layout.nav_header);
        navEmail =  header.findViewById(R.id.emailtext);
        navName =  header.findViewById(R.id.uname);

        mDrawer.addDrawerListener(drawerToggle);
        // Setup drawer view
        setupDrawerContent(nvDrawer);
        drawerToggle = setupDrawerToggle();
        drawerToggle.setDrawerIndicatorEnabled(true);
        findViewById(R.id.search).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getBaseContext(),getString(R.string.search),Toast.LENGTH_SHORT).show();
            }
        });
    }
    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        // Sync the toggle state after onRestoreInstanceState has occurred.
        drawerToggle.syncState();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        // Pass any configuration change to the drawer toggles
        drawerToggle.onConfigurationChanged(newConfig);
    }
    private ActionBarDrawerToggle setupDrawerToggle() {
        // NOTE: Make sure you pass in a valid toolbar_home reference.  ActionBarDrawToggle() does not require it
        // and will not render the hamburger icon without it.
        return new ActionBarDrawerToggle(this, mDrawer, toolbar, R.string.navigation_drawer_open,  R.string.navigation_drawer_close)
        {
            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };
    }
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        selectDrawerItem(menuItem);
                        return true;
                    }
                });
    }
    public void selectDrawerItem(MenuItem menuItem) {
        mDrawer.closeDrawers();
        if (menuItem.getItemId()==R.id.nav_log)
        {
            Intent intent = new Intent(getBaseContext(),SignInActivity.class);
            startActivity(intent);

        }
        if(menuItem.getItemId()==R.id.nav_fav){
        Favourite fragment = Favourite.newInstance();
        fragment.show(getFragmentManager(),"favourite");

        }
//        if(menuItem.getItemId()==R.id.nav_fav)
//        {
//            Intent intent = new Intent(getBaseContext(),FavouriteToursFragment.class);
//            startActivity(intent);
//        }
        /*Intent intent;
        switch(menuItem.getItemId()) {
            case R.id.nav_third_feedback:
                intent = new Intent(getBaseContext(),FeedbackActivity.class);
                startActivity(intent);
                break;
        }*/
    }
    private void setupTapPager(ViewPager TapPager) {
        TapPagerAdapter adapter = new TapPagerAdapter(getSupportFragmentManager());
        ToursFragment fragment  = ToursFragment.newInstance();
        adapter.addFragment(fragment,"Tours");
        ExploreFragment f = ExploreFragment.newInstance();
        adapter.addFragment(f,"Map");
        TapPager.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.toolbar_menu, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        switch (id) {

            case android.R.id.home:
                finish();
                break;

            case R.id.search:
               loadToolBarSearch();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    private class TapPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        TapPagerAdapter(FragmentManager manager) {
            super(manager);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentList.get(position);
        }

        @Override
        public int getCount() {
            return mFragmentList.size();
        }

        void addFragment(Fragment fragment, String title) {
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitleList.get(position);
        }
    }
    public void loadToolBarSearch() {

        ArrayList<String> countryStored = SharedPreference.loadList(MainActivity.this, Utils.PREFS_NAME, Utils.KEY_COUNTRIES);
        View view = MainActivity.this.getLayoutInflater().inflate(R.layout.view_toolbar_search, null);
        LinearLayout parentToolbarSearch = view.findViewById(R.id.parent_toolbar_search);
        ImageView imgToolBack = view.findViewById(R.id.img_tool_back);
        final EditText edtToolSearch = view.findViewById(R.id.edt_tool_search);
        final ListView listSearch = view.findViewById(R.id.list_search);
        final TextView txtEmpty = view.findViewById(R.id.txt_empty);
        Utils.setListViewHeightBasedOnChildren(listSearch);
        edtToolSearch.setHint("Search your tour");
        final Dialog toolbarSearchDialog = new Dialog(MainActivity.this, R.style.MaterialSearch);
        toolbarSearchDialog.setContentView(view);
        toolbarSearchDialog.setCancelable(false);
        toolbarSearchDialog.getWindow().setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.MATCH_PARENT);
        toolbarSearchDialog.getWindow().setGravity(Gravity.BOTTOM);
        toolbarSearchDialog.show();
        toolbarSearchDialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        countryStored = (countryStored != null && countryStored.size() > 0) ? countryStored : new ArrayList<String>();
        final SearchAdapter searchAdapter = new SearchAdapter(MainActivity.this, countryStored, false);
        listSearch.setVisibility(View.VISIBLE);
        listSearch.setAdapter(searchAdapter);
        listSearch.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                String country = String.valueOf(adapterView.getItemAtPosition(position));
                SharedPreference.addList(MainActivity.this, Utils.PREFS_NAME, Utils.KEY_COUNTRIES, country);
                Toast.makeText(MainActivity.this,
                        "clicked search result item is"+ country,
                        Toast.LENGTH_SHORT).show();
                edtToolSearch.setText(country);
                listSearch.setVisibility(View.GONE);
            }
        });
        edtToolSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                String[] country = null;
                mCountries = new ArrayList<String>(Arrays.asList(country));
                listSearch.setVisibility(View.VISIBLE);
                searchAdapter.updateList(mCountries, true);
            }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                ArrayList<String> filterList = new ArrayList<String>();
                boolean isNodata = false;
                if (s.length() > 0) {
                    for (int i = 0; i < mCountries.size(); i++) {
                        if (mCountries.get(i).toLowerCase().startsWith(s.toString().trim().toLowerCase())) {
                            filterList.add(mCountries.get(i));
                            listSearch.setVisibility(View.VISIBLE);
                            searchAdapter.updateList(filterList, true);
                            isNodata = true;
                        }
                    }
                    if (!isNodata) {
                        listSearch.setVisibility(View.GONE);
                        txtEmpty.setVisibility(View.VISIBLE);
                        txtEmpty.setText("No data found");
                    }
                } else {
                    listSearch.setVisibility(View.GONE);
                    txtEmpty.setVisibility(View.GONE);
                }
            }
            @Override
            public void afterTextChanged(Editable s) {
            }
        });

        imgToolBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toolbarSearchDialog.dismiss();
            }
        });
    }
}
