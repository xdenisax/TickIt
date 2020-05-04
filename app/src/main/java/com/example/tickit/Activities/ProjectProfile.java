package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.QuickContactBadge;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Adapters.SpinnerYearAdapter;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Project;
import com.example.tickit.R;

public class ProjectProfile extends AppCompatActivity {

    Project project;
    TextView nameTV;
    TextView descriptionTV;
    ImageView logo;
    Spinner edtionsSpinners;
    ImageButton backButton;
    Button addEditionButton;
    int REQUEST_CODE_ADD_EDITION = 20;
    int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_profile);

        assignViews();
        manageIntent(getIntent());
        setAllowanceOnViews();
        addEditionButtonPressed();
        backButtonPressed();
        setOnSpinnerAction();
    }

    private void assignViews() {
        nameTV= (TextView) findViewById(R.id.projectNameTextView);
        logo = (ImageView) findViewById(R.id.projectLogoImageView);
        edtionsSpinners = (Spinner) findViewById(R.id.spinnerEditions);
        descriptionTV = (TextView) findViewById(R.id.projectDescriptionTextView);
        backButton = (ImageButton) findViewById(R.id.backButtonProfileActivity);
        addEditionButton = (Button) findViewById(R.id.addNewEditionButton);
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("projectFromProjectsList")!= null) {
            project = (Project) intent.getParcelableExtra("projectFromProjectsList");
            fillWithInfo(project);
        }
    }

    private void setAllowanceOnViews() {
        if(MainActivity.getUserGrade()>2){
            addEditionButton.setVisibility(View.GONE);
        }
    }

    private void fillWithInfo(Project project) {
        nameTV.setText(project.getName());
        descriptionTV.setText(project.getDescription());
        if(project.getImageLink()!=null){
            Glide.with(getApplicationContext()).load(Uri.parse(project.getImageLink())).apply(RequestOptions.fitCenterTransform()).into(logo);
        }else{
            Glide.with(getApplicationContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerInsideTransform()).into(logo);
        }
        project.getEditions().add(0,new Edition(null,null, null,null, null, null, "Alege"));
        edtionsSpinners.setAdapter(new SpinnerYearAdapter(getApplicationContext(),project.getEditions()));
    }

    private void setOnSpinnerAction() {
        final int currentSelection = edtionsSpinners.getSelectedItemPosition();
        edtionsSpinners.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if(position!=0) {
                    if (project.getEditions().get(position) == null) {
                        Toast.makeText(getApplicationContext(), "Nu exista informatii despre acest proiect momentan.", Toast.LENGTH_LONG).show();
                    } else {
                        startActivity(new Intent(getApplicationContext(), EditionProfile.class).putExtra("editionFromProjectProfile", project.getEditions().get(position)));
                    }
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    private void addEditionButtonPressed() {
        addEditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddEdition.class), REQUEST_CODE_ADD_EDITION);
            }
        });
    }

    private void backButtonPressed() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
