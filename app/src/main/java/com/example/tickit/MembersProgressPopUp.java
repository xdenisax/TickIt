package com.example.tickit;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.Toast;

import com.github.ybq.android.spinkit.style.Wave;

import me.itangqi.waveloadingview.WaveLoadingView;

public class MembersProgressPopUp extends AppCompatActivity {

    private SeekBar progressSeekBar;
    private WaveLoadingView progressWave;
    private AssumedTasksSituation task;
    private Button saveButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_members_progress_pop_up);
        progressSeekBar = (SeekBar) findViewById(R.id.memberProgressSeekBar);
        progressWave = (WaveLoadingView) findViewById(R.id.memberProgressWave);
        saveButton = (Button) findViewById(R.id.saveProgressButton);

        manageIntent(getIntent());
        setActionsOnProgressBar();
        saveProgressButtonPressed();
        setMetrics();
    }


    private void manageIntent(Intent intent) {
        if(intent.getParcelableExtra("memberFromTaskProfile")!= null) {
            fillWithInfo((AssumedTasksSituation) intent.getParcelableExtra("memberFromTaskProfile"));
            task = (AssumedTasksSituation) intent.getParcelableExtra("memberFromTaskProfile");
            progressSeekBar.setEnabled(false);
        }
        if(intent.getParcelableExtra("personalProgressFromTaskProfile")!= null) {
            fillWithInfo((AssumedTasksSituation) intent.getParcelableExtra("personalProgressFromTaskProfile"));
            task = (AssumedTasksSituation) intent.getParcelableExtra("personalProgressFromTaskProfile");
            progressSeekBar.setEnabled(true);
        }

    }

    private void fillWithInfo(AssumedTasksSituation memberFromTaskProfile) {
        progressSeekBar.setProgress(memberFromTaskProfile.getProgress());
        progressWave.setProgressValue(memberFromTaskProfile.getProgress()*50);
        if(memberFromTaskProfile.getProgress()==0){
            progressWave.setBottomTitle("Asumat");
            progressWave.setTopTitle("");
            progressWave.setCenterTitle("");
        }else if(memberFromTaskProfile.getProgress()==1) {
            progressWave.setCenterTitle("Documentat");
            progressWave.setBottomTitle("");
            progressWave.setTopTitle("");
        }else if(memberFromTaskProfile.getProgress()==2){
            progressWave.setTopTitle("Implementat");
            progressWave.setCenterTitle("");
            progressWave.setBottomTitle("");
        }
    }

    private void setActionsOnProgressBar() {
        progressWave.setBottomTitle("Asumat");
        progressSeekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                progressWave.setProgressValue(progress*50);
                if(progress==0){
                    progressWave.setBottomTitle("Asumat");
                }else if(progress==1) {
                    progressWave.setCenterTitle("Documentat");
                }else if(progress==2){
                    progressWave.setTopTitle("Implementat");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    private void saveProgressButtonPressed() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                task.setProgress(progressSeekBar.getProgress());
                setResult(RESULT_OK, new Intent().putExtra("updatedProgress", progressSeekBar.getProgress()));
                finish();
            }
        });
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.5));
    }
}
