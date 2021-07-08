package com.example.onlineshopping;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class shopping_DB extends SQLiteOpenHelper {
    private static String DBName ="shopping_DB";
    private SQLiteDatabase SqlDB;

    public shopping_DB(Context C)
    {
        super(C,DBName,null,1);
    }
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table customer (cusID integer primary key, cusName text not null, CusEmail text not null, " +
                "password text not null, pinCode text not null, gender text not null, birthDate date not null, job text not null )");

        db.execSQL("create table orders (ordID integer primary key, ordDate text not null, ordAddress text not null, cus_ID integer not null," +
                "foreign key(cus_ID) references customer(cusID))");

        db.execSQL("create table category (catID integer primary key, catName text not null)");

        db.execSQL("create table product (prodID integer primary key, prodName text not null, prodImage blob not null, prodBarcode text not null, price float not null, quantity integer not null," +
                "cat_ID integer not null, foreign key (cat_ID) references category(catID))");

        db.execSQL("create table orderDetails (ordID integer not null, prodID text not null, quan integer not null, foreign key(ordID) references orders(ordID)" +
                ", foreign key(prodID) references product(prodID), primary key (ordID,prodID))");

        db.execSQL("create table cart (cus_id integer not null, prod_id integer not null, quan integer not null ," +
                "foreign key(cus_id) references customer(cusID), foreign key(prod_id) references product(prodID), primary key (cus_id,prod_id))");


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists orderDetails");
        db.execSQL("drop table if exists ordders");
        db.execSQL("drop table if exists customer");
        db.execSQL("drop table if exists product");
        db.execSQL("drop table if exists category");
        onCreate(db);


    }
    public void insertCustomer(String ID, String name, String email, String pass, String pinCode ,String bd, String gender, String job)
    {
        ContentValues r = new ContentValues();
        r.put("cusID", ID);
        r.put("cusName", name);
        r.put("CusEmail", email);
        r.put("password", pass);
        r.put("pinCode", pinCode);
        r.put("birthDate", bd);
        r.put("gender", gender);
        r.put("job", job);
        SqlDB = getWritableDatabase();
        SqlDB.insert("customer", null, r);
        SqlDB.close();
    }

    public Boolean check_email_pinCode(String E, String PINC)
    {
        SqlDB = getReadableDatabase();
        String[] args = {E,PINC};
        Cursor c = SqlDB.rawQuery("Select count(*) from customer where CusEmail like ? and pinCode like ?",args);
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        if(c.getString(0).equals("1")) {
            return true;
        }
        return false;
    }
    public Boolean change_password(String E, String PINC, String pass)
    {

        String[] S = {E, PINC};
        SqlDB = getWritableDatabase();
        ContentValues C = new ContentValues();
        C.put("password", pass);
        SqlDB.update("customer", C,"CusEmail =? and pinCode=?", S);

        SqlDB.close();
        return true;
    }

    public Boolean check_email_pass(String E, String pass)
    {
        SqlDB = getReadableDatabase();
        String[] args = {E,pass};
        Cursor c = SqlDB.rawQuery("Select count(*) from customer where CusEmail like ? and password like ?",args);
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        if(c.getString(0).equals("1")) {
            return true;
        }
        return false;
    }
    public Cursor getCatName()
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select catName from category ", null);
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }

    public boolean AddProduct(String name, byte[] image, String barCode, Float price, int quantity, int cat_id){
        ContentValues row =new ContentValues();
        row.put("prodName",name);
        row.put("prodImage",image);
        row.put("prodBarcode",barCode);
        row.put("price",price);
        row.put("quantity",quantity);
        row.put("cat_ID",cat_id);
        SqlDB=getWritableDatabase();
        SqlDB.insert("product",null,row);
        SqlDB.close();
        return true;
    }
    public int getCatID(String catName)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select catID from category where catName like ? ", new String[]{catName});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
         c.getString(0);
         return Integer.parseInt(c.getString(0));
    }

    public Cursor fetch_name_price_img()
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prodName, price, prodImage from product ", null);
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }

    public Cursor fetchProdOfCat(String ID)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prodName, price, prodImage from product where cat_ID like ? ",  new String[]{ID});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }

    public Cursor getProd(String Name)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prodName, price, prodImage from product where prodName like ? ",  new String[]{Name});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }
    public Cursor getProdDetails(String barCode)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prodName, price, prodImage, quantity from product where prodBarcode like ? ",  new String[]{barCode});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }



    public String getCusID(String email, String password)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select cusID from customer where CusEmail like ? and password like ? ", new String[]{email, password});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        String S = c.getString(0);
        return S;
    }

    public String getProdID(String name)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prodID from product where prodName like ? ", new String[]{ name });
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        String S = c.getString(0);
        return S;
    }


    public void addToCart(String cusID, String prodID, String Qauntity )
    {
        ContentValues r = new ContentValues();
        r.put("cus_id", cusID);
        r.put("prod_id", prodID);
        r.put("quan", Qauntity);

        SqlDB = getWritableDatabase();
        SqlDB.insert("cart", null, r);
        SqlDB.close();
    }


    public void removeFromCart(String prodID , String cusID)
    {
        SqlDB = getWritableDatabase();
        String [] arg = {prodID, cusID};
        SqlDB.delete("cart","prod_id like ? and cus_id like ? ",arg);
        SqlDB.close();
    }

    public void removeFromCartAfterOrder(String cusID)
    {
        SqlDB = getWritableDatabase();
        String [] arg = {cusID};
        SqlDB.delete("cart","cus_id like ? ",arg);
        SqlDB.close();
    }

    public Cursor fetch_prodID_Quan_AddedToCart(String C_ID)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prod_id, quan from cart where cus_id like ? ", new String[]{ C_ID });
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }

    public Cursor getProdByID(String ID)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select prodName, price, prodImage from product where prodID like ? ",  new String[]{ID});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c;
    }

    public void update_quantity(String ID, int Quantity) // update_quantity in cart table
    {
        ContentValues C = new ContentValues();
        C.put("quan",Quantity);
        SqlDB = getWritableDatabase();
        String[] s = {ID};
        SqlDB.update("cart",C,"prod_id = ?",s);
        SqlDB.close();

    }

    public String generateOrderID()
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("select count(*) from orders",null);
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        int orderID = Integer.parseInt(c.getString(0)) +1 ;
        return String.valueOf(orderID);

    }
    public void addOrder( String Date, String Address, String cusID)
    {
        ContentValues r = new ContentValues();
      //  r.put("ordID",OrderID);
        r.put("ordDate", Date);
        r.put("ordAddress", Address);
        r.put("cus_ID", cusID);

        SqlDB = getWritableDatabase();
        SqlDB.insert("orders", null, r);
        SqlDB.close();

    }

    public void addOrderDetails(String ordID, String prodID, String Quantity)
    {
        ContentValues r = new ContentValues();
        r.put("ordID", ordID);
        r.put("prodID", prodID);
        r.put("quan", Quantity);

        SqlDB = getWritableDatabase();
        SqlDB.insert("orderDetails", null, r);
        SqlDB.close();
    }


    public String getQuantityOfProduct(String ID)
    {
        SqlDB = getReadableDatabase();
        Cursor c = SqlDB.rawQuery("Select quantity from product where prodID like ? ",  new String[]{ID});
        if (c!=null)
            c.moveToFirst();
        SqlDB.close();
        return c.getString(0);

    }
    public void update_quantity_of_product(String prodID, String Quantity) //update_quantity in product table
    {
        ContentValues C = new ContentValues();
        C.put("quantity",Quantity);
        SqlDB = getWritableDatabase();
        String[] s = {prodID};
        SqlDB.update("product",C,"prodID = ?",s);
        SqlDB.close();

    }


    public void addCategories()
    {
        ContentValues r = new ContentValues();
        r.put("catID",1);
        r.put("catName", "Electronic Devices");
        SqlDB = getWritableDatabase();
        SqlDB.insert("category", null, r);


        r = new ContentValues();
        r.put("catID",2);
        r.put("catName", "Sports Equipment");
        SqlDB = getWritableDatabase();
        SqlDB.insert("category", null, r);

        r = new ContentValues();
        r.put("catID",3);
        r.put("catName", "Clothing");
        SqlDB = getWritableDatabase();
        SqlDB.insert("category", null, r);


        r = new ContentValues();
        r.put("catID",4);
        r.put("catName", "Books");
        SqlDB = getWritableDatabase();
        SqlDB.insert("category", null, r);


        SqlDB.close();

    }


}
