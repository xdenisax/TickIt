package com.example.tickit;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;
import java.util.List;

class ListViewAssumedTaskSituationAdapter extends   ArrayAdapter<AssumedTasksSituation> {
    int resourceID;
    public ListViewAssumedTaskSituationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AssumedTasksSituation> assumedTasksSituationArrayAdapter) {
        super(context, resource, assumedTasksSituationArrayAdapter);
        this.resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position).getUser();
        LayoutInflater inflater = LayoutInflater.from(getContext());
        View view = inflater.inflate(resourceID,null);

        ImageView profilImageView = (ImageView) view.findViewById(R.id.userPicture);
        TextView numeTextView =(TextView) view.findViewById(R.id.numeTextView);


        if(user.getLastName()!=null || user.getFirstName()!= null){
            Glide.with(getContext()).load(user.getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(profilImageView);
            numeTextView.setText(user.getLastName() + " " + user.getFirstName());
        }else{
            Glide.with(getContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerCropTransform()).into(profilImageView);
            numeTextView.setText(user.getEmail());
        }

        return view;
    }
}
