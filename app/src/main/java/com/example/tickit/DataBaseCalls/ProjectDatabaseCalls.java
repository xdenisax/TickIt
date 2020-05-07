package com.example.tickit.DataBaseCalls;

import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Callbacks.CallbackArrayListEditions;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListProjects;
import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackBoolean;
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
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.nio.file.attribute.UserDefinedFileAttributeView;
import java.sql.Time;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectDatabaseCalls {
    private static String COLLECTION_NAME = "projects";
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
        instance
                .collection("projects")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for(final DocumentSnapshot document: list){
                            getEditions(document, new CallbackArrayListEditions() {
                                @Override
                                public void callback(ArrayList<Edition> editions) {
                                    Project project = document.toObject(Project.class);
                                    project.setId(document.getId());
                                    project.setEditions(editions);
                                    projectsFromDataBase.add(project);
                                    if(projectsFromDataBase.size()==list.size() -1){
                                        callbackArrayListProjects.callback(projectsFromDataBase);
                                    }
                                }
                            });

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

    private static void getEditions(DocumentSnapshot document, final CallbackArrayListEditions callbackArrayListEditions){
        String subCollectionID= getSubCollectionName(document);
        final ArrayList<Edition> editions = new ArrayList<Edition>();

        instance
                .collection("projects")
                .document(document.getId())
                .collection(subCollectionID)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException e) {
                        final List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot document :list) {
                            initializeEdition(document, new CallbackEdition() {
                                @Override
                                public void callback(Edition edition) {
                                    editions.add(edition);
                                    if (editions.size() == list.size()) {
                                        callbackArrayListEditions.callback(editions);
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private static void initializeEdition(final DocumentSnapshot document, final CallbackEdition callbackEdition) {
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
                        edition.setEditionNumber(document.getId());
                        edition.setStartDate(document.getDate("startDate"));
                        edition.setStopDate(document.getDate("stopDate"));

                        final ArrayList<DocumentReference> documentReferences = (ArrayList<DocumentReference>) document.get("members");
                        if(documentReferences!=null){
                            final ArrayList<User> members = new ArrayList<>();
                            if(documentReferences.size()<1){
                                edition.setMembers(members);
                                callbackEdition.callback(edition);
                            }else{
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
                            }
                        }else{
                            edition.setMembers(null);
                            callbackEdition.callback(edition);
                        }
                    }
                });
            }
        });
    }

    public static void saveEdition(final Project project, final Edition edition, final CallbackBoolean callbackBoolean){
        final String subCollectionRef = getSubCollectionName(project.getName());
        Log.d("filteredList", edition.toString());
        final Map<String, Object> editionMap = new HashMap<>();

        UserDatabaseCalls.getUserReference(edition.getCoordinator1(), new CallbackDocumentReference() {
            @Override
            public void callback(final DocumentReference documentReference1) {
                UserDatabaseCalls.getUserReference(edition.getCoordinator2(), new CallbackDocumentReference() {
                    @Override
                    public void callback(DocumentReference documentReference2) {
                        editionMap.put("coordinator1",documentReference1);
                        if(documentReference2!=null){
                            editionMap.put("coordinator2",documentReference2);
                        }
                        editionMap.put("members", edition.getMembers());
                        editionMap.put("strategy", edition.getStrategy());
                        editionMap.put("year", edition.getYear());
                        editionMap.put("startDate", edition.getStartDate());
                        editionMap.put("stopDate", edition.getStopDate());

                        instance
                                .collection(COLLECTION_NAME)
                                .document(project.getId())
                                .collection(subCollectionRef)
                                .document(edition.getEditionNumber())
                                .set(editionMap)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        callbackBoolean.callback(true);
                                    }
                                });
                    }
                });
            }
        });
    }

    public static void updateEditionStrategy(String projectName, String projectID, Edition edition, final CallbackBoolean callbackBoolean){
        instance
                .collection(COLLECTION_NAME)
                .document(projectID)
                .collection(getSubCollectionName(projectName))
                .document(edition.getEditionNumber())
                .update("strategy", edition.getStrategy())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callbackBoolean.callback(true);
                    }
                });
    }

    public static void addEditionMembers(ArrayList<User> newlyAddedUsers, String projectName, String projectID, Edition edition){
        for(User user : newlyAddedUsers){
            instance
                    .collection(COLLECTION_NAME)
                    .document(projectID)
                    .collection(getSubCollectionName(projectName))
                    .document(edition.getEditionNumber())
                    .update("members", FieldValue.arrayUnion(instance.collection("users").document(user.getEmail())));
        }

    }

    public static void  removeEditionMember(String email, String projectName, String projectID, Edition edition){
        instance
                .collection(COLLECTION_NAME)
                .document(projectID)
                .collection(getSubCollectionName(projectName))
                .document(edition.getEditionNumber())
                .update("members", FieldValue.arrayRemove(instance.collection("users").document(email)));
    }

    public static void getPhotoUri(String imageLink, final CallbackString callbackString){
        StorageReference mImageStorage = FirebaseStorage.getInstance().getReference();
        StorageReference ref = mImageStorage.child("TickIt").child("ProjectsLogo").child(imageLink);

        ref.getDownloadUrl().addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Uri downUri = task.getResult();
                    String imageUrl = downUri.toString();
                    callbackString.onCallBack(imageUrl);
                }
            }
        });

    }

    private static String getSubCollectionName(DocumentSnapshot document){
        return  document.getString("name").substring(0,1).toLowerCase()
                +document.getString("name").substring(1).replace(" ","")
                + "Editions";
    }

    private static String getSubCollectionName(String document){
        return  document.substring(0,1).toLowerCase()
                +document.substring(1).replace(" ","")
                + "Editions";
    }
}
