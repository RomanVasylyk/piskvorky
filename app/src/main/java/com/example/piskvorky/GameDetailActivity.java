package com.example.piskvorky;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

public class GameDetailActivity extends AppCompatActivity {

    private int gameId;
    private GridLayout gameGrid;
    private char[][] board;
    private int gridSize;
    private String moves;
    private int currentMoveIndex = 0;
    private String[] movesArray;
    private Handler handler;
    private Runnable autoPlayRunnable;
    private boolean isAutoPlaying = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_detail);

        gameId = getIntent().getIntExtra("GAME_ID", -1);
        gameGrid = findViewById(R.id.gameDetailGrid);
        Button nextMoveButton = findViewById(R.id.nextMoveButton);
        Button prevMoveButton = findViewById(R.id.prevMoveButton);
        Button autoPlayButton = findViewById(R.id.autoPlayButton);

        loadGameDetails();
        initializeBoard();

        nextMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayNextMove();
            }
        });

        prevMoveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                displayPrevMove();
            }
        });

        autoPlayButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAutoPlaying) {
                    stopAutoPlay();
                } else {
                    startAutoPlay();
                }
            }
        });

        handler = new Handler();
        autoPlayRunnable = new Runnable() {
            @Override
            public void run() {
                if (currentMoveIndex < movesArray.length) {
                    displayNextMove();
                    handler.postDelayed(this, 1000);
                } else {
                    stopAutoPlay();
                    Toast.makeText(GameDetailActivity.this, "Game replay finished", Toast.LENGTH_LONG).show();
                }
            }
        };
    }

    private void loadGameDetails() {
        SQLiteDatabase db = new GameDatabaseHelper(this).getReadableDatabase();
        Cursor cursor = db.query("games", null, "id = ?", new String[]{String.valueOf(gameId)}, null, null, null);

        if (cursor.moveToFirst()) {
            gridSize = cursor.getInt(cursor.getColumnIndex("grid_size"));
            moves = cursor.getString(cursor.getColumnIndex("moves"));
        }
        cursor.close();
        db.close();

        gameGrid.setRowCount(gridSize);
        gameGrid.setColumnCount(gridSize);
        board = new char[gridSize][gridSize];
        movesArray = moves.split(";");
    }

    private void initializeBoard() {
        for (int i = 0; i < gridSize; i++) {
            for (int j = 0; j < gridSize; j++) {
                Button cell = new Button(this);
                GridLayout.LayoutParams params = new GridLayout.LayoutParams();
                params.width = 0;
                params.height = 0;
                params.rowSpec = GridLayout.spec(i, 1, 1f);
                params.columnSpec = GridLayout.spec(j, 1, 1f);
                cell.setLayoutParams(params);
                cell.setTextSize(24);
                gameGrid.addView(cell);
            }
        }
    }

    private void displayNextMove() {
        if (currentMoveIndex < movesArray.length) {
            String[] moveCoords = movesArray[currentMoveIndex].split(",");
            int row = Integer.parseInt(moveCoords[0]);
            int col = Integer.parseInt(moveCoords[1]);
            Button cell = (Button) gameGrid.getChildAt(row * gridSize + col);
            cell.setText(currentMoveIndex % 2 == 0 ? "X" : "O");
            currentMoveIndex++;
        }
    }

    private void displayPrevMove() {
        if (currentMoveIndex > 0) {
            currentMoveIndex--;
            String[] moveCoords = movesArray[currentMoveIndex].split(",");
            int row = Integer.parseInt(moveCoords[0]);
            int col = Integer.parseInt(moveCoords[1]);
            Button cell = (Button) gameGrid.getChildAt(row * gridSize + col);
            cell.setText("");
        }
    }

    private void startAutoPlay() {
        isAutoPlaying = true;
        handler.post(autoPlayRunnable);
    }

    private void stopAutoPlay() {
        isAutoPlaying = false;
        handler.removeCallbacks(autoPlayRunnable);
    }
}
