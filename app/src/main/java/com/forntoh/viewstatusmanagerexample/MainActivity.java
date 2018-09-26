package com.forntoh.viewstatusmanagerexample;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;

import com.forntoh.viewstatusmanager.Status;
import com.forntoh.viewstatusmanager.StatusManager;

public class MainActivity extends AppCompatActivity {

    Button restart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        restart = findViewById(R.id.button);
        restart(null);
    }

    public void restart(View view) {
        restart.setEnabled(false);
        StatusManager statusManager =

                StatusManager.from(findViewById(R.id.parent));

        statusManager.setStatus(R.id.target, Status.PROGRESS);

        new Handler().postDelayed(() -> {
            statusManager.setOnErrorClickListener(v -> {
                statusManager.setStatus(R.id.target, Status.PROGRESS);
                new Handler().postDelayed(() -> {
                    restart.setEnabled(true);
                    statusManager.setStatus(R.id.target, Status.SUCCESS);
                }, 3000);
            });
            statusManager.setStatus(R.id.target, Status.FAILURE);
        }, 3000);
    }
}
