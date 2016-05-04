package com.app.joe.mwsleeptracker;

import android.app.Activity;
import android.bluetooth.BluetoothDevice;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.app.joe.mwsleeptracker.help.HelpOptionAdapter;
import com.mbientlab.metawear.MetaWearBleService;
import com.mbientlab.metawear.MetaWearBoard;
import com.mbientlab.metawear.UnsupportedModuleException;

import java.io.InputStream;
import java.util.Locale;

/**
 * Class ModuleFragmentBase
 *
 * Taken from MetaWear sample application
 * Class used for MetaWear board operations
 *
 */

public abstract class ModuleFragmentBase extends Fragment implements ServiceConnection {
    public interface FragmentBus {
        BluetoothDevice getBtDevice();
        void resetConnectionStateHandler(long delay);
        void initiateDfu(InputStream path);
    }

    private boolean boardReady= false;
    protected MetaWearBoard mwBoard;
    protected FragmentBus fragBus;
    protected int sensorResId;

    protected abstract void boardReady() throws UnsupportedModuleException;
    protected abstract void fillHelpOptionAdapter(HelpOptionAdapter adapter);

    protected void showHelpDialog() {
        HelpOptionAdapter adapter= new HelpOptionAdapter(getContext(), R.id.config_help_list);
        fillHelpOptionAdapter(adapter);

        if (adapter.getCount() != 0) {
            AlertDialog dialog = new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.label_ok, null)
                    .setView(R.layout.layout_config_help)
                    .create();
            dialog.show();
            ((ListView) dialog.findViewById(R.id.config_help_list)).setAdapter(adapter);

            adapter.notifyDataSetChanged();
        } else {
            new AlertDialog.Builder(getActivity())
                    .setPositiveButton(R.string.label_ok, null)
                    .setMessage(R.string.message_no_config)
                    .create().show();
        }
    }

    public ModuleFragmentBase(int sensorResId) {
        this.sensorResId= sensorResId;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Activity owner= getActivity();
        if (!(owner instanceof FragmentBus)) {
            throw new ClassCastException(String.format(Locale.US, "%s %s", owner.toString(),
                    owner.getString(R.string.error_fragment_bus)));
        }

        fragBus= (FragmentBus) owner;
        owner.getApplicationContext().bindService(new Intent(owner, MetaWearBleService.class), this, Context.BIND_AUTO_CREATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        ///< Unbind the service when the activity is destroyed
        getActivity().getApplicationContext().unbindService(this);
    }

    @Override
    public void onViewCreated(final View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        if (boardReady) {
            try {
                boardReady();
            } catch (UnsupportedModuleException e) {
                unsupportedModule();
            }
        }
    }

    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
        mwBoard= ((MetaWearBleService.LocalBinder) iBinder).getMetaWearBoard(fragBus.getBtDevice());
        try {
            boardReady= true;
            boardReady();
        } catch (UnsupportedModuleException e) {
            unsupportedModule();
        }
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }

    public void reconnected() { }

    private void unsupportedModule() {
        new AlertDialog.Builder(getActivity()).setTitle(R.string.title_error)
                .setMessage(String.format("%s %s", getContext().getString(sensorResId), getActivity().getString(R.string.error_unsupported_module)))
                .setCancelable(false)
                .setPositiveButton(R.string.label_ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        enableDisableViewGroup((ViewGroup) getView(), false);
                    }
                })
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
}
