package com.example.tickit.Activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackMapStrinnMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    private static Context context;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    private static User loggedInUser;
    private static int userGrade=4;
    private static Map<String, Mandate> currentMandates;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this;

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
        UserDatabaseCalls.getUserByID(loggedInUser.getEmail(), new CallbackUser() {
            @Override
            public void callback(User user) {
                if(user==null){
                    Toast.makeText(getApplicationContext(), "Autentificare nepermisa persoanelor neautorizate.", Toast.LENGTH_LONG).show();
                    signOut();
                }else{
                    if(user.getFirstName()==null ){
                        UserDatabaseCalls.updateUserInfoIfNew(loggedInUser, new CallbackBoolean() {
                            @Override
                            public void callback(Boolean bool) {
                                startActivity(new Intent(getApplicationContext(), activity).putExtra("userLoggedInFromMainActivity", loggedInUser.getEmail()));
                            }

                        });
                    }else {
                        loggedInUser=user;
                        setUsersCurrentMandates(new CallbackMapStrinnMandate() {
                            @Override
                            public void callback(Map<String, Mandate> map) {
                                startActivity(new Intent(getApplicationContext(), activity).putExtra("userLoggedInFromMainActivity", loggedInUser.getEmail()));
                            }
                        });
                    }
                }
            }
        });
    }

    private void setUsersCurrentMandates(final CallbackMapStrinnMandate callbackMapStringInteger) {
        userGrade=4;
        if(loggedInUser.getMandates()!=null) {
            currentMandates = new HashMap<>();
            for (final Mandate mandate : loggedInUser.getMandates()) {
                ProjectDatabaseCalls.getProjectName(mandate.getProject_name(), new CallbackString() {
                    @Override
                    public void onCallBack(String value) {
                        if (checkIfInMandate(System.currentTimeMillis(), mandate.getStop_date())) {
                            currentMandates.put(value+mandate.getStop_date(), mandate);
                            if (userGrade > mandate.getGrade()) {
                                userGrade = mandate.getGrade();
                            }
                        }
                        if (loggedInUser.getMandates().indexOf(mandate) == loggedInUser.getMandates().size() - 1) {
                            callbackMapStringInteger.callback(currentMandates);
                        }
                    }
                });
            }
        }else{
            callbackMapStringInteger.callback(null);
        }
    }

    public static Map<String, Mandate> getCurrentMandates() {
        return currentMandates;
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

    public static Context getContext() {
        return context;
    }

    public static ArrayList<DocumentReference>getMandateProjects(){
        ArrayList<DocumentReference> projects = new ArrayList<>();
        if(loggedInUser.getMandates()!=null){
            for(Mandate mandate : loggedInUser.getMandates()){
                if(mandate.getProject_name().getPath().equals("bebc/be-bc")){
                    Log.d("projects", mandate.getProject_name().getPath());
                    return null;
                }
                projects.add(mandate.getProject_name());
            }
        }
        return projects;
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