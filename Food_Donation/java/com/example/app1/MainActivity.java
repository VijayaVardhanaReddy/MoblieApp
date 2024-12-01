package com.example.app1;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import com.google.firebase.auth.FirebaseAuth;


import android.os.Handler;
import android.os.Looper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    CardView donate, receive, logout, foodmap, about, contact, mypin, history;
    FirebaseAuth fAuth;
    ExecutorService executorService;  // Executor for threading
    Handler mainHandler;              // Handler to update UI after background tasks

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views
        donate = findViewById(R.id.cardDonate);
        receive = findViewById(R.id.cardReceive);
        logout = findViewById(R.id.cardLogout);
        foodmap = findViewById(R.id.cardFoodmap);
        mypin = findViewById(R.id.cardMyPin);
        history = findViewById(R.id.cardHistory);
        about = findViewById(R.id.cardAboutus);
        contact = findViewById(R.id.cardContact);

        // Initialize Firebase and Executors
        fAuth = FirebaseAuth.getInstance();
        executorService = Executors.newSingleThreadExecutor();
        mainHandler = new Handler(Looper.getMainLooper());

        if (fAuth.getCurrentUser() == null) {
            Intent intent = new Intent(MainActivity.this, landingpage.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        donate.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Donate.class)));
        receive.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Receive.class)));
        foodmap.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), FoodMap.class)));
        about.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), About.class)));
        mypin.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), MyPin.class)));
        history.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), UserdataActivity.class)));
        contact.setOnClickListener(v -> startActivity(new Intent(getApplicationContext(), Contact.class)));

        logout.setOnClickListener(v -> {
            // Perform logout in a background thread
            executorService.execute(() -> {
                FirebaseAuth.getInstance().signOut();

                // Post result back to the main thread
                mainHandler.post(() -> {
                    Intent intent = new Intent(MainActivity.this, landingpage.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                });
            });
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Shut down the executor service to prevent memory leaks
        executorService.shutdown();
    }
}
