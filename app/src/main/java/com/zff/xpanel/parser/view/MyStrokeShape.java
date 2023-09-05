package com.zff.xpanel.parser.view;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.shapes.RectShape;

public class MyStrokeShape extends RectShape {

    private RectF mRect = new RectF();
    int strokeWidth;
    Paint strokePaint=new Paint();
    public MyStrokeShape(int strokeWidth,int strokeColor){
        this.strokeWidth=strokeWidth;
        strokePaint.setStrokeWidth(strokeWidth);
        strokePaint.setColor(strokeColor);
        strokePaint.setStyle(Paint.Style.STROKE);
    }

    @Override
    public void draw(Canvas canvas, Paint paint) {
        super.draw(canvas, paint);
        canvas.drawRect(mRect,strokePaint);
    }

    @Override
    protected void onResize(float width, float height) {
        super.onResize(width, height);
        mRect.set(strokeWidth/2f,strokeWidth/2f,width-strokeWidth/2f,height-strokeWidth/2f);
    }
}
