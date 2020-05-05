package com.example.tickit.DataBaseCalls;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListUser;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Callbacks.CallbackDocumentReference;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.User;
import com.example.tickit.Utils.DateProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.common.hash.BloomFilter;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.security.auth.callback.Callback;

public class UserDatabaseCalls {
    private static final String COLLECTION_NAME="users";
    private static FirebaseFirestore instance = FirebaseFirestore.getInstance();

    public static void addListOfMembersToDataBase(String[] emailsArray, String department) {
        for(String member:emailsArray){
            member = member.replace(" ", "");
            User newUser = new User(null, null, null, member, null, department, new ArrayList<Mandate>());
            instance.collection("users").document(member).set(newUser);
        }
    }

    public static void getUsers(final CallbackArrayListUser callbackArrayListUser) {
        instance
                .collection("users")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    final List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    final ArrayList<User> users = new ArrayList<>();
                    for (final DocumentSnapshot d : list) {
                        getUser(d, new CallbackUser() {
                            @Override
                            public void callback(final User userFromFirestore) {
                                if (!userFromFirestore.getEmail().equals(MainActivity.getLoggedInUser().getEmail())) {
                                    users.add(userFromFirestore);
                                    if(users.size()==list.size()-1){
                                        callbackArrayListUser.callback(users);
                                    }
                                }
                            }
                        });
                    }
                }
            }
        });
    }

    public static void getUser(final DocumentSnapshot documentSnapshot, final CallbackUser callbackUser){
        getMandates(documentSnapshot.getId(), new CallbackArrayListMandates() {
            @Override
            public void callback(ArrayList<Mandate> mandates) {
                User user = documentSnapshot.toObject(User.class);
                user.setEmail(documentSnapshot.getId());
                user.setMandates(mandates);
                callbackUser.callback(user);
            }
        });
    }

    public static void getUser(final DocumentReference documentReference, final CallbackUser callbackUser){
        getMandates(documentReference.getId(), new CallbackArrayListMandates() {
            @Override
            public void callback(final ArrayList<Mandate> mandates) {
                documentReference.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                User user = task.getResult().toObject(User.class);
                                user.setEmail(task.getResult().getId());
                                user.setMandates(mandates);
                                callbackUser.callback(user);
                            }else{
                                callbackUser.callback(null);
                            }

                        }
                    }
                });
            }
        });
    }

    public static void getUserByID(final String userID, final CallbackUser callbackUser) {
        instance
                .collection("users")
                .document(userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<DocumentSnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult().exists()){
                                getMandates(userID, new CallbackArrayListMandates() {
                                    @Override
                                    public void callback(ArrayList<Mandate> mandates) {
                                        User user= task.getResult().toObject(User.class);
                                        user.setEmail(userID);
                                        user.setMandates(mandates);
                                        callbackUser.callback(user);
                                    }
                                });
                            }else{
                                callbackUser.callback(null);
                            }
                        }
                    }
                });

    }

    public static void getMandates(final String userID, final CallbackArrayListMandates callbackArrayListMandates) {
        instance
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
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            initializeMandate(document, new CallbackMandate() {
                                @Override
                                public void callback(Mandate mandate) {
                                    mandatesFromFirestore.add(mandate);
                                    if(mandatesFromFirestore.size() == queryDocumentSnapshots.size()){
                                        callbackArrayListMandates.callback(mandatesFromFirestore);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public static void getUserReference(User user, final CallbackDocumentReference callbackDocumentReference){
       instance
               .collection(COLLECTION_NAME)
               .document(user.getEmail())
               .get()
               .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        callbackDocumentReference.callback(task.getResult().getReference());
                    }
                });
    }

    public static void initializeMandate(final QueryDocumentSnapshot document, final CallbackMandate callbackMandate) {
        final Mandate mandate = new Mandate();
        ProjectDatabaseCalls.getProjectName(document.getDocumentReference("project_name"), new CallbackString() {
            @Override
            public void onCallBack(String value) {
                Date startDate = ((Timestamp) document.get("start_date")).toDate();
                Date endDate = ((Timestamp) document.get("stop_date")).toDate();

                mandate.setProjectName(value);
                mandate.setEndDate(DateProcessing.dateFormat.format(endDate));
                mandate.setStartDate(DateProcessing.dateFormat.format(startDate));
                mandate.setGrade(Integer.parseInt(String.valueOf(document.get("grade"))));
                mandate.setPosition(document.getString("position"));

                callbackMandate.callback(mandate);
            }
        });
    }

    public static void updateUserInfoIfNew(User loggedInUser) {
        instance.collection("users").document(loggedInUser.getEmail()).update("firstName",loggedInUser.getFirstName());
        instance.collection("users").document(loggedInUser.getEmail()).update("lastName",loggedInUser.getLastName());
        instance.collection("users").document(loggedInUser.getEmail()).update("phoneNumber",loggedInUser.getPhoneNumber());
        instance.collection("users").document(loggedInUser.getEmail()).update("profilePicture",loggedInUser.getProfilePicture());
    }

    public static void updatePhoneNumber(String phoneNumber) {
        instance.collection("users").document(MainActivity.getLoggedInUser().getEmail()).update("phoneNumber", phoneNumber);
    }
}
