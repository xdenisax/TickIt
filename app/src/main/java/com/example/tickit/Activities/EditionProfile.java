package com.example.tickit.Activities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
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
import com.example.tickit.Classes.Edition;
import com.example.tickit.Classes.Mandate;
import com.example.tickit.Classes.User;
import com.example.tickit.R;

import java.util.ArrayList;

public class EditionProfile extends AppCompatActivity {
    Edition edition;
    TextView coordinator1TextView, coordinator2TextView,yearTextView;
    ListView editionMembersListView;
    ImageView coordinator1ImageView,coordinator2ImageView;
    Button strategyButton;
    ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edition_profile);

        assignViews();
        manageIntent(getIntent());
        strategyButtonPressed();
        backButtonPressed();
        coordinatorsPressed();
    }

    private void assignViews() {
        coordinator1TextView = (TextView) findViewById(R.id.coordinator1TextView);
        coordinator2TextView = (TextView) findViewById(R.id.coordinator2TextView);
        coordinator1ImageView = (ImageView) findViewById(R.id.editionCoordinator1ImageView);
        coordinator2ImageView = (ImageView) findViewById(R.id.editionCoordinator2ImageView);
        backButton = (ImageButton) findViewById(R.id.backButtonEditionActivity);
        editionMembersListView = (ListView) findViewById(R.id.editionMembersListView);
        yearTextView = (TextView) findViewById(R.id.editionYearTextView);
        strategyButton = (Button) findViewById(R.id.strategyButton);
    }

    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("editionFromProjectProfile")!= null) {
            edition = (Edition) intent.getParcelableExtra("editionFromProjectProfile");
            fillWithInfo(edition);
        }
    }

    private void fillWithInfo(Edition edition) {
        yearTextView.setText(edition.getYear());
        coordinator1TextView.setText(edition.getCoordinator1().getLastName() + " " + edition.getCoordinator1().getFirstName());
        coordinator2TextView.setText(edition.getCoordinator2().getLastName() + " " + edition.getCoordinator2().getFirstName());
        Glide.with(getApplicationContext()).load(edition.getCoordinator1().getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(coordinator1ImageView);
        Glide.with(getApplicationContext()).load(edition.getCoordinator2().getProfilePicture()).apply(RequestOptions.circleCropTransform()).into(coordinator2ImageView);
        editionMembersListView.setAdapter(new ListViewMemberAdapter(getApplicationContext(), R.layout.member_card, edition.getMembers()));
        setActionOnMembersListView(editionMembersListView);
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
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(edition.getStrategy())));
            }
        });
    }

    private void setActionOnMembersListView(ListView editionMembersListView) {
        editionMembersListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startActivity(new Intent(getApplicationContext(),Profile.class).putExtra("memberFromEditionProfile", edition.getMembers().get(position)));
            }
        });
    }

}
