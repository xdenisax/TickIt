package com.example.tickit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;

import java.util.ArrayList;

public class MyTasks extends Fragment {
    ListView myTasksListView;
    ProgressBar spinkitProgressBar;
    TextView noAssumedTasks;
    ArrayList<ProjectTask> myTasks;

    public MyTasks() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_my_tasks, container, false);

        assignViews(view);
        loadListView();

        return view;
    }

    private void loadListView() {
        getMyTasksFromDataBase(new CallbackArrayListTasks() {
            @Override
            public void onCallBack(ArrayList<ProjectTask> tasks) {
                spinkitProgressBar.setVisibility(View.GONE);
                if(tasks!=null){
                    myTasksListView.setAdapter(new ListViewTasksAdapter(MainActivity.getContext(),R.layout.task_card, tasks));
                    noAssumedTasks.setVisibility(View.GONE);
                }

            }
        });

    }

    private void getMyTasksFromDataBase(final CallbackArrayListTasks callbackArrayListTasks) {
        myTasks = new ArrayList<>();
        ProjectTasksDatabaseCalls.getTasks("assumedTasks", new CallbackArrayListTasks() {
            @Override
            public void onCallBack(ArrayList<ProjectTask> tasks) {
                for(ProjectTask task:tasks){
                    for(AssumedTasksSituation situation : task.getMembersWhoAssumed()){
                        if(situation.getUser().getEmail().equals(MainActivity.getLoggedInUser().getEmail())){
                            myTasks.add(task);
                        }
                    }
                }
                callbackArrayListTasks.onCallBack(myTasks);
            }
        });
    }

    private void assignViews(View view) {
        myTasksListView = (ListView) view.findViewById(R.id.myTasksListView);
        spinkitProgressBar = (ProgressBar) view.findViewById(R.id.spin_kitMyTasks);
        noAssumedTasks = (TextView) view.findViewById(R.id.textViewNoPersonalTasks);
    }


}
