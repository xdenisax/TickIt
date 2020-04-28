package com.example.tickit.DataBaseCalls;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.ProjectTask;
import com.google.android.gms.tasks.OnCompleteListener;
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
}
