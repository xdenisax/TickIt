package com.example.tickit.PopUps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Adapters.SpinnerStringAdapter;
import com.opencsv.CSVReader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class AddMemberChoicePopUp extends AppCompatActivity {
    Button addMembersInDataBaseButton, addMembersFromExcel, addMembersManually;
    EditText emailsEditText;
    TextView hintTextView;
    Spinner spinnerDepartment;
    int REQUEST_CODE_FILE_PATH =10;
    Uri fileUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_choice_pop_up);

        assignViews();
        setMetrics(0.8, 0.2);
        setAvailability(false);
        setSpinnerUp();
        setActionOnExcelButton();
        setActionOnManuallyButton();
        setActionOnAddInDataBase();
    }

    private void assignViews() {
        spinnerDepartment = (Spinner) findViewById(R.id.spinnerDepartments);
        addMembersInDataBaseButton = (Button) findViewById(R.id.addMembersInDataBaseButton);
        emailsEditText = (EditText) findViewById(R.id.addMembersEmailsEditText);
        addMembersManually = (Button) findViewById(R.id.addMembersManuallyButton);
        hintTextView = (TextView) findViewById(R.id.hintMailTextView);
        addMembersFromExcel= (Button) findViewById(R.id.addMembersFromExcelButton);
    }

    private void setAvailability(Boolean isVisible) {
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

    private void setSpinnerUp() {
        spinnerDepartment.setAdapter(new SpinnerStringAdapter(getApplicationContext(),getResources().getStringArray(R.array.departments)));
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 1002 && resultCode == RESULT_OK && data!=null ) {
            readCSV(data.getData());
        }else{
            Toast.makeText(getApplicationContext(), "Nu s-a selectat fisierul.", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if(requestCode==1001){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Toast.makeText(getApplicationContext(), "Permisiune acordata.", Toast.LENGTH_LONG).show();
                selectFile();
            }else{
                Toast.makeText(getApplicationContext(), "Permisiune respinsa.", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private void readCSV(Uri path) {
        try{
            CSVReader csvReader = new CSVReader( new BufferedReader(new InputStreamReader(getContentResolver().openInputStream(path))));
            String[] line = csvReader.readNext();
            ArrayList<String> emails =new ArrayList<>();
            ArrayList<String> departments = new ArrayList<>();

            while((line = csvReader.readNext())!=null){
                emails.add(line[0]);
                departments.add(line[1]);
            }

            String[] emailsArray  =  emails.toArray(new String[0]);
            String[] departmentsArray  =  departments.toArray(new String[0]);

            if(emails.size()>0){
                addToDataBase(emailsArray, departmentsArray);
            }
            if(emails.size()<=0){
                Toast.makeText(getApplicationContext(), "Nu s-a gasit niciun membru in CSV.", Toast.LENGTH_LONG).show();
            }

        } catch (FileNotFoundException e) {
            Log.d("traceERR", e.toString());
            Toast.makeText(getApplicationContext(), "Nu s-a putut gasi fisierul CSV.",Toast.LENGTH_LONG).show();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("traceERR", e.toString());
            Toast.makeText(getApplicationContext(), "Probleme la citire. ",Toast.LENGTH_LONG).show();
        }
    }

    private void addToDataBase(String[] emails, String[] departments) {
        if(validationCSV(emails, departments)){
            for(int i=0;i< emails.length;i++){
                UserDatabaseCalls.addUser(emails[i], departments[i]);
            }
        }
    }

    private boolean validationCSV(String[] emails, String[] departments) {
        if(emails.length != departments.length){
            makeToast("Numarul membrilor nu corespunde cu numarul departamentelor.");
            return false;
        }

        for(int i=0; i<emails.length;i++){
            if(emails[i]==null || emails[i].equals("")){
                makeToast("Nu s-a gasit emailul de la pozitia "+ i);
                return false;
            }
            if (emails[i].length() <=12) {
                makeToast(emails[i]+ " nu are o lungime corespunzatoare.");
                return false;
            }
            if (!emails[i].substring(emails[i].length() - 10, emails[i].length()).equals("@gmail.com")) {
                makeToast(emails[i]+ " nu contine @gmail.com");
                return false;
            }
        }

        for(int i=0; i<departments.length;i++){
            if(departments[i]==null || departments[i].equals("")){
                makeToast("Nu s-a gasit departametul de la pozitia "+ i);
                return false;
            }
        }
        return true;
    }

    private void setActionOnAddInDataBase() {
        addMembersInDataBaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String emails = String.valueOf(emailsEditText.getText());
                String department = getSpinnerSelection();
                if(validation(emails)&& department.length()>1){
                    String[] emailsArray = emails.split(", ");
                    UserDatabaseCalls.addListOfMembersToDataBase(emailsArray, department);
                    if(emailsArray.length==1){
                        makeToast(emailsArray.length +" membru a fost adaugat in departamentul "+ department+".");
                    }else{
                        makeToast(emailsArray.length +" membri au fost adaugati in departamentul "+ department+".");
                    }
                    emailsEditText.setText("");
                }
            }
        });
    }

    private void setActionOnManuallyButton() {
        addMembersManually.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMetrics(0.8, 0.7);
                setAvailability(true);
            }
        });
    }

    private void setActionOnExcelButton() {
        addMembersFromExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setMetrics(0.8, 0.2);
                setAvailability(false);
                if(ContextCompat.checkSelfPermission(AddMemberChoicePopUp.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }else{
                    ActivityCompat.requestPermissions(AddMemberChoicePopUp.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1001);
                }
            }
        });
    }

    private void selectFile() {
        Intent intent= new Intent();
        intent.setType("*/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1002);
    }

    private boolean validation(String emails) {
        if (emails == null) {
            makeToast("Nu s-au introdus emailuri.");
            return false;
        }
        if (emails.length() < 10) {
            makeToast("Lungime email invalida.");
            return false;
        }
        String[] emailsArray = emails.split(", ");
        boolean allEmailsOk = true;
        for (int i = 0; i < emailsArray.length; i++) {
            emailsArray[i]= emailsArray[i].replace(" ","");
            if (emailsArray[i].length() < 10) {
                makeToast(emailsArray[i]+ " nu are o lungime corespunzatoare.");
                allEmailsOk = false;
            } else {
                if (!emailsArray[i].substring(emailsArray[i].length() - 10, emailsArray[i].length()).equals("@gmail.com")) {
                    makeToast(emailsArray[i]+ " nu contine @gmail.com");
                    allEmailsOk = false;
                }
            }
        }
        return allEmailsOk;

    }

    private void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void setMetrics(double widthPercent, double heightPercent) {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*widthPercent), (int) (windowHeigth*heightPercent));
    }
}
