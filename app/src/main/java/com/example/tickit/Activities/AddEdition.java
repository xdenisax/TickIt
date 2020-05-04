package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import com.example.tickit.Adapters.ListViewMemberAdapter;
import com.example.tickit.Callbacks.CallbackArrayListUser;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AddEdition extends AppCompatActivity {

    EditText editionStartDate, editionStopDate, searchCoordionators;
    ListView membersListView;
    Button addEditionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edition);

        assignViews();
        loadListViewMembers();
        addEditionButtonPressed();
        search();

    }

    private void assignViews() {
        editionStartDate =(EditText) findViewById(R.id.editionStartDate);
        editionStopDate =(EditText) findViewById(R.id.editionStopDate);
        searchCoordionators =(EditText) findViewById(R.id.searchBarEditTextAddEdition);
        membersListView = (ListView) findViewById(R.id.searchCoordinatorListview);
        addEditionButton = (Button) findViewById(R.id.addEditionButton);
    }

    private void loadListViewMembers() {
        UserDatabaseCalls.getUsers(new CallbackArrayListUser() {
            @Override
            public void callback(ArrayList<User> users) {
                final ListViewMemberAdapter adapter = new ListViewMemberAdapter(getApplicationContext(), R.layout.member_card, users);
                adapter.notifyDataSetChanged();
                membersListView.setAdapter(adapter);

                searchCoordionators.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        adapter.getFilter().filter(s.toString());
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                    }
                });
            }
        });
    }

    private void search() {

    }

    private void addEditionButtonPressed() {
        addEditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    //build edition
                    //add edition in db
                }
            }
        });
    }

    private boolean validation() {
        if(!DateProcessing.dateValidation(editionStartDate)){
            makeToast("Ziua, luna sau anul nu sunt intr-un format corect. ex: 02-05-2020");
            return false;
        }
        if(DateProcessing.getDate(editionStartDate).after(DateProcessing.getDate(editionStopDate))){
            makeToast("Data de inceput trebuie sa fie inaintea celei de sfarsit.");
            return false;
        }
        return true;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();
    }


}
