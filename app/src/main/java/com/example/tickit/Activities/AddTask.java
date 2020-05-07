package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Adapters.SpinnerStringAdapter;
import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackDocumentReference;
import com.example.tickit.Callbacks.CallbackPrjectTask;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.google.firebase.firestore.DocumentReference;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddTask extends AppCompatActivity {
    EditText taskNameEditText, taskDescriptionEditText, taskResourceEditText, startDateTaskEditText, stopDateTaskEditText, numberOfMemberEditText;
    ImageButton backButton;
    Button saveButton;
    Spinner spinnerProject, divisionsSpinner;
    ProjectTask task;
    TextView title;
    int isEditMode =0;
    int REQUEST_CODE_EDIT_TASK =5;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        assignViews();

        backButtonPressed();
        saveButtonPressed();
        setSpinnersUp();
        manageIntent(getIntent());
    }

    private void assignViews() {
        title = (TextView) findViewById(R.id.taskNameTextView);
        divisionsSpinner = (Spinner) findViewById(R.id.spinnerDivisionAddTask);
        spinnerProject = (Spinner) findViewById(R.id.spinnerProjectAddTask);
        divisionsSpinner = (Spinner) findViewById(R.id.spinnerDivisionAddTask);
        taskNameEditText = (EditText) findViewById(R.id.taskNameEditText);
        taskDescriptionEditText = (EditText) findViewById(R.id.taskDescriptionEditText);
        taskResourceEditText= (EditText) findViewById(R.id.taskResourceEditText);
        startDateTaskEditText= (EditText) findViewById(R.id.taskStartDate);
        stopDateTaskEditText = (EditText) findViewById(R.id.taskStopDate);
        numberOfMemberEditText = (EditText) findViewById(R.id.taskMaxNoOfVolunteers);
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("taskFromTaskProfile")!= null) {
            task = (ProjectTask) intent.getParcelableExtra("taskFromTaskProfile");
            fillWithInfo();
            isEditMode =1;
        }
    }

    private void fillWithInfo() {
        title.setTextSize(35);
        ProjectDatabaseCalls.getProjectName(task.getProject(), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                title.setText("Editare task " + task.getTaskName() + " pe proiectul " + value);
            }
        });
        divisionsSpinner.setVisibility(View.GONE);
        spinnerProject.setVisibility(View.GONE);
        taskNameEditText.setText(task.getTaskName());
        taskDescriptionEditText.setText(task.getTaskDescription());
        taskResourceEditText.setText(task.getTaskResource());
        startDateTaskEditText.setText(DateProcessing.dateFormat.format(task.getStartDate()));
        stopDateTaskEditText.setText(DateProcessing.dateFormat.format(task.getStopDate()));
        numberOfMemberEditText.setText(String.valueOf(task.getNumberOfVolunteers()));
    }

    private void setSpinnersUp() {
        getUserCurrentMandates(new CallbackArrayListStrings() {
            @Override
            public void onCallback(ArrayList<String> strings) {
                String[] projects =  new String[strings.size()];
                projects= strings.toArray(new String[0]);
                spinnerProject.setAdapter(new SpinnerStringAdapter(getApplicationContext(), projects));
                divisionsSpinner.setAdapter(new SpinnerStringAdapter(getApplicationContext(), getResources().getStringArray(R.array.departments)));
            }
        });
    }

    private void getUserCurrentMandates(final CallbackArrayListStrings callbackArrayListStrings) {
        final ArrayList<String> projects = new ArrayList<>();
        projects.add("Proiect");
        boolean isBeBC=false;
        for (final Mandate mandate : ((GlobalVariables) getApplicationContext()).getLoggedInUser().getMandates()){
            if(isOnGoing(mandate)  ) {
                ProjectDatabaseCalls.getProjectName(mandate.getProject_name(), new CallbackString() {
                    @Override
                    public void onCallBack(String value) {
                        if(value.equals("BE-BC")) {
                            Log.d("filteredList", value+"");
                            ProjectDatabaseCalls.getProjectsNames(new CallbackArrayListStrings() {
                                @Override
                                public void onCallback(ArrayList<String> strings) {
                                    projects.addAll(strings);
                                    callbackArrayListStrings.onCallback(projects);
                                }
                            });
                        }else{
                            ProjectDatabaseCalls.getProjectName(mandate.getProject_name(), new CallbackString() {
                                @Override
                                public void onCallBack(String value) {
                                    projects.add(value);
                                }
                            });
                        }
                    }
                });

            }
        }
        if(!isBeBC) {
            callbackArrayListStrings.onCallback(projects);
        }
    }

    private boolean isOnGoing(Mandate mandate) {
        return System.currentTimeMillis() < mandate.getStop_date().getTime();
    }

    private void saveButtonPressed() {
        saveButton = (Button) findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    buildTaskFromForm(isEditMode, new CallbackPrjectTask() {
                        @Override
                        public void onCallBack(final ProjectTask projectTask) {
                            if(task!=null){
                                if(task.getNumberOfVolunteers()==task.getMembersWhoAssumed().size()) {
                                    ProjectTasksDatabaseCalls.addProjectTaskInDataBase("assumedTasks", projectTask, new CallbackBoolean() {
                                        @Override
                                        public void callback(Boolean bool) {
                                            Toast.makeText(getApplicationContext(), "S-a editat un task pentru divizia " + projectTask.getDivision() + " in cadrul proiectului " + spinnerProject.getSelectedItem(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }else{
                                    ProjectTasksDatabaseCalls.addProjectTaskInDataBase("openTasks", projectTask, new CallbackBoolean() {
                                        @Override
                                        public void callback(Boolean bool) {
                                            Toast.makeText(getApplicationContext(), "S-a editat un task pentru divizia " + projectTask.getDivision() + " in cadrul proiectului " + spinnerProject.getSelectedItem(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                                }
                                setResult(RESULT_OK);
                            }else {
                                ProjectTasksDatabaseCalls.addProjectTaskInDataBase("openTasks", projectTask, new CallbackBoolean() {
                                    @Override
                                    public void callback(Boolean bool) {
                                        Toast.makeText(getApplicationContext(), "S-a adaugat un task pentru divizia " + projectTask.getDivision() + " in cadrul proiectului " + spinnerProject.getSelectedItem(), Toast.LENGTH_LONG).show();
                                        //send notification
//                                        manageNotification();
                                    }
                                });
                            }
                            finish();
                        }
                    });
            }
        });
    }

    private void buildTaskFromForm(final int flag, final CallbackPrjectTask callbackPrjectTask) {
        if( validation()){
            if(flag == 0){
                ProjectDatabaseCalls.getDocumentReferenceProject(spinnerProject.getSelectedItem().toString(), new CallbackDocumentReference() {
                    @Override
                    public void callback(DocumentReference documentReference) {
                        callbackPrjectTask.onCallBack(
                                new ProjectTask((taskNameEditText.getText().toString() + spinnerProject.getSelectedItem().toString() + (DateProcessing.dateFormat.format(new Date().getTime())).replace("/", "")).replace(" ", ""),
                                        taskNameEditText.getText().toString(),
                                        taskDescriptionEditText.getText().toString(),
                                        documentReference,
                                        divisionsSpinner.getSelectedItem().toString(),
                                        DateProcessing.getDate(startDateTaskEditText),
                                        DateProcessing.getDate(stopDateTaskEditText),
                                        Integer.parseInt(numberOfMemberEditText.getText().toString()),
                                        taskResourceEditText.getText().toString(),
                                        new ArrayList<AssumedTasksSituation>()
                                )
                        );
                    }
                });
            }
            if(flag==1) {
                callbackPrjectTask.onCallBack(
                        new ProjectTask(task.getId(),
                                taskNameEditText.getText().toString(),
                                taskDescriptionEditText.getText().toString(),
                                task.getProject(),
                                task.getDivision(),
                                DateProcessing.getDate(startDateTaskEditText),
                                DateProcessing.getDate(stopDateTaskEditText),
                                Integer.parseInt(numberOfMemberEditText.getText().toString()),
                                taskResourceEditText.getText().toString(),
                                task.getMembersWhoAssumed()
                        )
                );
            }
        }
    }

    private boolean validation() {
        if (spinnerProject.getSelectedItemPosition() == 0 && isEditMode ==0) {
            Toast.makeText(getApplicationContext(), "Alegeti proiectul pentru care doriti adaugare task-ului.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if(!divisionsSpinner.getSelectedItem().equals(((GlobalVariables) getApplicationContext()).getLoggedInUser().getDepartament())&& isEditMode ==0){
                Toast.makeText(getApplicationContext(), "Divizia aleasa nu corespunde cu divizia ta.", Toast.LENGTH_LONG).show();
                return false;
            }else{
                if (taskNameEditText.getText().length() < 3) {
                    Toast.makeText(getApplicationContext(), "Numele task-ului trebuie sa aiba mai mult de 3 caractere.", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    if (taskDescriptionEditText.getText().length() < 10) {
                        Toast.makeText(getApplicationContext(), "Descrierea task-ului trebuie sa aiba mai mult de 10 caractere.", Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        if (!DateProcessing.dateValidation(startDateTaskEditText) || !DateProcessing.dateValidation(stopDateTaskEditText) || DateProcessing.getDate(stopDateTaskEditText) == null || DateProcessing.getDate(startDateTaskEditText)==null) {
                            Toast.makeText(getApplicationContext(), "Data nu a fost introdusa in formatul corect: dd/mm/yyyy.", Toast.LENGTH_LONG).show();
                            return false;
                        } else {
                            if (System.currentTimeMillis() > DateProcessing.getDate(stopDateTaskEditText).getTime() || DateProcessing.getDate(startDateTaskEditText).getTime() > DateProcessing.getDate(stopDateTaskEditText).getTime()) {
                                Toast.makeText(getApplicationContext(), "Data de inceput sau cea de sfarsit nu sunt corect alese.", Toast.LENGTH_LONG).show();
                                return false;
                            } else {
                                if (numberOfMemberEditText.getText().length() < 1) {
                                    Toast.makeText(getApplicationContext(), "Introduceti numarul de voluntari necesari.", Toast.LENGTH_LONG).show();
                                    return false;
                                } else {
                                    if (taskResourceEditText.getText().length() < 5 || URLUtil.isValidUrl(taskResourceEditText.getText().toString())) {
                                        Toast.makeText(getApplicationContext(), "Introduceti un URL valid.", Toast.LENGTH_LONG).show();
                                        return false;
                                    }else{
                                        if(divisionsSpinner.getSelectedItemPosition()==0&& isEditMode ==0){
                                            Toast.makeText(getApplicationContext(), "Alegeti divizia pentru care doriti adaugare task-ului.", Toast.LENGTH_LONG).show();
                                            return false;
                                        }else{
                                            return true;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }

        }
    }

    private void backButtonPressed() {
        backButton = (ImageButton) findViewById(R.id.backButtonAddTask);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


}
