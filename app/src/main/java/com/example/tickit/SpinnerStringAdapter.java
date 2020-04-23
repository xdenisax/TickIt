package com.example.tickit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class SpinnerStringAdapter extends ArrayAdapter<String> {

    public SpinnerStringAdapter(Context context, String[] strings) {
        super(context,0, strings);
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        return initView(position,convertView,parent);
    }

    private View initView(int position, View convertView, ViewGroup parent ) {
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.spinner_adapter_layout, parent, false);
        }

        TextView yearTextView = convertView.findViewById(R.id.yearTextView);
        String string = (String) getItem(position);
        yearTextView.setText(string);

        return convertView;
    }
}
