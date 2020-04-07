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
    public Members() { }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);
        loggedInUser=MainActivity.getLoggedInUser();


        users = new ArrayList<>();
//            users.add(new User("Denisa", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa1", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa2", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa3", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa4", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"))
        listview = (ListView) view.findViewById(R.id.membriListView);

        getUsers(new CallbackArrayListUser() {
            @Override
            public void callback(ArrayList<User> usersCallBack) {
                users.addAll(usersCallBack);
                Log.d("checkRef", "USERSSS"+users.toString());
                adapter = new ListViewMemberAdapter(getContext(), R.layout.member_card, users);
                listview.setAdapter(adapter);
                listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        startActivityForResult(new Intent(getContext(),Profile.class).putExtra("userFromMembersList", users.get(position)),
                                getContext().getResources().getInteger(R.integer.REQUEST_CODE_MEMBERS_LIST));
                    }
                });
            }
        });


        return view;
    }

    private void getUsers(final CallbackArrayListUser callbackArrayListUser){
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                if (!queryDocumentSnapshots.isEmpty()) {
                    List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                    ArrayList<User> usersFromFirestore = new ArrayList<>();
                    for (DocumentSnapshot d : list) {
                        final User user = d.toObject(User.class);
                        user.setEmail(d.getId());
                        getMandates(d.getId(), new CallbackArrayListMandates() {
                            @Override
                            public void callback(ArrayList<Mandate> mandates) {
                                user.setMandates(mandates);
                                Log.d("checkRef", "oncreate"+user.getMandates().toString());
                            }
                        });
                        Log.d("checkRef", "oncreate"+user.getMandates().toString());
                        if(!user.getEmail().equals(loggedInUser.getEmail())){
                            usersFromFirestore.add(user);
                        }
                    }
                    callbackArrayListUser.callback(usersFromFirestore);
                }
            }
        });
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
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    final ArrayList<Mandate> mandatesFromFirestore = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        initializeMandate(document, new CallbackMandate() {
                            @Override
                            public void callback(Mandate mandate) {
                                mandatesFromFirestore.add(mandate);
                                Log.d("checkRef", "mndates"+mandate.toString());
                            }
                        });
                    }
                    callbackArrayListMandates.callback(mandatesFromFirestore);
            }
         });
    }

    private void initializeMandate(final QueryDocumentSnapshot document, final CallbackMandate callbackMandate) {
        final Mandate mandate= new Mandate();

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
                Log.d("checkRef", "apel" +mandate.getProjectName());
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
                    Log.d("checkRef","get"+value);
                    callback.onCallBack(value);
                } else {
                    Log.d("checkRef", "No such document");
                }
            }
        });
    }



}
