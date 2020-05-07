package com.example.tickit.Fragments;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Adapters.ListViewTasksAdapter;
import com.example.tickit.Activities.AddTask;
import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.Project;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.R;
import com.example.tickit.Activities.TaskProfile;
import com.google.firebase.firestore.DocumentReference;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;
import java.util.Map;

public class OpenTasks extends Fragment {
    ImageButton addTaskButton;
    ListView openTasks, assumedTasks;
    ProgressBar spinkitOpenTasks, spinKitAssumedTasks;
    ListViewTasksAdapter adapter;
    TextView textViewNoOpenTasks, textViewNoAssumedTasks;
    View view;
    public OpenTasks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_open_tasks, container, false);

        assignViews();
        addTaskButtonPressed(view);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadOpenTasks();
        loadAssumedTasks();
        setAllowanceOnAddTaskButton(view);
    }

    private void assignViews() {
        openTasks = (ListView) view.findViewById(R.id.openTasksListView);
        assumedTasks = (ListView) view.findViewById(R.id.assumedTasksListView);
        spinkitOpenTasks = (ProgressBar) view.findViewById(R.id.spin_kitOpenTasks);
        spinKitAssumedTasks  = (ProgressBar) view.findViewById(R.id.spin_kitAssumedTasks);
        textViewNoOpenTasks = (TextView) view.findViewById(R.id.textViewNoOpenTasks);
        textViewNoAssumedTasks = (TextView) view.findViewById(R.id.textViewNoAssumedTasks);
        textViewNoOpenTasks.setVisibility(View.GONE);
        textViewNoAssumedTasks.setVisibility(View.GONE);
    }

    private void loadAssumedTasks() {
        ProjectTasksDatabaseCalls.getTasks("assumedTasks", new CallbackArrayListTasks() {
            @Override
            public void onCallBack(final ArrayList<ProjectTask> tasks) {
                for(ProjectTask task: tasks){
                    isMemberOnProject(task.getProject());
                }
                if(MainActivity.getContext()!=null){
                    adapter = new ListViewTasksAdapter(MainActivity.getContext(),R.layout.task_card, tasks );
                    adapter.notifyDataSetChanged();
                    assumedTasks.setAdapter(adapter);
                    assumedTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(getContext(), TaskProfile.class).putExtra("openTaskFromOpenTasks",tasks.get(position)));
                        }
                    });
                }
                spinKitAssumedTasks.setVisibility(View.GONE);

                if(tasks.size()>0){
                    textViewNoAssumedTasks.setVisibility(View.INVISIBLE);
                }else{
                    textViewNoAssumedTasks.setVisibility(View.VISIBLE);
                }
            }
        });
    }

    private void loadOpenTasks() {
        ProjectTasksDatabaseCalls.getTasks("openTasks", new CallbackArrayListTasks() {
            @Override
            public void onCallBack(final ArrayList<ProjectTask> tasks) {
               // manageNotification();
                ArrayList<ProjectTask> tasksOnMyProject =new ArrayList<>();
                for(ProjectTask task:tasks){
                    if(isMemberOnProject(task.getProject())){
                        tasksOnMyProject.add(task);
                    }
                }
                if(MainActivity.getContext() !=null){
                    adapter = new ListViewTasksAdapter(MainActivity.getContext(),R.layout.task_card, tasksOnMyProject);
                    openTasks.setAdapter(adapter);
                    openTasks.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(getContext(),TaskProfile.class).putExtra("openTaskFromOpenTasks",tasks.get(position)));
                        }
                    });
                }


                spinkitOpenTasks.setVisibility(View.GONE);
                if(tasksOnMyProject.size()>0){
                    textViewNoOpenTasks.setVisibility(View.INVISIBLE);
                }else{
                    textViewNoOpenTasks.setVisibility(View.VISIBLE);
                }

            }
        });
    }

    private void manageNotification() {
        NotificationCompat.Builder builder =new NotificationCompat.Builder(MainActivity.getContext())
                .setSmallIcon(R.drawable.sisc_logo250)
                .setContentTitle("S-a adaugat un task nou.")
                .setAutoCancel(true);
        Intent intent = new Intent(MainActivity.getContext(), OpenTasks.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);
        NotificationManager notificationManager = (NotificationManager) MainActivity.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, builder.build());
    }

    private void addTaskButtonPressed(View view) {
        addTaskButton = (ImageButton) view.findViewById(R.id.addTasksButton);
        addTaskButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddTask.class));
            }
        });
    }

    private void setAllowanceOnAddTaskButton( View view) {
        addTaskButton = (ImageButton) view.findViewById(R.id.addTasksButton);
        if(MainActivity.getUserGrade()>=4){
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


    private boolean isMemberOnProject(DocumentReference projectName) {
        for (final Map.Entry<String, Mandate>  mandateEntry: MainActivity.getCurrentMandates().entrySet()){
            if(mandateEntry.getKey().contains("BE-BC")){
                return true;
            }else{
                if(mandateEntry.getValue().getProject_name().equals(projectName)) {
                    return true;
                }
            }
        }
        return false;
    }
//    private boolean isAtLeast(int grade){
//        if(MainActivity.getUserGrade() < 2){
//            return true;
//        }else{
//            if(MainActivity.getCurrentMandates().containsKey(projectName+edition.getStopDate())){
//                return MainActivity.getCurrentMandates().get(projectName+edition.getStopDate()).getStop_date().equals(edition.getStopDate())
//                        && (MainActivity.getCurrentMandates().get(projectName+edition.getStopDate()).getGrade() <= grade);
//            }else{
//                return false;
//            }
//        }
//    }

}
