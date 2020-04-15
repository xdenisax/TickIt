package com.example.tickit;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Members extends Fragment {
    ListView listview;
    FirebaseFirestore db;
    ArrayList<User> users;
    ListViewMemberAdapter adapter;
    User loggedInUser;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    ProgressBar progressBar;

    public Members() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contacts, container, false);
        listview = (ListView) view.findViewById(R.id.membriListView);
        loggedInUser = MainActivity.getLoggedInUser();
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);

        users = new ArrayList<>();
        getUsers(new CallbackArrayListUser() {
            @Override
            public void callback(final ArrayList<User> users) {
                progressBar.setVisibility(View.GONE);
                listview.setAdapter(new ListViewMemberAdapter(getContext(), R.layout.member_card, users));
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivity(new Intent(getContext(),Profile.class).putExtra("userFromMembersList", users.get(position)));
                    }
                });
            }
        });
        return view;
    }

    private void getUsers(final CallbackArrayListUser callbackArrayListUser) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    final List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    final ArrayList<User> usersFromFirestore = new ArrayList<>();
                    final int[] counter = {0};
                    for (final DocumentSnapshot d : list) {
                        getUser(d, new CallbackUser() {
                            @Override
                            public void callbackk(final User userFromFirestore) {
                                getMandates(d.getId(), new CallbackArrayListMandates() {
                                    @Override
                                    public void callback(ArrayList<Mandate> mandates) {
                                        User user = userFromFirestore;
                                        user.setMandates(mandates);
                                        if (!user.getEmail().equals(loggedInUser.getEmail())) {
                                            usersFromFirestore.add(user);
                                        }
                                        counter[0]++;
                                        Log.d("checkRef", "getUsers"+ counter[0]+list.size() + user);
                                        if(counter[0] == list.size()){
                                            callbackArrayListUser.callback(usersFromFirestore);
                                            Log.d("checkRef", "getUsers" + usersFromFirestore);
                                        }
                                    }
                                });
                            }
                        });
                    }
                }
            }
        });
    }

    private void getUser(final DocumentSnapshot documentSnapshot, final CallbackUser callbackUser){
        User user = documentSnapshot.toObject(User.class);
        user.setEmail(documentSnapshot.getId());
        callbackUser.callbackk(user);
    }

    private void getMandates(final String userID, final CallbackArrayListMandates callbackArrayListMandates) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        db
                .collection("users")
                .document(userID)
                .collection("mandates")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(final QuerySnapshot queryDocumentSnapshots) {
                        final ArrayList<Mandate> mandatesFromFirestore = new ArrayList<>();
                        if(queryDocumentSnapshots.isEmpty()){
                            mandatesFromFirestore.add(new Mandate());
                            callbackArrayListMandates.callback(mandatesFromFirestore);
                            Log.d("checkRef", "getMandatesNull" + mandatesFromFirestore.toString());
                        }
                        final int[] counter = {0};
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            initializeMandate(document, new CallbackMandate() {
                                @Override
                                public void callback(Mandate mandate) {
                                    mandatesFromFirestore.add(mandate);
                                    counter[0]++;
                                    if(counter[0] == queryDocumentSnapshots.size()){
                                        callbackArrayListMandates.callback(mandatesFromFirestore);
                                        Log.d("checkRef", "getMandates" + mandatesFromFirestore.toString());
                                    }
                                }
                            });
                        }
                    }
                });
    }

    private void initializeMandate(final QueryDocumentSnapshot document, final CallbackMandate callbackMandate) {
        final Mandate mandate = new Mandate();
        getProjName(document, new CallbackString() {
            @Override
            public void onCallBack(String value) {
                db = FirebaseFirestore.getInstance();
                Date startDate = ((Timestamp) document.get("start_date")).toDate();
                Date endDate = ((Timestamp) document.get("stop_date")).toDate();
                mandate.setProjectName(value);
                mandate.setEndDate(dateFormat.format(endDate));
                mandate.setStartDate(dateFormat.format(startDate));
                mandate.setGrade(Integer.parseInt(document.get("grade").toString()));
                mandate.setPosition(document.getString("position"));
                callbackMandate.callback(mandate);
            }
        });
    }

    private void getProjName(final QueryDocumentSnapshot document, final CallbackString callback) {
        final DocumentReference docRef = document.getDocumentReference("project_name");
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    String value = documentSnapshot.getString("name");
                    callback.onCallBack(value);
                } else {
                    Log.d("checkRef", "No such document");
                }
            }
        });

    }
}

