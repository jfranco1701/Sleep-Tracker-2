package com.app.joe.mwsleeptracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;


/**
 * MWStatus Fragment
 *
 * This fragment is used to display the current status of the MetaWear board
 * If a board has not been selected in the settings activity, a message will be displayed
 * that indicates that they must do so.
 */
public class MWStatusFragment extends Fragment {
    public MWStatusFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mwstatus, container, false);
    }

    public void updateStatusInfo(final MetaWearBoard mwBoard, final String deviceMACAddress){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {

                TextView tvSelectedDevice = (TextView) getView().findViewById(R.id.tvSelectedDevice);

                //Display the currently selected board MAC address.  If one is not selected
                //show a message indicating they must do so.
                if (tvSelectedDevice != null) {
                    if (deviceMACAddress.equals("")) {
                        tvSelectedDevice.setText(R.string.no_device_selected);
                    } else {
                        tvSelectedDevice.setText(deviceMACAddress);
                    }
                }

                //Display connection status.
                TextView tvBoardStatus = (TextView) getView().findViewById(R.id.tvBoardStatus);
                if (tvBoardStatus != null) {
                    if (mwBoard == null){
                        tvBoardStatus.setText(R.string.device_status_disconnected);
                    }
                    else{
                        //Display the current status of the board
                        if (mwBoard.isConnected()) {
                            tvBoardStatus.setText(R.string.device_status_connected);
                        } else {
                            tvBoardStatus.setText(R.string.device_status_disconnected);
                        }
                    }
                }
            }
        });
    }
}
