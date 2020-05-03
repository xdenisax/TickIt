package com.example.tickit.Activities;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
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

import com.example.tickit.Adapters.ListViewAssumedTaskSituationAdapter;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.PopUps.MembersProgressPopUp;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TaskProfile extends AppCompatActivity {
    ImageButton backButton;
    ProjectTask task;
    TextView taskNameTextView, projectNameTextView,startDateTextView, deadlineTextView, descriptionTextView, noMemberAssumedYetTextView;
    Button resourcesButton, assumptionButton, editButton, deleteButton,progressButton,helpButton;
    ListView membersWhoAssumedListView;
    final int REQUEST_CODE_EDIT_TASK =5;
    final int REQUEST_CODE_EDIT_PROGRESS =6;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_profile);

        assignViews();
        manageIntent(getIntent());
        backButtonPressed();
        editButtonPressed();
        resourcesButtonPressed();
        assumptionButtonPressed();
        progressButtonPressed();
        helpButtonPressed();
        deleteButtonPressed();
        setActionsOnMembersListView();
        setAllowanceOnViews();
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_EDIT_TASK){
            finish();
        }
        if(requestCode==REQUEST_CODE_EDIT_PROGRESS && RESULT_OK==resultCode){
            task.getMembersWhoAssumed().get(getPersonalProgressIndex()).setProgress(data.getIntExtra("updatedProgress",0));
            ProjectTasksDatabaseCalls.updateProgressInDataBase("openTasks", task, getPersonalProgressIndex(), new CallbackBoolean() {
                @Override
                public void callback(Boolean bool) {
                    Toast.makeText(getApplicationContext(), "Progres inregistrat.",Toast.LENGTH_LONG).show();
                }
            });
            ProjectTasksDatabaseCalls.updateProgressInDataBase("assumedTasks", task, getPersonalProgressIndex(), new CallbackBoolean() {
                @Override
                public void callback(Boolean bool) {
                    Toast.makeText(getApplicationContext(), "Progres inregistrat .",Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    private void assignViews() {
        backButton = (ImageButton) findViewById(R.id.backButtonTaskProfile);
        taskNameTextView = (TextView) findViewById(R.id.taskNameTextView);
        projectNameTextView= (TextView) findViewById(R.id.projectNameTextViewTaskProfile);
        startDateTextView= (TextView) findViewById(R.id.taskStartDateTextView);
        deadlineTextView= (TextView) findViewById(R.id.taskStopDateTextView);
        descriptionTextView= (TextView) findViewById(R.id.taskDescriptionTextView);
        noMemberAssumedYetTextView = (TextView) findViewById(R.id.noMemberAssumedYet);
        noMemberAssumedYetTextView.setVisibility(View.GONE);
        resourcesButton = (Button) findViewById(R.id.ResourcesButton);
        assumptionButton =(Button) findViewById(R.id.assumptionButton);
        progressButton =(Button) findViewById(R.id.progressButton);
        editButton =(Button) findViewById(R.id.editTaskButton);
        helpButton =(Button) findViewById(R.id.helpTaskProfile);
        deleteButton =(Button) findViewById(R.id.deleteTaskButton);
        membersWhoAssumedListView = (ListView) findViewById(R.id.assumedTaskMembersListView);
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("openTaskFromOpenTasks")!= null) {
            task = (ProjectTask) intent.getParcelableExtra("openTaskFromOpenTasks");
            progressButton.setVisibility(View.GONE);
            helpButton.setVisibility(View.GONE);
            fillWithInfo();
        }
        if(intent.getParcelableExtra("taskFromMyTasks")!= null) {
            task = (ProjectTask) intent.getParcelableExtra("taskFromMyTasks");
            fillWithInfo();
            assumptionButton.setVisibility(View.GONE);
            editButton.setVisibility(View.GONE);
            deleteButton.setVisibility(View.GONE);
        }
    }

    private void fillWithInfo() {
        ProjectDatabaseCalls.getProjectName(task.getProject(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                projectNameTextView.setText(value);
                taskNameTextView.setText(task.getTaskName());
                startDateTextView.setText(DateProcessing.dateFormat.format(task.getStartDate()));
                deadlineTextView.setText(DateProcessing.dateFormat.format(task.getStopDate()));
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

    private void setAllowanceOnViews() {
        if(MainActivity.getUserGrade()<=3) {
            membersWhoAssumedListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    if (task.getMembersWhoAssumed().get(position) != null) {
                        startActivity(new Intent(getApplicationContext(), MembersProgressPopUp.class).putExtra("memberFromTaskProfile", task.getMembersWhoAssumed().get(position)));
                    }
                    return true;
                }
            });
        }

        if(MainActivity.getUserGrade()<=3 && task.getDivision().equals(MainActivity.getLoggedInUser().getDepartament())){
            deleteButton.setVisibility(View.VISIBLE);
            editButton.setVisibility(View.VISIBLE);
        }else{
            deleteButton.setVisibility(View.INVISIBLE);
            editButton.setVisibility(View.INVISIBLE);
        }
    }

    private void setActionsOnMembersListView() {
        membersWhoAssumedListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(task.getMembersWhoAssumed().get(position)!=null){
                    if(!task.getMembersWhoAssumed().get(position).getUser().getEmail().equals(MainActivity.getLoggedInUser().getEmail())){
                        startActivity(new Intent(getApplicationContext(),Profile.class).putExtra("memberFromTaskProfile",task.getMembersWhoAssumed().get(position).getUser()));
                    }
                }
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
                        ProjectTasksDatabaseCalls.addProjectTaskInDataBase("assumedTasks", task, new CallbackBoolean() {
                            @Override
                            public void callback(Boolean bool) { }
                        });
                        ProjectTasksDatabaseCalls.removeTask("openTasks", task, new CallbackBoolean() {
                            @Override
                            public void callback(Boolean bool) { }
                        });
                    }else{
                        ProjectTasksDatabaseCalls.updateProgressInDataBase("openTasks", task, getPersonalProgressIndex(), new CallbackBoolean() {
                            @Override
                            public void callback(Boolean bool) { }
                        });
                    }
                }
            }
        });
    }

    private int getPersonalProgressIndex() {
        for(int i=0;i< task.getMembersWhoAssumed().size();i++){
            if(MainActivity.getLoggedInUser().getEmail().equals(task.getMembersWhoAssumed().get(i).getUser().getEmail())){
                return i;
            }
        }
        return -1;
    }

    private void editButtonPressed(){
        editButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(),AddTask.class).putExtra("taskFromTaskProfile",task), REQUEST_CODE_EDIT_TASK);
            }
        });
    }

    private void deleteButtonPressed(){
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProjectTasksDatabaseCalls.removeTask("assumedTasks", task, new CallbackBoolean() {
                    @Override
                    public void callback(Boolean bool) {
                        ProjectTasksDatabaseCalls.removeTask("openTasks", task, new CallbackBoolean() {
                            @Override
                            public void callback(Boolean bool) {
                                finish();
                            }
                        });
                    }
                });
            }
        });
    }

    private void progressButtonPressed() {
        progressButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(getPersonalProgressIndex()!= -1){
                    startActivityForResult(new Intent(getApplicationContext(), MembersProgressPopUp.class).putExtra("personalProgressFromTaskProfile", task.getMembersWhoAssumed().get(getPersonalProgressIndex())), REQUEST_CODE_EDIT_PROGRESS);
                }else{
                    Toast.makeText(getApplicationContext(), "Eroare de gasire.", Toast.LENGTH_LONG).show();
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

    private void helpButtonPressed() {
        helpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(task.getMembersWhoAssumed().get(getPersonalProgressIndex()).isNeedingHelp()){
                    Toast.makeText(getApplicationContext(), "Deja ai cerut ajutorul.", Toast.LENGTH_LONG).show();
                }else{
                    launchAlertDialog();
                }
            }
        });
    }

    private void launchAlertDialog() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(TaskProfile.this);
        dialog.setTitle("Doresti sa soliciti ajutor suplimentar?");
        dialog.setMessage("Apasa 'Da' doar in cazul in care consideri ca nu vei reusi finalizarea task-ului pana la deadline.");
        dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                task.setNumberOfVolunteers(task.getNumberOfVolunteers()+1);
                task.getMembersWhoAssumed().get(getPersonalProgressIndex()).setNeedingHelp(true);
                if(task.getMembersWhoAssumed().size()==task.getNumberOfVolunteers()-1){
                    transferTaskFromAssumedToOpen();
                }else{
                    ProjectTasksDatabaseCalls.updateProgressInDataBase("openTasks", task, getPersonalProgressIndex(), new CallbackBoolean() {
                        @Override
                        public void callback(Boolean bool) {
                            Toast.makeText(getApplicationContext(), "Cererea a fost ingregistrata."+task.getMembersWhoAssumed().get(getPersonalProgressIndex()).isNeedingHelp(), Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void transferTaskFromAssumedToOpen() {
        ProjectTasksDatabaseCalls.addProjectTaskInDataBase("openTasks", task, new CallbackBoolean() {
            @Override
            public void callback(Boolean bool) {
                ProjectTasksDatabaseCalls.removeTask("assumedTasks", task, new CallbackBoolean() {
                    @Override
                    public void callback(Boolean bool) {
                        Toast.makeText(getApplicationContext(), "Cererea a fost ingregistrata."+task.getNumberOfVolunteers(), Toast.LENGTH_LONG).show();
                    }
                });
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
