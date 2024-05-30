package com.example.piskvorky;

import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.GridLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class GameActivity extends AppCompatActivity {

    private int gridSize;
    private int winLength;
    private TextView statusTextView;
    private GridLayout gameGrid;
    private char[][] board;
    private boolean player1Turn = true;
    private int movesCount = 0;
    private StringBuilder movesHistory;
    private GameDatabaseHelper dbHelper;
    private long startTime;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game);

        gridSize = getIntent().getIntExtra("GRID_SIZE", 3);
        winLength = getIntent().getIntExtra("WIN_LENGTH", 3);
        statusTextView = findViewById(R.id.statusTextView);
        gameGrid = findViewById(R.id.gameGrid);
        dbHelper = new GameDatabaseHelper(this);
        startTime = System.currentTimeMillis();

        gameGrid.setRowCount(gridSize);
        gameGrid.setColumnCount(gridSize);
        board = new char[gridSize][gridSize];
        movesHistory = new StringBuilder();

        initializeGameGrid();
    }

    private void initializeGameGrid() {
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
                cell.setOnClickListener(new CellClickListener(i, j));
                gameGrid.addView(cell);
            }
        }
    }

    private class CellClickListener implements View.OnClickListener {
        private int row;
        private int col;

        public CellClickListener(int row, int col) {
            this.row = row;
            this.col = col;
        }

        @Override
        public void onClick(View v) {
            Button button = (Button) v;
            if (button.getText().toString().isEmpty()) {
                if (player1Turn) {
                    button.setText("X");
                    board[row][col] = 'X';
                } else {
                    button.setText("O");
                    board[row][col] = 'O';
                }
                movesCount++;
                movesHistory.append(row).append(",").append(col).append(";");
                if (checkForWin(row, col)) {
                    saveGame(true);
                    showWinnerMenu(player1Turn ? "Player 1" : "Player 2");
                } else if (movesCount == gridSize * gridSize) {
                    saveGame(false);
                    showWinnerMenu("Draw");
                } else {
                    player1Turn = !player1Turn;
                    statusTextView.setText("Player " + (player1Turn ? "1" : "2") + "'s Turn");
                }
            }
        }
    }

    private boolean checkForWin(int row, int col) {
        char symbol = board[row][col];
        return (checkDirection(row, col, 1, 0, symbol) + checkDirection(row, col, -1, 0, symbol) + 1 >= winLength) ||
                (checkDirection(row, col, 0, 1, symbol) + checkDirection(row, col, 0, -1, symbol) + 1 >= winLength) ||
                (checkDirection(row, col, 1, 1, symbol) + checkDirection(row, col, -1, -1, symbol) + 1 >= winLength) ||
                (checkDirection(row, col, 1, -1, symbol) + checkDirection(row, col, -1, 1, symbol) + 1 >= winLength);
    }

    private int checkDirection(int row, int col, int rowDelta, int colDelta, char symbol) {
        int count = 0;
        int r = row + rowDelta;
        int c = col + colDelta;
        while (r >= 0 && r < gridSize && c >= 0 && c < gridSize && board[r][c] == symbol) {
            count++;
            r += rowDelta;
            c += colDelta;
        }
        return count;
    }

    private void saveGame(boolean hasWinner) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("start_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date(startTime)));
        values.put("end_time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()).format(new Date()));
        values.put("grid_size", gridSize);
        values.put("win_length", winLength);
        values.put("moves", movesHistory.toString());
        db.insert("games", null, values);
        db.close();
    }

    private void showWinnerMenu(String winner) {
        Intent intent = new Intent(GameActivity.this, WinnerActivity.class);
        intent.putExtra("WINNER", winner);
        startActivity(intent);
        finish();
    }
}
