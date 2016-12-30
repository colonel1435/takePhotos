package com.sunland.signalinspect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;

import com.sunland.view.SmoothImageView;
import com.sunland.view.ZoomImageView;

public class DispalyImageActivity extends AppCompatActivity {

    private String mPath;
    private ZoomImageView mZoomImageView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        mPath = getIntent().getStringExtra("path");
        setContentView(R.layout.activity_dispaly_image);

        mZoomImageView = (ZoomImageView) findViewById(R.id.iv_display);
        mZoomImageView.setImageBitmap(BitmapFactory.decodeFile(mPath));
    }

    @Override
    public void onBackPressed() {
        finish();
        overridePendingTransition(0,0);

    }
}
