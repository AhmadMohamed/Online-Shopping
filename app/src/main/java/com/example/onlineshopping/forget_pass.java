package com.example.onlineshopping;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class forget_pass extends AppCompatActivity {

    EditText email;
    EditText pinCode;
    Button changePass;
    EditText pass;
    EditText confirmPass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_pass);



        Button btn1 = (Button)findViewById(R.id.btn1);
        shopping_DB Customer = new shopping_DB(this);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (EditText)findViewById(R.id.email);
                pinCode = (EditText)findViewById(R.id.pinCode);

                pass = (EditText)findViewById(R.id.password);
                confirmPass = (EditText)findViewById(R.id.confirm_password);
                changePass = (Button)findViewById(R.id.changePass);

                if(!email.getText().toString().equals("")&&!pinCode.getText().toString().equals(""))
                {
                    if(Customer.check_email_pinCode(email.getText().toString(), pinCode.getText().toString()))
                    {
                        pass.setVisibility(View.VISIBLE);
                        confirmPass.setVisibility(View.VISIBLE);
                        changePass.setVisibility(View.VISIBLE);

                    }
                    else
                        Toast.makeText(forget_pass.this, "Wrong Email or pin code", Toast.LENGTH_SHORT).show();

                }
                else{
                    Toast.makeText(forget_pass.this, "Fill the empty fields", Toast.LENGTH_SHORT).show();
                }
            }
        });


        changePass = (Button)findViewById(R.id.changePass);
        changePass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = (EditText)findViewById(R.id.email);
                pinCode = (EditText)findViewById(R.id.pinCode);
                pass = (EditText)findViewById(R.id.password);
                confirmPass = (EditText)findViewById(R.id.confirm_password);
                if(!pass.getText().toString().equals("")&&!confirmPass.getText().toString().equals(""))
                {

                    if(pass.getText().toString().equals(confirmPass.getText().toString()))
                    {
                        try {
                            if(Customer.change_password(email.getText().toString(), pinCode.getText().toString(),pass.getText().toString())){
                                Toast.makeText(forget_pass.this, "password changed", Toast.LENGTH_SHORT).show();

                                SharedPreferences sharedPref = getSharedPreferences("com.example.onlineShopping", MODE_PRIVATE);
                                SharedPreferences.Editor editor = sharedPref.edit();
                                editor.clear();
                                editor.apply();
                                Intent I = new Intent(forget_pass.this, sign_in.class);
                                startActivity(I);
                            }
                        }
                        catch (Exception Ex) {
                            Toast.makeText(forget_pass.this, Ex.getMessage().toString(), Toast.LENGTH_LONG).show();
                        }
                    }
                }
                else
                    Toast.makeText(forget_pass.this, "Fill the empty fields", Toast.LENGTH_SHORT).show();
            }
        });

    }

}