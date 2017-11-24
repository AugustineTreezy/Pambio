package com.codegreed_devs.pambio.Activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.codegreed_devs.pambio.Fragments.AboutUsFragment;
import com.codegreed_devs.pambio.Fragments.ContactUsFragment;
import com.codegreed_devs.pambio.Fragments.HomeFragment;
import com.codegreed_devs.pambio.Fragments.UpcomingEventFragment;
import com.codegreed_devs.pambio.R;
import com.daimajia.slider.library.SliderLayout;
import com.daimajia.slider.library.SliderTypes.BaseSliderView;
import com.daimajia.slider.library.SliderTypes.TextSliderView;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout drawerLayout;
    private ViewPager viewPager;
    public SliderLayout sliderShow;
    TabLayout tabLayout;
    private int[] tabIcons = {
            R.drawable.ic_home,
            R.drawable.ic_event,
            R.drawable.ic_contact,
            R.drawable.ic_about
    };
    HashMap<String, String> HashMapForURL ;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.mToolbar);
        setSupportActionBar(toolbar);

        final ActionBar ab = getSupportActionBar();
        assert ab != null;
        ab.setHomeAsUpIndicator(R.drawable.ic_menu);
        ab.setDisplayHomeAsUpEnabled(true);

        drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);
        NavigationView navView = (NavigationView) findViewById(R.id.navigation_view);
        if (navView != null){
            setupDrawerContent(navView);
        }

        viewPager = (ViewPager)findViewById(R.id.tab_viewpager);
        if (viewPager != null){
            setupViewPager(viewPager);

        }

        //image slider
        sliderShow = (SliderLayout) findViewById(R.id.slider);
        AddImagesUrlOnline();
        for(String name : HashMapForURL.keySet()){

            TextSliderView textSliderView = new TextSliderView(MainActivity.this);

            textSliderView
                    .description(name)
                    .image(HashMapForURL.get(name))
                    .setScaleType(BaseSliderView.ScaleType.Fit);

            textSliderView.bundle(new Bundle());

            textSliderView.getBundle()
                    .putString("extra",name);

            sliderShow.addSlider(textSliderView);
            sliderShow.setDuration(10300);
        }



        tabLayout = (TabLayout)findViewById(R.id.tabLayout);
        tabLayout.setupWithViewPager(viewPager);
        setupTabIcons();

        tabLayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }
    //url for image slider
    public void AddImagesUrlOnline(){

        HashMapForURL = new HashMap<>();

        HashMapForURL.put("Peace begins with me", "http://pambio.org/wp-content/uploads/2017/06/DSCN2321-1.jpg");
        HashMapForURL.put("Run for a Drug free & Peaceful Society", "http://pambio.org/wp-content/uploads/2017/01/mar2.jpg");
        HashMapForURL.put("Giving back to the Community", "http://pambio.org/wp-content/uploads/2017/01/sasah.jpg");
        HashMapForURL.put("A walk for Peace", "http://pambio.org/wp-content/uploads/2017/01/14715609_1304230262955555_1947524938596085328_o.jpg");
        HashMapForURL.put("Bridging to the future", "http://pambio.org/wp-content/uploads/2017/03/WhatsApp-Image-2017-03-03-at-10.44.02-AM.jpeg");

    }
    @Override
    //stop auto_cycle to prevent memory lead
    protected void onStop() {
        sliderShow.stopAutoCycle();
        super.onStop();
    }
    //setting title an fragments for the tabs
    private void setupViewPager(ViewPager viewPager){
        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFrag(new HomeFragment(), "Home");
        adapter.addFrag(new UpcomingEventFragment(), "Upcoming Events");
        adapter.addFrag(new ContactUsFragment(), "Contact Us");
        adapter.addFrag(new AboutUsFragment(), "About Us");

        viewPager.setAdapter(adapter);
    }
    //setting icons for the tabs
    private void setupTabIcons() {
        tabLayout.getTabAt(0).setIcon(tabIcons[0]);
        tabLayout.getTabAt(1).setIcon(tabIcons[1]);
        tabLayout.getTabAt(2).setIcon(tabIcons[2]);
        tabLayout.getTabAt(3).setIcon(tabIcons[3]);
    }

    private void setupDrawerContent(NavigationView navigationView){
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                menuItem.setChecked(true);

                switch (menuItem.getItemId()) {
                    case R.id.mentor:
                        Intent mentor=new Intent(MainActivity.this,MentorActivity.class);
                        startActivity(mentor);
                        break;
                    case R.id.green_prt:
                        Intent green_prt=new Intent(MainActivity.this,GreenPrintActivity.class);
                        startActivity(green_prt);
                        break;
                    case R.id.peace_edu:
                        Intent peace_edu=new Intent(MainActivity.this,PeaceEduActivity.class);
                        startActivity(peace_edu);
                        break;
                    case R.id.peace_advo:
                        Intent peace_advo=new Intent(MainActivity.this,PeaceAdvoActivity.class);
                        startActivity(peace_advo);
                        break;
                    case R.id.health:
                        Intent health=new Intent(MainActivity.this,HealthActivity.class);
                        startActivity(health);
                        break;
                    case R.id.life_skills:
                        Intent life_skills=new Intent(MainActivity.this,LifeSkillsActivity.class);
                        startActivity(life_skills);
                        break;
                    case R.id.nav_share:
                        Intent share=new Intent(Intent.ACTION_SEND);
                        share.setType("text/plain");
                        share.putExtra(Intent.EXTRA_TEXT,"https://play.google.com/store/apps/details?id=com.codegreed_devs.pambio");
                        startActivity(Intent.createChooser(share,"Share Via"));
                        break;
                    case R.id.nav_rate:
                        openStore();
                        break;
                    case R.id.nav_settings:
                        Intent settings=new Intent(MainActivity.this,SettingsActivity.class);
                        startActivity(settings);
                        break;


                }

                drawerLayout.closeDrawers();
                return true;
            }
        });
    }
    public void openStore(){
        Uri playstore = Uri.parse("https://play.google.com/store/apps/details?id=com.codegreed_devs.pambio");
        Intent store = new Intent(Intent.ACTION_VIEW, playstore);
        store.setPackage("com.android.vending");
        try {
            startActivity(store);

        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://play.google.com/store/apps/details?id=com.codegreed_devs.pambio")));
        }
    }


    private static class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> mFragmentList = new ArrayList<>();
        private final List<String> mFragmentTitleList = new ArrayList<>();

        ViewPagerAdapter(FragmentManager manager){
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

        void addFrag(Fragment fragment, String title){
            mFragmentList.add(fragment);
            mFragmentTitleList.add(title);
        }

        @Override
        public CharSequence getPageTitle(int position){
            return mFragmentTitleList.get(position);
        }
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle actionh bar item clicks here. The action bar will
        // automatically handle clicks on the HomeFragment/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id){
            case android.R.id.home:
                if (drawerLayout.isDrawerOpen(GravityCompat.START)){
                    drawerLayout.closeDrawer(GravityCompat.START);
                } else {
                    drawerLayout.openDrawer(GravityCompat.START);
                }

                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}

