package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
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
import com.example.tickit.Classes.Project;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.google.firebase.firestore.DocumentReference;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

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
        saveButton = (Button) findViewById(R.id.saveTaskButton);
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
                String titleString = getString(R.string.editare_task) + task.getTaskName() + getString(R.string.pe_proiectul) + value;
                title.setText(titleString);
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
        final int[] count={0};
        for (final Map.Entry<String, Mandate>  mandateEntry: MainActivity.getCurrentMandates().entrySet()){
            count[0]++;
            ProjectDatabaseCalls.getProjectName(mandateEntry.getValue().getProject_name(), new CallbackString() {
                    @Override
                    public void onCallBack(String value) {
                        if(value.equals("BE-BC")) {
                            ProjectDatabaseCalls.getProjectsNames(new CallbackArrayListStrings() {
                                @Override
                                public void onCallback(ArrayList<String> strings) {
                                    projects.addAll(strings);
                                    callbackArrayListStrings.onCallback(projects);
                                }
                            });
                        }else{
                            if(mandateEntry.getValue().getGrade()<4 && !projects.contains(value)) {
                                projects.add(value);
                                if (count[0] == MainActivity.getCurrentMandates().size()) {
                                    callbackArrayListStrings.onCallback(projects);
                                }
                            }
                        }
                    }
                });
        }
    }

    private void saveButtonPressed() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                    buildTaskFromForm(isEditMode, new CallbackPrjectTask() {
                        @Override
                        public void onCallBack(final ProjectTask projectTask) {
                            if(task!=null){
                                task=projectTask;
                                if(task.getNumberOfVolunteers()==task.getMembersWhoAssumed().size()) {
                                    transferTaskFromOpenToAssumed(projectTask);
                                }else{
                                    transferTaskFromAssumedToOpen(projectTask);
                                }
                                setResult(RESULT_OK);
                            }else {
                                ProjectTasksDatabaseCalls.addProjectTaskInDataBase("openTasks", projectTask, new CallbackBoolean() {
                                    @Override
                                    public void callback(Boolean bool) {
                                        Toast.makeText(getApplicationContext(), "S-a adaugat un task pentru divizia " + projectTask.getDivision() , Toast.LENGTH_LONG).show();
                                    }
                                });
                                ProjectTasksDatabaseCalls.fakeUpdate("openTasks", projectTask);
                            }
                            finish();
                        }
                    });
            }
        });
    }

    private void transferTaskFromAssumedToOpen(final ProjectTask projectTask) {
        ProjectTasksDatabaseCalls.addProjectTaskInDataBase("openTasks", projectTask, new CallbackBoolean() {
            @Override
            public void callback(Boolean bool) {
                ProjectTasksDatabaseCalls.removeTask("assumedTasks", projectTask, new CallbackBoolean() {
                    @Override
                    public void callback(Boolean bool) {
                        Toast.makeText(getApplicationContext(), "Task-ul este redeschis.", Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
    }

    private void transferTaskFromOpenToAssumed(final ProjectTask projectTask) {
        ProjectTasksDatabaseCalls.addProjectTaskInDataBase("assumedTasks", projectTask, new CallbackBoolean() {
            @Override
            public void callback(Boolean bool) {
                ProjectTasksDatabaseCalls.removeTask("openTasks", projectTask, new CallbackBoolean() {
                    @Override
                    public void callback(Boolean bool) {
                        Toast.makeText(getApplicationContext(), "Task-ul nu este asumat complet.", Toast.LENGTH_LONG).show();
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
            Toast.makeText(getApplicationContext(), R.string.proiect_neselectatt, Toast.LENGTH_LONG).show();
            return false;
        }
        if(divisionsSpinner.getSelectedItemPosition()==0 && isEditMode ==0){
            Toast.makeText(getApplicationContext(), R.string.divizie_neselectata, Toast.LENGTH_LONG).show();
            return false;
        }
        if(MainActivity.getLoggedInUser()!=null && !divisionsSpinner.getSelectedItem().equals(MainActivity.getLoggedInUser().getDepartament())&& isEditMode ==0){
            Toast.makeText(getApplicationContext(), R.string.divizie_incorecta, Toast.LENGTH_LONG).show();
            return false;
        }
        if (taskNameEditText.getText().length() < 3) {
            Toast.makeText(getApplicationContext(), R.string.task_uri_minim_3_caractere, Toast.LENGTH_LONG).show();
            return false;
        }
        if (taskDescriptionEditText.getText().length() < 10) {
            Toast.makeText(getApplicationContext(), R.string.descrirerea_task_ului_insuficient_de_lunga, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!validationLink(taskResourceEditText)) {
            return false;
        }
        if (numberOfMemberEditText.getText().length() < 1) {
            Toast.makeText(getApplicationContext(), R.string.numar_voluntari_neintrodus, Toast.LENGTH_LONG).show();
            return false;
        }
        if(!DateProcessing.dateValidation(startDateTaskEditText) || DateProcessing.getDate(startDateTaskEditText)==null){
            Toast.makeText(getApplicationContext(), R.string.data_inceput_format_incorectt, Toast.LENGTH_LONG).show();
            return false;
        }
        if (!DateProcessing.dateValidation(stopDateTaskEditText) || DateProcessing.getDate(stopDateTaskEditText) == null) {
            Toast.makeText(getApplicationContext(), R.string.data_inceput_format_incorect, Toast.LENGTH_LONG).show();
            return false;
        }
        if (DateProcessing.getDate(startDateTaskEditText).getTime() > DateProcessing.getDate(stopDateTaskEditText).getTime()) {
            Toast.makeText(getApplicationContext(), R.string.data_inceput_dupa_data_sfarsit, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private boolean validationLink(EditText taskResourceEditText) {
        if(taskResourceEditText.getText().toString().length()<5){
            Toast.makeText(getApplicationContext(), "Nu ati introdus link-ul.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(!Patterns.WEB_URL.matcher(taskResourceEditText.getText().toString()).matches()){
            Toast.makeText(getApplicationContext(), "URL invalid"+taskResourceEditText.getText().toString(), Toast.LENGTH_LONG).show();
            return false;
        }
        if(!taskResourceEditText.getText().toString().contains("http")){
            Toast.makeText(getApplicationContext(), "Nu s-a putut gasi protocolul. Link-ul contine http?", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
