package com.example.tickit.Utils;

import android.widget.EditText;
import android.widget.Toast;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class DateProcessing {
    public static  SimpleDateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");

    public static  boolean dateValidation(EditText date) {
        if(date.getText().length()!=10){
            return false;
        }else {
            int day = Integer.parseInt(date.getText().toString().substring(0, 2));
            int month = Integer.parseInt(date.getText().toString().substring(3, 5));
            int year = Integer.parseInt(date.getText().toString().substring(6, 10));

            if (!(day >= 1 && day <= 31)) {
                return false;
            } else {
                if (!(month >= 1 && month <= 12)) {
                    return false;
                } else {
                    if (!(year >= 2010 && year <= 2100)) {
                        return false;
                    } else {
                        return true;
                    }
                }
            }

        }
    }

    public static  Date getDate(EditText date) {
        Date startDate= new Date();
        try{
            startDate = dateFormat.parse(date.getText().toString());
        } catch (ParseException e) {
            startDate =null;
        }
        return startDate;
    }
}
