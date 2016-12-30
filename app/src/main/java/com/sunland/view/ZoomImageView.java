package com.sunland.view;

import android.animation.Animator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.FloatMath;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.ImageView;

/**
 * Created by Administrator on 2016/12/19.
 */

public class ZoomImageView extends ImageView {
    private static final String TAG = "wumin";

    Matrix matrix = null;
    Matrix savedMatrix = null;
    Bitmap bmp = null;
    /** Screen res */
    private DisplayMetrics dm;
    /** minimum scale ratio */
    static final float MIN_SCALE = 0.5f;
    /** maximum scale ratio */
    static final float MAX_SCALE = 15f;
    /** init status */
    static final int NONE = 0;
    /** drag */
    static final int DRAG = 1;
    /** scale */
    static final int ZOOM = 2;
    /** current mode */
    int mode = NONE;

    /** Save res-x & res-y */
    PointF prev = new PointF();
    PointF mid = new PointF();
    float dist = 1f;

    public ZoomImageView(Context context) {
        super(context);
        setupView();
    }

    public ZoomImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setupView();
    }

    public ZoomImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        setupView();
    }


    public void setupView(){

        matrix = new Matrix();
        savedMatrix = new Matrix();

        Context context = getContext();
        // Screen res
        dm = context.getResources().getDisplayMetrics();
        BitmapDrawable bd = (BitmapDrawable)this.getDrawable();
        if(bd != null){
            bmp = bd.getBitmap();
        }

        if(bmp != null) {
            center(true, true);
        }

        this.setImageMatrix(matrix);
        this.setOnTouchListener(new OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction() & MotionEvent.ACTION_MASK) {
                    // major
                    case MotionEvent.ACTION_DOWN:
                        savedMatrix.set(matrix);
                        prev.set(event.getX(), event.getY());
                        mode = DRAG;
                        break;
                    // senior
                    case MotionEvent.ACTION_POINTER_DOWN:
                        dist = spacing(event);
                        // 如果连续两点距离大于10，则判定为多点模式
                        if (spacing(event) > 10f) {
                            savedMatrix.set(matrix);
                            midPoint(mid, event);
                            mode = ZOOM;
                        }
                        break;
                    case MotionEvent.ACTION_UP:{
                        break;
                    }
                    case MotionEvent.ACTION_POINTER_UP:
                        mode = NONE;
                        //savedMatrix.set(matrix);
                        break;
                    case MotionEvent.ACTION_MOVE:
                        ZoomImageView.this.setScaleType(ScaleType.MATRIX);
                        if (mode == DRAG) {
                            matrix.set(savedMatrix);
                            matrix.postTranslate(event.getX() - prev.x, event.getY() - prev.y);
                        } else if (mode == ZOOM) {
                            float newDist = spacing(event);
                            if (newDist > 10f) {
                                matrix.set(savedMatrix);
                                float tScale = newDist / dist;
                                matrix.postScale(tScale, tScale, mid.x, mid.y);
                            }
                        }
                        break;
                }
                ZoomImageView.this.setImageMatrix(matrix);
                checkView(true, true);
                return true;
            }
        });
    }

    /**
     * horizontal & vertical center
     */
    protected void center(boolean horizontal, boolean vertical) {
        Matrix m = new Matrix();
        m.set(matrix);
        RectF rect = new RectF(0, 0, bmp.getWidth(), bmp.getHeight());
        m.mapRect(rect);

        float height = rect.height();
        float width = rect.width();

        float deltaX = 0, deltaY = 0;

        if (vertical) {
            // center
            int screenHeight = dm.heightPixels;
            if (height < screenHeight) {
                deltaY = (screenHeight - height) / 2 - rect.top;
            } else if (rect.top > 0) {
                deltaY = -rect.top;
            } else if (rect.bottom < screenHeight) {
                deltaY = this.getHeight() - rect.bottom;
            }
        }

        if (horizontal) {
            int screenWidth = dm.widthPixels;
            if (width < screenWidth) {
                deltaX = (screenWidth - width) / 2 - rect.left;
            } else if (rect.left > 0) {
                deltaX = -rect.left;
            } else if (rect.right < screenWidth) {
                deltaX = screenWidth - rect.right;
            }
        }
        matrix.postTranslate(deltaX, deltaY);
    }

    private float getDelX(float dx, float width, float[] values) {
        float deltaX = 0;
        if(width*values[Matrix.MSCALE_X] < width)
            return  width*values[Matrix.MSCALE_X];
        if(values[Matrix.MTRANS_X] + dx > 0)
            deltaX = -values[Matrix.MTRANS_X];
        else if(values[Matrix.MTRANS_X] + dx <-(width*values[Matrix.MSCALE_X] - width))
            deltaX = -(width*values[Matrix.MSCALE_X]-width)-values[Matrix.MTRANS_X];
        return deltaX;
    }

    private float getDelY(float dy, float height, float[] values) {
        float deltaY = 0;
        if(height * values[Matrix.MSCALE_Y] < height)
            return height * values[Matrix.MSCALE_Y];
        if(values[Matrix.MTRANS_Y] + dy > 0)
            deltaY = -values[Matrix.MTRANS_Y];
        else if(values[Matrix.MTRANS_Y] + dy < -(height*values[Matrix.MSCALE_Y]-height))
            deltaY = -(height*values[Matrix.MSCALE_Y]-height)-values[Matrix.MTRANS_Y];
        return deltaY;
    }
    private void checkBorder(boolean horizontal, boolean vertical)
    {

        RectF rect = getMatrixRectF();
        float deltaX = 0;
        float deltaY = 0;

        int width = getWidth();
        int height = getHeight();

//        Log.i(TAG, "WIDTH -> " + width + " HEIGHT -> " + height
//                + " + rect.width() =  " + rect.width() + " , rect.height  ="
//        + rect.height() + " top -> " + rect.top + ", Bottom -> " + rect.bottom
//        + ", Left -> " + rect.left + ", Right -> " + rect.right);
        if (horizontal) {
            if (rect.width() >= width)
            {
                if (rect.left > 0)
                {
                    deltaX = -rect.left;
                }
                if (rect.right < width)
                {
                    deltaX = width - rect.right;
                }
            } else {
                if (rect.left < 0) {
                    deltaX = -rect.left;
                }
                if (rect.right > width) {
                    deltaX = width - rect.right;
                }
            }
        }
        if (vertical) {
            if (rect.height()  >= height)
            {
                if (rect.top > 0)
                {
                    deltaY = -rect.top;
                }
                if (rect.bottom < height)
                {
                    deltaY = height - rect.bottom;
                }
            } else {
                if (rect.top <= 0) {
                    deltaY = -rect.top;
                }
                if (rect.bottom >= height) {
                    deltaY = height - rect.bottom;
                }
            }
        }
        matrix.postTranslate(deltaX, deltaY);

    }

    private void checkView(boolean horizontal, boolean vertical) {
        float p[] = new float[9];
        matrix.getValues(p);
        if (mode == ZOOM) {
            if (p[0] < MIN_SCALE) {
                matrix.setScale(MIN_SCALE, MIN_SCALE);
                matrix.set(savedMatrix);
                if (savedMatrix.isIdentity()) {
                    center(true, true);
                }
            }
            if (p[0] > MAX_SCALE) {
                matrix.set(savedMatrix);
            }
        }
        if (mode == DRAG) {
            checkBorder(true, true);
        }

    }

    private RectF getMatrixRectF()
    {
        Matrix rectMatrix = matrix;
        RectF rect = new RectF();
        Drawable d = getDrawable();
        if (null != d)
        {
            rect.set(0, 0, d.getIntrinsicWidth(), d.getIntrinsicHeight());
            rectMatrix.mapRect(rect);
        }
        return rect;
    }

    /**
     * Distance of two point
     */
    private float spacing(MotionEvent event) {
        float x = event.getX(0) - event.getX(1);
        float y = event.getY(0) - event.getY(1);
        return (float) Math.sqrt(x * x + y * y);
    }

    /**
     * Midpoint of two point
     */
    private void midPoint(PointF point, MotionEvent event) {
        float x = event.getX(0) + event.getX(1);
        float y = event.getY(0) + event.getY(1);
        point.set(x / 2, y / 2);
    }
}
