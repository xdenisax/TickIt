package com.example.tickit;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    private static User loggedInUser;
    private static int userGrade=4;

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

    private void startActivityIfMemberIsSiSC(final Class activity, GoogleSignInAccount account) {
        loggedInUser = new User(account.getFamilyName(), account.getGivenName(), null, account.getEmail(), String.valueOf(account.getPhotoUrl()), null, null); //account.getPhotoUrl().toString()
        FirebaseFirestore
                .getInstance()
                .collection("users")
                .document(loggedInUser.getEmail())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful()) {
                            if (task.getResult().exists()) {
                                updateInfoIfNewUser(task.getResult(), loggedInUser, new CallbackBoolean() {
                                    @Override
                                    public void callback(Boolean bool) {
                                        Intent toClassIntent = new Intent(getApplicationContext(), activity);
                                        toClassIntent.putExtra("userLoggedInFromMainActivity", loggedInUser.getEmail());
                                        startActivity(toClassIntent);
                                    }
                                });
                            } else {
                                Toast.makeText(getApplicationContext(), "Autentificare nepermisa persoanelor neautorizate.", Toast.LENGTH_LONG).show();
                                signOut();
                            }
                        } else {
                            Log.d("Google ", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void updateInfoIfNewUser(final DocumentSnapshot document, final User loggedInUser, final CallbackBoolean callbackBoolean) {
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        if(document.getString("firstName")==null ){
            database.collection("users").document(document.getId()).update("firstName",loggedInUser.getFirstName());
            database.collection("users").document(document.getId()).update("lastName",loggedInUser.getLastName());
            database.collection("users").document(document.getId()).update("phoneNumber",loggedInUser.getPhoneNumber());
            database.collection("users").document(document.getId()).update("profilePicture",loggedInUser.getProfilePicture());
            //trebuie creata colectia de mandate
        }else {
            getMandates(document.getId(), new CallbackArrayListMandates() {
                @Override
                public void callback(ArrayList<Mandate> mandates) {
                    loggedInUser.setMandates(mandates);
                    callbackBoolean.callback(true);
                    loggedInUser.setPhoneNumber(document.getString("phoneNumber"));
                    loggedInUser.setDepartament(document.getString("departament"));
                }
            });
        }
    }

    private void getMandates(final String userID, final CallbackArrayListMandates callbackArrayListMandates) {
        FirebaseFirestore
                .getInstance()
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
                Date startDate = ((Timestamp) document.get("start_date")).toDate();
                Date endDate = ((Timestamp) document.get("stop_date")).toDate();
                mandate.setProjectName(value);
                mandate.setEndDate(dateFormat.format(endDate));
                mandate.setStartDate(dateFormat.format(startDate));
                mandate.setGrade(Integer.parseInt(String.valueOf(document.get("grade"))));
                mandate.setPosition(document.getString("position"));

                if(checkIfInMandate(System.currentTimeMillis(), endDate) && (userGrade>mandate.getGrade())){
                    userGrade=mandate.getGrade();
                }

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
                    callback.onCallBack(documentSnapshot.getString("name"));
                } else {
                    Log.d("checkRef", "No such document");
                }
            }
        });
    }

    private boolean checkIfInMandate(long currentTimeMillis, Date endDate) {
        Date currentDate = new Date();
        currentDate.setTime(currentTimeMillis);
        return currentDate.before(endDate);
    }

    public static User getLoggedInUser() {
        return loggedInUser;
    }

    public static void setLoggedInUser(User user) {
        loggedInUser=user;
    }

    public static int getUserGrade() {
        return userGrade;
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

