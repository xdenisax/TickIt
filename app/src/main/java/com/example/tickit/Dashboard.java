package com.example.tickit;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
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
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity {
    DatabaseReference myRef;
    GoogleSignInClient mGoogleSignInClient;
    TextView nameTextView;
    TextView emailTextView;
    TextView idTextView;
    Button signOutButton;
    ImageView userPictureImageView;


    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem membersTab, projectsTab, openTasksTab, myTasksTab;
    public PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        nameTextView = (TextView) findViewById(R.id.userName);
        emailTextView =(TextView) findViewById(R.id.userEmail);
        idTextView = (TextView) findViewById(R.id.userID);
        signOutButton = (Button) findViewById(R.id.signOutButton);
        userPictureImageView = (ImageView) findViewById(R.id.userPicture);

        tabLayout = (TabLayout)findViewById(R.id.tabs);
        membersTab = (TabItem) findViewById(R.id.membersTab);
        projectsTab = (TabItem) findViewById(R.id.projectsTab);
        openTasksTab =(TabItem) findViewById(R.id.openedTasksTab);
        myTasksTab = (TabItem) findViewById(R.id.myTasksTab);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
        pagerAdapter = new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if( tab.getPosition() == 0 ||
                        tab.getPosition() == 1 ||
                        tab.getPosition() == 2 ||
                        tab.getPosition() == 3){
                    pagerAdapter.notifyDataSetChanged();
                }
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(Dashboard.this);
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
                        Glide.with(getApplicationContext()).load(picture).into(userPictureImageView);
                    }else{
                        ((TabLayout) findViewById(R.id.tabs)).setVisibility(View.GONE);
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

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(Dashboard.this);
        dialog.setTitle("Doresti sa te deconectezi la iesirea din aplicatie?");
        dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                signOut();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }
}
