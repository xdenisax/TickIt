package com.example.tickit.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Project;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.R;

public class AddProject extends AppCompatActivity {

    private static final int REQUEST_PICK_IMAGE=1;
    private Button addImage, addProject;
    private EditText projectName, projectDescription;
    private TextView imagePath;
    private Uri imageUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_project);

        setMetrics();
        assignViews();
        addImagePressed();
        addProjectPressed();

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_PICK_IMAGE && resultCode == RESULT_OK && data!=null && data.getData()!=null){
            imageUri=data.getData();
            imagePath.setText(imageUri.toString());
        }
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.7));
    }

    private void assignViews() {
        addImage = (Button) findViewById(R.id.projectAddImage);
        addProject = (Button) findViewById(R.id.saveProject);
        projectName = (EditText) findViewById(R.id.projectAddName);
        projectDescription = (EditText) findViewById(R.id.projectAddDescription);
        imagePath = (TextView) findViewById(R.id.imagePathAddProject);
    }

    private void addImagePressed() {
        addImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
    }

    private void addProjectPressed() {
        addProject.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()) {
                    uploadImage();
                    ProjectDatabaseCalls.addProject(buildProjectFromForm(), new CallbackBoolean() {
                        @Override
                        public void callback(Boolean bool) {
                            if(bool){
                                Toast.makeText(getApplicationContext(), "S-a adaugat proiectul cu succes.", Toast.LENGTH_LONG).show();
                                    }else{
                                Toast.makeText(getApplicationContext(), "Nu s-a adaugat proiectul.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void uploadImage() {
        ProjectDatabaseCalls.uploadPhoto(imageUri, projectName.getText().toString().replace(" ",""), new CallbackBoolean() {
            @Override
            public void callback(Boolean bool) {
                if(bool){
                    Toast.makeText(getApplicationContext(), "S-a incarcat imaginea imaginea. Mai incercati.", Toast.LENGTH_LONG).show();
                }else{
                    Toast.makeText(getApplicationContext(), "Nu s-a putut incarca imaginea. Mai incercati.", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private Project buildProjectFromForm() {
        return new Project(
                projectName.getText().toString().replace(" ", ""),
                projectName.getText().toString(),
                projectDescription.getText().toString(),
                projectName.getText().toString().replace(" ", ""),
                null
        );
    }

    private boolean validation() {
        if(projectName.getText().toString().length()<4){
            Toast.makeText(getApplicationContext(), "Numele proiectului trebuie sa aiba minim 5 caractere.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(projectDescription.getText().toString().length()<10){
            Toast.makeText(getApplicationContext(), "Descrierea proiectului trebuie sa aiba minim 10 caractere.", Toast.LENGTH_LONG).show();
            return false;
        }
        if(imageUri==null){
            Toast.makeText(getApplicationContext(), "Nu ati incarcat imaginea proiectului. ", Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    private void openFileChooser() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, REQUEST_PICK_IMAGE);
    }


}
