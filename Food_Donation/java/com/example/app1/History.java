package com.example.app1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.Set;

public class History extends AppCompatActivity {

    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private CollectionReference notebookref = db.collection("user data");
    public static final String TAG = "TAG";
    private TextView textViewData;
    TextView deletebtn;
    FirebaseAuth fAuth;
    private String documentId = null;  // To store the document ID of the selected entry for deletion

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        fAuth = FirebaseAuth.getInstance();
        textViewData = findViewById(R.id.data);
        deletebtn = findViewById(R.id.delete);

        // Set delete button listener
        deletebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteData();
            }
        });

        loadNotes();
    }

//    public void loadNotes() {
//        notebookref.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            StringBuilder data = new StringBuilder(); // Use StringBuilder for efficiency
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());
//
//                                if (document.contains("name") && document.contains("description") && document.contains("user type") && document.contains("userid")) {
//
//                                    String name = document.getString("name");
//                                    String type = document.getString("user type");
//                                    String description = document.getString("description");
//                                    String Userid = document.getString("userid");
//                                    String userID = fAuth.getCurrentUser().getUid();
//                                    Timestamp ts = document.getTimestamp("timestamp");
//                                    String dateandtime = (ts != null) ? ts.toDate().toString() : "No timestamp";
//
//                                    if (Userid.equals(userID)) {
//                                        // Set the document ID for the entry you want to delete
//                                        documentId = document.getId();  // Store the document ID for future deletion
//
//                                        data.append("Name: ").append(name)
//                                                .append("\nUser Type: ").append(type)
//                                                .append("\nDescription: ").append(description)
//                                                .append("\nDate & Time: ").append(dateandtime)
//                                                .append("\n\n");
//                                    }
//                                }
//                            }
//                            textViewData.setText(data.toString());
//                        } else {
//                            Log.d(TAG, "Error fetching data: ", task.getException());
//                        }
//                    }
//                });
//    }

    public void loadNotes() {
        notebookref.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            StringBuilder data = new StringBuilder(); // Use StringBuilder for efficiency
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());

                                if (document.contains("name") && document.contains("description") && document.contains("user type") && document.contains("userid")) {

                                    String name = document.getString("name");
                                    String type = document.getString("user type");
                                    String description = document.getString("description");
                                    String Userid = document.getString("userid");
                                    String userID = fAuth.getCurrentUser().getUid();
                                    Timestamp ts = document.getTimestamp("timestamp");
                                    String dateandtime = (ts != null) ? ts.toDate().toString() : "No timestamp";

                                    if (Userid.equals(userID)) {
                                        // Set the document ID for the entry you want to delete
                                        documentId = document.getId();  // Store the document ID for future deletion

                                        data.append("Name: ").append(name)
                                                .append("\nUser Type: ").append(type)
                                                .append("\nDescription: ").append(description)
                                                .append("\nDate & Time: ").append(dateandtime)
                                                .append("\n\n");

                                        // Set the document ID to a TextView or another variable
                                        String finalDocumentId = document.getId();
                                        textViewData.append(data.toString());

                                        // Set OnClickListener for delete
                                        textViewData.setOnClickListener(v -> {
                                            documentId = finalDocumentId;  // Set documentId to the clicked item
                                        });
                                    }
                                }
                            }
                            textViewData.setText(data.toString());
                        } else {
                            Log.d(TAG, "Error fetching data: ", task.getException());
                        }

                    }
                });
    }

    // Function to delete the document from Firestore
    public void deleteData() {
        if (documentId != null) {
            // Delete the document from Firestore
            notebookref.document(documentId).delete()
                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.d(TAG, "Document successfully deleted! r u happy");
                            // Clear the data from the TextView
                            textViewData.setText("");
                            Toast.makeText(History.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();
                            documentId = null; // Reset document ID after deletion
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.w(TAG, "Error deleting document", e);
                        Toast.makeText(History.this, "Error deleting data", Toast.LENGTH_SHORT).show();
                    });
        } else {
            Toast.makeText(History.this, "No data to delete", Toast.LENGTH_SHORT).show();
        }
    }
}



//public class History extends AppCompatActivity {
//
//    private FirebaseFirestore db = FirebaseFirestore.getInstance();
//    private CollectionReference notebookref = db.collection("user data");
//    public static final String TAG = "TAG";
//    private TextView textViewData;
//    FirebaseAuth fAuth;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_history);
//        fAuth= FirebaseAuth.getInstance();
//        textViewData=findViewById(R.id.data);
//        View deletebtn = findViewById(R.id.delete);
//
//        // Set delete button listener
//        deletebtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                deleteData();
//            }
//        });
//
//        loadNotes();
//    }
//
//    public void loadNotes()
//    {
//        notebookref.get()
//                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
//                    @Override
//                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
//                        if (task.isSuccessful()) {
//                            String data="";
//                            for (QueryDocumentSnapshot document : task.getResult()) {
//                                Log.d(TAG, document.getId() + " => " + document.getData());//
//                                if (document.contains("name") && document.contains("description") && document.contains("user type") && document.contains("userid")) {
//
//                                    String name = (String) document.get("name");
//                                    String type = (String) document.get("user type");
//                                    String description = (String) document.get("description");
//                                    String Userid = (String) document.get("userid");
//                                    String userID = fAuth.getCurrentUser().getUid();
//                                    Timestamp ts = (Timestamp) document.get("timestamp");
//                                    //String dateandtime=String.valueOf(ts);
//                                    String dateandtime=String.valueOf(ts.toDate());
//                                    //String dateandtime = ts.toString();
//
//                                    if(Userid.equals(userID)) {
//                                        data += "Name: " + name + "\nUser Type: " + type + "\nDescription: " + description + "\nDate & Time: " + dateandtime + "\n\n";
//                                        //data += "Name: " + name + "\nUser Type: " + type + "\nDescription: " + description + "\n";
//                                    }
//                                    textViewData.setText(data);
//                                }
//                            }
//                            //textViewData.setText(data);
//                        } else {
//                            Log.d(TAG, "Error fetching data: ", task.getException());
//                        }
//                    }
//                });
//    }
//
//    // Function to delete the document from Firestore
//    public void deleteData() {
//        if (documentId != null) {
//            // Delete the document from Firestore
//            notebookref.document(documentId).delete()
//                    .addOnSuccessListener(new OnSuccessListener<Void>() {
//                        @Override
//                        public void onSuccess(Void aVoid) {
//                            Log.d(TAG, "Document successfully deleted!");
//                            // Clear the data from the TextView
//                            textViewData.setText("");
//                            Toast.makeText(History.this, "Data deleted successfully", Toast.LENGTH_SHORT).show();
//                        }
//                    })
//                    .addOnFailureListener(e -> {
//                        Log.w(TAG, "Error deleting document", e);
//                        Toast.makeText(History.this, "Error deleting data", Toast.LENGTH_SHORT).show();
//                    });
//        } else {
//            Toast.makeText(History.this, "No data to delete", Toast.LENGTH_SHORT).show();
//        }
//    }
//}