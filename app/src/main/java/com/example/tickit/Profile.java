package com.example.tickit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

public class Profile extends AppCompatActivity {

    TextView name;
    TextView phoneNumber;
    TextView email;
    ImageView profilePicture;
    ImageButton backButton, editPhoneNumberButton;
    private static final int  PHONE_NUMBER_EDIT = 1;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = getIntent().getParcelableExtra("userLoggedInFromDashboard");
        fillWithInfo(user);
        backButtonPressed(backButton);
        editPhoneNumberButtonPressed(editPhoneNumberButton);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == PHONE_NUMBER_EDIT){
            if(resultCode == Activity.RESULT_OK){
                user.setPhoneNumber( data.getStringExtra("phoneNumberModified"));
                fillWithInfo(user);
                Toast.makeText(getApplicationContext(), R.string.numar_de_telefon_actualiza,Toast.LENGTH_LONG).show();

            }
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), R.string.numar_de_telefon_nu_a_fost_actualizat,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void editPhoneNumberButtonPressed(ImageButton editButton) {
        editPhoneNumberButton = (ImageButton) findViewById(R.id.editPhoneNumberProfileActivity);
        editPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), EditPopUp.class),PHONE_NUMBER_EDIT);
            }
        });
    }

    private void backButtonPressed(ImageButton backButton) {
        backButton = (ImageButton) findViewById(R.id.backButtonProfileActivity);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(),FragmentsContainer.class));
            }
        });
    }

    private void fillWithInfo(User user) {
        name= (TextView) findViewById(R.id.nameTextView);
        phoneNumber= (TextView) findViewById(R.id.phoneNumberTextView);
        email = (TextView) findViewById(R.id.emailTextView);
        profilePicture = (ImageView) findViewById(R.id.profilePicture);

        name.setText( user.getFirstName()+ " " +user.getLastName());
        email.setText(user.getEmail());
        if(user.getPhoneNumber() == null){
            phoneNumber.setText(R.string.numarul_de_telefon_nu_a_fost_adaugat);
        }else{
            phoneNumber.setText(user.getPhoneNumber());
        }
        Glide.with(getApplicationContext()).load(Uri.parse(user.getProfilePicture())).into(profilePicture);
    }
}
