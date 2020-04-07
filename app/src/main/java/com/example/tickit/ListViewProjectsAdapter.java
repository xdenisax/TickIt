package com.example.tickit;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class ListViewProjectsAdapter extends ArrayAdapter<Project> {

    int resourceID;

    public ListViewProjectsAdapter(@NonNull Context context, int resource, @NonNull ArrayList<Project> objects) {
        super(context, resource, objects);
        this.resourceID = resource;
    }


    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        Project project= getItem(position);
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(resourceID,null);

        TextView numeTextView =(TextView) view.findViewById(R.id.numeTextView);

//        Glide.with(getContext()).load(pro.getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(profilImageView);
        numeTextView.setText(project.getName() );

        return view;
    }
}
