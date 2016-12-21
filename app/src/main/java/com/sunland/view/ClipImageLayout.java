package com.sunland.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.bumptech.glide.Glide;
import com.sunland.signalinspect.ClipImageActivity;

import java.io.File;


public class ClipImageLayout extends RelativeLayout
{

	private static final String TAG = "wumin";
	private ClipImageView mZoomImageView;
	private ClipBorderView mClipImageView;

	private int mHorizontalPadding = 50;

	public ClipImageLayout(Context context, AttributeSet attrs)
	{
		super(context, attrs);

		mZoomImageView = new ClipImageView(context);
		mClipImageView = new ClipBorderView(context);

		android.view.ViewGroup.LayoutParams lp = new LayoutParams(
				ViewGroup.LayoutParams.MATCH_PARENT,
				ViewGroup.LayoutParams.MATCH_PARENT);

		Bitmap bmp = BitmapFactory.decodeFile(ClipImageActivity.photoFile);
		mZoomImageView.setImageBitmap(bmp);
		Glide.with(context).load(new File(ClipImageActivity.photoFile)).crossFade().into(mZoomImageView);
//		mZoomImageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
//		mZoomImageView.setImageDrawable(getResources().getDrawable(R.drawable.bg));
		
		this.addView(mZoomImageView, lp);
		this.addView(mClipImageView, lp);

		
		// 计算padding的px
		mHorizontalPadding = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, mHorizontalPadding, getResources()
						.getDisplayMetrics());
		mZoomImageView.setHorizontalPadding(mHorizontalPadding);
		mClipImageView.setHorizontalPadding(mHorizontalPadding);
	}

	public void setHorizontalPadding(int mHorizontalPadding)
	{
		this.mHorizontalPadding = mHorizontalPadding;
	}

	public Bitmap clip()
	{
		return mZoomImageView.clip();
	}

}
