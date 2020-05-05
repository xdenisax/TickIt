package com.example.tickit.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Adapters.ListViewMemberAdapter;
import com.example.tickit.Callbacks.CallbackArrayListUser;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.google.api.LogDescriptor;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class AddEdition extends AppCompatActivity {

    EditText editionStartDate, editionStopDate, searchCoordinators, editionNumber;
    ListView membersListView;
    Button addEditionButton;
    ImageButton deleteSelectedCoordinatorsButton, backButton;
    TextView selectedCoordinators;
    String str="";
    ListViewMemberAdapter adapter;
    User coordinator1, coordinator2;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_edition);

        assignViews();
        searchListViewMembers();
        setListenerOnListView();
        deleteSelectedCoordinatorsButtonPressed();
        addEditionButtonPressed();
        backButtonPressed();
    }

    private void assignViews() {
        editionStartDate =(EditText) findViewById(R.id.editionStartDate);
        editionStopDate =(EditText) findViewById(R.id.editionStopDate);
        searchCoordinators =(EditText) findViewById(R.id.searchBarEditTextAddEdition);
        membersListView = (ListView) findViewById(R.id.searchCoordinatorListview);
        addEditionButton = (Button) findViewById(R.id.addEditionButton);
        deleteSelectedCoordinatorsButton = (ImageButton) findViewById(R.id.deleteSelectedCoordinatorsButton);
        backButton = (ImageButton) findViewById(R.id.backButtonAddEdition);
        selectedCoordinators = (TextView) findViewById(R.id.selectedCoordinators);
        editionNumber=(EditText) findViewById(R.id.editionNumber);
    }

    private void searchListViewMembers() {
        UserDatabaseCalls.getUsers(new CallbackArrayListUser() {
            @Override
            public void callback(final ArrayList<User> users) {
                adapter = new ListViewMemberAdapter(getApplicationContext(), R.layout.member_card, users);
                adapter.notifyDataSetChanged();
                membersListView.setAdapter(adapter);
            }
        });
    }

    private void setListenerOnListView() {
        membersListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        searchCoordinators.addTextChangedListener(new TextWatcher() {
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
        membersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (coordinator1==null){
                    str+=adapter.getItem(position).getEmail()+", ";
                    coordinator1=adapter.getItem(position);
                }else{
                    if(coordinator2==null){
                        str+=adapter.getItem(position).getEmail()+", ";
                        coordinator2=adapter.getItem(position);
                    }else {
                        makeToast("Ati introdus deja doi coordonatori");
                    }
                }
                selectedCoordinators.setText(str);
            }
        });
    }

    private void deleteSelectedCoordinatorsButtonPressed() {
        deleteSelectedCoordinatorsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectedCoordinators.setText(R.string.coordonatori);
                str="";
                coordinator1=null;
                coordinator2=null;
            }
        });
    }

    private void addEditionButtonPressed() {
        addEditionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    Edition newEdition =  new Edition(coordinator1,
                            coordinator2,
                            null,
                            null,
                            DateProcessing.getDate(editionStartDate),
                            DateProcessing.getDate(editionStopDate),
                            editionStopDate.getText().toString().substring(6),
                            editionNumber.getText().toString());
                    setResult(Activity.RESULT_OK, new Intent().putExtra("newAddedEdition", newEdition));
                    finish();
                }
            }
        });
    }

    private void backButtonPressed() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeToast("Nu a fost adaugata o editie noua.");
                finish();
            }
        });
    }

    private boolean validation() {
        if(editionNumber.getText().toString().length()<1){
            makeToast("Introduceti numarul editiei");
            return false;
        }
        if(!DateProcessing.dateValidation(editionStartDate)){
            makeToast("Ziua, luna sau anul nu sunt intr-un format corect. ex: 02-05-2020");
            return false;
        }
        if(DateProcessing.getDate(editionStartDate).after(DateProcessing.getDate(editionStopDate))){
            makeToast("Data de inceput trebuie sa fie inaintea celei de sfarsit.");
            return false;
        }
        if(coordinator1==null){
            makeToast("Nu ati introdus coordonatorii.");
            return false;
        }
        if(coordinator2 == null ) {
            launchAlertBox();
            return false;
        }

        return true;
    }

    private void launchAlertBox() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(AddEdition.this);
        dialog.setTitle("Sunteti sigur ca doriti adaugarea unui singur coordonator?");
        dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Edition newEdition =  new Edition(
                        coordinator1,
                        coordinator2,
                        null,
                        null,
                        DateProcessing.getDate(editionStartDate),
                        DateProcessing.getDate(editionStopDate),
                        editionStopDate.getText().toString().substring(6),
                        editionNumber.getText().toString());
                setResult(Activity.RESULT_OK, new Intent().putExtra("newAddedEdition", newEdition));
                finish();
            }
        }).setNegativeButton("Nu", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });

        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();
    }


}
