package com.example.tickit.PopUps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DownloadManager;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;

import com.example.tickit.Adapters.ListviewMemberHistoryAdapter;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.R;
import com.example.tickit.RecyclerViewAdapters.MandatesAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.util.ArrayList;

public class HistoryPopUp extends AppCompatActivity {

    private  ListView historyListView;
    ArrayList<Mandate> mandatesArrayList = new ArrayList<>();
    MandatesAdapter adapter;
    RecyclerView recyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pop_up);

        setMetrics();
        manageIntent(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.startListening();
    }

    private void manageIntent(Intent intent) {
        if(intent.getStringExtra("memberEmailFromProfile")!= null) {
           setUpRecyclerView(intent.getStringExtra("memberEmailFromProfile"));
        }else{
            setUpRecyclerView(MainActivity.getLoggedInUser().getEmail());
        }
    }

    private void setUpRecyclerView(String email){
        Query query = FirebaseFirestore.getInstance().collection("users").document(email).collection("mandates");
        FirestoreRecyclerOptions<Mandate> options = new FirestoreRecyclerOptions.Builder<Mandate>()
                .setQuery(query, Mandate.class)
                .build();

        adapter = new MandatesAdapter(options);
        recyclerView = findViewById(R.id.mandatesRecyclerView);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.8));
    }
}
