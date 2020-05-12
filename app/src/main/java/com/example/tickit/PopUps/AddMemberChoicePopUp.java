package com.example.tickit.PopUps;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
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

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.nio.file.Files;
import java.nio.file.Paths;

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
        setMetrics();
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
            fileUri= data.getData();
            readCSV(fileUri);
            Toast.makeText(getApplicationContext(), fileUri.getPath(), Toast.LENGTH_LONG).show();
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
//            File file = new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+path);
//            Toast.makeText(getApplicationContext(), file.getAbsolutePath() +"", Toast.LENGTH_LONG).show();
//            Reader reader = Files.newBufferedReader(Paths.get(new File(Environment.getExternalStorageDirectory().getAbsolutePath()+"/"+path).getAbsolutePath()));
            CSVReader csvReader = new CSVReader(new FileReader(new File(path.getPath())));
            Toast.makeText(getApplicationContext(), String.valueOf(csvReader.readNext()),Toast.LENGTH_LONG).show();

            String[] line;
            while((line = csvReader.readNext())!=null){
                Toast.makeText(getApplicationContext(), line[0],Toast.LENGTH_LONG).show();
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
                setAvailability(true);
            }
        });
    }

    private void setActionOnExcelButton() {
        addMembersFromExcel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //setAvailability(false);
                //startActivityForResult(new Intent(Intent.ACTION_OPEN_DOCUMENT).addCategory(Intent.CATEGORY_OPENABLE).setType("*/*"), REQUEST_CODE_FILE_PATH);
                if(ContextCompat.checkSelfPermission(AddMemberChoicePopUp.this, Manifest.permission.READ_EXTERNAL_STORAGE)==PackageManager.PERMISSION_GRANTED){
                    selectFile();
                }else{
                    ActivityCompat.requestPermissions(AddMemberChoicePopUp.this, new String[] {Manifest.permission.READ_EXTERNAL_STORAGE},1001);
                }

//                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
//                        && checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE )!= PackageManager.PERMISSION_GRANTED){
//                    requestPermissions(new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},1001);
//                }
//                new MaterialFilePicker()
//                        .withActivity(AddMemberChoicePopUp.this)
//                        .withRequestCode(10)
//                        .withHiddenFiles(true) // Show hidden files and folders
//                        .withTitle("Alegeti fisierul CSV.")
//                        .start();
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
        } else {
            if (emails.length() < 10) {
                makeToast("Lungime email invalida.");
                return false;
            } else {
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
        }
    }

    private void makeToast(String text){
        Toast.makeText(getApplicationContext(), text, Toast.LENGTH_LONG).show();
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.75));
    }
}
