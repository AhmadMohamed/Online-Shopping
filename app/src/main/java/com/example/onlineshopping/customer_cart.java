package com.example.onlineshopping;

import java.util.Dictionary;
import java.util.HashMap;
import java.util.Hashtable;

public class customer_cart {
    public static HashMap<String, product> cart = new HashMap<>();

    public static HashMap<String, product> prev_cart = new HashMap<>();

}
 class product
{
    public String name;
    public String  price;
    public byte[] image;
    public int quant;

}
