package com.chenyanlong.blockout;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.widget.TableLayout;
import android.widget.TableRow;

public class LevelActivity extends Activity {
    private String ACTIVITY_TAG = "LevelActivity";
    private SharedPreferences sharedPreferences;
    private int LEVEL_TABLE_ROW = 3;
    private int LEVEL_TABLE_COLUMN = 3;
    private LevelView[][] levelViews = new LevelView[LEVEL_TABLE_COLUMN][LEVEL_TABLE_ROW];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_level);
        try {
            sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
            createTable();
        }
        catch (Exception e) {
            Log.e(ACTIVITY_TAG, e.toString());
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        refresh();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        levelViews = null;
        sharedPreferences = null;
    }

    private void createTable() {
        TableLayout levelTable = (TableLayout)findViewById(R.id.level_table);
        int level = 0;
        for (int row = 0; row < LEVEL_TABLE_ROW; ++row) {
            TableRow levelRow = new TableRow(this);

            for (int column = 0; column < LEVEL_TABLE_COLUMN; ++column) {
                LevelView levelView = createBlock(++level);
                levelRow.addView(levelView);
                levelViews[column][row] = levelView;
            }
            levelTable.addView(levelRow);
        }
    }

    private LevelView createBlock(int level) {
        LevelView levelView = new LevelView(this);
        levelView.setLevel(level);
        return levelView;
    }

    private void refresh() {
        int level = 0;
        for (int row = 0; row < LEVEL_TABLE_ROW; ++row) {
            for (int column = 0; column < LEVEL_TABLE_COLUMN; ++column) {
                refreshLevelView(levelViews[column][row], ++level);
            }
        }
    }

    private void refreshLevelView(LevelView levelView, int level) {
        int highScore = loadHighScore(level);
        levelView.setScore(highScore);
        boolean isLock = loadLevelLock(level);
        levelView.setLock(level != 1 && isLock);
    }

    private int loadHighScore(int level) {
        return sharedPreferences.getInt(GameHelper.getSaveScoreKey(level), 0);
    }

    private boolean loadLevelLock(int level) {
        return sharedPreferences.getBoolean(GameHelper.getSavePassKey(level), true);
    }
}