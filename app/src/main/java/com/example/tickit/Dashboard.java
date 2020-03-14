package com.example.tickit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

public class Dashboard extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    TextView nameTextView;
    TextView emailTextView;
    TextView idTextView;
    Button signOutButton;
    ImageView userPictureImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        nameTextView = (TextView) findViewById(R.id.userName);
        emailTextView =(TextView) findViewById(R.id.userEmail);
        idTextView = (TextView) findViewById(R.id.userID);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        userPictureImageView = (ImageView) findViewById(R.id.userPicture);

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();

        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Dashboard.this);
        if(account !=null){
            String name = account.getDisplayName();
            String email = account.getEmail();
            String id = account.getId();
            Uri picture = account.getPhotoUrl();

            nameTextView.setText(name);
            emailTextView.setText(email);
            idTextView.setText(id);
            Glide.with(this).load(picture).into(userPictureImageView);
        }

        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this,
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        Toast.makeText(getApplicationContext(), "Signed Out Succesfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                    }
                });
    }
}
