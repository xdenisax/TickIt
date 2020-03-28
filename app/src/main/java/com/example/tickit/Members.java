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
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class Members extends Fragment {
    ListView listview;
    FirebaseFirestore db;
    ArrayList<User> users ;
    public Members() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);
        users = new ArrayList<>();
        db= FirebaseFirestore.getInstance();
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot documentSnapshots) {
                if (documentSnapshots.isEmpty()) {
                    Log.d("database", "onSuccess: LIST EMPTY");
                } else {
                    List<User> userList = documentSnapshots.toObjects(User.class);
                    users.addAll(userList);
                }
            }
        });
        db.collection("users")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                users.add(new User(document.getString("firstName"),
                                        document.getString("laseName"),
                                        document.getString("phoneNumber"),
                                        document.getId(),
                                        document.getString("profilePicture"),
                                        document.getString("department")));
                                Log.d("database", users.toString());
                            }
                        } else {
                            Log.d("database", "Error getting documents: ", task.getException());
                        }
                    }
                });


        ListViewMemberAdapter adapter = new ListViewMemberAdapter(getContext(), R.layout.member_card, users);
        listview = (ListView) view.findViewById(R.id.membriListView);
        listview.setAdapter(adapter);

        return view;
    }


}
