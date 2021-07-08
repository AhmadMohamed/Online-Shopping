package com.example.onlineshopping;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    EditText AddressTxt;
    LocationManager LocManager;
    MyLocationListener LocListener;
    Button getLocation;

    private final int REQUEST_CODE_ASK_PERMISSIONS=123;

    String CUSTOMER_ID;
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode)
        {
            case REQUEST_CODE_ASK_PERMISSIONS:
                if (grantResults[0]==PackageManager.PERMISSION_GRANTED) { /* permission granted*/ }
                else
                {
                    Toast.makeText(getApplicationContext(), "find location is Denied", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        Intent I = getIntent();
        CUSTOMER_ID =  I.getStringExtra("CID");

        AddressTxt = (EditText) findViewById(R.id.editText);
        getLocation = (Button) findViewById(R.id.btn1);

        LocListener = new MyLocationListener(getApplicationContext());
        LocManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        try {

            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

                //-------------------------------
                if(!shouldShowRequestPermissionRationale(Manifest.permission.ACCESS_FINE_LOCATION))
                {
                    requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION},REQUEST_CODE_ASK_PERMISSIONS);
                }
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.

                return ;
            }
            LocManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 6000, 0, LocListener);
        }
        catch (Exception EX){
            Toast.makeText(getApplicationContext(), EX.getMessage(), Toast.LENGTH_LONG).show();
        }

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        Button confirmBtn = (Button)findViewById(R.id.btnConfirmAdd);
        confirmBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                EditText E = (EditText)findViewById(R.id.editText);
                if(E.getText().toString().equals(""))
                    Toast.makeText(getApplicationContext(), "determine location please", Toast.LENGTH_LONG).show();

               else
                {
                    Intent I = new Intent(MapsActivity.this, make_orders.class);
                    I.putExtra("address",E.getText().toString());
                    I.putExtra("CID",CUSTOMER_ID);
                    startActivity(I);
                }
            }
        });
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(30.04441960,31.235711600), 8));
        getLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mMap.clear();
                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                Location Loc = null;
                try {
                    Loc = LocManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }
                catch (SecurityException X)
                {
                    Toast.makeText(getApplicationContext(), X.getMessage(), Toast.LENGTH_LONG).show();
                }

                if (Loc!=null)
                {
                    LatLng myPosition = new LatLng(Loc.getLatitude(),Loc.getLongitude());
                    try {

                        addressList = coder.getFromLocation(myPosition.latitude, myPosition.longitude, 1);
                        if(!addressList.isEmpty())
                        {
                            String address = "";
                            for(int i=0; i<=addressList.get(0).getMaxAddressLineIndex();i++)
                                address+= addressList.get(0).getAddressLine(i)+", ";

                            mMap.addMarker(new MarkerOptions().position(myPosition).title("My Location").snippet(address)).setDraggable(true);
                            AddressTxt.setText(address);

                        }

                    }
                    catch (IOException C)
                    {
                        Toast.makeText(getApplicationContext(), C.getMessage(), Toast.LENGTH_LONG).show();
                    }
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition,15));
                }
                else
                {
                    Toast.makeText(getApplicationContext(), "please wait until your position is determined", Toast.LENGTH_LONG).show();
                }
            }
        });
        mMap.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {

            }

            @Override
            public void onMarkerDrag(Marker marker) {

            }

            @Override
            public void onMarkerDragEnd(Marker marker) {
                Geocoder coder = new Geocoder(getApplicationContext());
                List<Address> addressList;
                try {
                    addressList = coder.getFromLocation(marker.getPosition().latitude, marker.getPosition().longitude, 1);
                    if(!addressList.isEmpty())
                    {
                        String address = "";
                        for(int i=0; i<=addressList.get(0).getMaxAddressLineIndex();i++)
                            address+= addressList.get(0).getAddressLine(i)+", ";
                        AddressTxt.setText(address);
                    }
                    else
                    {
                        Toast.makeText(getApplicationContext(), "No address for this location", Toast.LENGTH_LONG).show();
                        AddressTxt.getText().clear();
                    }

                }
                catch (Exception E)
                {
                    Toast.makeText(getApplicationContext(), "can't get the address, check your network", Toast.LENGTH_LONG).show();
                }

            }
        });
        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }
}