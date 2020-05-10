package com.example.tickit.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.PopUps.EditPopUp;
import com.example.tickit.PopUps.HistoryPopUp;
import com.example.tickit.R;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.commons.lang3.RandomStringUtils;

public class Profile extends AppCompatActivity {
    int REQUEST_CODE_MEMBERS_LIST = 1;

    TextView name;
    TextView phoneNumber;
    TextView email, department;
    ImageView profilePicture;
    ImageButton backButton, editPhoneNumberButton, upgradeAsBoard;
    Button historyButton;
    User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        user = MainActivity.getLoggedInUser();
        assignViews();
        fillWithInfo( true);
        manageIntent(getIntent());
        historyButtonPressed();
        backButtonPressed(backButton);
        editPhoneNumberButtonPressed(editPhoneNumberButton);
        upgradeAsBoardPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)     {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 1){
            if(resultCode == Activity.RESULT_OK){
                user.setPhoneNumber( data.getStringExtra("phoneNumberModified"));
                MainActivity.setLoggedInUser(user);
                UserDatabaseCalls.updatePhoneNumber(user.getPhoneNumber());
                fillWithInfo(true);
                Toast.makeText(getApplicationContext(), getString(R.string.numar_de_telefon_actualiza),Toast.LENGTH_LONG).show();
            }
            if(resultCode == Activity.RESULT_CANCELED){
                Toast.makeText(getApplicationContext(), R.string.numar_de_telefon_nu_a_fost_actualizat,Toast.LENGTH_LONG).show();
            }
        }
    }

    private void assignViews() {
        backButton = (ImageButton) findViewById(R.id.backButtonProfileActivity);
        editPhoneNumberButton = (ImageButton) findViewById(R.id.editPhoneNumberProfileActivity);
        historyButton = (Button) findViewById(R.id.memberHistoryButton);
        name= (TextView) findViewById(R.id.nameTextView);
        phoneNumber= (TextView) findViewById(R.id.phoneNumberTextView);
        email = (TextView) findViewById(R.id.emailTextView);
        profilePicture= (ImageView) findViewById(R.id.profilePicture);
        department= (TextView) findViewById(R.id.departmentTextView);
        upgradeAsBoard = (ImageButton) findViewById(R.id.upgradeAsBoard);
        upgradeAsBoard.setVisibility(View.GONE);
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("userFromMembersList")!= null) {
            user = (User) intent.getParcelableExtra("userFromMembersList");
            fillWithInfo(false);
        }
        if(intent.getParcelableExtra("memberFromEditionProfile")!= null) {
            user = (User) intent.getParcelableExtra("memberFromEditionProfile");
            fillWithInfo(false);
        }
        if(intent.getParcelableExtra("memberFromTaskProfile")!=null){
            user = (User) intent.getParcelableExtra("memberFromTaskProfile");
            fillWithInfo(false);
        }
    }

    private void fillWithInfo( boolean isPersonalProfile) {
        if(user.getLastName()!=null || user.getFirstName()!= null){
            Glide.with(getApplicationContext()).load(user.getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(profilePicture);
            name.setText(user.getLastName() + " " + user.getFirstName());
        }else{
            if(user.getProfilePicture()!=null){
                Glide.with(getApplicationContext()).load(Uri.parse(user.getProfilePicture())).apply(RequestOptions.circleCropTransform()).into(profilePicture);
            }else{
                Glide.with(getApplicationContext()).load(R.drawable.account_cyan).apply(RequestOptions.centerInsideTransform()).into(profilePicture);
            }
            name.setText(user.getEmail().substring(0,user.getEmail().length()-10));
        }

        department.setText(user.getDepartament());
        email.setText(user.getEmail());
        if(user.getPhoneNumber() == null){
            phoneNumber.setText("Telefon neactualizat.");
        }else{
            phoneNumber.setText(user.getPhoneNumber());
            if(!isPersonalProfile){
                setActionOnPhoneNumber(user.getPhoneNumber());
            }
        }
        if(!isPersonalProfile){
            findViewById(R.id.editPhoneNumberProfileActivity).setVisibility(View.GONE);
            setActionOnEmail(user.getEmail());
        }
    }

    private void setActionOnEmail(final String recipient) {
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
        if(number.length()==10){
            phoneNumber.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(Intent.ACTION_DIAL).setData(Uri.parse("tel:"+number)));
                }
            });
        }
    }

    private void editPhoneNumberButtonPressed(ImageButton editButton) {
        editPhoneNumberButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), EditPopUp.class),1);
            }
        });
    }

    private void backButtonPressed(ImageButton backButton) {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void historyButtonPressed() {
        historyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (user.getMandates()==null) {
                    Toast.makeText(getApplicationContext(), "Utilizatorul nu are nicio activitate pana in acest moment.", Toast.LENGTH_LONG).show();
                } else {
                    if(user.getMandates().size()<1){
                        Toast.makeText(getApplicationContext(), "Utilizatorul nu are nicio activitate pana in acest moment.", Toast.LENGTH_LONG).show();
                    }else{
                        startActivity(new Intent(getApplicationContext(), HistoryPopUp.class).putExtra("memberEmailFromProfile", user.getEmail()));
                    }
                }
            }
        });
    }

    private void upgradeAsBoardPressed() {
        if(MainActivity.getUserGrade()<1){
            upgradeAsBoard.setVisibility(View.VISIBLE);
            upgradeAsBoard.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    launchBoardAlertDialog();
                }
            });
        }
    }

    private void launchBoardAlertDialog() {
        final String[] birouri = new String[2]; birouri[0] = "Biroul de conducere"; birouri[1] = "Biroul executiv";

        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Alegeti ramura Biroului.");
        dialogBuilder.setSingleChoiceItems(birouri, 1, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                launchFunctionAlertDialog(which);
                dialog.dismiss();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void launchFunctionAlertDialog(int which) {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Alegeti functia.");
        if (which == 0) {
            dialogBuilder.setSingleChoiceItems( getResources().getStringArray(R.array.BCFunctions), 1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    launchConfirmationAlertDialog(which, 0);
                    dialog.dismiss();
                }
            });
        }
        if (which == 1) {
            dialogBuilder.setSingleChoiceItems( getResources().getStringArray(R.array.BEFunctions), 1, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    launchConfirmationAlertDialog(which, 1);
                    dialog.dismiss();
                }
            });
        }
        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }

    private void launchConfirmationAlertDialog(int whichFunction, int whichBirou) {
        final String key = RandomStringUtils.randomAlphanumeric(5);
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
        dialogBuilder.setTitle("Confirmare");
        if(whichBirou==0){
            dialogBuilder.setMessage("Pentru a confirma intarea in mandatul de "+ getResources().getStringArray(R.array.BCFunctions)[whichBirou] +" a membrului " + user.getLastName()  + " " + user.getFirstName() + " completati textul "+ key+" in campul de mai jos." );
        }
        if(whichBirou==1){
            dialogBuilder.setMessage("Pentru a confirma intarea in mandatul de "+ getResources().getStringArray(R.array.BEFunctions)[whichBirou] +" a membrului " + user.getLastName()  + " " + user.getFirstName() + " completati textul "+ key+" in campul de mai jos." );
        }

        final EditText keyInput = new EditText(this);
        dialogBuilder.setView(keyInput);

        dialogBuilder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(key.equals(keyInput.getText().toString())){
                    Toast.makeText(getApplicationContext(), "S-a add", Toast.LENGTH_LONG).show();
                }
                if(!key.equals(keyInput.getText().toString())){
                    Toast.makeText(getApplicationContext(), "nu e corect ", Toast.LENGTH_LONG).show();
                }
            }
        });
        dialogBuilder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (key.equals(keyInput.getText().toString())) {
                    Toast.makeText(getApplicationContext(), "Nu s-a add", Toast.LENGTH_LONG).show();
                }
                dialog.cancel();
            }
        });

        AlertDialog dialog = dialogBuilder.create();
        dialog.show();
    }


}
