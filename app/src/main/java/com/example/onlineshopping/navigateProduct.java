package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Map;

public class navigateProduct extends AppCompatActivity {

    String [] products_names;
    String [] products_price;
    byte[][] products_imgs;
    product_adapter productAdapter;
    ListView L;
    shopping_DB shopping_db;
    ImageView search_by_voice;

    int voice_code = 1;

    String CUSTOMER_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigate_product);

        Intent I = getIntent();
         shopping_db = new shopping_DB(this);
         CUSTOMER_ID = shopping_db.getCusID(I.getStringExtra("email"), I.getStringExtra("pass"));

        loadTheProdAddedToCart();

        ImageView cat = (ImageView) findViewById(R.id.categories);
        cat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Creating the instance of PopupMenu
                PopupMenu popup = new PopupMenu(navigateProduct.this, cat);
                Cursor cursor = shopping_db.getCatName();

                while (!cursor.isAfterLast())
                {
                    popup.getMenu().add(cursor.getString(0));
                    cursor.moveToNext();
                }
                popup.getMenu().add("All Products");
                popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    public boolean onMenuItemClick(MenuItem item) {
                       // Toast.makeText(navigateProduct.this,"You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                        TextView T = (TextView)findViewById(R.id.choosedcat);
                        T.setText(item.getTitle().toString());
                        if(item.getTitle().toString().equals("All Products"))
                            showAllproducts();
                        else
                            getCategory(item.getTitle().toString());
                        return true;
                    }
                });
                popup.show();//showing popup menu
            }
        });

        showAllproducts();
        EditText Esearch = (EditText)findViewById(R.id.search);
        Button searchBtn = (Button)findViewById(R.id.searchBtn);
        ImageView search_by_text = (ImageView)findViewById(R.id.search_by_text);
        search_by_text.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Esearch.setVisibility(View.VISIBLE);
                searchBtn.setVisibility(View.VISIBLE);
            }
        });
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               if(!Esearch.getText().toString().equals(""))
               {
                   getProd(Esearch.getText().toString());
               }
               else
               {
                   Toast.makeText(getApplicationContext(), "Enter Product", Toast.LENGTH_LONG).show();
               }
            }
        });
        search_by_voice = (ImageView)findViewById(R.id.search_by_voice);
        search_by_voice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Esearch.setText("");
                Esearch.setVisibility(View.VISIBLE);
                Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); // to show to us the google dialog
                startActivityForResult(intent,voice_code);

            }
        });

        //////////////////////
        //////////////////////
        //////////////////////
        customer_cart.cart.clear();

        Button showAddedProducts = (Button)findViewById(R.id.showAddedProducts);
        showAddedProducts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(navigateProduct.this, make_orders.class);
                I.putExtra("CID",CUSTOMER_ID);
                startActivity(I);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {


            ArrayList<String> t = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS); // get extra data from intent (google dialog)
            EditText Esearch = (EditText)findViewById(R.id.search);
            Esearch.setText(t.get(0));
            getProd(Esearch.getText().toString());

        super.onActivityResult(requestCode, resultCode, data);

    }

    public void showAllproducts()
    {
        L = (ListView)findViewById(R.id.list1);
        try {

            Cursor C = shopping_db.fetch_name_price_img();
            products_names = new String[C.getCount()];
            products_price = new String[C.getCount()];
            products_imgs = new byte[C.getCount()][];
            int i=0;
            while (!C.isAfterLast())
            {
                products_names[i] = (C.getString(0));
                products_price[i] = (C.getString(1));
                products_imgs[i] = (C.getBlob(2));
                i++;
                C.moveToNext();
            }
            productAdapter = new product_adapter(this, products_names,products_imgs,products_price);
            L.setAdapter(productAdapter);
        }
        catch (Exception E)
        {
            Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    public void getCategory(String S)
    {
        int catID = shopping_db.getCatID(S);
        L = (ListView)findViewById(R.id.list1);
        try {

            Cursor C = shopping_db.fetchProdOfCat(String.valueOf(catID));
            products_names = new String[C.getCount()];
            products_price = new String[C.getCount()];
            products_imgs = new byte[C.getCount()][];
            int i=0;
            while (!C.isAfterLast())
            {
                products_names[i] = (C.getString(0));
                products_price[i] = (C.getString(1));
                products_imgs[i] = (C.getBlob(2));
                i++;
                C.moveToNext();
            }
            productAdapter = new product_adapter(this, products_names,products_imgs,products_price);
            L.setAdapter(productAdapter);
        }
        catch (Exception E) { Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show(); }
    }

    public void getProd(String S)
    {
        L = (ListView)findViewById(R.id.list1);
        try {
            Cursor C = shopping_db.getProd(S);
            if(C.getCount()>0)
            {
                products_names = new String[C.getCount()];
                products_price = new String[C.getCount()];
                products_imgs = new byte[C.getCount()][];

                int i=0;
                while (!C.isAfterLast())
                {
                    products_names[i] = (C.getString(0));
                    products_price[i] = (C.getString(1));
                    products_imgs[i] = (C.getBlob(2));
                    i++;
                    C.moveToNext();
                }
                productAdapter = new product_adapter(this, products_names,products_imgs,products_price);
                L.setAdapter(productAdapter);
            }
            else
                Toast.makeText(getApplicationContext(), S+" Not Found", Toast.LENGTH_LONG).show();
        }
        catch (Exception E) { Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show(); }
    }

    public void loadTheProdAddedToCart()
    {
        try {
            Cursor cursorOfprodId_Quan =  shopping_db.fetch_prodID_Quan_AddedToCart(CUSTOMER_ID);

            while (!cursorOfprodId_Quan.isAfterLast())
            {
                Cursor C = shopping_db.getProdByID(cursorOfprodId_Quan.getString(0));
                product p = new product();
                p.name = C.getString(0);
                p.price = C.getString(1);
                p.image = C.getBlob(2);
                p.quant = Integer.parseInt(cursorOfprodId_Quan.getString(1));
                customer_cart.prev_cart.put(p.name,p);

            //    Toast.makeText(getApplicationContext(), "ID: "+ cursorOfprodId_Quan.getString(0) +" "+ C.getString(0)+" "+ C.getString(1), Toast.LENGTH_LONG).show();
                cursorOfprodId_Quan.moveToNext();
            }
        }
        catch (Exception E) {
            Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onBackPressed() {

        // type your code here
        DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which){
                    case DialogInterface.BUTTON_POSITIVE:
                        //Yes button clicked
                        for (Map.Entry<String, product> entry: customer_cart.cart.entrySet())
                        {
                            String productName = entry.getKey(); // the key is name of product
                            String prodID = shopping_db.getProdID(productName);
                            shopping_db.addToCart(CUSTOMER_ID,prodID, String.valueOf(entry.getValue().quant));
                        }
                            Intent I = new Intent(navigateProduct.this, sign_in.class);
                            startActivity(I);
                        break;

                    case DialogInterface.BUTTON_NEGATIVE:
                     //   Toast.makeText(getApplicationContext(),"You Clicked : no" , Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Do You Want To Exit ?").setPositiveButton("Yes", dialogClickListener).setNegativeButton("No", dialogClickListener).show();
    }

}