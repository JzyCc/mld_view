package com.example.jzycc.mld_view.View;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.jzycc.mld_view.utils.PixelCalculationUtils;

import java.util.ArrayList;
import java.util.List;

public class FlowLabelLayout extends ViewGroup {

    private int horizontalSpacing = PixelCalculationUtils.dp2px(getContext(), 15);
    private int verticalSpacing = PixelCalculationUtils.dp2px(getContext(), 15);
    private List<Line> lines = new ArrayList<>();
    private List<Label> labels = new ArrayList<>();


    public interface OnLabelClickListener{
        void onClick(Label label, int position);
    }

    private int lineSize = 0;

    public FlowLabelLayout(Context context) {
        super(context);
    }

    public FlowLabelLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FlowLabelLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public void setFlowLabelLayout(List<Label> labels, final OnLabelClickListener onLabelClickListner){
        this.labels = labels;
        int i = 0;
        for(final Label label : this.labels){
            TextView textView = new TextView(getContext());
            textView.setGravity(Gravity.CENTER);
            label.setPosition(i++);
            final int position = label.getPosition();
            textView.setText(label.getText());
            textView.setTextColor(label.getTextColor());
            textView.setTextSize(PixelCalculationUtils.sp2px(getContext(), label.getTextSize()));
            textView.setPadding(PixelCalculationUtils.dp2px(getContext(), label.getPaddingStart()),
                    PixelCalculationUtils.dp2px(getContext(), label.getPaddingTop()),
                    PixelCalculationUtils.dp2px(getContext(), label.getPaddingEnd()),
                    PixelCalculationUtils.dp2px(getContext(), label.getPaddingBottom()));
            if(label.getBackgroundDrawable() != null){
                textView.setBackgroundResource(label.getBackgroundDrawable());
            }

            addView(textView,new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            label.setLabel(textView);
            if(onLabelClickListner != null){
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLabelClickListner.onClick(label, position);
                    }
                });
            }
        }
    }

    public void setFlowLabelLayout(List<String> labels, Label label,final OnLabelClickListener onLabelClickListner){
        this.labels.clear();
        int i = 0;
        for(String txt : labels){
            TextView textView = new TextView(getContext());
            final Label newLabel = new Label();
            newLabel.setPosition(i++);
            final int position = label.getPosition();
            textView.setGravity(Gravity.CENTER);
            textView.setText(txt);
            textView.setTextColor(label.getTextColor());
            textView.setTextSize(label.getTextSize());

            textView.setPadding(PixelCalculationUtils.dp2px(getContext(), label.getPaddingStart()),
                    PixelCalculationUtils.dp2px(getContext(), label.getPaddingTop()),
                    PixelCalculationUtils.dp2px(getContext(), label.getPaddingEnd()),
                    PixelCalculationUtils.dp2px(getContext(), label.getPaddingBottom()));

            if(label.getBackgroundDrawable() != null){
                textView.setBackgroundResource(label.getBackgroundDrawable());
            }

            newLabel.setLabel(textView);
            newLabel.setText(txt);
            newLabel.setBackgroundDrawable(label.getBackgroundDrawable());
            newLabel.setPaddingStart(label.getPaddingStart());
            newLabel.setPaddingEnd(label.getPaddingEnd());
            newLabel.setPaddingTop(label.getPaddingTop());
            newLabel.setPaddingBottom(label.getPaddingBottom());

            addView(textView,new ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
            if(onLabelClickListner != null){
                textView.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onLabelClickListner.onClick(newLabel, position);
                    }
                });
            }
            this.labels.add(newLabel);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int maxLineWidth = MeasureSpec.getSize(widthMeasureSpec) - getPaddingLeft() - getPaddingRight();
        int maxLineHeight = MeasureSpec.getSize(heightMeasureSpec) - getPaddingTop() - getPaddingBottom();
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        Log.i("jzy111", "onMeasure: "+"!@2");
        lines.clear();
        lineSize = 0;
        @SuppressLint("DrawAllocation") Line nowLine = new Line();

        for (int i = 0; i < getChildCount(); i++) {

            View child = getChildAt(i);

            //根据数值选择子view测量的方式
            int childWidthMeasureSpec = MeasureSpec.makeMeasureSpec(maxLineWidth, widthMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : widthMode);
            int childHeightMeasureSpec = MeasureSpec.makeMeasureSpec(maxLineHeight, heightMode == MeasureSpec.EXACTLY ? MeasureSpec.AT_MOST : heightMode);

            child.measure(childWidthMeasureSpec, childHeightMeasureSpec);

            if ((lineSize + child.getMeasuredWidth()) <= maxLineWidth) {
                lineSize += horizontalSpacing;
                Log.i("jzy111", "onMeasure: "+ lineSize +"," + maxLineWidth);
            } else {
                lineSize = 0;
                lines.add(nowLine);
                nowLine = new Line();
                Log.i("jzy111", "onMeasure: "+ lineSize +"," + maxLineWidth);
            }

            nowLine.addChild(labels.get(i), child.getMeasuredHeight());
            lineSize += child.getMeasuredWidth();
        }

        if (!lines.contains(nowLine)) {
            lines.add(nowLine);
        }

        //计算总高度
        int totalHeight = 0;
        for (int i = 0; i < lines.size(); i++) {
            totalHeight += lines.get(i).getHeight();
        }
        totalHeight += (verticalSpacing * (lines.size() - 1) + getPaddingBottom() + getPaddingTop());

        setMeasuredDimension(MeasureSpec.getSize(widthMeasureSpec),
                resolveSize(totalHeight, heightMeasureSpec));

    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        int left = getPaddingLeft();
        int top = getPaddingTop();
        for (Line line : lines) {
            Log.i("jzy111", "onLayout: "+ line.getChilds().size());
            line.layout(left, top);
            top = top+line.getHeight() + verticalSpacing;
        }
    }


    private class Line {
        private List<Label> childs = new ArrayList<>();
        private int height;

        public List<Label> getChilds() {
            return childs;
        }

        public void setChilds(List<Label> childs) {
            this.childs = childs;
        }

        void addChild(Label label, int height) {
            childs.add(label);
            this.height = Math.max(height, this.height);

        }

        public void layout(int left, int top) {
            for (Label label : childs) {
                View view = label.getLabel();
                view.layout(left, top, left + view.getMeasuredWidth(), top + view.getMeasuredHeight());
                left += (horizontalSpacing + view.getMeasuredWidth());
            }
        }

        public int getHeight() {
            return height;
        }
    }

    public static class Label {
        private View label;
        private int position;
        private String text;
        private int textColor = Color.BLACK;
        private int textSize = 12;
        private Integer backgroundDrawable;
        private int paddingHorizontal;
        private int paddingVertical;
        private int paddingStart;
        private int paddingTop;
        private int paddingEnd;
        private int paddingBottom;

        public View getLabel() {
            return label;
        }

        public void setLabel(View label) {
            this.label = label;
        }

        public String getText() {
            return text;
        }

        public void setText(String text) {
            this.text = text;
        }

        public int getPosition() {
            return position;
        }

        public void setPosition(int position) {
            this.position = position;
        }

        public int getTextColor() {
            return textColor;
        }

        public void setTextColor(int textColor) {
            this.textColor = textColor;
        }

        public int getTextSize() {
            return textSize;
        }

        public void setTextSize(int textSize) {
            this.textSize = textSize;
        }

        public Integer getBackgroundDrawable() {
            return backgroundDrawable;
        }

        public void setBackgroundDrawable(Integer backgroundDrawable) {
            this.backgroundDrawable = backgroundDrawable;
        }

        public int getPaddingHorizontal() {
            return paddingHorizontal;
        }

        public void setPaddingHorizontal(int paddingHorizontal) {
            this.paddingStart = this.paddingEnd = paddingHorizontal;
            this.paddingHorizontal = paddingHorizontal;
        }

        public int getPaddingVertical() {
            return paddingVertical;
        }

        public void setPaddingVertical(int paddingVertical) {
            this.paddingTop = this.paddingBottom = paddingVertical;
            this.paddingVertical = paddingVertical;
        }

        public int getPaddingStart() {
            return paddingStart;
        }

        public void setPaddingStart(int paddingStart) {
            this.paddingStart = paddingStart;
        }

        public int getPaddingTop() {
            return paddingTop;
        }

        public void setPaddingTop(int paddingTop) {
            this.paddingTop = paddingTop;
        }

        public int getPaddingEnd() {
            return paddingEnd;
        }

        public void setPaddingEnd(int paddingEnd) {
            this.paddingEnd = paddingEnd;
        }

        public int getPaddingBottom() {
            return paddingBottom;
        }

        public void setPaddingBottom(int paddingBottom) {
            this.paddingBottom = paddingBottom;
        }
    }
}
