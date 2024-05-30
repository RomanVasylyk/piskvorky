package com.example.piskvorky;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;

public class GameReplayActivity extends AppCompatActivity {

    private GameDatabaseHelper dbHelper;
    private ListView gameListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_replay);

        dbHelper = new GameDatabaseHelper(this);
        gameListView = findViewById(R.id.gameListView);

        loadGameList();
    }

    private void loadGameList() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("games", null, null, null, null, null, "start_time DESC");

        List<String> gameList = new ArrayList<>();
        final List<Integer> gameIds = new ArrayList<>();

        while (cursor.moveToNext()) {
            int id = cursor.getInt(cursor.getColumnIndex("id"));
            String startTime = cursor.getString(cursor.getColumnIndex("start_time"));
            String endTime = cursor.getString(cursor.getColumnIndex("end_time"));
            gameList.add("Game from " + startTime + " to " + endTime);
            gameIds.add(id);
        }
        cursor.close();
        db.close();

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, gameList);
        gameListView.setAdapter(adapter);

        gameListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                int gameId = gameIds.get(position);
                Intent intent = new Intent(GameReplayActivity.this, GameDetailActivity.class);
                intent.putExtra("GAME_ID", gameId);
                startActivity(intent);
            }
        });
    }
}
