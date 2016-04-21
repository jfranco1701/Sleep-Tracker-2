package com.app.joe.mwsleeptracker;

import android.content.Context;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


///**
// * A simple {@link Fragment} subclass.
// * Activities that contain this fragment must implement the
// * {@link MWStatusFragment.OnFragmentInteractionListener} interface
// * to handle interaction events.
// * Use the {@link MWStatusFragment#newInstance} factory method to
// * create an instance of this fragment.
// */
public class MWStatusFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_mwstatus, container, false);
    }

    public void setStatus(final int status){
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                TextView tvStatus = (TextView) getView().findViewById(R.id.tvStatusText);

                switch (status){
                    case 0:
                        tvStatus.setText("Disconnected");
                        tvStatus.setTextColor(Color.RED);
                        break;
                    case 1:
                        tvStatus.setText("Connected");
                        tvStatus.setTextColor(Color.GREEN);
                        break;
                    case 99:
                        tvStatus.setText("No Device Selected");
                        tvStatus.setTextColor(Color.RED);
                        break;
                    default:
                        tvStatus.setText("Error");
                        tvStatus.setTextColor(Color.RED);
                        break;
                }
            }
        });
    }
}
