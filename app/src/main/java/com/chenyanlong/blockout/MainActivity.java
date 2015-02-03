package com.chenyanlong.blockout;

import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends Activity {

    public static SoundPool SoundPool;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        createSound();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        SoundPool.release();
    }

    private void createSound() {
        SoundPool = new SoundPool(3, AudioManager.STREAM_SYSTEM, 5);
        SoundPool.load(this, R.raw.click, 0);
        SoundPool.load(this, R.raw.error, 0);
        SoundPool.load(this, R.raw.comple, 0);
        SoundPool.load(this, R.raw.win, 0);
        SoundPool.load(this, R.raw.lose, 0);
        SoundPool.load(this, R.raw.level, 0);
    }

    // 开始游戏
    public void startGame(View view) {
        GameHelper.setCurrentLevel(0);
        Intent intent = new Intent(this, GameTipActivity.class);
        startActivity(intent);
    }

    // 游戏关卡
    public void gameLevel(View view) {
        Intent intent = new Intent(this, LevelActivity.class);
        startActivity(intent);
    }

    // 关于
    public void about(View view) {
        Intent intent = new Intent(this, AboutActivity.class);
        startActivity(intent);
    }

    // 退出游戏
    public void exitGame(View view) {
        android.os.Process.killProcess(android.os.Process.myPid());
    }
}