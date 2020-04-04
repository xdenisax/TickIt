package com.example.tickit;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.firestore.FirebaseFirestore;

public class Profile extends AppCompatActivity {
    int REQUEST_CODE_MEMBERS_LIST = 1;

    TextView name;
    TextView phoneNumber;
    TextView email;
    ImageView profilePicture;
    ImageButton backButton, editPhoneNumberButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = MainActivity.getLoggedInUser();
        fillWithInfo(user, true);
        manageIntent(getIntent());
        backButtonPressed(backButton);
        editPhoneNumberButtonPressed(editPhoneNumberButton);
        Toast.makeText(getApplicationContext(),user.getMandates().toString(),Toast.LENGTH_LONG).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                user.setPhoneNumber( data.getStringExtra("phoneNumberModified"));
                Log.d("fragmentContainer", "profile"+user);
                MainActivity.setLoggedInUser(user);
                FirebaseFirestore database= FirebaseFirestore.getInstance();
                database.collection("users").document(user.getEmail()).update("phoneNumber", user.getPhoneNumber());
                fillWithInfo(user, true);
                Toast.makeText(getApplicationContext(), R.string.numar_de_telefon_actualiza + data.getStringExtra("phoneNumberModified"),Toast.LENGTH_LONG).show();
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), R.string.numar_de_telefon_nu_a_fost_actualizat,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("userFromMembersList")!= null) {
            fillWithInfo((User) intent.getParcelableExtra("userFromMembersList"),false);
            user = (User) intent.getParcelableExtra("userFromMembersList");
        }
    }

    private void fillWithInfo(User user, boolean isPersonalProfile) {
        name= (TextView) findViewById(R.id.nameTextView);
        phoneNumber= (TextView) findViewById(R.id.phoneNumberTextView);
        email = (TextView) findViewById(R.id.emailTextView);
        profilePicture= (ImageView) findViewById(R.id.profilePicture);

        name.setText( user.getFirstName()+ " " +user.getLastName());
        email.setText(user.getEmail());
        if(user.getPhoneNumber() == null){
            phoneNumber.setText("Telefon neactualizat.");
        }else{
            phoneNumber.setText(user.getPhoneNumber());
            if(!isPersonalProfile){
                setActionOnPhoneNumber(user.getPhoneNumber());
            }
        }
        if(user.getProfilePicture()!=null){
            Glide.with(getApplicationContext()).load(Uri.parse(user.getProfilePicture())).apply(RequestOptions.circleCropTransform()).into(profilePicture);
        }
        if(!isPersonalProfile){
            findViewById(R.id.editPhoneNumberProfileActivity).setVisibility(View.GONE);
            setActionOnEmail(user.getEmail());
        }
    }

    private void setActionOnEmail(final String recipient) {
        email = (TextView) findViewById(R.id.emailTextView);
        email.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setData(Uri.parse("mailto:"));
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_EMAIL, new String[]{recipient});
                intent.putExtra(Intent.EXTRA_TEXT, "Sent with TickIt.");

                startActivity(Intent.createChooser(intent,"Alegeti clientul de email."));
            }
        });
    }

    private void setActionOnPhoneNumber(final String number) {
        phoneNumber= (TextView) findViewById(R.id.phoneNumberTextView);
        phoneNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:"+number)));
            }
        });
    }

    private void editPhoneNumberButtonPressed(ImageButton editButton) {
        editPhoneNumberButton = (ImageButton) findViewById(R.id.editPhoneNumberProfileActivity);
        editPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), EditPopUp.class),1);
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


}
