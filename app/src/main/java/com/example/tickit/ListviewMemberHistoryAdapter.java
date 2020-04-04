package com.example.tickit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import org.w3c.dom.Text;

import java.util.ArrayList;

public class ListviewMemberHistoryAdapter extends ArrayAdapter<Mandate> {
    int resourceID;

    public ListviewMemberHistoryAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Mandate> objects) {
        super(context, resource, objects);
        resourceID=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Mandate mandate = getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(resourceID, null);

        ((TextView) view.findViewById(R.id.positionTextView)).setText(mandate.getPosition());
        ((TextView) view.findViewById(R.id.projectNameTextView)).setText("Nume proiect");
        ((TextView)view.findViewById(R.id.editionTextView)).setText(mandate.getEndDate().substring(mandate.getEndDate().length()-4,mandate.getEndDate().length()));

        return view;
    }
}
