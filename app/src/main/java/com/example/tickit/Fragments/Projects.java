package com.example.tickit.Fragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;

import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Adapters.ListViewProjectsAdapter;
import com.example.tickit.Callbacks.CallbackArrayListEditions;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListProjects;
import com.example.tickit.Callbacks.CallbackEdition;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.Project;
import com.example.tickit.Activities.ProjectProfile;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Classes.User;
import com.example.tickit.Utils.DateProcessing;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.apache.log4j.chainsaw.Main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Projects extends Fragment {
    ListView projectListview;
    ProgressBar progressBar;
    View view;

    public Projects() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view=inflater.inflate(R.layout.fragment_projects, container, false);
        assignViews();


        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadListView();
    }

    private void assignViews() {
        projectListview = (ListView) view.findViewById(R.id.projectsListvView);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
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
