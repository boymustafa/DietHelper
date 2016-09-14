package com.mustafa.diethelper;

import android.app.DatePickerDialog;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;
import org.joda.time.format.DateTimeFormatterBuilder;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener{

    private static final String TAG = "MainActivity";

    private static final String KEY_DATE_SERIALIZABLE = "DATE";
    private static final String KEY_FAB_SHOWN_BOOLEAN = "FAB";

    private static final DateTimeFormatter DATE_FORMATTER;

    static {
        DateTimeFormatterBuilder builder = new DateTimeFormatterBuilder();
        builder.appendDayOfWeekShortText();
        builder.appendLiteral(", ");
        builder.append(DateTimeFormat.mediumDate());
        DATE_FORMATTER = builder.toFormatter();
    }

    private FloatingActionButton mFab, mFabFood, mFabDrink, mFabMore;
    private ActionBar mActionBarl;
//    private CalendarFragment mCalendarFragment = null;

    private Animation mFab2AnimationShow, mFab2AnimationHide;
    private Animation mFabAnimationRotateFw, mFabAnimatonRotateBw;

    private DatePickerDialog mDatePickerDialog;

    private LocalDate mSelectedDate;
    private Boolean mFabShown;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (savedInstanceState!=null){
            mSelectedDate = (LocalDate) savedInstanceState.getSerializable(KEY_DATE_SERIALIZABLE);
            mFabShown = savedInstanceState.getBoolean(KEY_FAB_SHOWN_BOOLEAN);
        } else {
            mSelectedDate = LocalDate.now();
            mFabShown = false;
        }

        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if(toolbar!=null){
            toolbar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    showDPDialog();
                }
            });
            setSupportActionBar(toolbar);;
        }

        mActionBarl = getSupportActionBar();
        mFabAnimationRotateFw = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fab_rotate_fw);
        mFabAnimatonRotateBw = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fab_rotate_bw);
        mFab2AnimationShow = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fab2_show);
        mFab2AnimationHide = AnimationUtils.loadAnimation(MainActivity.this,R.anim.fab2_hide);

        mFab = (FloatingActionButton) findViewById(R.id.fab);
        mFabFood = (FloatingActionButton) findViewById(R.id.fabFood);
        mFabDrink = (FloatingActionButton) findViewById(R.id.fabDrink);
        mFabMore = (FloatingActionButton) findViewById(R.id.fabMore);

        if(mFabShown){
            mFab.setAnimation(mFabAnimationRotateFw);
            mFab.animate().setDuration(0);

            mFabFood.setAnimation(mFab2AnimationShow);
            mFabFood.animate().setDuration(0);

            mFabDrink.setAnimation(mFab2AnimationShow);
            mFabDrink.animate().setDuration(0);

            mFabMore.setAnimation(mFab2AnimationShow);
            mFabMore.animate().setDuration(0);
        }

        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if(drawerLayout!=null){
            ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(MainActivity.this,
                    drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
            drawerLayout.addDrawerListener(toggle);
            toggle.syncState();
        }

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        if(navigationView!=null){
            navigationView.setNavigationItemSelectedListener(this);
        }
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_DATE_SERIALIZABLE,mSelectedDate);
        outState.putBoolean(KEY_FAB_SHOWN_BOOLEAN,mFabShown);

        Log.d(TAG,"Main ACtivity instance has beend saved");
    }

    //region FAB events & methods
    public void onFabClicked(View view) {
        if (mFabShown) {
            mFabShown = false;
            mFab.startAnimation(mFabAnimatonRotateBw);

            mFabFood.setClickable(false);
            mFabFood.startAnimation(mFab2AnimationHide);

            mFabDrink.setClickable(false);
            mFabDrink.startAnimation(mFab2AnimationHide);

            mFabMore.setClickable(false);
            mFabMore.startAnimation(mFab2AnimationHide);
        } else {
            mFabShown = true;
            mFab.startAnimation(mFabAnimationRotateFw);

            mFabMore.startAnimation(mFab2AnimationShow);
            mFabMore.setClickable(true);

            mFabDrink.startAnimation(mFab2AnimationShow);
            mFabDrink.setClickable(true);

            mFabFood.startAnimation(mFab2AnimationShow);
            mFabFood.setClickable(true);
        }
    }


    private void showDPDialog(){

    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        return false;
    }
}
