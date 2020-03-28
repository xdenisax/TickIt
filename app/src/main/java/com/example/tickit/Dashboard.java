package com.example.tickit;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class Dashboard extends Fragment {
    DatabaseReference myRef;
    GoogleSignInClient mGoogleSignInClient;
    TextView nameTextView;
    TextView emailTextView;
    TextView idTextView;
    ImageButton signOutButton;
    ImageView userPictureImageView;
    ImageButton profileButton;
    GoogleSignInOptions gso;
    FirebaseFirestore db;
    public Dashboard() {}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        Intent intent = getActivity().getIntent();
        User loggedInUser= getUser(intent);

        profileButtonPressed(loggedInUser, profileButton, view);
        signOutButtonPressed(signOutButton, view);

        return view;
    }

    private User getUser(Intent intent) {
        final User user = new User();
        final String userID= getUserIDIntentCheck(intent);

        db= FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                      user.setFirstName(document.getString("firstName"));
                      user.setLastName(document.getString("lastName"));
                      user.setDepartament(document.getString("department"));
                      user.setEmail(userID);
                      user.setPhoneNumber(document.getString("phoneNumber"));
                      user.setProfilePicture(document.getString("profilePicture"));
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

        return user;
    }

    private String getUserIDIntentCheck(Intent intent) {
        String userID="";
        if(intent.getStringExtra("userLoggedInFromMainActivity") != null) {
            userID = intent.getStringExtra("userLoggedInFromMainActivity");
        }else if(intent.getStringExtra("userIDFromProfile") != null) {
            userID = intent.getStringExtra("userIDFromProfile");
        }
        return userID;
    }

    private void signOutButtonPressed(ImageButton signOutButton, View view) {
        signOutButton = (ImageButton) view.findViewById(R.id.signOutButton);
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
    }

    private void profileButtonPressed(final User loggedInUser, ImageButton profileButton, View view) {
        profileButton = (ImageButton) view.findViewById(R.id.profileButton);
        profileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(),Profile.class).putExtra("userLoggedInFromDashboard",loggedInUser));
            }
        });
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener((Activity) getContext(), new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        Toast.makeText(getContext(), "Signed Out Succesfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                });
    }

}
