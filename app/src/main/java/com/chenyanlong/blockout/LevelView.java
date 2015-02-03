package com.chenyanlong.blockout;

import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.TableRow;
import android.widget.TextView;

/**
 * TODO: document your custom view class.
 */
public class LevelView extends FrameLayout implements View.OnClickListener {
    private int level;
    private int score;
    private boolean isLock;
    private ImageButton imageButton;
    private TextView textLevel;
    private TextView textScore;

    public LevelView(Context context) {
        this(context, null);
    }

    public LevelView(Context context, AttributeSet attrs) {
        super(context, attrs);
        LayoutInflater.from(context).inflate(R.layout.sample_level_view, this);

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.LevelView);
        try {
            level = typedArray.getInteger(R.styleable.LevelView_level, 0);
            score = typedArray.getInteger(R.styleable.LevelView_score, 0);
            isLock = typedArray.getBoolean(R.styleable.LevelView_isLock, true);
        }
        finally {
            typedArray.recycle();
        }

        imageButton = (ImageButton)findViewById(R.id.background_button);
        imageButton.setOnClickListener(this);
        textLevel = (TextView)findViewById(R.id.text_level);
        textScore = (TextView)findViewById(R.id.text_score);
    }

    public void setLevel(int level) {
        this.level = level;
        refreshLevel();
        requestLayout();
        invalidate();
    }

    private void refreshLevel() {
        textLevel.setText(isLock ? "" : String.format("%d", level));

        TableRow.LayoutParams params = new TableRow.LayoutParams();
        if (level % 3 != 1) {
            params.leftMargin = 20;
        }
        params.topMargin = 10;
        setLayoutParams(params);
    }

    public void setScore(int score) {
        this.score = score;
        refreshScore();
        requestLayout();
        invalidate();
    }

    private void refreshScore() {
        textScore.setText(isLock || score == 0 ? "" : String.format("%d", score));
    }

    public void setLock(boolean isLock) {
        this.isLock = isLock;
        imageButton.setImageResource(isLock ? R.drawable.lock : R.drawable.level);
        refreshLevel();
        refreshScore();
        requestLayout();
        invalidate();
    }

    @Override
    public void onClick(View v) {
        MainActivity.SoundPool.play(isLock ? 2 : 6, 1, 1, 1, 0, 1);
        if (!isLock) {
            GameHelper.setCurrentLevel(level);
            Intent intent = new Intent(getContext(), GameTipActivity.class);
            getContext().startActivity(intent);
        }
    }
}