package com.app.joe.mwsleeptracker;

import android.app.ProgressDialog;
import android.content.*;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.IBinder;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
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
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.mbientlab.metawear.AsyncOperation;
import com.mbientlab.metawear.Message;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.RouteManager;
import com.mbientlab.metawear.UnsupportedModuleException;
import com.mbientlab.metawear.data.CartesianFloat;
import com.mbientlab.metawear.module.Accelerometer;
import com.mbientlab.metawear.module.Bma255Accelerometer;
import com.mbientlab.metawear.module.Bmi160Accelerometer;
import com.mbientlab.metawear.module.Mma8452qAccelerometer;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;


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

    private boolean isAllowDisconnect = false;

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

        if (navigationView != null) {
            navigationView.setNavigationItemSelectedListener(this);
        }

        Switch switchConnection = (Switch) findViewById(R.id.switchConnection);

        if (switchConnection != null) {
            switchConnection.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        connectMWBoard();

                    } else {
                        disconnectMWBoard();
                    }
                }
            });
        }

        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class), this, BIND_AUTO_CREATE);

        PrefManager.Init(this);
        deviceMACAddress = PrefManager.readMACAddress();

        updateStatusFragment();

        DBHandler dbhandler = new DBHandler(MainActivity.this);
        int test = dbhandler.getRecordCount();

        Toast.makeText(MainActivity.this, String.valueOf(test), Toast.LENGTH_LONG).show();
    }

    private void updateStatusFragment(){
        FragmentManager fm = getSupportFragmentManager();
        MWStatusFragment fragment = (MWStatusFragment) fm.findFragmentById(R.id.status_fragment);
        fragment.updateStatusInfo(mwBoard, deviceMACAddress);
    }


    private void updateInfoFragment(){
        FragmentManager fm = getSupportFragmentManager();
        MWInfoFragment fragment = (MWInfoFragment) fm.findFragmentByTag("MWINFOFRAGMENT");
        fragment.updateDeviceInfo(mwBoard);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);

        if(drawer != null){
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                super.onBackPressed();
            }
        }
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        Intent intent;

        int id = item.getItemId();

        if (id == R.id.nav_view_history) {
            intent = new Intent(MainActivity.this, SleepLogActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_settings) {
            intent = new Intent(MainActivity.this, AppSettingsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_about) {
            intent = new Intent(MainActivity.this, AboutActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer != null) {
            drawer.closeDrawer(GravityCompat.START);
        }

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

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        setConnectionSwitch(true);
                    }
                });

//                //Load the information fragment
//                FragmentManager fm = getSupportFragmentManager();
//                FragmentTransaction ft = fm.beginTransaction();
//                MWInfoFragment mwInfoFragment = new MWInfoFragment();
//                ft.add(R.id.infofragment_container, mwInfoFragment, "MWINFOFRAGMENT");
//                ft.commit();


                updateStatusFragment();
//                updateInfoFragment();

                Log.i("MainActivity", "Connected");

                try {
                    startAccelerometer();
                } catch (UnsupportedModuleException e) {
                    unsupportedModule();
                }
            }

            @Override
            public void disconnected() {
                if (connectDialog.isShowing()) {
                    connectDialog.dismiss();
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        setConnectionSwitch(false);
                    }
                });

                updateStatusFragment();
            }

            @Override
            public void failure(int status, Throwable error) {
                if (connectDialog.isShowing()) {
                    connectDialog.dismiss();
                }

                runOnUiThread(new Runnable(){
                    @Override
                    public void run(){
                        setConnectionSwitch(false);
                    }
                });

                updateStatusFragment();

                mwBoard.connect();
            }
        });
    }

    private void setConnectionSwitch(boolean isChecked){
        Switch switchConnection = (Switch) findViewById(R.id.switchConnection);

        if(switchConnection != null) {
            switchConnection.setChecked(isChecked);
        }
    }

    private void unsupportedModule() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);

        alertDialogBuilder.setTitle(R.string.title_error);
        alertDialogBuilder
                .setMessage("Unsupported Module")
                .setCancelable(false)
                .create()
                .show();
    }

    ///<  Taken from: http://stackoverflow.com/a/20742032/4872841
    protected void enableDisableViewGroup(ViewGroup viewGroup, boolean enabled) {
        int childCount = viewGroup.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View view = viewGroup.getChildAt(i);
            view.setEnabled(enabled);
            if (view instanceof ViewGroup) {
                enableDisableViewGroup((ViewGroup) view, enabled);
            }
        }
    }

    private void connectMWBoard(){
        //
        isAllowDisconnect = false;

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

    private void disconnectMWBoard(){
        isAllowDisconnect = true;

//        mwBoard.setConnectionStateHandler(null);
        mwBoard.disconnect();

        //Remove the information fragment
        Fragment fragment = getSupportFragmentManager().findFragmentByTag("MWINFOFRAGMENT");
        if(fragment != null){
            getSupportFragmentManager().beginTransaction().remove(fragment).commit();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public void retrieveBoard() {
        final BluetoothManager btManager=
                (BluetoothManager) getSystemService(Context.BLUETOOTH_SERVICE);
        final BluetoothDevice remoteDevice=
                btManager.getAdapter().getRemoteDevice(MW_MAC_ADDRESS);

        // Create a MetaWear board object for the Bluetooth Device
        mwBoard= serviceBinder.getMetaWearBoard(remoteDevice);
    }

    private void startAccelerometer() throws UnsupportedModuleException {
        Bmi160Accelerometer bmi160AccModule= mwBoard.getModule(Bmi160Accelerometer.class);
        bmi160AccModule.setOutputDataRate(2.f);
        bmi160AccModule.setAxisSamplingRange(3.0f);

// Route data from the chip's motion detector
        bmi160AccModule.routeData().fromAxes().stream("motion").commit()
                .onComplete(new AsyncOperation.CompletionHandler<RouteManager>() {
                    @Override
                    public void success(RouteManager result) {
                        result.subscribe("motion", new RouteManager.MessageHandler() {
                            @Override
                            public void process(Message msg) {
                                SleepEntry sleepEntry = new SleepEntry();

                                Calendar calendar = msg.getTimestamp();
                                sleepEntry.setLogDateTime(calendar.getTimeInMillis());



                                sleepEntry.setSleepState(10);

                                DBHandler dbhandler = new DBHandler(MainActivity.this);
                                dbhandler.addSleepHistory(sleepEntry);

                                Log.i("MainActivity", msg.getData(CartesianFloat.class).toString());
                            }
                        });
                    }
                });

        bmi160AccModule.enableAxisSampling();

        // Switch the accelerometer to active mode
        bmi160AccModule.start();
    }
}
