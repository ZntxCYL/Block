package com.chenyanlong.blockout;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BlockActivity extends Activity {

    // 属性

    private String ACTIVITY_TAG = "BlockActivity";
    private int GAME_TABLE_ROW = 8;
    private int GAME_TABLE_COLUMN = 8;
    private int pressCount = 0;
    private int pressColor = 0;
    private int score;
    private int highScore;
    private Block[][] allBlock = new Block[GAME_TABLE_COLUMN][GAME_TABLE_ROW];
    private Block[] pressBlock = new Block[4];
    private Timer timer;
    private int gameTime = 180;
    private Handler setTimeHandler;
    private SharedPreferences sharedPreferences;


    // 重写方法

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_block);
        try {
            initPreference();
            createTable();
            init();
            checkHideView();
        }
        catch (Exception e) {
            Log.e(ACTIVITY_TAG, e.toString());
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        allBlock = null;
        pressBlock = null;
        timer.cancel();
        timer.purge();
        sharedPreferences = null;
    }


    // 方法

    private void init() {
        pressCount = 0;
        pressColor = 0;
        score = 0;
        gameTime = 180;
        initTime();
        initHandler();
        setScore();
        setLevel();
        setPassScore();
        initHighScore();
    }

    private void initTime() {
        timer = new Timer();
        timer.schedule(new GameTime(), 1000, 1000);
    }

    private void createTable() {
        TableLayout gameTable = (TableLayout)findViewById(R.id.game_table);

        for (int row = 0; row < GAME_TABLE_ROW; ++row) {
            TableRow gameRow = new TableRow(this);

            for (int column = 0; column < GAME_TABLE_COLUMN; ++column) {
                Block block = createBlock(column, row);
                gameRow.addView(block);
                allBlock[column][row] = block;
            }
            gameTable.addView(gameRow);
        }
    }

    private Block createBlock(int x, int y) {
        Block block = new Block(this, x, y);
        int blockWidth = (int)(getScreenWidth() * (1f / (GAME_TABLE_ROW + 1)));
        TableRow.LayoutParams params = new TableRow.LayoutParams(blockWidth, blockWidth);
        block.setLayoutParams(params);
        block.setOnClickListener(new BlockClickEvent());
        return block;
    }

    private int getScreenWidth() {
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        return dm.widthPixels;
    }

    private void initPreference() {
        sharedPreferences = getSharedPreferences(getString(R.string.preference_file_key), Context.MODE_PRIVATE);
    }

    private void initHighScore() {
        highScore = sharedPreferences.getInt(GameHelper.getSaveScoreKey(), 0);
        setHighScore(highScore);
    }

    private void saveHighScore() {
        if (score > highScore) {
            setHighScore(score);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.putInt(GameHelper.getSaveScoreKey(), score);
            editor.commit();
        }
    }

    private void setScore() {
        TextView textView = (TextView)findViewById(R.id.score_text);
        textView.setText(String.format("当前得分：%d", score));
    }

    private void setHighScore(int highScore) {
        TextView textView = (TextView)findViewById(R.id.high_score_text);
        textView.setText(String.format("最佳分数：%d", highScore));
    }

    private void setLevel() {
        TextView textView = (TextView) findViewById(R.id.level_text);
        textView.setText(String.format("当前关卡：%d", GameHelper.getCurrentLevel()));
    }

    private void setPassScore() {
        TextView textView = (TextView) findViewById(R.id.pass_text);
        textView.setText(String.format("目标分数：%d", GameHelper.getPassScore()));
    }

    private void checkHideView() {
        if (GameHelper.getCurrentLevel() == 0) {
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(0, 0);

            TextView textView = (TextView)findViewById(R.id.level_text);
            textView.setLayoutParams(params);
            textView.setVisibility(View.INVISIBLE);

            textView = (TextView)findViewById(R.id.pass_text);
            textView.setLayoutParams(params);
            textView.setVisibility(View.INVISIBLE);

            LinearLayout linearLayout = (LinearLayout)findViewById(R.id.liner_layout_top);
            params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT);
            params.setMargins(0, 14, 0, 0);
            linearLayout.setLayoutParams(params);

            linearLayout = (LinearLayout)findViewById(R.id.liner_layout);
            linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        }
    }

    private void reStart() {
        init();
        for (int x = 0; x < GAME_TABLE_ROW; ++x) {
            for (int y = 0; y < GAME_TABLE_COLUMN; ++y) {
                allBlock[x][y].startAnim();
            }
        }
        MainActivity.SoundPool.play(3, 1, 1, 3, 0, 1);
    }


    // 事件

    class BlockClickEvent implements View.OnClickListener {
        Block block;

        public void onClick(View view) {
            try {
                block = (Block) view;
                if (!checkPressBlock())
                    return;
                pressBlock[pressCount++] = block;
                if (!checkPressBlock2()) {
                    return;
                }
                checkPressColor();
                check();
            }
            catch (Exception e) {
                Log.e(ACTIVITY_TAG, e.toString());
            }
        }

        private boolean checkPressBlock() {
            if (pressCount < 1)
                return true;

            for (Block block : pressBlock) {
                if (block != null && block == this.block) {
                    pressError();
                    return false;
                }
            }
            return true;
        }

        private boolean checkPressBlock2() {
            if (pressCount < 3)
                return true;

            for (Block x : pressBlock) {
                int xCount = 0;
                int yCount = 0;

                for (Block y : pressBlock) {
                    if (x == null || y == null || x == y)
                        continue;
                    if (x.getRelativeX() == y.getRelativeX())
                        ++xCount;
                    if (x.getRelativeY() == y.getRelativeY())
                        ++yCount;
                }
                if (xCount > 1 || yCount > 1) {
                    pressError();
                    return false;
                }
            }
            return true;
        }

        private void checkPressColor() {
            block.setPress();
            boolean isPlay = true;
            if (pressCount > 1) {
                if (pressColor != block.getColorCode()) {
                    pressError();
                    isPlay = false;
                }
            }
            if (isPlay && pressCount != 4) {
                MainActivity.SoundPool.play(1, 1, 1, 2, 0, 1);
            }
            pressColor = block.getColorCode();
        }

        private void pressError() {
            MainActivity.SoundPool.play(2, 1, 1, 1, 0, 1);
            resetColor();
            clearPressBlock();
        }

        private void clearPressBlock() {
            for (int i = 0; i < pressBlock.length; ++i) {
                pressBlock[i] = null;
            }
            pressCount = 0;
        }

        private void resetColor() {
            for (Block block : pressBlock) {
                if (block == null)
                    return;
                block.resetColor();
            }
        }

        private void check() {
            if (pressCount == pressBlock.length) {
                if (!checkColor()) {
                    pressError();
                    return;
                }
                if (!checkBox()) {
                    pressError();
                    return;
                }

                resetColor();
                Block minBlock = getMinBlock();
                Block maxBlock = getMaxBlock();

                for (int row = 0; row < GAME_TABLE_ROW; ++row) {
                    for (int column = 0; column < GAME_TABLE_COLUMN; ++column) {
                        Block tempBlock = allBlock[column][row];
                        if (tempBlock.greaterThan(minBlock) &&
                                tempBlock.lessThan(maxBlock)) {
                            tempBlock.startAnim();
                            score += 10;
                        }
                    }
                }
                setScore();
                MainActivity.SoundPool.play(3, 1, 1, 3, 0, 1);
                pressCount = 0;
                clearPressBlock();
            }
        }

        private boolean checkColor() {
            int colorCode = pressBlock[0].getColorCode();
            for (int i = 1; i < pressBlock.length; ++i) {
                if (colorCode != pressBlock[i].getColorCode())
                    return false;
            }
            return true;
        }

        private boolean checkBox() {
            for (int x = 0; x < pressBlock.length; ++x) {
                boolean yesX = false;
                boolean yesY = false;

                for (int y = 0; y < pressBlock.length; ++y) {
                    if (x == y)
                        continue;
                    if (pressBlock[x].getRelativeX() == pressBlock[y].getRelativeX())
                        yesX = true;
                    if (pressBlock[x].getRelativeY() == pressBlock[y].getRelativeY())
                        yesY = true;
                }
                if (!yesX || !yesY)
                    return false;
            }
            return true;
        }

        private Block getMinBlock() {
            Block min = pressBlock[0];
            for (int i = 1; i < pressBlock.length; ++i) {
                if (!min.lessThan(pressBlock[i]))
                    min = pressBlock[i];
            }
            return min;
        }

        private Block getMaxBlock() {
            Block max = pressBlock[0];
            for (int i = 1; i < pressBlock.length; ++i) {
                if (!max.greaterThan(pressBlock[i]))
                    max = pressBlock[i];
            }
            return max;
        }
    }

    class GameTime extends TimerTask {
        @Override
        public void run() {
            setTimeHandler.sendEmptyMessage(0);
        }
    }

    // 游戏结束
    private void initHandler() {
        setTimeHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                try {
                    Date time = new Date(--gameTime * 1000);
                    TextView textView = (TextView) findViewById(R.id.time_text);
                    SimpleDateFormat format = new SimpleDateFormat("mm:ss");
                    String temp = format.format(time);
                    String residualTime = String.format("剩余时间：%s", temp);
                    textView.setText(residualTime);

                    if (gameTime == 0) {
                        timer.purge();
                        timer.cancel();
                        saveHighScore();
                        checkPass();
                        tip();
                    }
                }
                catch (Exception e) {
                    Log.e(ACTIVITY_TAG, e.toString());
                }
            }

            private void checkPass() {
                if (score >= GameHelper.getPassScore()) {
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putBoolean(GameHelper.getSavePassKey(GameHelper.getNextLevel()), false);
                    editor.commit();
                }
            }

            private void tip () {
                AlertDialog.Builder builder = new AlertDialog.Builder(BlockActivity.this)
                        .setMessage(String.format("时间到，您的得分为：%d", score))
                        .setNeutralButton("返回", new ReturnEvent())
                        .setNegativeButton("重玩", new ReStartEvent())
                        .setOnKeyListener(new DialogBackEvent());
                if (GameHelper.getCurrentLevel() == 0) {
                    MainActivity.SoundPool.play(4, 1, 1, 4, 0, 1);
                    builder.setTitle("游戏结束");
                    builder.show();
                    return;
                }
                if (score >= GameHelper.getPassScore()) {
                    if (GameHelper.getCurrentLevel() < 9) {
                        builder.setTitle("闯过成功");
                        builder.setPositiveButton("下一关", new NextEvent());
                        MainActivity.SoundPool.play(4, 1, 1, 4, 0, 1);
                    }
                    else {
                        builder.setTitle("恭喜你成功通关了！");
                        MainActivity.SoundPool.play(4, 1, 1, 4, 0, 1);
                    }
                }
                else {
                    builder.setTitle("闯过失败");
                    MainActivity.SoundPool.play(5, 1, 1, 4, 0, 1);
                }
                builder.show();
            }
        };
    }

    private class ReturnEvent implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            BlockActivity.this.finish();
        }
    }

    private class ReStartEvent implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            reStart();
        }
    }

    private class NextEvent implements DialogInterface.OnClickListener {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            GameHelper.nextLevel();
            reStart();
        }
    }

    private class DialogBackEvent implements DialogInterface.OnKeyListener {
        @Override
        public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
            if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
                dialog.dismiss();
                BlockActivity.this.finish();
            }
            return false;
        }
    }
}