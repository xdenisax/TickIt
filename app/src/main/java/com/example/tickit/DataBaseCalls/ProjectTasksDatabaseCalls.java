package com.example.tickit.DataBaseCalls;

import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.ProjectTask;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FieldValue;
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
                                         .update("membersWhoAssumed", FieldValue.arrayRemove("0"))
                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                             @Override
                                             public void onComplete(@NonNull Task<Void> task) {
                                                 instance
                                                         .collection(collection)
                                                         .document(projectTask.getId())
                                                         .update("membersWhoAssumed", FieldValue.arrayUnion(projectTask.getMembersWhoAssumed().get(memberIndex)))
                                                         .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                             @Override
                                                             public void onComplete(@NonNull Task<Void> task) {
                                                                 callbackBoolean.callback(true);
                                                             }
                                                         });
                                             }
                                         });
                             }
                         }
                     }
                 });
    }
}
