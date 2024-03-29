package com.example.bexttool;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class BarView extends View {

    private Paint mPaint;
    private double mValue;                 // Current value
    private double mMax;                   // Maximum +/- value allowed
    private int mColor = Color.GREEN;      // Color to render with
    private final static int mHeight = 20; // Height of the view in pixels

    public BarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mPaint = new Paint();
        mPaint.setColor(mColor);
        reset();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec){
        int parentWidth = MeasureSpec.getSize(widthMeasureSpec);
        this.setMeasuredDimension(parentWidth, mHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // Draw a bar from the center out to the left or right depending on the mValue
        if (mValue >= 0)
            canvas.drawRect(canvas.getWidth()/2, 0, (int)(canvas.getWidth()/2.0 + mValue/mMax*canvas.getWidth()/2.0), canvas.getHeight()-1, mPaint);
        else // Cannot render right to left so need to flip around X arguments
            canvas.drawRect((int)(canvas.getWidth()/2.0 + mValue/mMax*canvas.getWidth()/2.0), 0, canvas.getWidth()/2, canvas.getHeight()-1, mPaint);
    }

    public void setValue(double in) {
        if (mValue != in) {
            mValue = in;
            invalidate();
        }
    }

    public void setMaximum(double in) {
        if (mMax != in) {
            mMax = in;
            mValue = 0.0;
            invalidate();
        }
    }

    public void reset() {
        mMax = 1.0;
        mValue = 0.0;
        invalidate();
    }
}
