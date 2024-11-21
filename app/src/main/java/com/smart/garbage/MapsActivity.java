package com.smart.garbage;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapsActivity extends AppCompatActivity implements
        OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMapLongClickListener {

    private GoogleMap mMap;
    private FusedLocationProviderClient fusedLocationClient;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1;
    private FirebaseFirestore db;
    private ListenerRegistration markerListener;
    private List<MarkerData> markers = new ArrayList<>();
    private static final String COLLECTION_MARKERS = "Markers";

    // Firestore data model
    public static class MarkerData {
        private GeoPoint location;
        private String title;
        private String snippet;
        private String id;

        public MarkerData() {}

        public MarkerData(LatLng position, String title, String snippet) {
            this.location = new GeoPoint(position.latitude, position.longitude);
            this.title = title;
            this.snippet = snippet;
        }

        public static MarkerData fromMap(Map<String, Object> map, String id) {
            MarkerData data = new MarkerData();
            data.location = (GeoPoint) map.get("location");
            data.title = (String) map.get("title");
            data.snippet = (String) map.get("snippet");
            data.id = id;
            return data;
        }

        public Map<String, Object> toMap() {
            Map<String, Object> map = new HashMap<>();
            map.put("location", location);
            map.put("title", title);
            map.put("snippet", snippet);
            return map;
        }

        public LatLng getPosition() {
            return new LatLng(location.getLatitude(), location.getLongitude());
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Initialize map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        setupFirestoreListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (markerListener != null) {
            markerListener.remove();
        }
    }

    private void setupFirestoreListener() {
        markerListener = db.collection(COLLECTION_MARKERS)
                .addSnapshotListener((snapshots, e) -> {
                    if (e != null) {
                        Toast.makeText(this, "Error loading markers: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show();
                        return;
                    }

                    if (snapshots != null && mMap != null) {
                        mMap.clear();
                        markers.clear();

                        for (QueryDocumentSnapshot document : snapshots) {
                            MarkerData markerData = MarkerData.fromMap(
                                    document.getData(),
                                    document.getId()
                            );
                            markers.add(markerData);
                            addMarkerToMap(markerData);
                        }
                    }
                });
    }

    private void addMarkerToMap(MarkerData markerData) {
        MarkerOptions markerOptions = new MarkerOptions()
                .position(markerData.getPosition())
                .title(markerData.title)
                .snippet(markerData.snippet);
        Marker marker = mMap.addMarker(markerOptions);
        if (marker != null) {
            marker.setTag(markerData.id);
        }
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
                    addMarkerToFirestore(position, title, snippet);
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    private void addMarkerToFirestore(LatLng position, String title, String snippet) {
        MarkerData markerData = new MarkerData(position, title, snippet);
        db.collection(COLLECTION_MARKERS)
                .add(markerData.toMap())
                .addOnSuccessListener(documentReference ->
                        Toast.makeText(MapsActivity.this,
                                "Marker added successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MapsActivity.this,
                                "Failed to add marker: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }

    @Override
    public boolean onMarkerClick(Marker marker) {
        String markerId = (String) marker.getTag();
        if (markerId != null) {
            marker.showInfoWindow();
        }
        return true;
    }

    private void deleteMarker(String markerId) {
        db.collection(COLLECTION_MARKERS)
                .document(markerId)
                .delete()
                .addOnSuccessListener(aVoid ->
                        Toast.makeText(MapsActivity.this,
                                "Marker deleted successfully", Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e ->
                        Toast.makeText(MapsActivity.this,
                                "Failed to delete marker: " + e.getMessage(),
                                Toast.LENGTH_SHORT).show());
    }
}