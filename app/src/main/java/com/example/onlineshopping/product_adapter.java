package com.example.onlineshopping;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.Map;

public class product_adapter extends ArrayAdapter {
    Context context;
    byte[][] images;
    String [] prodName;
    String [] prodPrice;
    int p=0;
    public product_adapter(@NonNull Context context, String[] pName, byte[][] img, String[] prodPrice) {
        super(context, R.layout.single_item, R.id.pName, pName );
        this.context = context;
        this.images = img;
        this.prodName = pName;
        this.prodPrice = prodPrice;

    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View singleItem = convertView;
        programViewHolder holder = null;
        if(singleItem==null)
        {
            LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            singleItem = layoutInflater.inflate(R.layout.single_item, parent, false);
            holder = new programViewHolder(singleItem);
            singleItem.setTag(holder);
        }
        else
        {
            holder = (programViewHolder)singleItem.getTag();

        }

        byte[]img_byte = images[position];
        Bitmap bitmap = BitmapFactory.decodeByteArray(img_byte,0, img_byte.length);
        holder.IV.setImageBitmap(bitmap);
        holder.prodName.setText(prodName[position]);
        holder.prodPrice.setText(prodPrice[position]+"$");
        if(context instanceof navigateProduct )
            holder.btnAddToCart.setText("add to cart");
        else
            holder.btnAddToCart.setText("Remove from cart");


        singleItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(context instanceof make_orders)
                {
                    int Q=0;
                    boolean flag = true;
                    for (Map.Entry<String, product> entry: customer_cart.cart.entrySet())
                    {
                        if(entry.getKey().equals(prodName[position]))
                        {
                            Q= entry.getValue().quant;
                            flag = false;
                            ((make_orders)context).assignQquantity(prodName[position], String.valueOf(Q));
                            break;
                        }
                    }
                    if(flag)
                    {
                        for (Map.Entry<String, product> entry: customer_cart.prev_cart.entrySet())
                        {
                            if(entry.getKey().equals(prodName[position]))
                            {
                                Q= entry.getValue().quant;
                                break;
                            }
                        }
                        ((make_orders)context).assignQquantity(prodName[position], String.valueOf(Q));
                    }

                }
            }
        });
        holder.btnAddToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(context instanceof navigateProduct )
                {
                    if(customer_cart.cart.containsKey(prodName[position])||customer_cart.prev_cart.containsKey(prodName[position]))
                    {
                        Toast.makeText(getContext(), prodName[position]+ " Already Added to cart", Toast.LENGTH_SHORT).show();
                    }
                    else
                    {
                        product p = new product();
                        p.name = prodName[position];
                        p.price = prodPrice[position];
                        p.image = images[position];
                        p.quant = 1;
                        customer_cart.cart.put(prodName[position],p);
                        Toast.makeText(getContext(), prodName[position]+ " Added to cart", Toast.LENGTH_SHORT).show();

                    }
                }
                else
                {
                    if(customer_cart.cart.containsKey(prodName[position]))
                    {
                        customer_cart.cart.remove(prodName[position]);
                        Toast.makeText(getContext(), prodName[position]+ " Removed from cart", Toast.LENGTH_SHORT).show();
                        ((make_orders)context).getAddedProducts();
                        ((make_orders)context).getOrderTotal();
                        ((make_orders)context).removeFromCart(prodName[position]);
                    }

                    if(customer_cart.prev_cart.containsKey(prodName[position]))
                    {
                        customer_cart.prev_cart.remove(prodName[position]);
                        Toast.makeText(getContext(), prodName[position]+ " Removed from cart", Toast.LENGTH_SHORT).show();
                        ((make_orders)context).getAddedProducts();
                        ((make_orders)context).getOrderTotal();
                        ((make_orders)context).removeFromCart(prodName[position]);
                    }
                }

            }
        });
        
        return singleItem;
    }




}
