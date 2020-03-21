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
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.bumptech.glide.Glide;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends Fragment {
    DatabaseReference myRef;
    GoogleSignInClient mGoogleSignInClient;
    TextView nameTextView;
    TextView emailTextView;
    TextView idTextView;
    Button signOutButton;
    ImageView userPictureImageView;
    FloatingActionButton profileButton;


    public Dashboard() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_dashboard,container,false);

        nameTextView = (TextView) view.findViewById(R.id.userName);
        emailTextView =(TextView) view.findViewById(R.id.userEmail);
        idTextView = (TextView) view.findViewById(R.id.userID);
        signOutButton = (Button) view.findViewById(R.id.signOutButton);
        userPictureImageView = (ImageView) view.findViewById(R.id.userPicture);
        profileButton = (FloatingActionButton) view.findViewById(R.id.profileFloatingButton);


        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(getContext());

        if(account !=null){
            final String name = account.getDisplayName();
            final String email = account.getEmail();
            final String id = account.getId();
            final Uri picture = account.getPhotoUrl();

            User newUser = new User(name, email);

            myRef = FirebaseDatabase.getInstance().getReference("Users");
//          myRef.child(id).setValue(newUser); //adauga noi useri in bd

            myRef.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    if(dataSnapshot.child(id).exists()){
                        Log.d("exists", id + "exists");
                        nameTextView.setText(name);
                        emailTextView.setText(email);
                        idTextView.setText(id);
                        Glide.with(getContext()).load(picture).into(profileButton);
                        Glide.with(getContext()).load(picture).into(userPictureImageView);
                    }else{
                        ((TabLayout) view.findViewById(R.id.tabs)).setVisibility(View.GONE);
                        nameTextView.setText("Acest cont Gmail nu este asociat unui membru SiSC, prin urmare functionalitatile nu sunt disponibile.");
                    }
                }
                @Override
                public void onCancelled(DatabaseError error) {
                }
            });
        }
        signOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });
        return view;
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener((Activity) getContext(),
                new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                        Toast.makeText(getContext(), "Signed Out Succesfully", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getContext(), MainActivity.class));
                        getActivity().finish();
                    }
                });
    }

}
