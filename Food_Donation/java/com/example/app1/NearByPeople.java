package com.example.app1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class NearByPeople extends AppCompatActivity implements OnMapReadyCallback {

    private RecyclerView recyclerView;
    private ArrayList<UserModel> userList;
    private FirebaseFirestore db;
    private FusedLocationProviderClient fusedLocationClient;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_near_bypeople);

        // Initialize RecyclerView
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        userList = new ArrayList<>();

        // Initialize Firestore and location services
        db = FirebaseFirestore.getInstance();
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Set up the map
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        // Get current location
        getCurrentLocation();
    }

    private void getCurrentLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Request location permission
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
            return;
        }

        fusedLocationClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    fetchNearbyUsers(location);
                }
            }
        });
    }

    private void fetchNearbyUsers(Location currentLocation) {
        double currentLat = currentLocation.getLatitude();
        double currentLon = currentLocation.getLongitude();

        // Example Firestore query to find nearby users
        db.collection("users").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot doc : queryDocumentSnapshots) {
                    UserModel user = doc.toObject(UserModel.class);
                    // Calculate distance between current user and this user
                    double distance = calculateDistance(currentLat, currentLon, user.getLatitude(), user.getLongitude());

                    if (distance < 10) { // e.g., 10 km radius
                        userList.add(user);
                        // Add marker on the map for this user
                        LatLng userLatLng = new LatLng(user.getLatitude(), user.getLongitude());
                        mMap.addMarker(new MarkerOptions().position(userLatLng).title(user.getName()));
                    }
                }
                // Update the RecyclerView directly
                recyclerView.setAdapter(new RecyclerView.Adapter<RecyclerView.ViewHolder>() {
                    @NonNull
                    @Override
                    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_user, parent, false);
                        return new UserViewHolder(view);
                    }

                    @Override
                    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                        UserModel user = userList.get(position);
                        UserViewHolder userViewHolder = (UserViewHolder) holder;
                        userViewHolder.name.setText(user.getName());
                        userViewHolder.phoneNumber.setText(user.getPhoneNumber());
                        userViewHolder.location.setText(user.getLocation());
                    }

                    @Override
                    public int getItemCount() {
                        return userList.size();
                    }
                });
            }
        });
    }

    // ViewHolder class for RecyclerView
    static class UserViewHolder extends RecyclerView.ViewHolder {
        TextView name, phoneNumber, location;

        public UserViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.user_name);
            phoneNumber = itemView.findViewById(R.id.user_phone);
            location = itemView.findViewById(R.id.user_location);
        }
    }

    static class UserModel {
        private String name;
        private String phoneNumber;
        private String location;
        private double latitude;
        private double longitude;

        public UserModel() {
            // Default constructor for Firebase
        }

        public UserModel(String name, String phoneNumber, String location, double latitude, double longitude) {
            this.name = name;
            this.phoneNumber = phoneNumber;
            this.location = location;
            this.latitude = latitude;
            this.longitude = longitude;
        }

        // Getters and setters for each field
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }

        public String getPhoneNumber() { return phoneNumber; }
        public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

        public String getLocation() { return location; }
        public void setLocation(String location) { this.location = location; }

        public double getLatitude() { return latitude; }
        public void setLatitude(double latitude) { this.latitude = latitude; }

        public double getLongitude() { return longitude; }
        public void setLongitude(double longitude) { this.longitude = longitude; }
    }
    // Haversine formula to calculate distance between two locations
    private double calculateDistance(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radius of the Earth in km
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2) +
                Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2)) *
                        Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        return R * c; // Distance in km
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // Set default location on the map (e.g., user's current location)
    }
}
