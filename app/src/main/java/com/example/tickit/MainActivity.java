package com.example.tickit;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    FirebaseFirestore database;
    private static User loggedInUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        googleSignInButton = (SignInButton) findViewById(R.id.googleSignInButton);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        account = GoogleSignIn.getLastSignedInAccount(this);

        googleSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        getUser(getIntent());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

         if (requestCode == RC_SIGN_IN) {
             if (resultCode == RESULT_OK) {
                 Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                 try {
                       GoogleSignInAccount account = task.getResult(ApiException.class);
                       startActivityIfMemberIsSiSC(FragmentsContainer.class,account);
                 } catch (ApiException e) {
                     Log.d("Google", "Google sign in failed", e);
                     Toast.makeText(getApplicationContext(), "LogIn failed", Toast.LENGTH_LONG).show();
                 }
             }
         }
    }

    @Override
    public void onStart() {
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        if(account != null) {
            startActivityIfMemberIsSiSC(FragmentsContainer.class, account);
        }
        super.onStart();
    }

    private void startActivityIfMemberIsSiSC(final Class activity, GoogleSignInAccount account){
        loggedInUser = new User(account.getFamilyName(), account.getGivenName(), null, account.getEmail(), account.getPhotoUrl().toString(), null);
        database= FirebaseFirestore.getInstance();
        database.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    if(isUserSiSCMember(task, loggedInUser)){
                        Intent toClassIntent = new Intent(getApplicationContext(), activity);
                        toClassIntent.putExtra("userLoggedInFromMainActivity",loggedInUser.getEmail());
                        startActivity(toClassIntent);
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Autentificare nepermisa persoanelor neautorizate.", Toast.LENGTH_LONG).show();
                        signOut();
                    }
                }else{
                    Log.d("Google ", "Error getting documents.", task.getException());
                }
            }

        });
    }

    private boolean isUserSiSCMember(Task<QuerySnapshot> task, User loggedInUser) {
        boolean isMember=false;
        for (QueryDocumentSnapshot document : task.getResult()) {
            if(loggedInUser.getEmail().equals(document.getId())) {
                isMember = true;
                updateInfoIfNewUser(document, loggedInUser);
            }
        }
        return isMember;
    }

    private void updateInfoIfNewUser(QueryDocumentSnapshot document, User loggedInUser) {
        if(document.getString("firstName")==null ){
            database.collection("users").document(document.getId()).update("firstName",loggedInUser.getFirstName());
            database.collection("users").document(document.getId()).update("lastName",loggedInUser.getLastName());
            database.collection("users").document(document.getId()).update("phoneNumber",loggedInUser.getPhoneNumber());
            database.collection("users").document(document.getId()).update("profilePicture",loggedInUser.getProfilePicture());
        }else{
            loggedInUser.setPhoneNumber(document.getString("phoneNumber"));
            loggedInUser.setDepartament(document.getString("department"));
        }
    }

    private  FirebaseFirestore initializeDatabase() {
        return  FirebaseFirestore.getInstance();
    }

    private void initializeUser(User user, DocumentSnapshot document) {
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setDepartament(document.getString("department"));
        user.setEmail(document.getId());
        user.setPhoneNumber(document.getString("phoneNumber"));
        user.setProfilePicture(document.getString("profilePicture"));
    }

    public void getUser(Intent intent) {
        final String userID= intent.getStringExtra("userLoggedInFromMainActivity");
        if(userID != null) {
            FirebaseFirestore db= initializeDatabase();
            final DocumentReference docRef = db.collection("users").document(userID);
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            initializeUser(loggedInUser, document);
                        }
                    } else {
                        Log.d("Firestore", "get failed with ", task.getException());
                    }
                }
            });
        }else {
            Log.d("FragmentContainer", "Tried to retrieve user from different intent than mainActivity one.");
        }

    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User loggedUser) {
        loggedInUser = loggedUser;
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    private void signOut(){
        mGoogleSignInClient.signOut().addOnCompleteListener(this, new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete( Task<Void> task) {
                    }
                });
    }
}

