package com.example.onlineshopping;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

public class make_orders extends AppCompatActivity {

    product_adapter productAdapter;
    ListView L;
    shopping_DB shopping_db;
    String [] products_names;
    String [] products_price;
    byte[][] products_imgs;
    EditText orderTotal;
    Button determineAddress;
    EditText txtAddress;
    EditText EtxtQuan;
    TextView txtQuan;
    Button change_quan;
    Button make_order;

    String CUSTOMER_ID;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_make_orders);
        Intent I  = getIntent();
       CUSTOMER_ID =  I.getStringExtra("CID");

        shopping_db = new shopping_DB(this);
        orderTotal = (EditText)findViewById(R.id.orderTotal);
        L = (ListView)findViewById(R.id.added_products);
        getAddedProducts();
        getOrderTotal();
//---------------------------------
        determineAddress = (Button)findViewById(R.id.locOfDelivery);
        determineAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent I = new Intent(make_orders.this, MapsActivity.class);
                I.putExtra("CID",CUSTOMER_ID);
                startActivity(I);
            }
        });
        deliveryAddress();
        //-----------------
        change_quan = (Button)findViewById(R.id.btnChangeQuan);
        change_quan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                change_Quan();
            }
        });
        //------------------
        make_order = (Button)findViewById(R.id.btnSubmitOrder);
        make_order.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                submitOrder();

            }
        });

    }

    public  void getAddedProducts()
    {
        try {
            int size = customer_cart.cart.size() + customer_cart.prev_cart.size();
            products_names = new String[size];
            products_price = new String[size];
            products_imgs = new byte[size][];
            int i =0;
            for (Map.Entry<String, product> entry: customer_cart.cart.entrySet())
            {
                products_names[i] = entry.getKey();
                products_price[i] = entry.getValue().price;
                products_imgs[i] = entry.getValue().image;
                i++;
            }
            for (Map.Entry<String, product> entry: customer_cart.prev_cart.entrySet())
            {
                products_names[i] = entry.getKey();
                products_price[i] = entry.getValue().price;
                products_imgs[i] = entry.getValue().image;
                i++;
            }
            productAdapter = new product_adapter(this, products_names,products_imgs,products_price);
            L.setAdapter(productAdapter);
        }
        catch (Exception E) { Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show(); }
    }

    public void getOrderTotal()
    {
        int total = 0;
        for (Map.Entry<String, product> entry: customer_cart.cart.entrySet())
            total+=  Integer.parseInt(entry.getValue().price)*entry.getValue().quant;

        for (Map.Entry<String, product> entry: customer_cart.prev_cart.entrySet())
            total+=  Integer.parseInt(entry.getValue().price)*entry.getValue().quant;

        orderTotal.setText(String.valueOf(total)+"$");
    }


    public void removeFromCart(String prod_Name)
    {

        try {
            String prodID = shopping_db.getProdID(prod_Name);
            shopping_db.removeFromCart(prodID,CUSTOMER_ID);
        }
    catch (Exception E) { Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show(); }

    }
    public void deliveryAddress()
    {
        Intent I =  getIntent();
        boolean flag = I.hasExtra("address");
        if(flag)
        {
            String s = I.getStringExtra("address");
            txtAddress = (EditText)findViewById(R.id.txtAddress);
            txtAddress.setText(s);
        }
    }

    public void assignQquantity(String name, String Quan)
    {
        txtQuan = (TextView)findViewById(R.id.txtQuan);
        EtxtQuan = (EditText) findViewById(R.id.EtxtQuan);

        txtQuan.setText(name+" "+"Quantity");
        EtxtQuan.setText(Quan);

    }
    public void change_Quan()
    {
        try {
            EtxtQuan = (EditText) findViewById(R.id.EtxtQuan);
            txtQuan = (TextView)findViewById(R.id.txtQuan);
            String  prodtext = txtQuan.getText().toString();
            boolean flag = true;
            int Quan=0;
            if(!prodtext.equals("Quantity") && !EtxtQuan.getText().toString().equals(""))
            {
               String prodName = prodtext.substring(0, prodtext.length()-9);
                Quan = Integer.parseInt(EtxtQuan.getText().toString());
                for (Map.Entry<String, product> entry : customer_cart.cart.entrySet()) {
                    if (entry.getKey().equals(prodName) )
                    {
                   //     Toast.makeText(getApplicationContext(), "cart", Toast.LENGTH_LONG).show();
                        product p = entry.getValue();
                        p.quant = Quan;
                        flag = false;
                        break;
                    }
                }
                if(flag)
                {
                    for (Map.Entry<String, product> entry : customer_cart.prev_cart.entrySet()) {
                        if (entry.getKey().equals(prodName)) {
                            product p = entry.getValue();
                            p.quant = Quan;
                            break;
                        }
                    }
                }

                String ID = shopping_db.getProdID(prodName);
                shopping_db.update_quantity(ID, Quan);
                getOrderTotal();
                Toast.makeText(getApplicationContext(), "quantity of "+prodName+ " "+ "changed to " + String.valueOf(Quan), Toast.LENGTH_LONG).show();

            }
            else {Toast.makeText(getApplicationContext(), "select a product", Toast.LENGTH_LONG).show();}
        }
        catch (Exception E){Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();}

    }
    public void submitOrder ()
    {
        txtAddress = (EditText)findViewById(R.id.txtAddress);
        if(!txtAddress.getText().toString().equals(""))
        {
            try {
                String ordID = shopping_db.generateOrderID();

                Date currentTime = Calendar.getInstance().getTime();
                String date = currentTime.toString();

                String Address = txtAddress.getText().toString();
                shopping_db.addOrder(date,Address,CUSTOMER_ID);

                for (Map.Entry<String, product> entry : customer_cart.cart.entrySet()) {
                    String ProdID = shopping_db.getProdID(entry.getValue().name);
                    String Quan = String.valueOf(entry.getValue().quant);
                    shopping_db.addOrderDetails(ordID,ProdID,Quan);
                    String productQuan = shopping_db.getQuantityOfProduct(ProdID);
                    int newQuan = Integer.parseInt(productQuan) - Integer.parseInt(Quan);
                    shopping_db.update_quantity_of_product(ProdID,String.valueOf(newQuan));
                }
                for (Map.Entry<String, product> entry : customer_cart.prev_cart.entrySet()) {
                    String ProdID = shopping_db.getProdID(entry.getValue().name);
                    String Quan = String.valueOf(entry.getValue().quant);
                    shopping_db.addOrderDetails(ordID,ProdID,Quan);
                    String productQuan = shopping_db.getQuantityOfProduct(ProdID);
                    int newQuan = Integer.parseInt(productQuan) - Integer.parseInt(Quan);
                    shopping_db.update_quantity_of_product(ProdID,String.valueOf(newQuan));
                }
                shopping_db.removeFromCartAfterOrder(CUSTOMER_ID);
                Toast.makeText(getApplicationContext(), "order submitted successfully", Toast.LENGTH_LONG).show();
                customer_cart.cart.clear();
                customer_cart.prev_cart.clear();

            }
            catch (Exception E){Toast.makeText(getApplicationContext(), E.getMessage(), Toast.LENGTH_LONG).show();}
        }
        else
        {
            Toast.makeText(getApplicationContext(), "determine the address", Toast.LENGTH_LONG).show();
        }



    }

}