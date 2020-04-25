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
import android.widget.AdapterView;
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
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.core.OrderBy;

import java.util.ArrayList;
import java.util.List;

public class OpenTasks extends Fragment {
    ImageButton addTaskButton;
    ListView openTasks, assumedTasks;
    ProgressBar spinkitOpenTasks, spinKitAssumedTasks;
    ListViewTasksAdapter adapter;
    TextView textViewNoOpenTasks, textViewNoAssumedTasks;
    public OpenTasks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_open_tasks, container, false);
        openTasks = (ListView) view.findViewById(R.id.openTasksListView);
        assumedTasks = (ListView) view.findViewById(R.id.assumedTasksListView);
        spinkitOpenTasks = (ProgressBar) view.findViewById(R.id.spin_kitOpenTasks);
        spinKitAssumedTasks  = (ProgressBar) view.findViewById(R.id.spin_kitAssumedTasks);
        textViewNoOpenTasks = (TextView) view.findViewById(R.id.textViewNoOpenTasks);
        textViewNoAssumedTasks = (TextView) view.findViewById(R.id.textViewNoAssumedTasks);
        textViewNoOpenTasks.setVisibility(View.GONE);
        textViewNoAssumedTasks.setVisibility(View.GONE);

        addTaskButtonPressed(view);
        setAllowanceOnAddTaskButton(view);

        loadOpenTasks();
        loadAssumedTasks();
        return view;
    }

    private void loadAssumedTasks() {
        getTasksFromDataBase("assumedTasks", new CallbackArrayListTasks() {
            @Override
            public void onCallBack(final ArrayList<ProjectTask> tasks) {
                spinKitAssumedTasks.setVisibility(View.GONE);

                    adapter = new ListViewTasksAdapter(MainActivity.getContext(),R.layout.task_card, tasks );
                    assumedTasks.setAdapter(adapter);
                    assumedTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(getContext(),TaskProfile.class).putExtra("openTaskFromOpenTasks",tasks.get(position)));
                        }
                    });
                if(tasks.size()>0){
                    textViewNoAssumedTasks.setVisibility(View.INVISIBLE);
                }else{
                    textViewNoAssumedTasks.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadOpenTasks() {
        getTasksFromDataBase("openTasks", new CallbackArrayListTasks() {
            @Override
            public void onCallBack(final ArrayList<ProjectTask> tasks) {
                adapter = new ListViewTasksAdapter(MainActivity.getContext(),R.layout.task_card, tasks );
                openTasks.setAdapter(adapter);
                openTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(getContext(),TaskProfile.class).putExtra("openTaskFromOpenTasks",tasks.get(position)));
                    }
                });

                spinkitOpenTasks.setVisibility(View.GONE);
                if(tasks.size()>0){
                    textViewNoOpenTasks.setVisibility(View.INVISIBLE);
                }else{
                    textViewNoOpenTasks.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void getTasksFromDataBase(final String collection, final CallbackArrayListTasks callbackArrayListTasks){
        (FirebaseFirestore.getInstance())
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

}
