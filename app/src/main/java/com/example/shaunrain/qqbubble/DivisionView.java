package com.example.shaunrain.qqbubble;

import android.content.Context;
import android.graphics.*;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;


public class DivisionView extends View {

    private final float C = 0.551915024494f;

    private Paint mPaint;
    private Path mPath;
    private int mWidth;
    private int mHeight;
    private int mCenterX;
    private int mCenterY;

    private Bubble originBack;
    private Bubble origin;
    private Bubble copy;
    private float radius = 30;
    private float distance = 0;     //圆心与原点距离
    private float maxDistance = 6 * radius; //极限
    private double mRadian = 0;
    private boolean uRNotAlone = false;
    private boolean firstTouch = true;


    public DivisionView(Context context) {
        super(context, null);
    }

    public DivisionView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {
        mPaint = new Paint();
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setColor(Color.RED);
        mPaint.setAntiAlias(true);
        mPath = new Path();

        origin = new Bubble(new PointF(0, radius), new PointF(radius * C, radius), new PointF(radius, radius * C)
                , new PointF(radius, 0), new PointF(radius, -radius * C), new PointF(radius * C, -radius)
                , new PointF(0, -radius), new PointF(-radius * C, -radius), new PointF(-radius, -radius * C)
                , new PointF(-radius, 0), new PointF(-radius, radius * C), new PointF(-radius * C, radius));

        originBack = origin.divide(0);

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = w;
        mHeight = h;
        mCenterX = mWidth / 2;
        mCenterY = mHeight / 2;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        float x = event.getX() - mCenterX;
        float y = event.getY() - mCenterY;

        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                if (firstTouch) {
                    if (!inside(new PointF(x, y), 0, 0, radius)) {
                        break;
                    } else {
                        firstTouch = false;
                    }
                }
                mRadian = Math.atan2(y, x);
                distance = (float) Math.sqrt(y * y + x * x);
                uRNotAlone = true;
                invalidate();
                break;
            case MotionEvent.ACTION_UP:
                uRNotAlone = false;
                distance = 0;
                firstTouch = true;
                invalidate();
                break;
        }

        return true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(mCenterX, mCenterY);

        origin = originBack.scale(1 - (distance / maxDistance) * 0.8f);

        if (uRNotAlone) {
            canvas.scale(1.1f, 1.1f);
            canvas.rotate((float) (mRadian * 180 / Math.PI));
            copy = originBack.divide(distance);
            canvas.drawPath(getCirclePath(copy.getRef(), mPath), mPaint);
            canvas.drawPath(getCurvePath(mPath), mPaint);
        }
        canvas.drawPath(getCirclePath(origin.getRef(), mPath), mPaint);

    }

    private Path getCirclePath(PointF[] refs, Path path) {

        if (refs == null || refs.length % 4 != 0) {
            return null;
        }
        int len = refs.length;
        path.reset();
        path.moveTo(refs[0].x, refs[0].y);
        for (int i = 1; i + 2 <= len; i += len / 4) {
            if (i + 2 == len) {
                path.cubicTo(refs[i].x, refs[i].y, refs[i + 1].x, refs[i + 1].y, refs[0].x, refs[1].y);
            } else {
                path.cubicTo(refs[i].x, refs[i].y, refs[i + 1].x, refs[i + 1].y, refs[i + 2].x, refs[i + 2].y);
            }
        }
        return path;

    }

    private Path getCurvePath(Path path) {

        path.reset();
        path.moveTo(origin.ref[0].x, origin.ref[0].y);
        path.cubicTo(origin.ref[3].x, origin.ref[3].y, copy.ref[9].x, copy.ref[9].y, copy.ref[0].x, copy.ref[0].y);
        path.lineTo(copy.ref[6].x, copy.ref[6].y);
        path.cubicTo(copy.ref[9].x, copy.ref[9].y, origin.ref[3].x, origin.ref[3].y, origin.ref[6].x, origin.ref[6].y);
        path.lineTo(origin.ref[0].x, origin.ref[0].y);

        return path;

    }

    private boolean inside(PointF pointF, float a, float b, float radius) {

        return (pointF.x - a) * (pointF.x - a) + (pointF.x - b) * (pointF.x - b) <= radius * radius;

    }

}

class Bubble {

    public PointF[] ref;

    public Bubble(PointF... ref) {
        this.ref = ref;
    }

    public Bubble divide(float offset) {
        if (ref == null || ref.length == 0)
            return null;
        PointF[] clone = new PointF[this.ref.length];
        for (int i = 0; i < ref.length; i++) {
            clone[i] = new PointF(ref[i].x, ref[i].y);
        }
        Bubble copy = new Bubble(clone);
        copy.adjustX(offset);
        return copy;
    }

    public Bubble scale(float s) {
        return scale(s, s);
    }

    public Bubble scale(float sx, float sy) {
        Bubble scaleBubble = this.divide(0);
        for (int i = 0; i < ref.length; i++) {
            scaleBubble.ref[i].x *= Math.sqrt(sx);
            scaleBubble.ref[i].y *= Math.sqrt(sy);
        }
        return scaleBubble;
    }

    public void adjustAll(float offsetX, float offsetY) {
        adjustX(offsetX);
        adjustY(offsetY);
    }

    public void adjustX(float offset) {
        for (int i = 0; i < ref.length; i++) {
            ref[i].x += offset;
        }
    }

    public void adjustY(float offset) {
        for (int i = 0; i < ref.length; i++) {
            ref[i].y += offset;
        }
    }

    public PointF[] getRef() {
        return ref;
    }
}

