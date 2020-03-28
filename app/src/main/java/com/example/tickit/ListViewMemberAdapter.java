package com.example.tickit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class ListViewMemberAdapter extends ArrayAdapter<User> {
    int resourceID;
    public ListViewMemberAdapter(Context context, int resource, @NonNull ArrayList<User> objects) {
        super(context, resource, objects);
        this.resourceID=resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
       User user = getItem(position);
       LayoutInflater inflater = LayoutInflater.from(getContext());
       View view = inflater.inflate(resourceID,null);

        ImageView profilImageView = (ImageView) view.findViewById(R.id.userPicture);
        TextView numeTextView =(TextView) view.findViewById(R.id.numeTextView);

        Glide.with(getContext()).load(user.getProfilePicture()).into(profilImageView);
        numeTextView.setText(user.getLastName() + " " + user.getFirstName());

        return view;
    }
}
