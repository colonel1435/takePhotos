package com.sunland.view;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.RelativeLayout;

/**
 * Created by Administrator on 2016/12/29.
 */

public class MySquareRelativeLayout extends RelativeLayout {

        public MySquareRelativeLayout(Context context, AttributeSet attrs,
                                      int defStyle) {
            super(context, attrs, defStyle);
        }

        public MySquareRelativeLayout(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        public MySquareRelativeLayout(Context context) {
            super(context);
        }

        @Override
        protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
            setMeasuredDimension(getDefaultSize(0, widthMeasureSpec),
                    getDefaultSize(0, heightMeasureSpec));

            int childWidthSize = getMeasuredWidth();
            // 高度和宽度一样
            heightMeasureSpec = widthMeasureSpec = MeasureSpec.makeMeasureSpec(
                    childWidthSize, MeasureSpec.EXACTLY);
            super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        }
}
