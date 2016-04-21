package com.app.joe.mwsleeptracker.help;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.app.joe.mwsleeptracker.R;
import com.app.joe.mwsleeptracker.help.HelpOption;

public class HelpOptionAdapter extends ArrayAdapter<HelpOption> {
    public HelpOptionAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, final ViewGroup parent) {
        final ViewHolder viewHolder;

        if (convertView == null) {
            convertView= LayoutInflater.from(getContext()).inflate(R.layout.layout_config_help_entry, parent, false);

            viewHolder= new ViewHolder();
            viewHolder.optionName= (TextView) convertView.findViewById(R.id.config_option_name);
            viewHolder.optionDescription= (TextView) convertView.findViewById(R.id.config_option_description);

            convertView.setTag(viewHolder);
        } else {
            viewHolder= (ViewHolder) convertView.getTag();
        }

        final HelpOption current = getItem(position);
        viewHolder.optionDescription.setText(current.description);
        viewHolder.optionName.setText(current.name);

        return convertView;
    }

    private class ViewHolder {
        public TextView optionName, optionDescription;

    }
}
