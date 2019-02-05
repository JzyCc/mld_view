package com.example.jzycc.mld_view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
/*
*  用于实现一个自定义的可拖拽的进度条
*/
public class DragProgressView extends View {

    private Paint paint;


    private float loadProgress;
    private int loadedColor = 0xFF1E90FF;
    private int spotColor = 0xFFFFFFFF;
    private int defaultColor = 0xFFCCCCCC;
    private float lineHeight = 20;
    private float spotRadius = 50;
    private float paddingLeft = 60;
    private int backgroundaColor = 0x80000000;

    private float lineRadiusX = 10;
    private float lineRadiusY = 10;

    private PointF touchPoint = new PointF();

    private boolean canDrag = false;

    private boolean dragEnabled = true;

    public static interface OnProgressListener{
        void onCurrentProgress(float value);
    }

    private List<OnProgressListener> onProgressListeners = new ArrayList<>();




    public DragProgressView(Context context) {
        super(context);
        initPaint();
    }

    public DragProgressView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initPaint();
    }

    public DragProgressView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initPaint();
    }

    public void openDrag(boolean dragEnabled){
        this.dragEnabled = dragEnabled;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        canvas.drawColor(backgroundaColor);

        paint.setColor(defaultColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(getLineStartX(), getLineStartY(), getLineStopX(), getLineStopY(),lineRadiusX, lineRadiusY, paint);
        }else {
            canvas.drawRect(getLineStartX(), getLineStartY(), getLineStopX(), getLineStopY(),paint);
        }

        paint.setColor(loadedColor);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            canvas.drawRoundRect(getLineStartX(), getLineStartY(), touchPoint.x, touchPoint.y + lineHeight/2, lineRadiusX, lineRadiusY, paint);
        }else{
            canvas.drawRect(getLineStartX(), getLineStartY(), touchPoint.x, touchPoint.y + lineHeight/2, paint);
        }

        paint.setColor(spotColor);
        canvas.drawCircle(touchPoint.x, touchPoint.y, spotRadius, paint);

    }

    private float getLineStartX() {
        return paddingLeft;
    }

    private float getLineStartY() {
        return (getMeasuredHeight() - lineHeight) / 2;
    }

    private float getLineStopX() {
        return getMeasuredWidth() - paddingLeft;
    }

    private float getLineStopY() {
        return (getMeasuredHeight() + lineHeight) / 2;
    }

    private float getSpotY() {
        return getMeasuredHeight() / 2;
    }

    private void initPaint() {
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        post(new Runnable() {
            @Override
            public void run() {
                touchPoint.x = getLineStartX();
                touchPoint.y = getSpotY();
            }
        });

    }

    public void setLoadedColor(int loadedColor) {
        this.loadedColor = loadedColor;
    }

    public void setDefaultColor(int defaultColor) {
        this.defaultColor = defaultColor;
    }

    public void setLineHeight(float lineHeight) {
        this.lineHeight = lineHeight;
    }

    public void setSpotRadius(float spotRadius) {
        this.spotRadius = spotRadius;
    }

    public void addProgressListener(OnProgressListener onProgressListener){
        onProgressListeners.add(onProgressListener);
    }

    public void setLoadProgress(float loadProgress){
        this.loadProgress = loadProgress;
    }

    private float getLoadInt(){
        //如果点挡住进度条最左边那么进度为0，挡住进度条最右边为 100，主要为了解决边缘问题
        if(touchPoint.x <= paddingLeft + spotRadius){
            return 0f;
        }else if(touchPoint.x >= getMeasuredWidth() - spotRadius - paddingLeft){
            return 100f;
        }
        return (touchPoint.x - paddingLeft - spotRadius)*100/(getMeasuredWidth() - 2*paddingLeft - 2*spotRadius);
    }
    private float checkTouchRange(float x, float y) {
        if(x >= getLineStartX() && x <= getLineStopX()
                && y >= (getSpotY() - spotRadius) && y <= (getSpotY() + spotRadius)){
            touchPoint.x = x;

            for (OnProgressListener onProgressListener : onProgressListeners){
                onProgressListener.onCurrentProgress(getLoadInt());
            }

            return x;
        }
        return -1f;
    }

    @Override
    public boolean performClick() {
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                performClick();
                if (checkTouchRange(event.getX(), event.getY()) > 0 && dragEnabled) {
                    canDrag = true;
                    invalidate();
                    return true;
                }else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                if (checkTouchRange(event.getX(), event.getY()) > 0 && dragEnabled && canDrag) {
                    invalidate();
                    return true;
                } else {
                    canDrag = false;
                    return false;
                }
            case MotionEvent.ACTION_UP:
                canDrag = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                canDrag = false;
                break;
        }
        return super.onTouchEvent(event);
    }
}
