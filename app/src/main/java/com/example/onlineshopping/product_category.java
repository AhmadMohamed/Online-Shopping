package com.example.onlineshopping;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Gallery;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URI;
import java.util.ArrayList;

public class product_category extends AppCompatActivity {

    final static int GALLERY_REQUEST_CODE= 101;
    final static int Camera_REQUEST_CODE= 1888;
    int x=0;
    ImageView imgView;
    EditText prodBarCode;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_category);
         imgView = (ImageView)findViewById(R.id.prodImg);

        shopping_DB shopping_db = new shopping_DB(this);
        shopping_db.addCategories();

        Spinner spn = (Spinner)findViewById(R.id.prodCat);
        Cursor cursor = shopping_db.getCatName();
        ArrayAdapter<String> arr  = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);
        spn.setAdapter(arr);
        while (!cursor.isAfterLast())
        {
            arr.add(cursor.getString(0).toString());
            cursor.moveToNext();
        }

        Button chooseImg = (Button)findViewById(R.id.chooseImg);
        chooseImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chooseImage();
            }
        });
       // byte[] arr = imageViewToByte(imgView);
        Button addProd = (Button)findViewById(R.id.addProd);
        addProd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText name = (EditText)findViewById(R.id.prodName);
                EditText price = (EditText)findViewById(R.id.prodPrice);
                EditText quan = (EditText)findViewById(R.id.prodQuan);
                Spinner catType = (Spinner)findViewById(R.id.prodCat);
                imgView = (ImageView)findViewById(R.id.prodImg);
               EditText prodBarCode  = (EditText)findViewById(R.id.prodBarCode);

                if(!name.getText().toString().equals("")&&!price.getText().toString().equals("")&&!quan.getText().toString().equals("")&&x==1&&!prodBarCode.getText().toString().equals(""))
                {
                    String CN = catType.getSelectedItem().toString();
                    float P = Float.parseFloat(price.getText().toString());
                    int Q = Integer.parseInt(quan.getText().toString());
                    String n = name.getText().toString();
                    byte[] prodImg = imageViewToByte(imgView);
                    String BarCode = prodBarCode.getText().toString();
                    int catID = shopping_db.getCatID(CN);

                    try {
                        shopping_db.AddProduct(n,prodImg,BarCode,P,Q,catID);
                        Toast.makeText(getApplicationContext(),n+" added successfully",Toast.LENGTH_LONG).show();
                    }
                    catch (Exception E)
                    {
                        Toast.makeText(getApplicationContext(),E.getMessage(),Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(),"fill empty feild",Toast.LENGTH_SHORT).show();
                }

            }
        });
            //-----------------------------------------------------

        prodBarCode = findViewById(R.id.prodBarCode);
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, PackageManager.PERMISSION_GRANTED);

        Button chooseBarCode = (Button)findViewById(R.id.chooseBarCode);
        chooseBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScanButton(v);
            }
        });

       Button searchByBarCode = (Button)findViewById(R.id.searchByBarCode);
        searchByBarCode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(product_category.this, searchBy_barCode.class);
                startActivity(I);
            }
        });

    }
    protected static byte[] imageViewToByte(ImageView imge)
    {
        Bitmap bitmap = ((BitmapDrawable)imge.getDrawable()).getBitmap();
        ByteArrayOutputStream stream = new ByteArrayOutputStream() ;
        bitmap.compress(Bitmap.CompressFormat.PNG,100,stream) ;
        byte[] bytearray = stream.toByteArray();
        return bytearray ;
    }

    protected void chooseImage()
    {
        x=1;
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, GALLERY_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        if(requestCode== GALLERY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                imgView.setImageBitmap(bitmap);

            }
            catch (FileNotFoundException E){
                E.printStackTrace();
            }
        }

        else
        {
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
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
    //---------------------------------------------
    public void ScanButton(View view){
        IntentIntegrator intentIntegrator = new IntentIntegrator(this);
        intentIntegrator.initiateScan();
    }


}