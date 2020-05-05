package com.example.tickit.PopUps;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tickit.R;

import java.net.URL;

public class AddStrategyPopUp extends AppCompatActivity {

    EditText strategyLink;
    Button saveButton, cancelButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_strategy_pop_up);

        setMetrics();
        assignViews();
        saveButtonPressed();

    }

    private void assignViews() {
        saveButton = (Button) findViewById(R.id.saveStrategy);
        cancelButton = (Button) findViewById(R.id.cancelStrategyPopUp);
        strategyLink = (EditText) findViewById(R.id.strategyLinkEditText);
    }

    private void saveButtonPressed() {
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(validation()){
                    setResult(RESULT_OK, new Intent().putExtra("strategyLink", strategyLink.getText().toString()));
                    finish();
                }
            }
        });
    }

    private boolean validation() {
        if(strategyLink.getText().toString().length()<5){
            makeToast("Nu ati introdus link-ul.");
            return false;
        }
        if(!Patterns.WEB_URL.matcher(strategyLink.getText().toString()).matches()){
            makeToast("URL invalid");
            return false;
        }
        if(!strategyLink.getText().toString().contains("http")){
            makeToast("Nu s-a putut gasi protocolul. Link-ul contine http?");
            return false;
        }
        return true;
    }

    private void makeToast(String s) {
        Toast.makeText(getApplicationContext(), s,Toast.LENGTH_LONG).show();
    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.2));
    }
}
