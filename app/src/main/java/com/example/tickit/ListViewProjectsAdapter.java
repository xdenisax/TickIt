package com.example.tickit;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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

        final ImageView profilImageView = (ImageView) view.findViewById(R.id.userPicture);
        TextView numeTextView =(TextView) view.findViewById(R.id.numeTextView);

        if(project.getImageLink()!=null){
            getPhotoUri(project.getImageLink(), new CallbackString() {
                @Override
                public void onCallBack(String value) {
                    Uri photoUri = Uri.parse(value);
                    Glide.with(getContext()).load(photoUri).apply(RequestOptions.circleCropTransform().centerInside()).into(profilImageView);
                }
            });
        }

        numeTextView.setText(project.getName() );

        return view;
    }


    private void getPhotoUri(String imageLink, final CallbackString callbackString){
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mImageStorage.child("TickIt").child("ProjectsLogo").child(imageLink);

        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    String imageUrl = downUri.toString();
                    callbackString.onCallBack(imageUrl);
                }else{
                    Toast.makeText(getContext(), "Nu s-a putut obtine imaginea proiectului.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}
