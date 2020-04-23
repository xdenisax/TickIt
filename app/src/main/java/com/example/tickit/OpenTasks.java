package com.example.tickit;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class OpenTasks extends Fragment {
    ImageButton addTaskButton;
    ListView openTasks;
    ProgressBar spinkitOpenTasks;
    TextView textViewNoOpenTasks;
    public OpenTasks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_open_tasks, container, false);
        openTasks = (ListView) view.findViewById(R.id.openTasksListView);
        spinkitOpenTasks = (ProgressBar) view.findViewById(R.id.spin_kitOpenTasks);
        textViewNoOpenTasks = (TextView) view.findViewById(R.id.textViewNoOpenTasks);
        textViewNoOpenTasks.setVisibility(View.GONE);


        Toast.makeText(getContext(), MainActivity.getUserGrade()+"task", Toast.LENGTH_LONG).show();
        loadOpenTasks();
        addTaskButtonPressed(view);
        setAllowanceOnAddTaskButton(view);
        return view;
    }

    private void setAllowanceOnAddTaskButton( View view) {
        addTaskButton = (ImageButton) view.findViewById(R.id.addTasksButton);
        if(MainActivity.getUserGrade()==4){
            addTaskButton.setVisibility(View.GONE);
        }else{
            addTaskButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), AddTask.class));
                }
            });
        }
    }

    private void loadOpenTasks() {
        getOpenTasksFromDataBase(new CallbackArrayListTasks() {
            @Override
            public void onCallBack(ArrayList<ProjectTask> tasks) {
                spinkitOpenTasks.setVisibility(View.GONE);
                openTasks.setAdapter(new ListViewTasksAdapter(getContext(),R.layout.task_card, tasks ));
            }
        });
    }

    private void getOpenTasksFromDataBase(final CallbackArrayListTasks callbackArrayListTasks){
        (FirebaseFirestore.getInstance())
                .collection("openTasks")
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
                    spinkitOpenTasks.setVisibility(View.GONE);
                    textViewNoOpenTasks.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void addTaskButtonPressed(View view) {
        addTaskButton = (ImageButton) view.findViewById(R.id.addTasksButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddTask.class));
            }
        });
        addTaskButton.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                startActivity(new Intent(getContext(),TaskProfile.class));
                return false;
            }
        });

    }


}
