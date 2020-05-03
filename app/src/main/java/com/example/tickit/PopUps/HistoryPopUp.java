package com.example.tickit.PopUps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.widget.ListView;

import com.example.tickit.Adapters.ListviewMemberHistoryAdapter;
import com.example.tickit.Activities.MainActivity;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.R;

import java.util.ArrayList;

public class HistoryPopUp extends AppCompatActivity {

    private  ListView historyListView;
    ArrayList<Mandate> mandatesArrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history_pop_up);

        setMetrics();
        manageIntent(getIntent());

    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableArrayListExtra("membersHistoryFromProfile")!= null) {
            mandatesArrayList = intent.getParcelableArrayListExtra("membersHistoryFromProfile");
            setListView(mandatesArrayList);
        }else{
            setListView(MainActivity.getLoggedInUser().getMandates());
        }
    }
    private void setListView(ArrayList<Mandate> mandatesArrayList) {
        historyListView = (ListView) findViewById(R.id.member_history_listview);
        ListviewMemberHistoryAdapter adapter = new ListviewMemberHistoryAdapter(getApplicationContext(),R.layout.member_history_card,mandatesArrayList);
        historyListView.setAdapter(adapter);
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.8));
    }
}
