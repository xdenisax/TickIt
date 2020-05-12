package com.example.tickit.DataBaseCalls;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Classes.ProjectTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class ProjectTasksDatabaseCalls {
    private static FirebaseFirestore instance = FirebaseFirestore.getInstance();

     public static void getTasks(final String collection, final CallbackArrayListTasks callbackArrayListTasks){
             instance
                     .collection(collection)
                     .orderBy("stopDate", Query.Direction.DESCENDING )
                     .addSnapshotListener(new EventListener<QuerySnapshot>() {
                         @Override
                         public void onEvent(@Nullable QuerySnapshot task, @Nullable FirebaseFirestoreException e) {
                             if(!task.isEmpty()){
                                 final List<DocumentSnapshot> list = task.getDocuments();
                                 ArrayList<ProjectTask> tasks = new ArrayList<>();
                                 for(DocumentSnapshot documentSnapshot : list){
                                     ProjectTask newTask = documentSnapshot.toObject(ProjectTask.class);
                                     tasks.add(newTask);
                                     if(tasks.size()==list.size()){
                                         callbackArrayListTasks.onCallBack(tasks);
                                     }
                                 }
                             }else{
                                 callbackArrayListTasks.onCallBack(new ArrayList<ProjectTask>() );
                             }
                         }
                     });

     }

    public static void updateProgressInDataBase(final String collection, final ProjectTask projectTask, final int memberIndex, final CallbackBoolean callbackBoolean){
         instance
                 .collection(collection)
                 .document(projectTask.getId())
                 .get()
                 .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                     @Override
                     public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                         if(task.isSuccessful()){
                             if(task.getResult().exists()){
                                 instance
                                         .collection(collection)
                                         .document(projectTask.getId())
                                         .update("membersWhoAssumed", projectTask.getMembersWhoAssumed())
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 if(task.getResult() !=null){
                                                     callbackBoolean.callback(true);
                                                 }else{
                                                     callbackBoolean.callback(false);
                                                 }
                                             }
                                         });

                             }
                         }
                     }
                 });
    }

    public static void addProjectTaskInDataBase(String collection, final ProjectTask projectTask, final CallbackBoolean callbackBoolean) {
        (FirebaseFirestore.getInstance())
                .collection(collection)
                .document(projectTask.getId())
                .set(projectTask)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        callbackBoolean.callback(true);
                    }
                });
    }

    public static void removeTask(String collection, ProjectTask task, final CallbackBoolean callbackBoolean) {
        (FirebaseFirestore.getInstance())
                .collection(collection)
                .document(task.getId())
                .delete()
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        callbackBoolean.callback(true);
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        callbackBoolean.callback(true);
                       }
                });
    }

    public static void fakeUpdate(String collection, ProjectTask projectTask) {
        (FirebaseFirestore.getInstance())
                .collection(collection)
                .document(projectTask.getId())
                .update("description", projectTask.getTaskDescription());
    }
}
