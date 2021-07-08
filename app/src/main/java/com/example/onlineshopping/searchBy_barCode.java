package com.example.onlineshopping;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class searchBy_barCode extends AppCompatActivity {

    EditText prodName;
    EditText prodPrice;
    EditText prodQuantity;
    EditText prodBarCode;
    ImageView prodImage;
    shopping_DB shoppingDb;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_search_by_bar_code);



        shoppingDb = new shopping_DB(this);
        prodName = (EditText)findViewById(R.id.search_prodName);
        prodPrice = (EditText)findViewById(R.id.search_prodPrice);
        prodQuantity = (EditText)findViewById(R.id.search_prodQuan);
        prodBarCode = (EditText)findViewById(R.id.search_Bar_code);
        prodImage = (ImageView) findViewById(R.id.search_prodImg);
        ScanBarCode();



        Button scanBarCode = (Button)findViewById(R.id.scanBarCodeBtn);
        scanBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                prodBarCode = (EditText)findViewById(R.id.search_Bar_code);
                ScanBarCode();

            }
        });



    }

    public void ScanBarCode(){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        prodBarCode = (EditText)findViewById(R.id.search_Bar_code);
            IntentResult intentResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            if (intentResult != null)
            {
                if (intentResult.getContents() == null)
                {
                    prodBarCode.setText("");
                }
                else
                {
                    prodBarCode.setText(intentResult.getContents());
                    showProdDetails(prodBarCode.getText().toString());

                }
            }
        super.onActivityResult(requestCode, resultCode, data);
    }




    public void showProdDetails(String S)
    {
        try {
            Cursor C = shoppingDb.getProdDetails(S);
            prodName.setText(C.getString(0));
            prodPrice.setText("price: "+C.getString(1)+"$");
            prodQuantity.setText("Quantity: "+C.getString(3));
            byte[]arr = C.getBlob(2);
            Bitmap bitmap = BitmapFactory.decodeByteArray(arr,0, arr.length);
            prodImage.setImageBitmap(bitmap);
        }
        catch (Exception E)
        {Toast.makeText(getApplicationContext(),E.getMessage(),Toast.LENGTH_LONG).show();}


    }
}