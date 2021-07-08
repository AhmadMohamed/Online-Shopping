package com.example.onlineshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class sign_in extends AppCompatActivity {

    private String sharedPrefFile ="com.example.onlineShopping";


    @Override
    public void onBackPressed() {

        // type your code here
        finish();
        moveTaskToBack(true);
        Intent I = new Intent(sign_in.this, MainActivity.class);
        startActivity(I);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);



        Intent intent = getIntent();
        String email = intent.getStringExtra("email");
        String pass = intent.getStringExtra("password");



        EditText Email = (EditText)findViewById(R.id.email);
        EditText password = (EditText)findViewById(R.id.password);
        Email.setText(email);
        password.setText(pass);

        shopping_DB customer = new shopping_DB(this);
        remember_me();
        Button btn1 = (Button)findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText Email = (EditText)findViewById(R.id.email);
                EditText password = (EditText)findViewById(R.id.password);
                CheckBox checkBox = (CheckBox)findViewById(R.id.checkBox1);

                if(Email.getText().toString().equals("admin") && password.getText().toString().equals("admin"))
                {
                    Intent I = new Intent(sign_in.this,product_category.class);
                    startActivity(I);
                }
                else if(customer.check_email_pass(Email.getText().toString(),password.getText().toString()))
                {
                    if(checkBox.isChecked())
                    {
                        SharedPreferences sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPref.edit();
                        editor.putString("email",Email.getText().toString());
                        editor.putString("password",password.getText().toString());
                        editor.apply();
                    }
                    Intent I = new Intent(sign_in.this,navigateProduct.class);
                    I.putExtra("email",Email.getText().toString());
                    I.putExtra("pass",password.getText().toString());
                    startActivity(I);

                }
                else
                    Toast.makeText(getApplicationContext(), "Wrong Email Or Password", Toast.LENGTH_SHORT).show();

            }
        });

        TextView forgetPass = (TextView)findViewById(R.id.forgetPass);
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I  = new Intent(sign_in.this, forget_pass.class);
                startActivity(I);
            }
        });

    }

    public void remember_me()
    {
        SharedPreferences sharedPref = getSharedPreferences(sharedPrefFile, MODE_PRIVATE);
        if(sharedPref.contains("email") && sharedPref.contains("password"))
        {
            String E = sharedPref.getString("email","not found");
            String password = sharedPref.getString("password","not found");
            EditText Email1 = (EditText)findViewById(R.id.email);
            EditText pass1 = (EditText)findViewById(R.id.password);
            Email1.setText(E);
            pass1.setText(password);

        }
    }
}