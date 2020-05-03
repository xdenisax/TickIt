package com.example.tickit.Adapters;

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
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.R;
import com.example.tickit.Classes.User;

import java.util.ArrayList;

public class ListViewAssumedTaskSituationAdapter extends   ArrayAdapter<AssumedTasksSituation> {
    private int resourceID;
    private ImageView profilImageView;
    private TextView numeTextView;
    public ListViewAssumedTaskSituationAdapter(@NonNull Context context, int resource, @NonNull ArrayList<AssumedTasksSituation> assumedTasksSituationArrayAdapter) {
        super(context, resource, assumedTasksSituationArrayAdapter);
        this.resourceID = resource;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        User user = getItem(position).getUser();
        View view = LayoutInflater.from(getContext()).inflate(resourceID,null);
        assignViews(view);

        if(user.getLastName()!=null){
            Glide.with(getContext()).load(user.getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(profilImageView);
            numeTextView.setText(user.getLastName() + " " + user.getFirstName());
        }else{
            Glide.with(getContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerCropTransform()).into(profilImageView);
            numeTextView.setText(user.getEmail());
        }

        return view;
    }

    private void assignViews(View view) {
        profilImageView= (ImageView) view.findViewById(R.id.userPicture);
        numeTextView =(TextView) view.findViewById(R.id.numeTextView);
    }
}
