package com.example.tickit.DataBaseCalls;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Callbacks.CallbackArrayListEditions;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListProjects;
import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackDocumentReference;
import com.example.tickit.Callbacks.CallbackEdition;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.Project;
import com.example.tickit.Classes.User;
import com.example.tickit.Utils.DateProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Date;

public class ProjectDatabaseCalls {
    private static FirebaseFirestore instance = FirebaseFirestore.getInstance();

    public static void getProjectName(DocumentReference docRef, final CallbackString callback) {
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String value = documentSnapshot.getString("name");
                    callback.onCallBack(value);
                } else {
                    Log.d("database", "No such document");
                }
            }
        });
    }

    public static  void getProjectsNames(final CallbackArrayListStrings callbackArrayListStrings){
        instance
                .collection("projects")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            ArrayList<String> projectsNames=new ArrayList<>();
                            for(QueryDocumentSnapshot doc:task.getResult()){
                                projectsNames.add(doc.getString("name"));
                                if(projectsNames.size()==task.getResult().size()){
                                    callbackArrayListStrings.onCallback(projectsNames);
                                }
                            }
                        }
                    }
                });

    }

    public static void getProjects(final CallbackArrayListProjects callbackArrayListProjects){
        final ArrayList<Project> projectsFromDataBase = new ArrayList<>();
        instance.collection("projects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        getEditions(document, new CallbackArrayListEditions() {
                            @Override
                            public void callback(ArrayList<Edition> editions) {
                                Project project = document.toObject(Project.class);
                                project.setEditions(editions);
                                projectsFromDataBase.add(project);
                                if(projectsFromDataBase.size()==task.getResult().size() -1){
                                    callbackArrayListProjects.callback(projectsFromDataBase);
                                }
                            }
                        });
                    }
                } else {
                    Log.d("projectsCheck", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public static void getDocumentReferenceProject(String projectName, final CallbackDocumentReference callbackDocumentReference) {
        instance
                .collection("projects")
                .whereEqualTo("name", projectName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        callbackDocumentReference.callback(task.getResult().getDocuments().get(0).getReference());
                    }
                });
    }

    private static void getEditions(QueryDocumentSnapshot document, final CallbackArrayListEditions callbackArrayListEditions){
        String subCollectionID= document.getString("name").substring(0,1).toLowerCase()
                +document.getString("name").substring(1).replace(" ","")
                + "Editions";
        final ArrayList<Edition> editions = new ArrayList<Edition>();

        instance
                .collection("projects")
                .document(document.getId())
                .collection(subCollectionID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                initializeEdition(document, new CallbackEdition() {
                                    @Override
                                    public void callback(Edition edition) {
                                        editions.add(edition);
                                        if (editions.size() == task.getResult().size()) {
                                            callbackArrayListEditions.callback(editions);
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("projectCheck", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private static void initializeEdition(final QueryDocumentSnapshot document, final CallbackEdition callbackEdition) {
        final Edition edition = new Edition();
        UserDatabaseCalls.getUser(document.getDocumentReference("coordinator1"),  new CallbackUser() {
            @Override
            public void callback(final User user1) {
                UserDatabaseCalls.getUser(document.getDocumentReference("coordinator2"),  new CallbackUser() {
                    @Override
                    public void callback(User user2) {
                        if(user2!=null){ edition.setCoordinator2(user2); }
                        edition.setCoordinator1(user1);
                        edition.setStrategy(document.getString("strategy"));
                        edition.setYear(String.valueOf(document.get("year")));

                        final ArrayList<DocumentReference> documentReferences = (ArrayList<DocumentReference>) document.get("members");
                        if(documentReferences!=null){
                            final ArrayList<User> members = new ArrayList<>();
                            for(final DocumentReference docRef: documentReferences){
                                UserDatabaseCalls.getUser(docRef, new CallbackUser() {
                                    @Override
                                    public void callback(User user) {
                                        members.add(user);
                                        if(members.size() == documentReferences.size()){
                                            edition.setMembers(members);
                                            callbackEdition.callback(edition);
                                        }
                                    }
                                });
                            }
                        }else{
                            Log.d("projectCheck", "nullnullnull"+edition.getStrategy()+ " " +edition.getYear()+edition.getCoordinator1());
                        }
                    }
                });
            }
        });
    }

}
