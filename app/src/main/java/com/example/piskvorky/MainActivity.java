package com.example.piskvorky;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText gridSizeInput;
    private Spinner winLengthSpinner;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        gridSizeInput = findViewById(R.id.gridSizeInput);
        winLengthSpinner = findViewById(R.id.winLengthSpinner);
        Button startButton = findViewById(R.id.startButton);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int gridSize = Integer.parseInt(gridSizeInput.getText().toString());
                int winLength = Integer.parseInt(winLengthSpinner.getSelectedItem().toString());

                Intent intent = new Intent(MainActivity.this, GameActivity.class);
                intent.putExtra("GRID_SIZE", gridSize);
                intent.putExtra("WIN_LENGTH", winLength);
                startActivity(intent);
            }
        });
    }
}
