package com.example.tickit.Activities;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.tickit.Adapters.ListViewMemberAdapter;
import com.example.tickit.Callbacks.CallbackBoolean;
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.User;
import com.example.tickit.DataBaseCalls.ProjectDatabaseCalls;
import com.example.tickit.DataBaseCalls.UserDatabaseCalls;
import com.example.tickit.PopUps.AddStrategyPopUp;
import com.example.tickit.R;
import com.example.tickit.Utils.DateProcessing;
import com.google.firebase.firestore.FirebaseFirestore;

import org.apache.log4j.chainsaw.Main;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

public class EditionProfile extends AppCompatActivity {
    Edition edition;
    TextView coordinator1TextView, coordinator2TextView,yearTextView, editionNumberTextView;
    ListView editionMembersListView;
    ImageView coordinator1ImageView,coordinator2ImageView;
    Button strategyButton;
    ImageButton backButton, addMembersButton;
    String projectName, projectId;
    ListViewMemberAdapter adapter;

    int REQUEST_CODE_ADD_STRATEGY=21;
    int REQUEST_CODE_ADD_MEMBERS=22;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition_profile);
        assignViews();
        manageIntent(getIntent());

    }

    @Override
    protected void onStart() {
        super.onStart();
        setAllowanceOnViews();
        strategyButtonPressed();
        backButtonPressed();
        addMembersButtonPressed();
        coordinatorsPressed();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==REQUEST_CODE_ADD_STRATEGY&& resultCode==RESULT_OK && data!=null){
            if(data.getStringExtra("strategyLink")==null){
                Toast.makeText(getApplicationContext(), "Nu s-a primit link-ul.", Toast.LENGTH_LONG).show();
            }else{
                edition.setStrategy(data.getStringExtra("strategyLink"));
                ProjectDatabaseCalls.updateEditionStrategy(projectName, projectId, edition, new CallbackBoolean() {
                    @Override
                    public void callback(Boolean bool) {
                        Toast.makeText(getApplicationContext(), "S-a adaugat strategia in baza de date.", Toast.LENGTH_LONG).show();

                    }
                });
            }
        }

        if(requestCode == REQUEST_CODE_ADD_MEMBERS && resultCode==RESULT_OK && data!=null ){
            if(data.getParcelableArrayListExtra("newlyAddedMembers")==null && data.getParcelableArrayListExtra("newlyAddedDivisionLead")==null ){
                Toast.makeText(getApplicationContext(), "Nu s-a primit lista cu noii membri. Mai incearaca o data.", Toast.LENGTH_LONG).show();
            }else{
                ArrayList<User> newlyAddedMembers = new ArrayList<>();
                boolean isDivisionLeads = false;
                if(data.getParcelableArrayListExtra("newlyAddedDivisionLead")!=null ){
                    isDivisionLeads=true;
                    newlyAddedMembers = data.getParcelableArrayListExtra("newlyAddedDivisionLead");
                    Log.d("filteredList", String.valueOf(newlyAddedMembers));
                }
                if(data.getParcelableArrayListExtra("newlyAddedMembers")!=null ){
                    isDivisionLeads=false;
                    newlyAddedMembers = data.getParcelableArrayListExtra("newlyAddedMembers");
                }
                setNewMembers(newlyAddedMembers);
                addMandates(newlyAddedMembers, isDivisionLeads, projectName);
                ProjectDatabaseCalls.addEditionMembers(newlyAddedMembers, projectName,projectId,edition);
                Toast.makeText(getApplicationContext(), "S-au adaugat membrii.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void setNewMembers(ArrayList<User> newlyAddedMembers ) {
        if(edition.getMembers()!=null){
            newlyAddedMembers.addAll(edition.getMembers());
        }
        edition.setMembers(newlyAddedMembers);
        loadListView();
    }

    private void addMandates(ArrayList<User> users, boolean isDivisionLeads, String projectName) {
        for(User user:users){
            Mandate mandate  = new Mandate(
                    (FirebaseFirestore.getInstance()).collection("projects").document(projectId),
                    "Membru",
                    edition.getStartDate(),
                    edition.getStopDate(),
                    4);
            if(isDivisionLeads){
                mandate.setPosition("Lider de divizie");
                mandate.setGrade(3);
            }
            UserDatabaseCalls.addMandate(user.getEmail(), mandate, projectName);
        }
    }

    private void assignViews() {
        coordinator1TextView = (TextView) findViewById(R.id.coordinator1TextView);
        coordinator2TextView = (TextView) findViewById(R.id.coordinator2TextView);
        coordinator1ImageView = (ImageView) findViewById(R.id.editionCoordinator1ImageView);
        coordinator2ImageView = (ImageView) findViewById(R.id.editionCoordinator2ImageView);
        backButton = (ImageButton) findViewById(R.id.backButtonEditionActivity);
        editionMembersListView = (ListView) findViewById(R.id.editionMembersListView);
        editionNumberTextView = (TextView) findViewById(R.id.editionNumberTextView);
        yearTextView = (TextView) findViewById(R.id.editionYearTextView);

        strategyButton = (Button) findViewById(R.id.strategyButton);
        addMembersButton = (ImageButton) findViewById(R.id.addMembersEditionProfile);
        addMembersButton.setVisibility(View.GONE);
    }

    private void setAllowanceOnViews() {
        if(isAtLeast(3)){
            addMembersButton.setVisibility(View.VISIBLE);
        }
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("editionFromProjectProfile")!= null) {
            edition = (Edition) intent.getParcelableExtra("editionFromProjectProfile");
            projectId = intent.getStringExtra("projectIdFromProjectProfile");
            projectName = intent.getStringExtra("projectNameFromProjectProfile");
            fillWithInfo(edition);
        }
    }

    private void fillWithInfo(Edition edition) {
        yearTextView.setText(projectName+ " " +edition.getYear());
        if(edition.getCoordinator1().getLastName()==null){
            coordinator1TextView.setText(edition.getCoordinator1().getEmail());
            coordinator1ImageView.setVisibility(View.GONE);
        }else{
            coordinator1TextView.setText(edition.getCoordinator1().getLastName() + " " + edition.getCoordinator1().getFirstName());
        }
        editionNumberTextView.setText("editia "+ edition.getEditionNumber());
        if(edition.getCoordinator2()!=null){
            if(edition.getCoordinator2().getLastName()==null){
                coordinator2TextView.setText(edition.getCoordinator2().getEmail());
                coordinator2ImageView.setVisibility(View.GONE);
            }else{
                coordinator2TextView.setText(edition.getCoordinator2().getLastName() + " " + edition.getCoordinator2().getFirstName());
                Glide.with(getApplicationContext()).load(edition.getCoordinator2().getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(coordinator2ImageView);
            }
        }else{
            coordinator2TextView.setVisibility(View.GONE);
            coordinator2ImageView.setVisibility(View.GONE);
        }
        Glide.with(getApplicationContext()).load(edition.getCoordinator1().getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(coordinator1ImageView);
        loadListView();
        setActionOnMembersListView(editionMembersListView);
    }

    private void loadListView(){
        adapter= new ListViewMemberAdapter(getApplicationContext(), R.layout.member_card, edition.getMembers());
        adapter.notifyDataSetChanged();
        editionMembersListView.setAdapter(adapter);
    }

    private void coordinatorsPressed() {

        coordinator1ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class).putExtra("memberFromEditionProfile", edition.getCoordinator1()));
            }
        });

        coordinator1TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class).putExtra("memberFromEditionProfile", edition.getCoordinator1()));
            }
        });

        coordinator2ImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class).putExtra("memberFromEditionProfile", edition.getCoordinator2()));
            }
        });

        coordinator2TextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), Profile.class).putExtra("memberFromEditionProfile", edition.getCoordinator2()));
            }
        });
    }

    private void backButtonPressed() {
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void strategyButtonPressed() {
        strategyButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(edition.getStrategy()==null){
                    if(isAtLeast(2)){
                        launchAlertBox( null);
                    }else{
                        Toast.makeText(getApplicationContext(), "Inca nu a fost incarcata strategia.", Toast.LENGTH_LONG).show();
                    }
                }else{
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(edition.getStrategy())));
                }
            }
        });
    }

    private void addMembersButtonPressed() {
        addMembersButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(getApplicationContext(), AddMemberProject.class)
                                .putParcelableArrayListExtra("membersOfEdition",edition.getMembers())
                                .putExtra("coordinator1", edition.getCoordinator1())
                                .putExtra("coordinator2", edition.getCoordinator2())
                        ,REQUEST_CODE_ADD_MEMBERS);
            }
        });
    }

    private void launchAlertBox(final User user) {
        AlertDialog.Builder dialog = new AlertDialog.Builder(EditionProfile.this);
        if (user == null){
            dialog.setTitle("Doriti adaugarea strategiei?");
            dialog
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            startActivityForResult(new Intent(getApplicationContext(), AddStrategyPopUp.class), REQUEST_CODE_ADD_STRATEGY);
                        }
                    })
                    .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }else{
            dialog.setTitle("Doriti stergerea membrului?");
            dialog
                    .setPositiveButton("Da", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            edition.getMembers().remove(edition.getMembers().indexOf(user));
                            loadListView();
                            ProjectDatabaseCalls.removeEditionMember(user.getEmail(), projectName,projectId,edition);
                            UserDatabaseCalls.deleteMandate(user.getEmail(), projectName, edition);
                        }
                    })
                    .setNegativeButton("Nu", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.cancel();
                        }
                    });
        }
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void setActionOnMembersListView(ListView editionMembersListView) {
        editionMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),Profile.class).putExtra("memberFromEditionProfile", edition.getMembers().get(position)));
            }
        });
        if(isAtLeast(3)){
            editionMembersListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                @Override
                public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                    launchAlertBox(edition.getMembers().get(position));
                    return true;
                }
            });
        }
    }

    private boolean isAtLeast(int grade){
        if(MainActivity.getUserGrade() < 2){
            return true;
        }
        if(MainActivity.getUserGrade()>3){
            return false;
        }
        if(MainActivity.getCurrentMandates()==null){
            return false;
        }
        if(MainActivity.getCurrentMandates().size()==0){
            return false;
        }
        if(MainActivity.getCurrentMandates().containsKey(projectName+edition.getStopDate())){
            return MainActivity.getCurrentMandates().get(projectName+edition.getStopDate()).getStop_date().equals(edition.getStopDate())
                    && (MainActivity.getCurrentMandates().get(projectName+edition.getStopDate()).getGrade() <= grade);
        }else{
            return false;
        }
    }
}