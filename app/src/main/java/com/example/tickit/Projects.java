package com.example.tickit;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class Projects extends Fragment {
    ListView projectListview;
    ArrayList<Project> projects;
    public Projects() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_projects, container, false);
        projectListview = (ListView) view.findViewById(R.id.projectsListvView);
        projects = new ArrayList<>();

        getProjects(new CallbackArrayListProjects() {
            @Override
            public void callback(ArrayList<Project> projectsFromDataBase) {
                projects.addAll(projectsFromDataBase);
                ListViewProjectsAdapter adapter = new ListViewProjectsAdapter(getContext(),R.layout.member_card,projectsFromDataBase);
                projectListview.setAdapter(adapter);
            }
        });
        return view;
    }

    public void getProjects(final CallbackArrayListProjects callbackArrayListProjects){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final ArrayList<Project> profectsFromDataBase = new ArrayList<>();
        database.collection("projects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Project project = document.toObject(Project.class);
                        profectsFromDataBase.add(project);
                        Log.d("projectsCheck", project.toString());
                    }
                    callbackArrayListProjects.callback(profectsFromDataBase);
                } else {
                    Log.d("projectsCheck", "Error getting documents.", task.getException());
                }
            }
        });
    }

}
