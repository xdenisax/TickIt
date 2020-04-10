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
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.Timestamp;
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
import com.google.firebase.firestore.Source;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    FirebaseFirestore database;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
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

//        getUser(getIntent());
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
        loggedInUser = new User(account.getFamilyName(), account.getGivenName(), null, account.getEmail(), account.getPhotoUrl().toString(), null,null);
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

    private void updateInfoIfNewUser(QueryDocumentSnapshot document, final User loggedInUser) {
        if(document.getString("firstName")==null ){
            database.collection("users").document(document.getId()).update("firstName",loggedInUser.getFirstName());
            database.collection("users").document(document.getId()).update("lastName",loggedInUser.getLastName());
            database.collection("users").document(document.getId()).update("phoneNumber",loggedInUser.getPhoneNumber());
            database.collection("users").document(document.getId()).update("profilePicture",loggedInUser.getProfilePicture());
        }else{
            loggedInUser.setPhoneNumber(document.getString("phoneNumber"));
            loggedInUser.setDepartament(document.getString("department"));
            getMandates(document.getId(), new CallbackArrayListMandates() {
                @Override
                public void callback(ArrayList<Mandate> mandates) {
                    loggedInUser.setMandates(mandates);
                }
            });
        }
    }

    private void getMandates(final String userID, final CallbackArrayListMandates callbackArrayListMandates) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .document(userID)
                .collection("mandates")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        final ArrayList<Mandate> mandatesFromFirestore = new ArrayList<>();
                        if(queryDocumentSnapshots.isEmpty()){
                            mandatesFromFirestore.add(new Mandate());
                            callbackArrayListMandates.callback(mandatesFromFirestore);
                            Log.d("checkRef", "getMandatesNull" + mandatesFromFirestore.toString());
                        }
                        final int[] counter = {0};
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            initializeMandate(document, new CallbackMandate() {
                                @Override
                                public void callback(Mandate mandate) {
                                    mandatesFromFirestore.add(mandate);
                                    counter[0]++;
                                    if(counter[0] == queryDocumentSnapshots.size()){
                                        callbackArrayListMandates.callback(mandatesFromFirestore);
                                        Log.d("checkRef", "getMandates" + mandatesFromFirestore.toString());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void initializeMandate(final QueryDocumentSnapshot document, final CallbackMandate callbackMandate) {
        final Mandate mandate = new Mandate();
        getProjName(document, new CallbackString() {
            @Override
            public void onCallBack(String value) {
                FirebaseFirestore db = FirebaseFirestore.getInstance();
                Date startDate = ((Timestamp) document.get("start_date")).toDate();
                Date endDate = ((Timestamp) document.get("stop_date")).toDate();
                mandate.setProjectName(value);
                mandate.setEndDate(dateFormat.format(endDate));
                mandate.setStartDate(dateFormat.format(startDate));
                mandate.setGrade(Integer.parseInt(document.get("grade").toString()));
                mandate.setPosition(document.getString("position"));
                callbackMandate.callback(mandate);
            }
        });
    }

    private void getProjName(final QueryDocumentSnapshot document, final CallbackString callback) {
        final DocumentReference docRef = document.getDocumentReference("project_name");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String value = documentSnapshot.getString("name");
                    callback.onCallBack(value);
                } else {
                    Log.d("checkRef", "No such document");
                }
            }
        });

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

