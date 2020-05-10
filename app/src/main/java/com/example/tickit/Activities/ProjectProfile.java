package com.example.tickit.Activities;

import androidx.annotation.Nullable;
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
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.Project;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

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
        Toast.makeText(getApplicationContext(), project.getId()+project.getName(), Toast.LENGTH_LONG).show();
        setAllowanceOnViews();
        addEditionButtonPressed();
        backButtonPressed();
        setOnSpinnerAction();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(REQUEST_CODE_ADD_EDITION==requestCode && resultCode==RESULT_OK){
            if(data!=null){
                Edition newEdition = (Edition) data.getParcelableExtra("newAddedEdition");
                addNewEditionToLocalProject(newEdition);
                ProjectDatabaseCalls.saveEdition(project, newEdition,  new CallbackBoolean() {
                    @Override
                    public void callback(Boolean bool) {
                        if (bool) {
                            Toast.makeText(getApplicationContext(), "S-a adaugat cu succes noua editie in baza de date.", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), "Nu s-a reusit adaugarea editiei in baza de date. Verificati si reincercati.", Toast.LENGTH_LONG).show();
                        }
                    }
                });
                addMandates(newEdition);
            }else{
                Toast.makeText(getApplicationContext(), "Nu s-a adaugat o editie noua.",Toast.LENGTH_LONG).show();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        edtionsSpinners.setSelection(0);
    }

    private void addMandates(Edition newEdition) {
        Mandate mandate  = new Mandate(
                (FirebaseFirestore.getInstance()).collection("projects").document(project.getId()),
                "Coordonator",
                newEdition.getStartDate(),
                newEdition.getStopDate(),
                2);
        UserDatabaseCalls.addMandate(newEdition.getCoordinator1().getEmail(), mandate, project.getName());
        if(newEdition.getCoordinator2()!=null){
            UserDatabaseCalls.addMandate(newEdition.getCoordinator2().getEmail(), mandate, project.getName());
        }
    }

    private void addNewEditionToLocalProject( Edition newEdition) {
        ArrayList<Edition> updatedEditions= project.getEditions();
        updatedEditions.add(newEdition);
        project.setEditions(updatedEditions);
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
        if(MainActivity.getUserGrade()>=2){
            addEditionButton.setVisibility(View.GONE);
        }
    }

    private void fillWithInfo(Project project) {
        nameTV.setText(project.getName());
        descriptionTV.setText(project.getDescription());
        ProjectDatabaseCalls.getPhotoUri(project.getImageLink(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                if(value!=null){
                    Glide.with(getApplicationContext()).load(Uri.parse(value)).apply(RequestOptions.fitCenterTransform()).into(logo);
                }else{
                    Glide.with(getApplicationContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerInsideTransform()).into(logo);
                }
            }
        });

        if(project.getEditions()!=null){
            project.getEditions().add(0,new Edition(null,null, null,null, null, null, "Alege",null));
        }else{
            project.setEditions(new ArrayList<Edition>());
            project.getEditions().add(new Edition(null,null, null,null, null, null, "Alege",null));
        }
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
                        startActivity(new Intent(getApplicationContext(), EditionProfile.class)
                                .putExtra("editionFromProjectProfile", project.getEditions().get(position))
                                .putExtra("projectNameFromProjectProfile", project.getName())
                                .putExtra("projectIdFromProjectProfile", project.getId()));
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
