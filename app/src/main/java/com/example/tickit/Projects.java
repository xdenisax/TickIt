package com.example.tickit;

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

import com.example.tickit.Callbacks.CallbackArrayListEditions;
import com.example.tickit.Callbacks.CallbackArrayListMandates;
import com.example.tickit.Callbacks.CallbackArrayListProjects;
import com.example.tickit.Callbacks.CallbackEdition;
import com.example.tickit.Callbacks.CallbackMandate;
import com.example.tickit.Callbacks.CallbackString;
import com.example.tickit.Callbacks.CallbackUser;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class Projects extends Fragment {
    ListView projectListview;
    ProgressBar progressBar;
    SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
    public Projects() { }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_projects, container, false);
        projectListview = (ListView) view.findViewById(R.id.projectsListvView);
        progressBar = (ProgressBar) view.findViewById(R.id.spin_kit);

        getProjects(new CallbackArrayListProjects() {
            @Override
            public void callback(final ArrayList<Project> projectsFromDataBase) {
                progressBar.setVisibility(View.GONE);
                if(getActivity()!=null) {
                    ListViewProjectsAdapter adapter = new ListViewProjectsAdapter(getContext(), R.layout.member_card, projectsFromDataBase);
                    adapter.notifyDataSetChanged();
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
        return view;
    }

    public void getProjects(final CallbackArrayListProjects callbackArrayListProjects){
        FirebaseFirestore database = FirebaseFirestore.getInstance();
        final ArrayList<Project> projectsFromDataBase = new ArrayList<>();
        database.collection("projects").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    final int[] contorIn = {0};
                    for (final QueryDocumentSnapshot document : task.getResult()) {
                        getEditions(document, new CallbackArrayListEditions() {
                            @Override
                            public void callback(ArrayList<Edition> editions) {
                                Project project = document.toObject(Project.class);
                                project.setEditions(editions);
                                projectsFromDataBase.add(project);
                                contorIn[0]++;
                                if(contorIn[0]==task.getResult().size() -1){ callbackArrayListProjects.callback(projectsFromDataBase);
                                    Log.d("projectCheck", "PROJECTS"+projectsFromDataBase.toString());}
                            }
                        });
                    }
                } else {
                    Log.d("projectsCheck", "Error getting documents.", task.getException());
                }
            }
        });
    }

    public void getEditions(QueryDocumentSnapshot document, final CallbackArrayListEditions callbackArrayListEditions){
        String subCollectionID= document.getString("name").substring(0,1).toLowerCase()
                +document.getString("name").substring(1).replace(" ","")
                + "Editions";
        final ArrayList<Edition> editions = new ArrayList<Edition>();

        (FirebaseFirestore.getInstance())
                .collection("projects")
                .document(document.getId())
                .collection(subCollectionID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull final Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            final int[] contorIn = {0};
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                initializeEdition(document, new CallbackEdition() {
                                    @Override
                                    public void callback(Edition edition) {
                                        editions.add(edition);
                                        contorIn[0]++;
                                        if (contorIn[0] == task.getResult().size()) {
                                            callbackArrayListEditions.callback(editions);
                                        }
                                    }
                                });
                            }
                        } else {
                            Log.d("projectCheck", "Error getting documents: ", task.getException());
                        }
                    }
                });

    }

    private void initializeEdition(final QueryDocumentSnapshot document, final CallbackEdition callbackEdition) {
        final Edition edition = new Edition();
        getUser(document.getDocumentReference("coordinator1"),  new CallbackUser() {
            @Override
            public void callbackk(final User user1) {
                getUser(document.getDocumentReference("coordinator2"),  new CallbackUser() {
                    @Override
                    public void callbackk(User user2) {
                        if(user2!=null){ edition.setCoordinator2(user2); }
                        edition.setCoordinator1(user1);
                        edition.setStrategy(document.getString("strategy"));
                        edition.setYear(String.valueOf(document.get("year")));
                        final ArrayList<DocumentReference> documentReferences = (ArrayList<DocumentReference>) document.get("members");
                        if(documentReferences!=null){
                            final ArrayList<User> members = new ArrayList<>();
                            final int[] contor = {0};
                            for(final DocumentReference docRef: documentReferences){
                                getUser(docRef, new CallbackUser() {
                                    @Override
                                    public void callbackk(User user) {
                                        members.add(user);
                                        contor[0]++;
                                        if(contor[0] == documentReferences.size()){
                                            edition.setMembers(members);
                                            callbackEdition.callback(edition);
                                        }
                                    }
                                });
                            }

                        }else{
                            Log.d("projectCheck", "nullnullnull"+edition.getStrategy()+ " " +edition.getYear()+edition.getCoordinator1());
                        }
                    }
                });
            }
        });
    }

    private void getUser(final DocumentReference docRef, final CallbackUser callbackUser){
        if(docRef!=null){
            docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(final DocumentSnapshot documentSnapshot) {
                    getMandates(documentSnapshot.getId(), new CallbackArrayListMandates() {
                        @Override
                        public void callback(ArrayList<Mandate> mandates) {
                            User user = documentSnapshot.toObject(User.class);
                            user.setEmail(documentSnapshot.getId());
                            user.setMandates(mandates);
                            Log.d("mandatesCheck", mandates.toString());
                            callbackUser.callbackk(user);
                        }
                    });

                }
            });
        }else{
            Log.d("projectsCheck",docRef+" NULLLLLLLLLLL"  );
        }
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
                FirebaseFirestore db = FirebaseFirestore.getInstance();
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
