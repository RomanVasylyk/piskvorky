package com.example.piskvorky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

public class WinnerActivity extends AppCompatActivity {

    private String winner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_winner);

        winner = getIntent().getStringExtra("WINNER");

        TextView winnerTextView = findViewById(R.id.winnerTextView);
        winnerTextView.setText("Winner: " + winner);

        Button replayButton = findViewById(R.id.replayButton);
        Button newGameButton = findViewById(R.id.newGameButton);

        replayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WinnerActivity.this, GameReplayActivity.class);
                startActivity(intent);
            }
        });

        newGameButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(WinnerActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
