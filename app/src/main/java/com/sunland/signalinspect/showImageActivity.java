package com.sunland.signalinspect;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ImageView;

public class showImageActivity extends AppCompatActivity {

    ImageView imgView = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_image);

        imgView = (ImageView) findViewById(R.id.img_show);
        Bitmap bitmap = BitmapFactory.decodeFile(ClipImageActivity.imgDir);
        if (bitmap != null)
        {
            imgView.setImageBitmap(bitmap);
        }
    }
}
