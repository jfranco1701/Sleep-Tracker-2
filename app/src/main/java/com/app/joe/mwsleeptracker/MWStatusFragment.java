package com.app.joe.mwsleeptracker;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mbientlab.metawear.MetaWearBoard;


/**
 * A simple {@link Fragment} subclass.
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
                //Display the currently selected board MAC address
                TextView tvSelectedDevice = (TextView) getView().findViewById(R.id.tvSelectedDevice);

                if (tvSelectedDevice != null) {
                    if (deviceMACAddress.equals("")) {
                        tvSelectedDevice.setText(R.string.no_device_selected);
                    } else {
                        tvSelectedDevice.setText(deviceMACAddress);
                    }
                }

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
