package com.example.onlineshopping;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class programViewHolder {
    ImageView IV;
    TextView prodName;
    TextView prodPrice;
    Button btnAddToCart;
    programViewHolder(View v)
    {
        IV = v.findViewById(R.id.pImg);
        prodName = v.findViewById(R.id.pName);
        prodPrice = v.findViewById(R.id.pPrice);
        btnAddToCart = v.findViewById(R.id.btnAddToCart);

    }
}
