package com.example.tickit.Fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.tickit.Adapters.ListViewMemberAdapter;
import com.example.tickit.Adapters.SpinnerStringAdapter;
import com.example.tickit.Adapters.SpinnerYearAdapter;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListUser;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.PopUps.AddMemberChoicePopUp;
import com.example.tickit.Activities.Profile;
import com.example.tickit.R;
import com.example.tickit.Classes.User;
import com.example.tickit.RecyclerViewAdapters.MemberAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.reflect.Member;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Members extends Fragment {
    private User loggedInUser = MainActivity.getLoggedInUser();
    private static ProgressBar progressBar;
    private Spinner spinnerDepartments;
    private ImageButton addMemberButton;
    private View view;
    private RecyclerView recyclerView;
    private MemberAdapter adapter;

    public Members() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_contacts, container, false);

        assignViews();
        setAllowanceAddMembersButton();
        setUpRecyclerView(0);
        setSpinnerUp();
        listenOnSpinnerChanges();

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
        recyclerView = (RecyclerView) view.findViewById(R.id.membersRecyclerView);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);
        addMemberButton = (view.findViewById(R.id.addMembersButton));
        spinnerDepartments = (Spinner) view.findViewById(R.id.spinnerFilterByDepartments);
        spinnerDepartments.setSelection(0);
    }

    private void setSpinnerUp() {
        if(MainActivity.getContext()!=null){
            spinnerDepartments.setAdapter(new SpinnerStringAdapter(MainActivity.getContext(), getResources().getStringArray(R.array.departments)));
        }
    }

    private void setAllowanceAddMembersButton() {
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

    private void listenOnSpinnerChanges() {
        final boolean[] pressedOnce = {false};
        spinnerDepartments.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                if (pressedOnce[0]) {
                    adapter.stopListening();
                    setUpRecyclerView(position);
                    adapter.startListening();
                }
                if(!pressedOnce[0]){
                    pressedOnce[0] =true;
                }
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setUpRecyclerView(int position) {
        Query query;
        if(position==0){
            query = FirebaseFirestore.getInstance().collection("users");
        }else{
            String department= getResources().getStringArray(R.array.departments)[position];
            Log.d("mandate", department);
            query = FirebaseFirestore.getInstance().collection("users").whereEqualTo("departament", department);
        }
        FirestoreRecyclerOptions<User> options = new FirestoreRecyclerOptions.Builder<User>()
                .setQuery(query, User.class)
                .build();

        adapter=new MemberAdapter(options);
        adapter.notifyDataSetChanged();
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        setClickListeners(adapter);
    }


    private void setClickListeners(MemberAdapter adapter) {
        adapter.setOnItemClickListener(new MemberAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(DocumentSnapshot memberReference, int position) {
                startActivity(new Intent(getContext(), Profile.class).putExtra("userFromMembersList", memberReference.toObject(User.class)));
            }
        });
        if(MainActivity.getUserGrade()<2){
            adapter.setOnItemLongClickListener(new MemberAdapter.OnItemLongClickListener() {
                @Override
                public void onItemLongClick(DocumentSnapshot memberReference, int position) {
                    launchAlertDialog(memberReference.getReference(), getContext());
                }
            });
        }
    }

    private void launchAlertDialog(final DocumentReference documentReference, Context context) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(context);
        dialog.setTitle("Doriti stergerea membrului?")
                .setMessage("Atentie! Stergerea este ireversibila.");
        dialog
                .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        UserDatabaseCalls.deleteMember(documentReference);
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

    public static void stopProgressBar(){
        progressBar.setVisibility(View.GONE);
    }

}

