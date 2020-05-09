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
import android.widget.AdapterView;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.tickit.Activities.AddProject;
import com.example.tickit.Activities.MainActivity;
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
    ListView projectListview;
    private ProjectAdapter adapter;
    private RecyclerView recyclerView;
    ProgressBar progressBar;
    View view;
    ImageButton addProjectButton;


    public Projects() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_projects, container, false);
        assignViews();
        setUpRecyclerView();
        setAllowanceOnViews();
        addProjectButtonPressed();

        return view;
    }

    private void setUpRecyclerView() {
        Query query = FirebaseFirestore.getInstance().collection("projects");
        FirestoreRecyclerOptions<Project> projectOptions = new FirestoreRecyclerOptions.Builder<Project>()
                .setQuery(query, Project.class)
                .build();
        adapter = new ProjectAdapter(projectOptions);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        adapter.setOnItemClickListener(new ProjectAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentReference projectReference, int position) {
                ProjectDatabaseCalls.getProject(projectReference, new CallbackProject() {
                    @Override
                    public void callback(Project project) {
                        startActivity(new Intent(getContext(), ProjectProfile.class).putExtra("projectFromProjectsList", project));
                    }
                });
            }
        });

        adapter.setOnItemLongClickListener(new ProjectAdapter.OnItemLongClickListener() {
            @Override
            public void onItemLongClick(DocumentReference projectReference, int position) {
                launchAlertDialog(projectReference, getContext());
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
      //  loadListView();
        adapter.startListening();
    }

    @Override
    public void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    private void setAllowanceOnViews() {
        if(MainActivity.getUserGrade()>=2){
            addProjectButton.setVisibility(View.GONE);
        }
    }

    private void addProjectButtonPressed() {
        addProjectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getContext(), AddProject.class));
            }
        });
    }

    private void assignViews() {
       // projectListview = (ListView) view.findViewById(R.id.projectsListvView);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        addProjectButton= (ImageButton) view.findViewById(R.id.addProjectsButton);
        recyclerView = (RecyclerView) view.findViewById(R.id.projectsRecyclerView);
    }

    private void launchAlertDialog(final DocumentReference documentReference, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Doriti stergerea proiectului?");
        dialog
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ProjectDatabaseCalls.deleteProject(documentReference);
                    }
                })
                .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void loadListView() {
        ProjectDatabaseCalls.getProjects(new CallbackArrayListProjects() {
            @Override
            public void callback(final ArrayList<Project> projectsFromDataBase) {
                progressBar.setVisibility(View.GONE);
                if(getActivity()!=null) {
                    ListViewProjectsAdapter adapter = new ListViewProjectsAdapter(MainActivity.getContext(), R.layout.member_card, projectsFromDataBase);
                    adapter.notifyDataSetChanged();
                    projectListview.requestLayout();
                    projectListview.setAdapter(adapter);
                    projectListview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(getContext(), ProjectProfile.class).putExtra("projectFromProjectsList", projectsFromDataBase.get(position)));
                        }
                    });
                }
            }
        });
    }

}
