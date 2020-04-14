package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentContainer;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.app.Fragment;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
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
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_profile);

        manageIntent(getIntent());
        backButtonPressed();
        setOnSpinnerAction();
    }

    private void setOnSpinnerAction() {
        edtionsSpinners = (Spinner) findViewById(R.id.spinnerEditions);
        final int currentSelection = edtionsSpinners.getSelectedItemPosition();
        edtionsSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(project.getEditions().get(position)==null){
                        Toast.makeText(getApplicationContext(), "Nu exista informatii despre acest proiect momentan.", Toast.LENGTH_LONG).show();
                    }else{
                        startActivity(new Intent(getApplicationContext(), EditionProfile.class).putExtra("editionFromProjectProfile",project.getEditions().get(position)));
                    }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) { }
        });
    }

    private void backButtonPressed() {
        backButton = (ImageButton) findViewById(R.id.backButtonProfileActivity);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FragmentsContainer.class));
            }
        });
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
        edtionsSpinners.setAdapter(new SpinnerYearAdapter(getApplicationContext(),project.getEditions()));
    }
}
