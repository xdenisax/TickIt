package com.example.tickit.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tickit.Adapters.ListViewTasksAdapter;
import com.example.tickit.Classes.AssumedTasksSituation;
import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.R;
import com.example.tickit.Activities.TaskProfile;

import java.util.ArrayList;

public class MyTasks extends Fragment {
    private ListView myTasksListView;
    private ProgressBar spinkitProgressBar;
    private TextView noAssumedTasks;
    private ArrayList<ProjectTask> myTasks;

    public MyTasks() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_tasks, container, false);
        assignViews(view);

        noAssumedTasks.setVisibility(View.GONE);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadListView();
        setActionOnListView();
    }

    private void setActionOnListView() {
        myTasksListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(MainActivity.getContext(), TaskProfile.class).putExtra("taskFromMyTasks", myTasks.get(position)));
            }
        });
    }

    private void loadListView() {
        getMyTasksFromDataBase("assumedTasks", new CallbackArrayListTasks() {
            @Override
            public void onCallBack(final ArrayList<ProjectTask> tasks) {
                getMyTasksFromDataBase("openTasks",new CallbackArrayListTasks() {
                    @Override
                    public void onCallBack(ArrayList<ProjectTask> tasks2) {
                        spinkitProgressBar.setVisibility(View.GONE);
                        myTasks = new ArrayList<>();
                        myTasks.addAll(tasks);
                        myTasks.addAll(tasks2);
                        if(myTasks.size()>0){
                            ListViewTasksAdapter adapter = new ListViewTasksAdapter(MainActivity.getContext(),R.layout.task_card, myTasks);
                            adapter.notifyDataSetChanged();
                            myTasksListView.setAdapter(adapter);
                        }else{
                            noAssumedTasks.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }

    private void getMyTasksFromDataBase(String collection, final CallbackArrayListTasks callbackArrayListTasks) {
        ProjectTasksDatabaseCalls.getTasks(collection, new CallbackArrayListTasks() {
            @Override
            public void onCallBack(ArrayList<ProjectTask> tasks) {
                ArrayList<ProjectTask> myTasksFromDatabase = new ArrayList<>();
                for(ProjectTask task:tasks){
                    for(AssumedTasksSituation situation : task.getMembersWhoAssumed()){
                        if(situation.getUser().getEmail().equals(MainActivity.getLoggedInUser().getEmail())){
                            myTasksFromDatabase.add(task);
                        }
                    }
                }
                callbackArrayListTasks.onCallBack(myTasksFromDatabase);
            }
        });
    }

    private void assignViews(View view) {
        myTasksListView = (ListView) view.findViewById(R.id.myTasksListView);
        spinkitProgressBar = (ProgressBar) view.findViewById(R.id.spin_kitMyTasks);
        noAssumedTasks = (TextView) view.findViewById(R.id.textViewNoPersonalTasks);
    }


}
