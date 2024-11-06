package com.smart.garbage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private List<MarkerData> markers = new ArrayList<>();

    private static class MarkerData {
        LatLng position;
        String title;
        String snippet;

        MarkerData(LatLng position, String title, String snippet) {
            this.position = position;
            this.title = title;
            this.snippet = snippet;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        initializeMap();
        initializeLocationServices();
        setupFab();
    }

    private void initializeMap() {
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void initializeLocationServices() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    private void setupFab() {
        FloatingActionButton fabAddMarker = findViewById(R.id.fabAddMarker);
        fabAddMarker.setOnClickListener(v -> showAddMarkerDialog());
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerClickListener(this);
        mMap.setOnMapLongClickListener(this);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        if (checkLocationPermissions()) {
            enableMyLocation();
        }

        loadMarkers();
    }

    private boolean checkLocationPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            return true;
        } else {
            requestPermissions(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            }, LOCATION_PERMISSION_REQUEST_CODE);
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    private void enableMyLocation() {
        mMap.setMyLocationEnabled(true);
        fusedLocationClient.getLastLocation().addOnSuccessListener(this, location -> {
            if (location != null) {
                LatLng currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLocation, 15f));
            }
        });
    }

    @Override
    public void onMapLongClick(LatLng latLng) {
        showAddMarkerDialog(latLng);
    }

    private void showAddMarkerDialog() {
        LatLng center = mMap.getCameraPosition().target;
        showAddMarkerDialog(center);
    }

    private void showAddMarkerDialog(LatLng position) {
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_add_marker, null);
        EditText titleInput = dialogView.findViewById(R.id.titleInput);
        EditText snippetInput = dialogView.findViewById(R.id.snippetInput);

        new AlertDialog.Builder(this)
                .setTitle("Add Marker")
                .setView(dialogView)
                .setPositiveButton("Add", (dialog, which) -> {
                    String title = titleInput.getText().toString();
                    String snippet = snippetInput.getText().toString();
                    addMarker(position, title, snippet);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addMarker(LatLng position, String title, String snippet) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(position)
                .title(title)
                .snippet(snippet);
        mMap.addMarker(markerOptions);
        markers.add(new MarkerData(position, title, snippet));
        saveMarkers();
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        marker.showInfoWindow();
        return true;
    }

    private void saveMarkers() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Gson gson = new Gson();
        String markersJson = gson.toJson(markers);
        editor.putString("markers", markersJson);
        editor.apply();
    }

    private void loadMarkers() {
        SharedPreferences prefs = getPreferences(MODE_PRIVATE);
        String markersJson = prefs.getString("markers", "");

        if (!markersJson.isEmpty()) {
            Gson gson = new Gson();
            Type type = new TypeToken<List<MarkerData>>() {}.getType();
            markers = gson.fromJson(markersJson, type);

            for (MarkerData marker : markers) {
                mMap.addMarker(new MarkerOptions()
                        .position(marker.position)
                        .title(marker.title)
                        .snippet(marker.snippet));
            }
        }
    }
}