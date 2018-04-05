package fr.iutlyon1.androidvelov;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import fr.iutlyon1.androidvelov.api.VelovRequest;
import fr.iutlyon1.androidvelov.model.VelovData;

public class SplashActivity extends AppCompatActivity {
    private static final int MAX_SPLASH_TIME = 1000;

    private boolean mainIntentStarted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        VelovData dataset = new VelovData();

        VelovRequest request = new VelovRequest(
                this,
                "Lyon",
                this::startIntent
            );
        request.execute(dataset);

        new Handler().postDelayed(
                () -> startIntent(dataset),
                MAX_SPLASH_TIME
        );

    }

    private void startIntent(VelovData dataset) {
        if (!mainIntentStarted) {
            mainIntentStarted = true;

            Intent intent = new Intent(this, MainActivity.class);
            intent.putExtra("dataset", dataset);

            startActivity(intent);
            finish();
        }
    }
}
