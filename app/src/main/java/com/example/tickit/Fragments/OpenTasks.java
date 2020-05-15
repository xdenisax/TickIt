package com.example.tickit.Fragments;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

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

import com.example.tickit.Activities.ProjectProfile;
import com.example.tickit.Adapters.ListViewTasksAdapter;
import com.example.tickit.Activities.AddTask;
import com.example.tickit.Callbacks.CallbackArrayListStrings;
import com.example.tickit.Callbacks.CallbackArrayListTasks;
import com.example.tickit.Callbacks.CallbackProject;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.Project;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.ProjectTasksDatabaseCalls;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.ProjectTask;
import com.example.tickit.R;
import com.example.tickit.Activities.TaskProfile;
import com.example.tickit.RecyclerViewAdapters.MemberAdapter;
import com.example.tickit.RecyclerViewAdapters.ProjectAdapter;
import com.example.tickit.RecyclerViewAdapters.TasksAdapter;
import com.firebase.ui.auth.util.data.TaskFailureLogger;
import com.firebase.ui.common.ChangeEventType;
import com.firebase.ui.firestore.ChangeEventListener;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.firebase.ui.firestore.ObservableSnapshotArray;
import com.google.android.gms.common.api.internal.TaskApiCall;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;
import java.util.Map;

public class OpenTasks extends Fragment {
    private ImageButton addTaskButton;
    private ListView openTasks, assumedTasks;
    private ProgressBar spinkitOpenTasks, spinKitAssumedTasks;
   // private ListViewTasksAdapter adapter;
   public static final String NOTIFICATION_CHANNEL_ID = "10001";
    private TextView textViewNoOpenTasks, textViewNoAssumedTasks;
    private RecyclerView openTasksRecyclerView, assumedTasksRecyclerView;
    private TasksAdapter adapterOpenTasks, adapterAssumedTasks;
    private View view;
    public OpenTasks() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_open_tasks, container, false);

        assignViews();
        addTaskButtonPressed(view);

//        loadOpenTasks();
//        loadAssumedTasks();
        setAllowanceOnAddTaskButton(view);
        setUpOpenTasksRecyclerView();
        setUpAssumedTasksRecyclerView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        if(adapterOpenTasks!=null){
            adapterOpenTasks.startListening();
            manageLoadingViews(spinkitOpenTasks, adapterOpenTasks.getItemCount(), textViewNoOpenTasks);
        }else{
            manageLoadingViews(spinkitOpenTasks, 0, textViewNoOpenTasks);
        }
        if(adapterAssumedTasks!=null){
            adapterAssumedTasks.startListening();
            manageLoadingViews(spinKitAssumedTasks, adapterAssumedTasks.getItemCount(), textViewNoAssumedTasks);
        }else{
            manageLoadingViews(spinKitAssumedTasks, 0, textViewNoAssumedTasks);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if(adapterAssumedTasks!=null) {
            adapterAssumedTasks.stopListening();
        }
        if(adapterOpenTasks!=null) {
            adapterOpenTasks.stopListening();
        }
    }

    private void assignViews() {
        assumedTasksRecyclerView=(RecyclerView) view.findViewById(R.id.assumedTasksRecyclerView);
        openTasksRecyclerView=(RecyclerView) view.findViewById(R.id.openTasksRecyclerview);
        spinkitOpenTasks = (ProgressBar) view.findViewById(R.id.spin_kitOpenTasks);
        spinKitAssumedTasks  = (ProgressBar) view.findViewById(R.id.spin_kitAssumedTasks);
        textViewNoOpenTasks = (TextView) view.findViewById(R.id.textViewNoOpenTasks);
        textViewNoAssumedTasks = (TextView) view.findViewById(R.id.textViewNoAssumedTasks);
        textViewNoOpenTasks.setVisibility(View.GONE);
        textViewNoAssumedTasks.setVisibility(View.GONE);
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

    private void setUpOpenTasksRecyclerView(){
        Query query;
        if(MainActivity.getMandateProjects()==null){
            query= FirebaseFirestore.getInstance().collection("openTasks");
            FirestoreRecyclerOptions<ProjectTask> options = new FirestoreRecyclerOptions.Builder<ProjectTask>()
                    .setQuery(query, ProjectTask.class)
                    .build();
            adapterOpenTasks=new TasksAdapter(options);
            openTasksRecyclerView.setHasFixedSize(true);
            openTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            openTasksRecyclerView.setAdapter(adapterOpenTasks);
            setClickListeners(adapterOpenTasks);

        }else{
            if(MainActivity.getMandateProjects().size()>0){
                query = FirebaseFirestore.getInstance().collection("openTasks").whereIn("project", MainActivity.getMandateProjects());
                FirestoreRecyclerOptions<ProjectTask> options = new FirestoreRecyclerOptions.Builder<ProjectTask>()
                        .setQuery(query, ProjectTask.class)
                        .build();
                adapterOpenTasks=new TasksAdapter(options);
                openTasksRecyclerView.setHasFixedSize(true);
                openTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                openTasksRecyclerView.setAdapter(adapterOpenTasks);
                setClickListeners(adapterOpenTasks);

            }
        }
    }

    private void setUpAssumedTasksRecyclerView(){
        Query query;
        if(MainActivity.getMandateProjects()==null){
             query= FirebaseFirestore.getInstance().collection("assumedTasks");
            FirestoreRecyclerOptions<ProjectTask> options = new FirestoreRecyclerOptions.Builder<ProjectTask>()
                    .setQuery(query, ProjectTask.class)
                    .build();
            adapterAssumedTasks=new TasksAdapter(options);
            assumedTasksRecyclerView.setHasFixedSize(true);
            assumedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
            assumedTasksRecyclerView.setAdapter(adapterAssumedTasks);
            setClickListeners(adapterAssumedTasks);
        }else{
            if(MainActivity.getMandateProjects().size()>0){
                query = FirebaseFirestore.getInstance().collection("assumedTasks").whereIn("project", MainActivity.getMandateProjects());
                FirestoreRecyclerOptions<ProjectTask> options = new FirestoreRecyclerOptions.Builder<ProjectTask>()
                        .setQuery(query, ProjectTask.class)
                        .build();
                adapterAssumedTasks=new TasksAdapter(options);
                assumedTasksRecyclerView.setHasFixedSize(true);
                assumedTasksRecyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
                assumedTasksRecyclerView.setAdapter(adapterAssumedTasks);
                setClickListeners(adapterAssumedTasks);
            }
        }
    }

    private void setClickListeners(TasksAdapter adapter) {
        adapter.setOnItemClickListener(new TasksAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentSnapshot documentSnapshot, int position) {
                startActivity(new Intent(getContext(), TaskProfile.class).putExtra("openTaskFromOpenTasks", documentSnapshot.toObject(ProjectTask.class)));
            }
        });
    }

    public static void manageLoadingViews(ProgressBar spinKit,int size , TextView textViewNoTasks) {
        spinKit.setVisibility(View.GONE);
        if(size>0){
            textViewNoTasks.setVisibility(View.GONE);
        }else{
            textViewNoTasks.setVisibility(View.VISIBLE);
        }
    }

    public static void manageNotification( ProjectTask task) {
        NotificationCompat.Builder builder =new NotificationCompat.Builder(MainActivity.getContext())
                .setSmallIcon(R.drawable.sisc_logo250)
                .setContentTitle("Task-ul "+ task.getTaskName() + " a fost adaugat sau a fost modificat.")
                .setAutoCancel(true);
        Intent intent = new Intent(MainActivity.getContext(), OpenTasks.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(MainActivity.getContext(), 0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        builder.setContentIntent(pendingIntent);

        NotificationManager notificationManager = (NotificationManager) MainActivity.getContext().getSystemService(Context.NOTIFICATION_SERVICE);
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O)
        {
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID, "NOTIFICATION_CHANNEL_NAME", importance);

            builder.setChannelId(NOTIFICATION_CHANNEL_ID);
            notificationManager.createNotificationChannel(notificationChannel);
        }

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


}
