package com.example.tickit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackString;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;

import java.text.SimpleDateFormat;

public class TaskProfile extends AppCompatActivity {

    ImageButton backButton;
    ProjectTask task;
    TextView taskNameTextView, projectNameTextView,startDateTextView, deadlineTextView, descriptionTextView, noMemberAssumedYetTextView;
    Button resourcesButton, assumptionButton;
    ListView membersWhoAssumedListView;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_profile);
        taskNameTextView = (TextView) findViewById(R.id.taskNameTextView);
        projectNameTextView= (TextView) findViewById(R.id.projectNameTextViewTaskProfile);
        startDateTextView= (TextView) findViewById(R.id.taskStartDateTextView);
        deadlineTextView= (TextView) findViewById(R.id.taskStopDateTextView);
        descriptionTextView= (TextView) findViewById(R.id.taskDescriptionTextView);
        noMemberAssumedYetTextView = (TextView) findViewById(R.id.noMemberAssumedYet);
        noMemberAssumedYetTextView.setVisibility(View.GONE);
        resourcesButton = (Button) findViewById(R.id.ResourcesButton);
        assumptionButton =(Button) findViewById(R.id.assumptionButton);

        backButtonPressed();
        setActionOnResourcesButton();
        manageIntent(getIntent());
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("openTaskFromOpenTasks")!= null) {
            task = (ProjectTask) intent.getParcelableExtra("openTaskFromOpenTasks");
            Toast.makeText(getApplicationContext(), task.toString(), Toast.LENGTH_LONG).show();
            fillWithInfo();
        }
    }

    private void fillWithInfo() {
        getProjectName(task.getProject(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                projectNameTextView.setText(value);
                taskNameTextView.setText(task.getTaskName());
                startDateTextView.setText(dateFormat.format(task.getStartDate()));
                deadlineTextView.setText(dateFormat.format(task.getStopDate()));
                descriptionTextView.setText(task.getTaskDescription());
                if(task.getMembersWhoAssumed()==null){
                    noMemberAssumedYetTextView.setVisibility(View.VISIBLE);
                }
            }
        });

    }

    private void getProjectName(DocumentReference docRef, final CallbackString callback) {
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String value = documentSnapshot.getString("name");
                    callback.onCallBack(value);
                } else {
                    Log.d("checkRef", "No such document");
                }
            }
        });
    }

    private void setActionOnResourcesButton() {
        resourcesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try{
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(task.getTaskResource())));
                }catch (ActivityNotFoundException error){
                    Toast.makeText(getApplicationContext(), "Nu am putut deschide link-ul resursei. Contacteaza liderul diviziei.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void backButtonPressed() {
        backButton = (ImageButton) findViewById(R.id.backButtonTaskProfile);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }
}
