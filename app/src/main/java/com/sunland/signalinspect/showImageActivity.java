package com.sunland.signalinspect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.sunland.view.SmoothImageView;

import java.util.ArrayList;
import java.util.List;

public class showImageActivity extends Activity {

    private List<String> mDatas;
    private int mPosition;
    private int mLocationX;
    private int mLocationY;
    private int mWidth;
    private int mHeight;
    SmoothImageView imageView = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mDatas = (ArrayList<String>) getIntent().getSerializableExtra("images");
        mPosition = getIntent().getIntExtra("position", 0);
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
        imageView.setImageBitmap(BitmapFactory.decodeFile(mDatas.get(mPosition)));
//        setContentView(R.layout.activity_show_image);
//
//        imgView = (ImageView) findViewById(R.id.img_show);
//        Bitmap bitmap = BitmapFactory.decodeFile(ClipImageActivity.imgDir);
//        if (bitmap != null)
//        {
//            imgView.setImageBitmap(bitmap);
//        }
    }
}
