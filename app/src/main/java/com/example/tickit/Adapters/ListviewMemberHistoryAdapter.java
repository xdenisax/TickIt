package com.example.tickit.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;

import java.util.ArrayList;

public class ListviewMemberHistoryAdapter extends ArrayAdapter<Mandate> {
    int resourceID;
    View view;

    public ListviewMemberHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Mandate> objects) {
        super(context, resource, objects);
        resourceID=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Mandate mandate = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        view= inflater.inflate(resourceID, null);

        ((TextView) view.findViewById(R.id.positionTextView)).setText(mandate.getPosition());
        String endDate = DateProcessing.dateFormat.format(mandate.getStop_date());
        ((TextView)view.findViewById(R.id.editionTextView)).setText(endDate.substring(endDate.length()-4,endDate.length()));

        ProjectDatabaseCalls.getProjectName(mandate.getProject_name(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                ((TextView) view.findViewById(R.id.projectNameTextView)).setText(value);
                    Toast.makeText(getContext(), value, Toast.LENGTH_LONG).show();
                }

        });
        return view;
    }


}
