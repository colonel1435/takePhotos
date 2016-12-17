package com.sunland.signalinspect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.sunland.view.ClipImageLayout;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClipImageActivity extends AppCompatActivity {

    private static final String TAG = "wumin";
    Button btSave = null;
    ClipImageLayout mClipLayout = null;
    private static final int CLIP_RECT_PADDING = 50;
    public static String ACTION_SAVE_IMG = "com.sunland.signalinspect.action.SAVA_IMG";
    public static String PHOTONAME_KEY = "PHOTO_NAME";
    public static String PHOTOFILE_KEY = "PHOTO_FILE";
    public static String PHOTODB_KEY = "PHOTO_DB";
    public static String DEPOT_KEY = "DEPOT";
    public static String imgDir = "";
    private static String photoName = "";
    private static String mDepot = "";
    public static String photoFile = "";
    private static String dbDir = "";
    private static final String DB_POSTFIX = ".db";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        photoFile = bundle.getString(PHOTOFILE_KEY);
        mDepot = bundle.getString(DEPOT_KEY);
        photoName = bundle.getString(PHOTONAME_KEY);
        setContentView(R.layout.activity_clip_image);

        mClipLayout = (ClipImageLayout) findViewById(R.id.layout_clip_image);
        mClipLayout.setHorizontalPadding(CLIP_RECT_PADDING);
        btSave = (Button) findViewById(R.id.bt_save_img);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = mClipLayout.clip();
                File file = new File(photoFile);
                if (file.exists())
                    file.delete();
//                byte[] bytes = compressImageByQuatity(bitmap);
                byte[] bytes = compressImageBySize(bitmap);
                try {
                    FileOutputStream fos = new FileOutputStream(file);
                    fos.write(bytes);
                    fos.flush();
                    fos.close();
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                } catch (IOException e) {
                    e.printStackTrace();
                }

//                Intent intent = new Intent(ClipImageActivity.this, showImageActivity.class);
//                startActivity(intent);
                dbDir = DepotActivity.imgDir + mDepot + DB_POSTFIX;

                Intent saveImg = new Intent();
                saveImg.setAction(ACTION_SAVE_IMG);
                saveImg.putExtra(PHOTONAME_KEY, photoName);
                saveImg.putExtra(PHOTOFILE_KEY, photoFile);
                saveImg.putExtra(PHOTODB_KEY, dbDir);
                Log.i(TAG, "Photo name -> " + photoName + " Photo file -> " + photoFile + " DB ->" + dbDir);
                startService(saveImg);
                ClipImageActivity.this.finish();
            }
        });
    }

    private byte[] compressImageByQuatity(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>100) {    //<=500k
            baos.reset();	//reset
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);

        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return baos.toByteArray();
    }

    private byte[] compressImageBySize(Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>500) {
            baos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        float hh = 800f;
        float ww = 480f;
        int be = 1;
        if (w > h && w > ww) {
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;
        newOpts.inPreferredConfig = Bitmap.Config.RGB_565;
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImageByQuatity(bitmap);
    }


}
