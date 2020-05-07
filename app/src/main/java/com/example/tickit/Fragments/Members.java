package com.example.tickit.Fragments;

import android.content.Intent;
import android.os.Bundle;

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
import android.widget.Toast;

import com.example.tickit.Adapters.ListViewMemberAdapter;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListUser;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.PopUps.AddMemberChoicePopUp;
import com.example.tickit.Activities.Profile;
import com.example.tickit.R;
import com.example.tickit.Classes.User;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Members extends Fragment {
    private User loggedInUser = MainActivity.getLoggedInUser();
    private ProgressBar progressBar;
    private ImageButton addMemberButton;
    private ListView listview;
    View view;

    public Members() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        assignViews();
        setAllowanceAddMembersButton(view);
        loadListView();

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        loadListView();
    }

    private void assignViews() {
        listview = (ListView) view.findViewById(R.id.membriListView);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        addMemberButton = (view.findViewById(R.id.addMembersButton));
    }

    private void loadListView() {
        UserDatabaseCalls.getUsers(new CallbackArrayListUser() {
            @Override
            public void callback(final ArrayList<User> users) {
                progressBar.setVisibility(View.GONE);
                if(MainActivity.getContext()!=null){
                    ListViewMemberAdapter adapter =new ListViewMemberAdapter(MainActivity.getContext(),R.layout.member_card,users);
                    adapter.notifyDataSetChanged();
                    listview.setAdapter(adapter);
                    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            startActivity(new Intent(getContext(), Profile.class).putExtra("userFromMembersList", users.get(position)));
                        }
                    });
                }

            }
        });
    }

    private void setAllowanceAddMembersButton(View view) {
        if(MainActivity.getUserGrade()>1){
            addMemberButton.setVisibility(View.GONE);
        }else{
            addMemberButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getContext(), AddMemberChoicePopUp.class));
                }
            });
        }
    }


}

