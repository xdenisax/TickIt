package com.example.tickit;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
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
    ArrayList<User> users;
    ListViewMemberAdapter adapter;
    public Members() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_contacts, container, false);
            FirebaseFirestore db = FirebaseFirestore.getInstance();
            users = new ArrayList<>();
//            users.add(new User("Denisa", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa1", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa2", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa3", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"));
//            users.add(new User("Denisa4", "Calota","0720151958", "calota.denisa14@gmail.com", "https://lh3.googleusercontent.com/a-/AOh14GibjAFrsjA1HF5hpV-Mgv-Suwm3dhnkilR3X-CwEtw" ,"Fundraising"))
            listview = (ListView) view.findViewById(R.id.membriListView);

            db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    if (!queryDocumentSnapshots.isEmpty()) {
                        List<DocumentSnapshot> list = queryDocumentSnapshots.getDocuments();
                        for (DocumentSnapshot d : list) {
                            User user = d.toObject(User.class);
                            user.setEmail(d.getId());
                            users.add(user);
                        }
                        Log.d("database", "cred"+ users.toString());
                        adapter = new ListViewMemberAdapter(getContext(), R.layout.member_card, users);
                        listview.setAdapter(adapter);
                    }
                }
            });

            listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                    startActivityForResult(new Intent(getContext(),Profile.class).putExtra("userFromMembersList", users.get(position)),
                            getContext().getResources().getInteger(R.integer.REQUEST_CODE_MEMBERS_LIST));
                }
            });

        return view;
    }


}
