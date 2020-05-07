package com.example.tickit.Activities;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.User;
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

import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 9001;
    SignInButton googleSignInButton;
    GoogleSignInClient mGoogleSignInClient;
    GoogleSignInAccount account;
    GoogleSignInOptions gso;
    private static User loggedInUser;
    //private static int userGrade=4;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ((GlobalVariables) getApplicationContext()).setContext(this);

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
                        UserDatabaseCalls.updateUserInfoIfNew(loggedInUser);
                    }else {
                        ((GlobalVariables) getApplicationContext()).setLoggedInUser(user);
                        loggedInUser=user;
                        setUserGrade();
                    }
                    startActivity(new Intent(getApplicationContext(), activity).putExtra("userLoggedInFromMainActivity", loggedInUser.getEmail()));
                }
            }
        });
    }

    private void setUserGrade() {
        ((GlobalVariables) getApplicationContext()).setUserGrade(4);
        for(Mandate mandate: loggedInUser.getMandates()){
            if(mandate.getPosition()!=null){
                if(checkIfInMandate(System.currentTimeMillis(), mandate.getStop_date()) && ((GlobalVariables) getApplicationContext()).getUserGrade()>mandate.getGrade()){
                    ((GlobalVariables) getApplicationContext()).setUserGrade(mandate.getGrade());
                }
            }
        }
    }

    private boolean checkIfInMandate(long currentTimeMillis,Date endDate) {
            Date currentDate = new Date();
            currentDate.setTime(currentTimeMillis);

            return currentDate.before(endDate);
    }

//    public static User getLoggedInUser() {
//        return loggedInUser;
//    }
//
//    public static void setLoggedInUser(User user) {
//        loggedInUser=user;
//    }
//
//    public static int getUserGrade() {
//        return userGrade;
//    }
//
//    public static Context getContext() {
//        return context;
//    }

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