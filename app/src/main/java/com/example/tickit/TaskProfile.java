package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TaskProfile extends AppCompatActivity {
    ImageButton backButton;
    ProjectTask task;
    TextView taskNameTextView, projectNameTextView,startDateTextView, deadlineTextView, descriptionTextView, noMemberAssumedYetTextView;
    Button resourcesButton, assumptionButton, editButton, deleteButton;
    ListView membersWhoAssumedListView;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
    int REQUEST_CODE_EDIT_TASK =5;

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
        editButton =(Button) findViewById(R.id.editTaskButton);
        deleteButton =(Button) findViewById(R.id.deleteTaskButton);
        membersWhoAssumedListView = (ListView) findViewById(R.id.assumedTaskMembersListView);
        manageIntent(getIntent());

        backButtonPressed();
        resourcesButtonPressed();
        assumptionButtonPressed();
        setActionsOnMembersListView();

        setAllowanceOnViews();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_EDIT_TASK){
            finish();
        }
    }

    private void setAllowanceOnViews() {
        if(MainActivity.getUserGrade()<=3){
            membersWhoAssumedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if(task.getMembersWhoAssumed().get(position)!=null){
                        startActivity(new Intent(getApplicationContext(),MembersProgressPopUp.class).putExtra("memberFromTaskProfile",task.getMembersWhoAssumed().get(position)));
                    }
                    return true;
                }
            });
            if(task.getDivision().equals(MainActivity.getLoggedInUser().getDepartament())){
                deleteButton.setVisibility(View.VISIBLE);
                editButton.setVisibility(View.VISIBLE);
                deleteButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        deleteTaskFromDataBase();
                        finish();
                    }
                });
                editButton.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent(getApplicationContext(),AddTask.class).putExtra("taskFromTaskProfile",task), REQUEST_CODE_EDIT_TASK);
                    }
                });
            }else {
                deleteButton.setVisibility(View.INVISIBLE);
                editButton.setVisibility(View.INVISIBLE);
            }
        }else{
            deleteButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
        }
    }

    private void deleteTaskFromDataBase() {
        (FirebaseFirestore.getInstance())
                .collection("openTasks")
                .document(task.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Task eliminat cu succes.",Toast.LENGTH_LONG).show();
                    }
                });

        (FirebaseFirestore.getInstance())
                .collection("assumedTasks")
                .document(task.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Task eliminat cu succes.",Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void setActionsOnMembersListView() {
        membersWhoAssumedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(task.getMembersWhoAssumed().get(position)!=null){
                    startActivity(new Intent(getApplicationContext(),Profile.class).putExtra("memberFromTaskProfile",task.getMembersWhoAssumed().get(position).getUser()));
                }
            }
        });
    }

    private void assumptionButtonPressed() {
        assumptionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    noMemberAssumedYetTextView.setVisibility(View.GONE);
                    task.getMembersWhoAssumed().add(new AssumedTasksSituation(MainActivity.getLoggedInUser(),0,false));
                    membersWhoAssumedListView.setAdapter(new ListViewAssumedTaskSituationAdapter(getApplicationContext(),R.layout.member_card,task.getMembersWhoAssumed()));
                    if(task.getMembersWhoAssumed().size()==task.getNumberOfVolunteers()){
                        assumptionButton.setVisibility(View.INVISIBLE);
                        addTaskToAssumedTasksInDataBase(task);
                        removeTaskFromOpenTasks(task);
                    }else{
                        updateDatabaseEntry(task.getId(), task);
                    }
                }
            }
        });
    }

    private void removeTaskFromOpenTasks(ProjectTask task) {
        (FirebaseFirestore.getInstance())
                .collection("openTasks")
                .document(task.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"Task-ul a fost trecut cu succes in categoria Task-uri asumate in totalitate." , Toast.LENGTH_LONG).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(),"Nu s-a putut trece task-ul in categoria Task-uri asumate." , Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void addTaskToAssumedTasksInDataBase(ProjectTask task) {
        (FirebaseFirestore.getInstance())
                .collection("assumedTasks")
                .document(task.getId())
                .set(task)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getApplicationContext(),"S-au ocupat toate locurile disponibile pentru acest task." , Toast.LENGTH_LONG).show();
                    }
                });
    }

    private boolean validation() {
        if(MainActivity.getLoggedInUser().getDepartament().equals(task.getDivision())){
            if(task.getMembersWhoAssumed().size()<task.getNumberOfVolunteers()){
                if(!hasMemberAssumedAlready(task.getMembersWhoAssumed(),MainActivity.getLoggedInUser())){
                    return true;
                }else{
                    Toast.makeText(getApplicationContext(), "Deja ti-ai asumat acest task.",Toast.LENGTH_LONG).show();
                    return false;
                }
            }else{
                Toast.makeText(getApplicationContext(), "Nu mai sunt locuri disponibile pentru acest task.", Toast.LENGTH_LONG).show();
                return false;
            }
        }else{
            Toast.makeText(getApplicationContext(), "Task-ul nu este destinat diviziei tale.",Toast.LENGTH_LONG).show();
            return false;
        }
    }

    private boolean hasMemberAssumedAlready(ArrayList<AssumedTasksSituation> membersWhoAssumed, User loggedInUser) {
        for(AssumedTasksSituation task : membersWhoAssumed){
            if(task.getUser().getEmail().equals(loggedInUser.getEmail())){
                return true;
            }
        }
        return false;
    }

    private void updateDatabaseEntry(String id, final ProjectTask task) {
        (FirebaseFirestore.getInstance())
                .collection("openTasks")
                .document(id)
                .update("membersWhoAssumed",task.getMembersWhoAssumed())
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(), "Ti-ai asumat task-ul" + task.getTaskName(), Toast.LENGTH_LONG).show();
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Nu s-a putut asuma task-ul. Mai asteapta cateva momemnte si reincearca.", Toast.LENGTH_LONG).show();
                    }
                });
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("openTaskFromOpenTasks")!= null) {
            task = (ProjectTask) intent.getParcelableExtra("openTaskFromOpenTasks");
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
                if(task.getMembersWhoAssumed().size()==0){
                    noMemberAssumedYetTextView.setVisibility(View.VISIBLE);
                }else{
                    membersWhoAssumedListView.setAdapter(new ListViewAssumedTaskSituationAdapter(getApplicationContext(),R.layout.member_card,task.getMembersWhoAssumed()));
                }
                if(task.getMembersWhoAssumed().size()==task.getNumberOfVolunteers()){
                    assumptionButton.setVisibility(View.INVISIBLE);
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

    private void resourcesButtonPressed() {
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
