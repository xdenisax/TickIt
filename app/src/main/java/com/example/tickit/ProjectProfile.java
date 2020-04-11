package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ProjectProfile extends AppCompatActivity {

    Project project;
    TextView nameTV;
    TextView descriptionTV;
    ImageView logo;
    Spinner edtionsSpinners;
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
        edtionsSpinners = (Spinner) findViewById(R.id.spinnerEditions);
        descriptionTV = (TextView) findViewById(R.id.projectDescriptionTextView);

        nameTV.setText(project.getName());
        descriptionTV.setText(project.getDescription());
        if(project.getImageLink()!=null){
            Glide.with(getApplicationContext()).load(Uri.parse(project.getImageLink())).apply(RequestOptions.fitCenterTransform()).into(logo);
        }else{
            Glide.with(getApplicationContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerInsideTransform()).into(logo);
        }
        ArrayList<String> years = getEditionsYears(project.getEditions());
        edtionsSpinners.setAdapter(new ArrayAdapter<String>(getApplicationContext(),R.layout.support_simple_spinner_dropdown_item,years ));
    }

    private ArrayList<String> getEditionsYears(ArrayList<Edition> editions) {
        ArrayList<String> years = new ArrayList<>();
        for (Edition edition: editions) {
            years.add(edition.getYear());
        }
        return years;
    }


}
