package com.example.tickit;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class AddMemberChoicePopUp extends AppCompatActivity {
    Button addMembersInDataBaseButton, addMembersFromExcel, addMembersManually;
    EditText emailsEditText;
    TextView hintTextView;
    Spinner spinnerDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_choice_pop_up);

        setMetrics();
        setAvailabilty(false);
        setSpinnerUp();
        setActionOnExcelButton();
        setActionOnManuallyButton();
        setActionOnAddInDataBase();
    }

    private void setSpinnerUp() {
        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartments);
        spinnerDepartment.setAdapter(new SpinnerStringAdapter(getApplicationContext(),getResources().getStringArray(R.array.departments)));
    }

    private void setActionOnAddInDataBase() {
        addMembersInDataBaseButton = (Button) findViewById(R.id.addMembersInDataBaseButton);
        emailsEditText = (EditText) findViewById(R.id.addMembersEmailsEditText);

        addMembersInDataBaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails = String.valueOf(emailsEditText.getText());
                String department = getSpinnerSelection();
                if(validation(emails)&& department.length()>1){
                    String[] emailsArray = emails.split(", ");
                    addMembersToDataBase(emailsArray, department);
                    if(emailsArray.length==1){
                        Toast.makeText(getApplicationContext(), emailsArray.length +" membru a fost adaugat in departamentul "+ department+".", Toast.LENGTH_LONG).show();
                    }else{
                        Toast.makeText(getApplicationContext(), emailsArray.length +" membri au fost adaugati in departamentul "+ department+".", Toast.LENGTH_LONG).show();
                    }
                    emailsEditText.setText("");
                }
                Log.d("checkMembers", emails);
            }
        });
    }

    private void addMembersToDataBase(String[] emailsArray, String department) {
        CollectionReference collectionReference = FirebaseFirestore.getInstance().collection("users");
        for(String member:emailsArray){
            member = member.replace(" ", "");
            User newUser = new User(null, null, null, member, null, department, new ArrayList<Mandate>());
            collectionReference.document(member).set(newUser);
        }
    }

    private String getSpinnerSelection() {
        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartments);
        if(spinnerDepartment.getSelectedItemPosition()==0){
            Toast.makeText(getApplicationContext(), "Departamanetul nu a fost selectat.", Toast.LENGTH_LONG).show();
            return "";
        }else{
            return (String) spinnerDepartment.getSelectedItem();
        }
    }

    private boolean validation(String emails) {
        if (emails == null) {
            Toast.makeText(getApplicationContext(), "Nu s-au introdus emailuri.", Toast.LENGTH_LONG).show();
            return false;
        } else {
            if (emails.length() < 10) {
                Toast.makeText(getApplicationContext(), "Lungime email invalida.", Toast.LENGTH_LONG).show();
                return false;
            } else {
                String[] emailsArray = emails.split(", ");
                boolean allEmailsOk = true;
                for (int i = 0; i < emailsArray.length; i++) {
                    emailsArray[i]= emailsArray[i].replace(" ","");
                    if (emailsArray[i].length() < 10) {
                        Toast.makeText(getApplicationContext(), emailsArray[i]+ " nu are o lungime corespunzatoare.", Toast.LENGTH_LONG).show();
                        allEmailsOk = false;
                    } else {
                        if (!emailsArray[i].substring(emailsArray[i].length() - 10, emailsArray[i].length()).equals("@gmail.com")) {
                            Log.d("checkMembers","|"+emailsArray[i].substring(emailsArray[i].length() - 10, emailsArray[i].length())+"|");
                            Toast.makeText(getApplicationContext(), emailsArray[i]+ " nu contine @gmail.com", Toast.LENGTH_LONG).show();
                            allEmailsOk = false;
                        }
                    }
                }
                return allEmailsOk;
            }
        }
    }

    private void setActionOnManuallyButton() {
        addMembersManually = (Button) findViewById(R.id.addMembersManuallyButton);
        addMembersManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvailabilty(true);
            }
        });
    }

    private void setActionOnExcelButton() {
        addMembersFromExcel= (Button) findViewById(R.id.addMembersFromExcelButton);
        addMembersFromExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setAvailabilty(false);
            }
        });

    }

    private void setAvailabilty(Boolean isVisible) {
        addMembersInDataBaseButton = (Button) findViewById(R.id.addMembersInDataBaseButton);
        emailsEditText = (EditText) findViewById(R.id.addMembersEmailsEditText);
        hintTextView = (TextView) findViewById(R.id.hintMailTextView);
        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartments);

        if(isVisible){
            hintTextView.setVisibility(View.VISIBLE);
            emailsEditText.setVisibility(View.VISIBLE);
            addMembersInDataBaseButton.setVisibility(View.VISIBLE);
            spinnerDepartment.setVisibility(View.VISIBLE);
        }else{
            hintTextView.setVisibility(View.GONE);
            emailsEditText.setVisibility(View.GONE);
            addMembersInDataBaseButton.setVisibility(View.GONE);
            spinnerDepartment.setVisibility(View.GONE);
        }
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.75));
    }
}
