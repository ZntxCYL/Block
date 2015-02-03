package com.chenyanlong.blockout;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.util.Random;

// 方块类
public class Block extends ImageView {
    private int relativeX;
    private int relativeY;
    private int colorCode;
    private int colorCode2;
    private Context context;
    private Animation animation;

    // 构造函数
    public Block(Context context, int relativeX, int relativeY) {
        super(context);
        changeColor();
        this.context = context;
        this.relativeX = relativeX;
        this.relativeY = relativeY;
        initAnimation();
    }

    // 初始化动画
    private void initAnimation() {
        animation = AnimationUtils.loadAnimation(context, R.anim.block_anim);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                changeColor();
            }

            @Override
            public void onAnimationRepeat(Animation animation) {}
        });
    }

    // 获取相对X
    public int getRelativeX() {
        return this.relativeX;
    }

    // 获取相对Y
    public int getRelativeY() {
        return this.relativeY;
    }

    // 获取颜色代码
    public int getColorCode() {
        return this.colorCode;
    }

    // 随机颜色
    private int randomColor() {
        Random random = new Random();
        colorCode2 = random.nextInt(4);
        switch (colorCode2) {
            case 0:
                return R.drawable.red;
            case 1:
                return R.drawable.green;
            case 2:
                return R.drawable.blue;
            case 3:
                return R.drawable.yellow;
            default:
                return R.drawable.red;
        }
    }

    // 更改颜色
    public void changeColor() {
        colorCode = randomColor();
        resetColor();
    }

    public void resetColor() {
        this.setBackgroundResource(colorCode);
    }

    // 获取按下颜色
    private int getPressColor() {
        switch (colorCode2) {
            case 0:
                return R.drawable.red2;
            case 1:
                return R.drawable.green2;
            case 2:
                return R.drawable.blue2;
            case 3:
                return R.drawable.yellow2;
            default:
                return R.drawable.red2;
        }
    }

    // 设置按下状态
    public void setPress() {
        this.setBackgroundResource(getPressColor());
    }

    // 执行动画
    public void startAnim() {
        this.startAnimation(animation);
    }

    // 大于或等于
    public boolean greaterThan(Block block) {
        return (this.relativeX > block.relativeX && this.relativeY > block.relativeY) ||
               (this.relativeX == block.relativeX && this.relativeY > block.relativeY) ||
               (this.relativeX > block.relativeX && this.relativeY == block.relativeY) ||
               (this.relativeX == block.relativeX && this.relativeY == block.relativeY);
    }

    // 小于或等于
    public boolean lessThan(Block block) {
        return (this.relativeX < block.relativeX && this.relativeY < block.relativeY) ||
               (this.relativeX == block.relativeX && this.relativeY < block.relativeY) ||
               (this.relativeX < block.relativeX && this.relativeY == block.relativeY) ||
               (this.relativeX == block.relativeX && this.relativeY == block.relativeY);
    }
}