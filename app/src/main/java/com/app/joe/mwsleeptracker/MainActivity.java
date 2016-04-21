package com.app.joe.mwsleeptracker;

import android.app.ProgressDialog;
import android.content.*;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothManager;
import android.widget.Button;
import android.widget.Toast;

import com.mbientlab.bletoolbox.scanner.BleScannerFragment;
import com.mbientlab.bletoolbox.scanner.BleScannerFragment.*;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;


public class MainActivity extends AppCompatActivity implements ServiceConnection, NavigationView.OnNavigationItemSelectedListener  {
    private static final int PREFERENCE_MODE_PRIVATE = 0;
    private static final int NO_DEVICE_SELECTED = 99;
    private static final int DEVICE_DISCONNECTED = 0;
    private static final int DEVICE_CONNECTED = 1;

    private MetaWearBleService.LocalBinder serviceBinder;
    private final String MW_MAC_ADDRESS= "D2:70:1B:23:EA:9E";
    private String deviceMACAddress = "";
    private MetaWearBoard mwBoard;
    private ProgressDialog connectDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Setup navagtion drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        Button btnConnect = (Button)findViewById(R.id.btnConnect);
        btnConnect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                connectMWBoard();
            }
        });

        PrefManager.Init(this);
        deviceMACAddress = PrefManager.readMACAddress();

        if (deviceMACAddress == ""){
            updateStatusFragment(NO_DEVICE_SELECTED);
        }
        else{
            // Bind the service when the activity is created
            getApplicationContext().bindService(new Intent(this, MetaWearBleService.class),
                    this, Context.BIND_AUTO_CREATE);

            updateStatusFragment(0);
        }
    }

    private void connectMWBoard(){
        //Open the connection dialog
        connectDialog = new ProgressDialog(MainActivity.this);
        connectDialog.setTitle(getString(R.string.title_connecting));
        connectDialog.setMessage(getString(R.string.message_wait));
        connectDialog.setCancelable(false);
        connectDialog.setCanceledOnTouchOutside(false);
        connectDialog.setIndeterminate(true);
        connectDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getString(R.string.label_cancel), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                mwBoard.disconnect();
            }
        });
        connectDialog.show();

        //Connect to the MetaWear board
        mwBoard.connect();
    }

    private void updateStatusFragment(final int status){
        //Update the status fragment with the current connection state

                FragmentManager fm = getSupportFragmentManager();
                MWStatusFragment fragment = (MWStatusFragment) fm.findFragmentById(R.id.mwStatusFragment);
                fragment.setStatus(status);

    }

    private void updateInfoFragment(){
        FragmentManager fm = getSupportFragmentManager();
        MWInfoFragment fragment = (MWInfoFragment) fm.findFragmentByTag("MWINFOFRAGMENT");
        fragment.updateDeviceInfo(mwBoard);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);/**/
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;

        int id = item.getItemId();

        if (id == R.id.nav_view_history) {

        } else if (id == R.id.nav_settings) {
            intent = new Intent(MainActivity.this, SettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    public void onServiceConnected(ComponentName name, IBinder service) {
        // Typecast the binder to the service's LocalBinder class
        serviceBinder = (MetaWearBleService.LocalBinder) service;

        retrieveBoard();

        mwBoard.setConnectionStateHandler(new MetaWearBoard.ConnectionStateHandler() {
            @Override
            public void connected() {
                //Close the connect dialog
                connectDialog.dismiss();

                //Update the status fragment
                updateStatusFragment(1);

                //Load the information fragment
                FragmentManager fm = getSupportFragmentManager();
                FragmentTransaction ft = fm.beginTransaction();
                MWInfoFragment mwInfoFragment = new MWInfoFragment();
                ft.add(R.id.infofragment_container, mwInfoFragment, "MWINFOFRAGMENT");
                ft.commit();

                updateInfoFragment();
            }

            @Override
            public void disconnected() {
                //Update the status fragment
                updateStatusFragment(0);
                mwBoard.connect();
            }

            @Override
            public void failure(int status, Throwable error) {
                //Update the status fragment
                updateStatusFragment(0);
                mwBoard.connect();
            }
        });
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {
        //Load the information fragment
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        MWInfoFragment mwInfoFragment = new MWInfoFragment();
        ft.remove(mwInfoFragment);
        ft.commit();
    }

    public void retrieveBoard() {
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice=
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard= serviceBinder.getMetaWearBoard(remoteDevice);
    }
}
