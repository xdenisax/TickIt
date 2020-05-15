package com.example.tickit.Fragments;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Activities.Profile;
import com.example.tickit.R;
import com.example.tickit.Classes.User;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.log4j.chainsaw.Main;

public class Dashboard extends Fragment  {
    private int REQUEST_CODE_DASHBOARD = 3;
    private DatabaseReference myRef;
    private GoogleSignInClient mGoogleSignInClient;
    private TextView nameTextView;
    private ImageButton signOutButton, profileButton;
    private ImageView userPictureImageView;
    private GoogleSignInOptions gso;
    private FirebaseFirestore db;
    private View view;
    private User loggedInUser= MainActivity.getLoggedInUser();

    public Dashboard() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_dashboard,container,false);
        assignViews();

        Intent intent = getActivity().getIntent();

        profileButtonPressed(loggedInUser, profileButton, view);
        signOutButtonPressed(signOutButton, view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        setGoogleUp();
    }

    private void assignViews() {
        signOutButton = (ImageButton) view.findViewById(R.id.signOutButton);
        profileButton = (ImageButton) view.findViewById(R.id.profileButton);
        nameTextView = (TextView) view.findViewById(R.id.userName);
    }

    private void setGoogleUp() {
        if(MainActivity.getContext()!=null){
            gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
            mGoogleSignInClient = GoogleSignIn.getClient(MainActivity.getContext(), gso);
        }
        String text= MainActivity.getLoggedInUser().getFirstName() + "!";
        nameTextView.setText(text);
    }

    private void signOutButtonPressed(ImageButton signOutButton, View view) {
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void profileButtonPressed(User loggedInUser, ImageButton profileButton, View view) {
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), Profile.class));
            }
        });
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        Toast.makeText(getContext(), R.string.pe_curand, Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                });
    }

}
