package com.sunland.view;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.ProviderInfo;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BlurMaskFilter;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.widget.ImageView;

import com.sunland.signalinspect.R;

import java.lang.ref.WeakReference;

/**
 * Created by Administrator on 2016/12/29.
 */

public class RoundImageView extends ImageView {

    private Paint mPaint;
    private Bitmap mMaskBmp;
    private Xfermode mXfermode;
    private WeakReference<Bitmap> mWeakBitmap;

    private int type;
    public static final int TYPE_CIRCLE = 0;
    public static final int TYPE_ROUND = 1;

    private int mBorderRadius;
    private static final int DEFAULT_BORDER_RADIUS = 50;


    public RoundImageView(Context context) {
        super(context);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);
    }

    public RoundImageView(Context context, AttributeSet attrs) {
        super(context, attrs);

        mXfermode = new PorterDuffXfermode(PorterDuff.Mode.SRC_IN);
        mPaint = new Paint();
        mPaint.setAntiAlias(true);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RoundImageViewByXfermode);
        mBorderRadius = a.getDimensionPixelSize(R.styleable.RoundImageViewByXfermode_borderRadius, (int) TypedValue
                        .applyDimension(TypedValue.COMPLEX_UNIT_DIP, DEFAULT_BORDER_RADIUS ,
                                getResources().getDisplayMetrics()));
        type = a.getInt(R.styleable.RoundImageViewByXfermode_type, TYPE_CIRCLE);
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        if (type == TYPE_CIRCLE) {
            int width = Math.min(getMeasuredWidth(), getMeasuredHeight());
            setMeasuredDimension(width, width);
        }
    }

    @Override
    public void invalidate() {
        mWeakBitmap = null;
        if (mMaskBmp != null) {
            mMaskBmp.recycle();
            mMaskBmp = null;
        }
        super.invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        Bitmap bmp = mWeakBitmap == null ? null : mWeakBitmap.get();
        if (null == bmp || bmp.isRecycled()) {
            Drawable drawable = getDrawable();
            int dWidth = drawable.getIntrinsicWidth();
            int dHeight = drawable.getIntrinsicHeight();
            if (drawable != null) {
                bmp = Bitmap.createBitmap(getWidth(), getHeight(), Bitmap.Config.ARGB_8888);
                float scale = 1.0f;
                Canvas dCanvas = new Canvas(bmp);
                if (TYPE_ROUND == type) {
                    scale = Math.max(getWidth()/dWidth, getHeight()/dHeight);
                } else  {
                    scale = getWidth()/Math.min(dWidth, dHeight);
                }
                drawable.setBounds(0, 0, (int)scale * dWidth, (int)scale * dHeight);
                drawable.draw(dCanvas);
                if (mMaskBmp == null || mMaskBmp.isRecycled()) {
                    mMaskBmp = getBitmap();
                }
                mPaint.reset();
                mPaint.setFilterBitmap(false);
                mPaint.setXfermode(mXfermode);
                dCanvas.drawBitmap(mMaskBmp, 0, 0, mPaint);
                mPaint.setXfermode(null);
                canvas.drawBitmap(bmp, 0.0f, 0.0f, null);
                mWeakBitmap = new WeakReference<Bitmap>(bmp);
            }
        }
        if (bmp != null) {
            mPaint.setXfermode(mXfermode);
            canvas.drawBitmap(bmp, 0.0f, 0.0f, mPaint);
            return;
        }
    }

    public Bitmap getBitmap()
    {
        Bitmap bitmap = Bitmap.createBitmap(getWidth(), getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setColor(Color.BLACK);

        if (type == TYPE_ROUND)
        {
            canvas.drawRoundRect(new RectF(0, 0, getWidth(), getHeight()),
                    mBorderRadius, mBorderRadius, paint);
        } else
        {
            canvas.drawCircle(getWidth() / 2, getWidth() / 2, getWidth() / 2,
                    paint);
        }

        return bitmap;
    }
}
