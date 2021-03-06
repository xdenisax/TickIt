package com.example.tickit.Activities;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.PagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.DialogInterface;
import android.os.Bundle;

import com.example.tickit.Adapters.PageAdapter;
import com.example.tickit.R;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class FragmentsContainer extends AppCompatActivity {

    GoogleSignInClient mGoogleSignInClient;
    private TabLayout tabLayout;
    private ViewPager viewPager;
    private TabItem membersTab, projectsTab, openTasksTab, myTasksTab, dashboardTab;
    public PagerAdapter pagerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_fragments_container);

        assignViews();

        tabLayout.setupWithViewPager(viewPager);
        tabLayout.addOnTabSelectedListener(new TabLayout.ViewPagerOnTabSelectedListener(viewPager));

        viewPager.setAdapter(new PageAdapter(getSupportFragmentManager(), tabLayout.getTabCount()));
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        mGoogleSignInClient = GoogleSignIn.getClient(getApplicationContext(), gso);

        setTabText(tabLayout);
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }
            @Override
            public void onTabUnselected(TabLayout.Tab tab) { }
            @Override
            public void onTabReselected(TabLayout.Tab tab) { }
        });
    }

    private void assignViews() {
        dashboardTab = (TabItem) findViewById(R.id.dashboardTab);
        tabLayout = (TabLayout)findViewById(R.id.tabs);
        membersTab = (TabItem) findViewById(R.id.membersTab);
        projectsTab = (TabItem) findViewById(R.id.projectsTab);
        openTasksTab =(TabItem) findViewById(R.id.openedTasksTab);
        myTasksTab = (TabItem) findViewById(R.id.myTasksTab);
        viewPager = (ViewPager) findViewById(R.id.viewPager);
    }

    @Override
    public void onBackPressed() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(FragmentsContainer.this);
        dialog.setTitle("Doriti iesirea din aplicatie?");
        dialog.setPositiveButton("Da", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finishAffinity();
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        AlertDialog alertDialog = dialog.create();
        alertDialog.show();
    }

    private void setTabText(TabLayout tabLayout) {
        tabLayout.getTabAt(0).setText("Tablou de bord");
        tabLayout.getTabAt(1).setText("Membri");
        tabLayout.getTabAt(2).setText("Proiecte");
        tabLayout.getTabAt(3).setText("Task-uri deschise");
        tabLayout.getTabAt(4).setText("Task-urile mele");
    }


}
