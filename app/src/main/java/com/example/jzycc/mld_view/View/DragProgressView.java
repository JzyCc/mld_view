package com.example.jzycc.mld_view.View;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Build;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;
/*
*  用于实现一个自定义的可拖拽的进度条
*/
public class DragProgressView extends View {

    private Paint paint;

    /*当前进度*/
    private float loadProgress;

    /*加载后进度条的颜色*/
    private int loadedColor = 0xFF1E90FF;

    /*当前进度位置点的颜色*/
    private int spotColor = 0xFFFFFFFF;

    /*进度度默认颜色*/
    private int defaultColor = 0xFFCCCCCC;

    /*进度条高度 px*/
    private float lineHeight = 20;

    /*圆点半径*/
    private float spotRadius = 50;

    /*进度条左右边距*/
    private float paddingLeft = 60;

    /*背景色*/
    private int backgroundaColor = 0x80000000;

    /*进度条 X 圆角*/
    private float lineRadiusX = 10;

    /*进度条 Y 圆角*/
    private float lineRadiusY = 10;

    /*存储当前进度位置的点*/
    private PointF touchPoint = new PointF();

    /*当前是否可以拖动进度条，用于开启拖拽功能时*/
    private boolean canDrag = false;

    /*是否开启拖拽功能*/
    private boolean dragEnabled = true;

    /*用于监听当前进度条的位置*/
    public static interface OnProgressListener{
        void onCurrentProgress(float value);

        void onError();
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
        transformProgress(loadProgress);
    }

    private void transformProgress(final float loadProgress){
        if(loadProgress == 0f){
            checkTouchRange(getLineStartX(), getSpotY());
        }else if (loadProgress == 100f){
            checkTouchRange(getLineStopX(), getSpotY());
        }else {
            post(new Runnable() {
                @Override
                public void run() {
                    checkTouchRange(loadProgress*(getMeasuredWidth() - paddingLeft - spotRadius)/100, getSpotY());
                }
            });
        }
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

            invalidate();

            for (OnProgressListener onProgressListener : onProgressListeners){
                onProgressListener.onCurrentProgress(getLoadInt());
            }

            return x;
        }

        for (OnProgressListener onProgressListener : onProgressListeners){
            onProgressListener.onError();
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
                if (dragEnabled && checkTouchRange(event.getX(), event.getY()) > 0) {
                    canDrag = true;
                    return true;
                }else {
                    return false;
                }
            case MotionEvent.ACTION_MOVE:
                if (dragEnabled && canDrag && checkTouchRange(event.getX(), event.getY()) > 0) {
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
