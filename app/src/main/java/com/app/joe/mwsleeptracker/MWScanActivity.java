package com.app.joe.mwsleeptracker;

        import android.app.Activity;
        import android.app.ProgressDialog;
        import android.bluetooth.BluetoothDevice;
        import android.content.ComponentName;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.content.ServiceConnection;
        import android.os.IBinder;
        import android.support.v7.app.AlertDialog;
        import android.support.v7.app.AppCompatActivity;
        import android.os.Bundle;

        import com.mbientlab.bletoolbox.scanner.BleScannerFragment;
        import com.mbientlab.bletoolbox.scanner.BleScannerFragment.*;
        import com.mbientlab.metawear.MetaWearBleService;
        import com.mbientlab.metawear.MetaWearBoard;

        import java.util.UUID;


public class MWScanActivity extends AppCompatActivity implements ScannerCommunicationBus, ServiceConnection {
    private final static UUID[] serviceUuids;
    public static final int REQUEST_START_APP= 1;

    static {
        serviceUuids= new UUID[] {
                MetaWearBoard.METAWEAR_SERVICE_UUID,
                MetaWearBoard.METABOOT_SERVICE_UUID
        };
    }

    private MetaWearBleService.LocalBinder serviceBinder;
    private MetaWearBoard mwBoard;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_mwscan);

        getApplicationContext().bindService(new Intent(this, MetaWearBleService.class), this, BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getApplicationContext().unbindService(this);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch(requestCode) {
            case REQUEST_START_APP:
                ((BleScannerFragment) getFragmentManager().findFragmentById(R.id.scanner_fragment)).startBleScan();
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onDeviceSelected(final BluetoothDevice btDevice) {
        mwBoard= serviceBinder.getMetaWearBoard(btDevice);
        String strMAC = mwBoard.getMacAddress();

        mwBoard.setConnectionStateHandler(null);
        mwBoard.disconnect();

        Intent intent = new Intent();
        intent.putExtra("MAC", strMAC);
        setResult(Activity.RESULT_OK, intent);
        finish();
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        serviceBinder = (MetaWearBleService.LocalBinder) iBinder;
        serviceBinder.executeOnUiThread();
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    @Override
    public UUID[] getFilterServiceUuids() {
        return serviceUuids;
    }

    @Override
    public long getScanDuration() {
        return 10000L;
    }


}

