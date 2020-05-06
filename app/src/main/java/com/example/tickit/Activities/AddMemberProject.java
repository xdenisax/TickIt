package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tickit.Adapters.ListViewMemberAdapter;
import com.example.tickit.Callbacks.CallbackArrayListUser;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.R;
import com.google.firebase.firestore.DocumentReference;

import java.lang.annotation.Documented;
import java.util.ArrayList;

public class AddMemberProject extends AppCompatActivity {

    ImageButton backButton, deleteSelectedMembers;
    EditText searchBar;
    TextView selectedMembers;
    Button saveMembersButton;
    ListView memberListView;
    ListViewMemberAdapter adapter;
    ArrayList<User > alreadyMembers;
    ArrayList<User> newlyAddedMembers;
    CheckBox isDivisionLead;
    String addedMembersString="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_member_project);

        assignViews();
        manageIntent(getIntent());
        backButtonPressed();
        saveMembersButtonPressed();
        deleteSelectedMembersPressed();
    }

    @Override
    protected void onStart() {
        super.onStart();
        loadListView();
        searchInListView();
        listViewItemPressed();
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableArrayListExtra("membersOfEdition")!=null){
            alreadyMembers =intent.getParcelableArrayListExtra("membersOfEdition");
            if(alreadyMembers==null){
                alreadyMembers=new ArrayList<>();
            }
            alreadyMembers.add((User) intent.getParcelableExtra("coordinator1"));
            alreadyMembers.add((User) intent.getParcelableExtra("coordinator2"));
        }
    }

    private void assignViews() {
        backButton = (ImageButton) findViewById(R.id.backButtonAddMembers);
        deleteSelectedMembers = (ImageButton) findViewById(R.id.deleteSelectedMembersButton);
        searchBar = (EditText) findViewById(R.id.searchBarEditTextAddMembers);
        selectedMembers = (TextView) findViewById(R.id.selectedMembers);
        saveMembersButton = (Button ) findViewById(R.id.addMembersButton);
        memberListView = (ListView)findViewById(R.id.searchMembersListView);
        isDivisionLead = (CheckBox) findViewById(R.id.isDivisionLeadCheckbox);
    }

    private void loadListView() {
        UserDatabaseCalls.getMembersOutsideProject(alreadyMembers, new CallbackArrayListUser() {
            @Override
            public void callback(final ArrayList<User> users) {
                adapter = new ListViewMemberAdapter(getApplicationContext(), R.layout.member_card, users);
                adapter.notifyDataSetChanged();
                memberListView.setAdapter(adapter);
            }
        });
    }

    private void searchInListView() {
        memberListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                adapter.getFilter().filter(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) { }
        });


    }

    private void listViewItemPressed() {
        memberListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(newlyAddedMembers!=null){
                    if(newlyAddedMembers.contains(adapter.getItem(position))){
                        Toast.makeText(getApplicationContext(), "Deja a fost adaugat acest membru", Toast.LENGTH_LONG).show();
                    }else {
                        newlyAddedMembers.add(adapter.getItem(position));
                        addedMembersString += ", " + newlyAddedMembers.get(newlyAddedMembers.size()-1).getEmail();
                        selectedMembers.setText(addedMembersString);
                    }
                }else{
                    newlyAddedMembers = new ArrayList<>();
                    newlyAddedMembers.add(adapter.getItem(position));
                    addedMembersString += newlyAddedMembers.get(newlyAddedMembers.size()-1).getEmail();
                    selectedMembers.setText(addedMembersString);
                }
            }
        });
    }

    private void deleteSelectedMembersPressed() {
        deleteSelectedMembers.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newlyAddedMembers!=null){
                    if(newlyAddedMembers.size()==1){
                        selectedMembers.setText(R.string.membri);
                        addedMembersString ="";
                        newlyAddedMembers =null;
                    }else{
                        addedMembersString = addedMembersString.substring(0,addedMembersString.lastIndexOf(","));
                        newlyAddedMembers.remove(newlyAddedMembers.size()-1);
                        selectedMembers.setText(addedMembersString);
                    }
                }
            }
        });
    }

    private void saveMembersButtonPressed() {
        saveMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(newlyAddedMembers!=null){
                    if(newlyAddedMembers.size()>0){
                        if(isDivisionLead.isChecked()){
                            setResult(RESULT_OK, new Intent().putParcelableArrayListExtra("newlyAddedDivisionLead", newlyAddedMembers));
                            finish();
                        }else{
                            setResult(RESULT_OK, new Intent().putParcelableArrayListExtra("newlyAddedMembers", newlyAddedMembers));
                            finish();
                        }
                    }else{
                        Toast.makeText(getApplicationContext(), "Nu s-a selectat niciun membru. ", Toast.LENGTH_LONG).show();
                    }
                }else{
                    Toast.makeText(getApplicationContext(), "Nu s-a selectat niciun membru. ", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    private void backButtonPressed() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Nu s-au adaugat membrii.", Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }
}
