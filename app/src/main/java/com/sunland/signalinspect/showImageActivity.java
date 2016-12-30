package com.sunland.signalinspect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPoolAdapter;
import com.sunland.utils.CustomUtils;
import com.sunland.view.SmoothImageView;

import java.util.ArrayList;
import java.util.List;

import static com.sunland.signalinspect.ActionSearchActivity.THUMBNAIL_LABEL;

public class showImageActivity extends Activity {

    public static String TAG = "wumin";
    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    private String rawPhoto = "";
    private Bitmap bitmap = null;
    SmoothImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);

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
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inSampleSize=2; //图片高宽度都为原来的二分之一，即图片大小为原来的大小的四分之一
//        options.inTempStorage = new byte[5*1024];
        bitmap = BitmapFactory.decodeFile(rawPhoto);
        imageView.setImageBitmap(bitmap);
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
        Log.i(TAG, "onDestroy");
        super.onPause();
        if (isFinishing()) {
            overridePendingTransition(0, 0);
        }
    }

    @Override
    protected void onDestroy() {
        Log.i(TAG, "onDestroy");
        super.onDestroy();
        if (!bitmap.isRecycled()) {
            Log.i(TAG, "onDestroy isRecycled");
            bitmap.recycle();
        }
    }
}
