package com.bbf.cruise;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bbf.cruise.activities.AboutUsActivity;
import com.bbf.cruise.activities.LoginActivity;
import com.bbf.cruise.activities.NearbyCarsActivity;
import com.bbf.cruise.activities.RideHistoryActivity;
import com.bbf.cruise.activities.SettingsActivity;

import com.bbf.cruise.activities.WalletActivity;
import com.bbf.cruise.adapters.DrawerListAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.bbf.cruise.fragments.MapFragment;
import com.bbf.cruise.tools.FragmentTransition;

import java.util.ArrayList;

import model.NavItem;

import static android.app.PendingIntent.getActivity;

public class MainActivity extends AppCompatActivity {

    private DrawerLayout mDrawerLayout;
    private ListView mDrawerList;
    private ActionBarDrawerToggle mDrawerToggle;
    private RelativeLayout mDrawerPane;
    private CharSequence mTitle;
    private ArrayList<NavItem> mNavItems = new ArrayList<NavItem>();
    private Button button;

    SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);

        prepareMenu(mNavItems);

        mTitle = getTitle();
        mDrawerLayout = findViewById(R.id.drawerLayout);
        mDrawerList = findViewById(R.id.navList);
        mDrawerList.setDivider(null);

        //TODO ovo se dobavlja iz baze za ulogovanog korisnika
        TextView usr = (TextView) findViewById(R.id.userName);
        usr.setText(sharedPreferences.getString("firstName", "User"));

        mDrawerPane = findViewById(R.id.drawerPane);
        DrawerListAdapter adapter = new DrawerListAdapter(this, mNavItems);

        //senka preko glavnog sadrzaja
        mDrawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        //listener elemenata liste
        mDrawerList.setOnItemClickListener(new DrawerItemClickListener());
        // definisan adapter za drawer
        mDrawerList.setAdapter(adapter);

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(R.string.cruise_logo);
        setSupportActionBar(toolbar);
        final ActionBar actionBar = getSupportActionBar();

        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
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
                getSupportActionBar().setTitle("Cruise");
                invalidateOptionsMenu(); // creates call to onPrepareOptionsMenu()
            }
        };

        button = (Button) findViewById(R.id.mapButton);
        button.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, NearbyCarsActivity.class);
                startActivity(intent);
            }
        });

        //logout dugme na dnu
        ListView logOutList = (ListView) findViewById(R.id.logoutListView);
        logOutList.setDivider(null);
        ArrayList<NavItem> navItem = new ArrayList<NavItem>();
        navItem.add(new NavItem(getString(R.string.log_out), R.drawable.outline_reply_24));
        DrawerListAdapter LogOutAdapter = new DrawerListAdapter(this, navItem);
        logOutList.setAdapter(LogOutAdapter);

        logOutList.setOnItemClickListener(new LogOutItemClickListener());

        TextView no_of_distance = (TextView) findViewById(R.id.no_of_distance);
        TextView no_of_rides = (TextView) findViewById(R.id.no_of_rides);
        TextView no_of_points = (TextView) findViewById(R.id.no_of_points);

        //TODO ubaciti vrednosti izvucene iz baze za korisnika
        no_of_distance.setText("58");
        no_of_rides.setText("4");
        no_of_points.setText("470");

        //TODO dodati mapu ovde
        FragmentTransition.to(MapFragment.newInstance(), this, false);


    }

    private void prepareMenu(ArrayList<NavItem> mNavItems ) {
        mNavItems.add(new NavItem(getString(R.string.ride_history), R.drawable.outline_history_24));
        mNavItems.add(new NavItem(getString(R.string.wallet), R.drawable.outline_account_balance_wallet_24));
        mNavItems.add(new NavItem(getString(R.string.settings), R.drawable.outline_settings_24));
        mNavItems.add(new NavItem(getString(R.string.about_us), R.drawable.outline_info_24));
    }

    private class DrawerItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            selectItemFromDrawer(position);
        }
    }

    private void selectItemFromDrawer(int position) {
        if(position == 0){
            Intent intent = new Intent(MainActivity.this, RideHistoryActivity.class);
            startActivity(intent);
        }else if(position == 1){
            Intent intent = new Intent(MainActivity.this, WalletActivity.class);
            startActivity(intent);
        }else if(position == 2){
            Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        }else if(position == 3){
            Intent intent = new Intent(MainActivity.this, AboutUsActivity.class);
            startActivity(intent);
        }else{
            Log.e("DRAWER", "Nesto van opsega!");
        }

        mDrawerList.setItemChecked(position, true);
        mDrawerLayout.closeDrawer(mDrawerPane);
    }

    private class LogOutItemClickListener implements ListView.OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if(position == 0){
                //TODO izloguje ga i odvede ga na login

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(MainActivity.this, "Logged out!", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(MainActivity.this, LoginActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }else{
                Log.e("DRAWER", "Nesto van opsega!");
            }

            mDrawerLayout.closeDrawer(mDrawerPane);
        }
    }
}
