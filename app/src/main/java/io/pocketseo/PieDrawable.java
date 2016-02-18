/*
 * Copyright (c) The Distance Agency Ltd 2016.
 */

package io.pocketseo;

import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Paint;
import android.graphics.PixelFormat;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;

/**
 * Created by pharris on 18/02/16.
 */
public class PieDrawable extends Drawable {

    private final float mPadding;
    private final int mColor2;
    private final int mColor1;
    private float mValue;
    private Paint mLinePaint;
    private RectF rectF;
    private float widthPadding;
    private float heightPadding;
    private float height;
    private float width;

    public PieDrawable(int color, int color2, float padding, float strokeWidth) {
        mLinePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mLinePaint.setColor(color);
        mLinePaint.setStyle(Paint.Style.STROKE);
        mLinePaint.setStrokeWidth(strokeWidth);

        mColor1 = color;
        mColor2 = color2;

        this.mPadding = padding + .5f * strokeWidth;
    }

    @Override
    public void draw(Canvas canvas) {
        mLinePaint.setColor(mColor1);
        canvas.drawArc(rectF, -90, 360f * getLevel() / 1000, false, mLinePaint);
        mLinePaint.setColor(mColor2);
        canvas.drawArc(rectF, 360f * getLevel() / 1000f - 90f , 360f * (1000f - getLevel()) / 1000f, false, mLinePaint);
    }

    @Override
    public void setAlpha(int alpha) {
        mLinePaint.setAlpha(alpha);
    }

    @Override
    public void setColorFilter(ColorFilter colorFilter) {
        mLinePaint.setColorFilter(colorFilter);
    }

    @Override
    public int getOpacity() {
        return PixelFormat.TRANSLUCENT;
    }

    @Override
    protected void onBoundsChange(Rect bounds) {
        super.onBoundsChange(bounds);

        width = bounds.width();
        height = bounds.height();
        float min = Math.min(width, height);
        widthPadding = mPadding + (width - min) / 2;
        heightPadding = mPadding + (height - min) / 2;
        rectF = new RectF(
                widthPadding,
                heightPadding,
                width - widthPadding,
                height - heightPadding);
    }

    @Override
    public boolean getPadding(Rect padding) {
        padding.set(48,48,48,48);
        return true;
    }

    /*

    public PieBorder(Context context, AttributeSet attrs, int defStyleAttr) {
    }


    @Override
    protected void onDraw(Canvas canvas) {
    } */
}
