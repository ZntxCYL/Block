package com.chenyanlong.blockout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;


public class GameTipActivity extends Activity {
    private LinearLayout linearLayout;
    private AnimationDrawable frameAnim;
    private boolean isFirstPlay;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_tip);
        init();
        if (isFirstPlay) {
            linearLayout = (LinearLayout) findViewById(R.id.game_tip);
            linearLayout.setBackgroundResource(R.drawable.game_tip_anim);
            frameAnim = (AnimationDrawable) linearLayout.getBackground();
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isFirstPlay) {
            frameAnim.start();
            saveNotFirstPlay();
        }
        else {
            skip(null);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isFirstPlay) {
            frameAnim.stop();
            frameAnim = null;
            linearLayout = null;
        }
    }

    private void init() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        isFirstPlay = preferences.getBoolean(GameHelper.getIsFirstPlayKey(), true);
    }

    private void saveNotFirstPlay() {
        SharedPreferences preferences = getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(GameHelper.getIsFirstPlayKey(), false);
        editor.commit();
    }

    public void skip(View view) {
        Intent intent = new Intent(this, BlockActivity.class);
        startActivity(intent);
        finish();
    }
}