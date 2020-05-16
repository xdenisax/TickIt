package com.example.tickit.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inspector.StaticInspectionCompanionProvider;
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tickit.Activities.AddProject;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Activities.Statistics;
import com.example.tickit.Adapters.ListViewProjectsAdapter;
import com.example.tickit.Callbacks.CallbackArrayListEditions;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListProjects;
import com.example.tickit.Callbacks.CallbackEdition;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackProject;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.Project;
import com.example.tickit.Activities.ProjectProfile;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Classes.User;
import com.example.tickit.RecyclerViewAdapters.ProjectAdapter;
import com.example.tickit.Utils.DateProcessing;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Projects extends Fragment {
    private ProjectAdapter adapter;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private View view;
    private ImageButton addProjectButton, stats;

    public Projects() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_projects, container, false);
        assignViews();
        setUpRecyclerView();
        setAllowanceOnViews();
        addProjectButtonPressed();
        statsButtonPressed();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void assignViews() {
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        addProjectButton= (ImageButton) view.findViewById(R.id.addProjectsButton);
        stats= (ImageButton) view.findViewById(R.id.statistics);
        recyclerView = (RecyclerView) view.findViewById(R.id.projectsRecyclerView);
    }

    private void setAllowanceOnViews() {
        if(MainActivity.getUserGrade()>=2){
            addProjectButton.setVisibility(View.GONE);
            stats.setVisibility(View.GONE);
        }
    }

    private void setUpRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection("projects");
        FirestoreRecyclerOptions<Project> projectOptions = new FirestoreRecyclerOptions
                .Builder<Project>()
                .setQuery(query, Project.class)
                .build();
        adapter = new ProjectAdapter(projectOptions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setClicksOnAdapter(adapter);
    }

    private void setClicksOnAdapter(ProjectAdapter adapter) {

        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(final DocumentReference projectReference, int position) {
                ProjectDatabaseCalls.getProject(projectReference, new CallbackProject() {
                    @Override
                    public void callback(Project project) {
                        project.setId(projectReference.getId());
                        startActivity(new Intent(getContext(), ProjectProfile.class).putExtra("projectFromProjectsList", project));
                    }
                });
            }
        });
    }

    private void addProjectButtonPressed() {
        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddProject.class));
            }
        });
    }

    private void statsButtonPressed() {
        stats.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.getContext()!=null){
                    startActivity(new Intent(MainActivity.getContext(), Statistics.class));
                }
            }
        });
    }

}
