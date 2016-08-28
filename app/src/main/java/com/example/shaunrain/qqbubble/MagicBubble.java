package com.example.shaunrain.qqbubble;

import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PointF;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.DecelerateInterpolator;


public class MagicBubble extends View {

    private final float C = 0.551915024494f; //画圆用

    private Paint mPaint;
    private Path mPath;

    private float stretchDistance;  //拉伸距离
    private float sumLength;
    private float mRadius = 50;
    private float cCircle = mRadius * C;
    private float cDistance = cCircle * (1 - C);

    private float ratio = 0;

    private Point p0, p1, p2, p3;

    private ValueAnimator anim;


    public MagicBubble(Context context) {
        super(context, null);
    }

    public MagicBubble(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        mPaint = new Paint();
        mPaint.setColor(Color.argb(255, 242, 147, 63));
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setAntiAlias(true);

        mPath = new Path();

        p0 = new Point();
        p1 = new Point();
        p2 = new Point();
        p3 = new Point();

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        mPath.reset();
        canvas.translate(mRadius, mRadius);

        if (ratio >= 0 && ratio < 0.2) {
            status1(ratio);
        } else if (ratio >= 0.2 && ratio < 0.5) {
            status2(ratio);
        } else if (ratio >= 0.5 && ratio < 0.8) {
            status3(ratio);
        } else if (ratio >= 0.8 && ratio < 0.9) {
            status4(ratio);
        } else if (ratio >= 0.9 && ratio <= 1.0) {
            status5(ratio);
        }

        float offset = sumLength * (ratio - 0.2f);
        offset = offset > 0 ? offset : 0;

        p0.adjustXAll(offset);
        p1.adjustXAll(offset);
        p2.adjustXAll(offset);
        p3.adjustXAll(offset);

        mPath.moveTo(p0.x, p0.y);
        mPath.cubicTo(p0.right.x, p0.right.y, p1.top.x, p1.top.y, p1.x, p1.y);
        mPath.cubicTo(p1.bottom.x, p1.bottom.y, p2.right.x, p2.right.y, p2.x, p2.y);
        mPath.cubicTo(p2.left.x, p2.left.y, p3.bottom.x, p3.bottom.y, p3.x, p3.y);
        mPath.cubicTo(p3.top.x, p3.top.y, p0.left.x, p0.left.y, p0.x, p0.y);

        canvas.drawPath(mPath, mPaint);

    }

    public void start() {

        anim = ValueAnimator.ofFloat(0f, 1f);
        long mDuration = 1500;
        anim.setDuration(mDuration);
        anim.setRepeatMode(ValueAnimator.RESTART);
        anim.setInterpolator(new DecelerateInterpolator());
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                ratio = (float) animation.getAnimatedValue();
                invalidate();
            }
        });
        anim.start();

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (anim != null) {
            anim.start();
        }
        return true;
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        int mWidth = getWidth();
        int mHeight = getHeight();
        stretchDistance = mRadius * 3f / 4;
        sumLength = mWidth - 2 * mRadius;
    }

    private void status0() {    //0.0

        p0.setY(mRadius);
        p1.setX(mRadius);
        p2.setY(-mRadius);
        p3.setX(-mRadius);

        p0.left.x = p2.left.x = -cCircle;
        p0.right.x = p2.right.x = cCircle;

        p0.x = p2.x = 0;

        p1.top.y = p3.top.y = cCircle;
        p1.bottom.y = p3.bottom.y = -cCircle;

        p1.y = p3.y = 0;

    }

    private void status1(float ratio) {  //0.0 ~ 0.2

        status0();
        p1.setX(mRadius + stretchDistance * ratio * 5);

    }

    private void status2(float ratio) {  //0.2 ~ 0.5

        status1(0.2f);
        ratio = (ratio - 0.2f) * (10f / 3);
        p0.adjustXAll(stretchDistance / 2 * ratio);
        p2.adjustXAll(stretchDistance / 2 * ratio);

        p1.adjustY(cDistance * ratio);
        p3.adjustY(cDistance * ratio);

    }

    private void status3(float ratio) {  //0.5 ~ 0.8

        status2(0.5f);
        ratio = (ratio - 0.5f) * (10f / 3);
        p0.adjustXAll(stretchDistance / 2 * ratio);
        p2.adjustXAll(stretchDistance / 2 * ratio);

        p1.adjustY(-cDistance * ratio);
        p3.adjustY(-cDistance * ratio);

        p3.adjustXAll(stretchDistance / 2 * ratio);

    }

    private void status4(float ratio) {  //0.8 ~ 0.9

        status3(0.8f);
        ratio = (ratio - 0.8f) * (10f / 1);
        p3.adjustXAll(stretchDistance / 2 * ratio);

    }

    private void status5(float ratio) {  //0.9 ~ 1.0

        status4(0.9f);
        ratio = (ratio - 0.9f) * (10f / 1);
        p3.adjustXAll((float) (Math.sin(Math.PI * ratio) * (1 / 5f * mRadius)));

    }

}

class Point {

    float x = 0;
    float y = 0;

    public PointF left = new PointF();
    public PointF right = new PointF();

    public PointF top = new PointF();
    public PointF bottom = new PointF();

    public void setX(float x) {
        this.x = x;
        left.x = x;
        right.x = x;
        top.x = x;
        bottom.x = x;
    }

    public void setY(float y) {
        this.y = y;
        left.y = y;
        right.y = y;
        top.y = y;
        bottom.y = y;
    }

    public void adjustXAll(float offset) {
        x += offset;
        left.x += offset;
        right.x += offset;
        top.x += offset;
        bottom.x += offset;
    }

    public void adjustY(float offset) {
        top.y += offset;
        bottom.y -= offset;
    }


}
