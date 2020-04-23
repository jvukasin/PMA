package com.bbf.cruise;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bbf.cruise.activities.SplashScreen;
import com.bbf.cruise.adapters.DrawerListAdapter;

import java.util.ArrayList;

import model.NavItem;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mDrawerPane;
    private CharSequence mTitle;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prepareMenu(mNavItems);

        mTitle = getTitle();
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerList = findViewById(R.id.navList);
        //TODO ovo se dobavlja iz baze za ulogovanog korisnika
        TextView usr = (TextView) findViewById(R.id.userName);
        usr.setText("BBF");

        mDrawerPane = findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);

        //senka preko glavnog sadrzaja
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //listener elemenata liste
//        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // definisan adapter za drawer
        mDrawerList.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.cruise_logo);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
//            actionBar.setIcon(R.drawable.ic_launcher);
            actionBar.setHomeAsUpIndicator(R.drawable.outline_menu_24);
            actionBar.setHomeButtonEnabled(true);
        }

        //sta se desava kada se drawer otvori i zatvori
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                toolbar,      /* nav drawer image to replace 'Up' caret */
                R.string.drawer_open,  /* "open drawer" description for accessibility */
                R.string.drawer_close  /* "close drawer" description for accessibility */
        ) {
            public void onDrawerClosed(View view) {
                getSupportActionBar().setTitle(mTitle);
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }

            public void onDrawerOpened(View drawerView) {
                getSupportActionBar().setTitle("iReviewer");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        // Izborom na neki element iz liste, pokrecemo akciju
        if (savedInstanceState == null) {
            selectItemFromDrawer(0);
        }

        button = (Button) findViewById(R.id.mapButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
//                openActivityOnButtonClick();
            }
        });

    }

//    public void openActivityOnButtonClick() {
//        Intent intent = new Intent(MainActivity.this, SplashScreen.class);
//        startActivity(intent);
//    }

    private void prepareMenu(ArrayList<NavItem> mNavItems ) {
        mNavItems.add(new NavItem(getString(R.string.ride_history), R.drawable.outline_history_24));
        mNavItems.add(new NavItem(getString(R.string.wallet), R.drawable.outline_account_balance_wallet_24));
        mNavItems.add(new NavItem(getString(R.string.settings), R.drawable.outline_settings_24));
        mNavItems.add(new NavItem(getString(R.string.about_us), R.drawable.outline_info_24));
        mNavItems.add(new NavItem(getString(R.string.log_out), 0));
    }

    private void selectItemFromDrawer(int position) {
        if(position == 0){
            //FragmentTransition.to(MyFragment.newInstance(), this, false);
        }else if(position == 1){
            //..
        }else if(position == 2){
            //..
        }else if(position == 3){
            //..
        }else if(position == 4){
            //..
        }else if(position == 5){
            //...
        }else{
            Log.e("DRAWER", "Nesto van opsega!");
        }

        mDrawerList.setItemChecked(position, true);
        if(position != 5) // za sve osim za sync
        {
            setTitle(mNavItems.get(position).getmTitle());
        }
        mDrawerLayout.closeDrawer(mDrawerPane);
    }
}
