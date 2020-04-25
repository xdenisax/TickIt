package com.example.tickit;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackDocumentReference;
import com.example.tickit.Callbacks.CallbackPrjectTask;
import com.example.tickit.Callbacks.CallbackString;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class AddTask extends AppCompatActivity {
    EditText taskNameEditText, taskDescriptionEditText, taskResourceEditText, startDateTaskEditText, stopDateTaskEditText, numberOfMemberEditText;
    ImageButton backButton;
    Button saveButton;
    Spinner spinnerProject, divisionsSpinner;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);

        divisionsSpinner = (Spinner) findViewById(R.id.spinnerDivisionAddTask);
        spinnerProject = (Spinner) findViewById(R.id.spinnerProjectAddTask);
        divisionsSpinner = (Spinner) findViewById(R.id.spinnerDivisionAddTask);
        taskNameEditText = (EditText) findViewById(R.id.taskNameEditText);
        taskDescriptionEditText = (EditText) findViewById(R.id.taskDescriptionEditText);
        taskResourceEditText= (EditText) findViewById(R.id.taskResourceEditText);
        startDateTaskEditText= (EditText) findViewById(R.id.taskStartDate);
        stopDateTaskEditText = (EditText) findViewById(R.id.taskStopDate);
        numberOfMemberEditText = (EditText) findViewById(R.id.taskMaxNoOfVolunteers);

        backButtonPressed();
        saveButtonPressed();
        setSpinnersUp();
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
        for (Mandate mandate : MainActivity.getLoggedInUser().getMandates()){
            if(isOnGoing(mandate)  ) {
                if(mandate.getProjectName().equals("BE-BC")) {
                    isBeBC=true;
                    getProjectsName(new CallbackArrayListStrings() {
                        @Override
                        public void onCallback(ArrayList<String> strings) {
                            projects.addAll(strings);
                            callbackArrayListStrings.onCallback(projects);
                        }
                    });
                }else{
                    projects.add(mandate.getProjectName());
                }
            }
        }
        if(!isBeBC) {
            callbackArrayListStrings.onCallback(projects);
        }
    }

    private void getProjectsName(final CallbackArrayListStrings callbackArrayListStrings){
        (FirebaseFirestore.getInstance())
                .collection("projects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<String> projectsNames=new ArrayList<>();
                            for(QueryDocumentSnapshot doc:task.getResult()){
                                projectsNames.add(doc.getString("name"));
                                if(projectsNames.size()==task.getResult().size()){
                                    callbackArrayListStrings.onCallback(projectsNames);
                                }
                            }
                        }
                    }
                });

    }

    private boolean isOnGoing(Mandate mandate) {
        try {
            SimpleDateFormat dateFormat2 = new SimpleDateFormat("dd-MM-yyyy");
            Date endDate = dateFormat2.parse(mandate.getEndDate());
            return System.currentTimeMillis() < endDate.getTime();

        } catch (ParseException e) {
            e.printStackTrace();
            Toast.makeText(getApplicationContext(), "Nu am putut obtine data de sfarsit pentru proiectul "+ mandate.getProjectName(), Toast.LENGTH_LONG).show();
        }
        return false;
    }

    private void saveButtonPressed() {
        saveButton = (Button) findViewById(R.id.saveTaskButton);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                buildTaskFromForm(new CallbackPrjectTask() {
                    @Override
                    public void onCallBack(ProjectTask projectTask) {
                        addProjectTaskInDataBase(projectTask);
                        finish();
                    }
                });
            }
        });
    }

    private void addProjectTaskInDataBase(final ProjectTask projectTask) {
        (FirebaseFirestore.getInstance()).collection("openTasks").document(projectTask.getId()).set(projectTask).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Toast.makeText(getApplicationContext(), "S-a adaugat un task pentru divizia "+projectTask.getDivision()+" in cadrul proiectului "+spinnerProject.getSelectedItem(), Toast.LENGTH_LONG).show();
            }
        });

    }

    private void buildTaskFromForm(final CallbackPrjectTask callbackPrjectTask) {
        if( validation()){
            getDocumentReferenceProject(spinnerProject.getSelectedItem().toString(), new CallbackDocumentReference() {
                @Override
                public void callback(DocumentReference documentReference) {
                        callbackPrjectTask.onCallBack(
                                new ProjectTask( (taskNameEditText.getText().toString() + spinnerProject.getSelectedItem().toString() + (dateFormat.format(new Date().getTime())).replace("/","")).replace(" ",""),
                                        taskNameEditText.getText().toString(),
                                        taskDescriptionEditText.getText().toString(),
                                        documentReference,
                                        divisionsSpinner.getSelectedItem().toString(),
                                        getDate(startDateTaskEditText),
                                        getDate(stopDateTaskEditText),
                                        Integer.parseInt(numberOfMemberEditText.getText().toString()),
                                        taskResourceEditText.getText().toString(),
                                        new ArrayList<AssumedTasksSituation>()
                                )
                        );
                }
            });
        }

    }

    private void getDocumentReferenceProject(String projectName, final CallbackDocumentReference callbackDocumentReference) {
        (FirebaseFirestore.getInstance())
                .collection("projects")
                .whereEqualTo("name", projectName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                callbackDocumentReference.callback(task.getResult().getDocuments().get(0).getReference());
            }
        });
    }

    private boolean validation() {
        if (spinnerProject.getSelectedItemPosition() == 0) {
            Toast.makeText(getApplicationContext(), "Alegeti proiectul pentru care doriti adaugare task-ului.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (taskNameEditText.getText().length() < 3) {
                Toast.makeText(getApplicationContext(), "Numele task-ului trebuie sa aiba mai mult de 3 caractere.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                if (taskDescriptionEditText.getText().length() < 10) {
                    Toast.makeText(getApplicationContext(), "Descrierea task-ului trebuie sa aiba mai mult de 10 caractere.", Toast.LENGTH_LONG).show();
                    return false;
                } else {
                    if (!dateValidation(startDateTaskEditText) || !dateValidation(stopDateTaskEditText)) {
                        Toast.makeText(getApplicationContext(), "Data nu a fost introdusa in formatul corect: dd/mm/yyyy.", Toast.LENGTH_LONG).show();
                        return false;
                    } else {
                        if (System.currentTimeMillis() > getDate(stopDateTaskEditText).getTime() || getDate(startDateTaskEditText).getTime() > getDate(stopDateTaskEditText).getTime()) {
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
                                    if(divisionsSpinner.getSelectedItemPosition()==0){
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

    private boolean dateValidation(EditText date) {
        if(date.getText().length()!=10){
            return false;
        }else {
            int day = Integer.parseInt(date.getText().toString().substring(0, 2));
            int month = Integer.parseInt(date.getText().toString().substring(3, 5));
            int year = Integer.parseInt(date.getText().toString().substring(6, 10));

            if (!(day >= 1 && day <= 31)) {
                return false;
            } else {
                if (!(month >= 1 && month <= 12)) {
                    return false;
                } else {
                    if (!(year >= 2010 && year <= 2100)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }

        }
    }

    private Date getDate(EditText date) {
        Date startDate= new Date();
        try{
            startDate = dateFormat.parse(date.getText().toString());
        } catch (ParseException e) {
            Toast.makeText(getApplicationContext(), "Data nu a fost introdusa in formatul corect: dd/mm/yyyy", Toast.LENGTH_LONG).show();
        }
        return startDate;
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
