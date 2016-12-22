package com.sunland.signalinspect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.sunland.utils.CustomUtils;
import com.sunland.view.SmoothImageView;

import java.util.ArrayList;
import java.util.List;

import static com.sunland.signalinspect.ActionSearchActivity.THUMBNAIL_LABEL;

public class showImageActivity extends Activity {

    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    private String rawPhoto = "";
    SmoothImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        rawPhoto = getIntent().getStringExtra("path");
        mLocationX = getIntent().getIntExtra("locationX", 0);
        mLocationY = getIntent().getIntExtra("locationY", 0);
        mWidth = getIntent().getIntExtra("width", 0);
        mHeight = getIntent().getIntExtra("height", 0);

        imageView = new SmoothImageView(this);
        imageView.setOriginalInfo(mWidth, mHeight, mLocationX, mLocationY);
        imageView.transformIn();
        imageView.setLayoutParams(new ViewGroup.LayoutParams(-1, -1));
        imageView.setScaleType(ImageView.ScaleType.FIT_CENTER);
        setContentView(imageView);
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize=2; //图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
        options.inTempStorage = new byte[5*1024];
        imageView.setImageBitmap(BitmapFactory.decodeFile(rawPhoto, options));
        imageView.setupView();
    }
	
	 @Override
    public void onBackPressed() {
        imageView.setOnTransformListener(new SmoothImageView.TransformListener() {
            @Override
            public void onTransformComplete(int mode) {
                if (mode == 2) {
                    finish();
                }
            }
        });
        imageView.transformOut();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }
}
