package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class ProjectProfile extends AppCompatActivity {

    Project project;
    TextView nameTV;
    TextView descriptionTV;
    ImageView logo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_profile);
        manageIntent(getIntent());

    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("projectFromProjectsList")!= null) {
            project = (Project) intent.getParcelableExtra("projectFromProjectsList");
            fillWithInfo(project);
        }
    }

    private void fillWithInfo(Project project) {
        nameTV= (TextView) findViewById(R.id.projectNameTextView);
        logo = (ImageView) findViewById(R.id.projectLogoImageView);
        descriptionTV = (TextView) findViewById(R.id.projectDescriptionTextView);

        nameTV.setText(project.getName());
        descriptionTV.setText(project.getDescription());
        if(project.getImageLink()!=null){
            Glide.with(getApplicationContext()).load(Uri.parse(project.getImageLink())).apply(RequestOptions.fitCenterTransform()).into(logo);
        }else{
            Glide.with(getApplicationContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerInsideTransform()).into(logo);
        }
    }


}
