package com.example.onlineshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class sign_up extends AppCompatActivity {

    private String sharedPrefFile ="com.example.onlineShopping";
    private DatePickerDialog.OnDateSetListener DSL;
    private shopping_DB shoppingDb;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
        shoppingDb = new shopping_DB(this);


        SharedPreferences sharedPreferences = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        SharedPreferences.Editor preferencesEditor = sharedPreferences.edit();



        EditText BD = (EditText)findViewById(R.id.cusBirthData);
        BD.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if(hasFocus)
                {
                    Calendar C = Calendar.getInstance();
                    int year = C.get(Calendar.YEAR);
                    int month = C.get(Calendar.MONTH);
                    int day = C.get(Calendar.DAY_OF_MONTH);

                    DatePickerDialog dialog = new DatePickerDialog(sign_up.this,
                            android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,DSL,year,month,day);
                    dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                    dialog.show();
                }
            }
        });


        BD.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Calendar C = Calendar.getInstance();
                int year = C.get(Calendar.YEAR);
                int month = C.get(Calendar.MONTH);
                int day = C.get(Calendar.DAY_OF_MONTH);

                DatePickerDialog dialog = new DatePickerDialog(sign_up.this,
                        android.R.style.Theme_Holo_Light_Dialog_NoActionBar_MinWidth,DSL,year,month,day);
                dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                dialog.show();

            }
        });
        DSL = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month++;
                String Date = year +"-"+ month +"-"+ dayOfMonth;
                BD.setText(Date);
            }
        };


        Button register = (Button)findViewById(R.id.register);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText ID = (EditText)findViewById(R.id.cusID);
                EditText name = (EditText)findViewById(R.id.cusName);
                EditText email = (EditText)findViewById(R.id.cusEmail);
                EditText password = (EditText)findViewById(R.id.cusPassword);
                EditText job = (EditText)findViewById(R.id.cusJob);
                RadioButton male = (RadioButton)findViewById(R.id.male);
                RadioButton female = (RadioButton)findViewById(R.id.female);
                EditText birthData = (EditText)findViewById(R.id.cusBirthData);
                EditText pinCode = (EditText)findViewById(R.id.cusPinCode);

                String gender;
                if(male.isChecked())
                    gender = male.getText().toString();
                else
                    gender = female.getText().toString();

                if(ID.getText().toString().equals("")||name.getText().toString().equals("")||email.getText().toString().equals("")||
                        password.getText().toString().equals("")||job.getText().toString().equals("")||birthData.getText().toString().equals("")||pinCode.getText().toString().equals(""))
                {
                    Toast.makeText(getApplicationContext(), "please enter an empty field", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    shoppingDb.insertCustomer(ID.getText().toString(),name.getText().toString(),email.getText().toString(),password.getText().toString(),
                            pinCode.getText().toString(), birthData.getText().toString(), gender,job.getText().toString());

                    Toast.makeText(getApplicationContext(), "Register Successfully", Toast.LENGTH_SHORT).show();

                    Intent I = new Intent(sign_up.this, sign_in.class);
                    I.putExtra("email", email.getText().toString());
                    I.putExtra("password", password.getText().toString());
                    startActivity(I);

                }

            }
        });
    }
}