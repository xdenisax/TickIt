package com.example.tickit;

import android.content.Intent;
import android.os.AsyncTask;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Queue;

public abstract class UserDatabaseRequests  {
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<User> users2 = new ArrayList<>();
    static boolean doneFetching =false;
    static int dbSize;

    public  static void theRealGetAllUsers(){
        getAllUsersMiddleWare();
    }

    public static  void getAllUsers(){
        dbSize= users.size();
        Log.d("database", "cred"+ users.toString());
        Log.d("database", "cred"+ dbSize);
    }

    public static void getAllUsersMiddleWare(){
        FirebaseFirestore db = initializeDatabase();
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    for (DocumentSnapshot d : list) {
                        User user = d.toObject(User.class);
                        UserDatabaseRequests.users.add(user);
                    }
                }
                getAllUsers();
            }
        });
    }

    private static FirebaseFirestore initializeDatabase() {
        return  FirebaseFirestore.getInstance();
    }
//    private static int userCount() {
//        FirebaseFirestore db = initializeDatabase();
//        db.collection("users").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//            @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            for (DocumentSnapshot document : task.getResult()) {
//                                count++;
//                            }
//                        } else {
//                            Log.d("database", "Error getting documents: ", task.getException());
//                        }
//                    }
//                });
//        return count;
//    }
    private static void initializeUser(User user, DocumentSnapshot document) {
        user.setFirstName(document.getString("firstName"));
        user.setLastName(document.getString("lastName"));
        user.setDepartament(document.getString("department"));
        user.setEmail(document.getId());
        user.setPhoneNumber(document.getString("phoneNumber"));
        user.setProfilePicture(document.getString("profilePicture"));
    }
    public static User getUser(Intent intent) {
        final User user = new User();
        final String userID= getUserIDIntentCheck(intent);

        FirebaseFirestore db= FirebaseFirestore.getInstance();
        final DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        initializeUser(user, document);
                    }
                } else {
                    Log.d("Firestore", "get failed with ", task.getException());
                }
            }
        });

        return user;
    }
    public static String getUserIDIntentCheck(Intent intent) {
        String userID="";
        if(intent.getStringExtra("userLoggedInFromMainActivity") != null) {
            userID = intent.getStringExtra("userLoggedInFromMainActivity");
        }else if(intent.getStringExtra("userIDFromProfile") != null) {
            userID = intent.getStringExtra("userIDFromProfile");
        }
        return userID;
    }

}
