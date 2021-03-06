package com.example.tickit.PopUps;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.tickit.R;

public class EditPopUp extends Activity {
    Button save, cancel;
    EditText phoneNumberEditText;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_pop_up);

        setMetrics();
        assignViews();
        setActionsOnSaveButton();
        setActionsOnCancelButton();

    }

    private void assignViews() {
        cancel = (Button) findViewById(R.id.cancelPopUp);
        save = (Button) findViewById(R.id.savePhoneNumberPopUp);
        phoneNumberEditText = (EditText)findViewById(R.id.phoneNumberEditText);
    }

    private void setActionsOnCancelButton() {
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(Activity.RESULT_CANCELED,new Intent());
                finish();
            }
        });
    }

    private void setActionsOnSaveButton() {
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!phoneNumberEditText.getText().toString().equals("")){
                    if(phoneNumberEditText.getText().toString().length()==10){
                        setResult(Activity.RESULT_OK,new Intent().putExtra("phoneNumberModified",phoneNumberEditText.getText().toString()));
                        finish();
                    }else{
                        Toast.makeText(EditPopUp.this, R.string.numar_de_telefon_mai_mult_de_10_cifre, Toast.LENGTH_LONG).show();
                    }
                }else{
                    setResult(Activity.RESULT_CANCELED,new Intent());
                    finish();
                }
            }
        });

    }

    private void setMetrics() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager(). getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeigth = dm.heightPixels;
        getWindow().setLayout((int) (windowWidth*0.8), (int) (windowHeigth*0.2));
    }
}
