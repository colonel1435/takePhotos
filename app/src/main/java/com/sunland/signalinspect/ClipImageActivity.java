package com.sunland.signalinspect;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import com.sunland.view.ClipImageLayout;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class ClipImageActivity extends AppCompatActivity {

    private static final String TAG = "wumin";
    Button btSave = null;
    ClipImageLayout mClipLayout = null;
    public static String imgDir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = getIntent();
        Bundle bundle = intent.getExtras();
        imgDir = bundle.getString(DepotActivity.ZOOM_PHOTO_KEY);
        setContentView(R.layout.activity_clip_image);

        mClipLayout = (ClipImageLayout) findViewById(R.id.layout_clip_image);
        btSave = (Button) findViewById(R.id.bt_save_img);
        btSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bitmap bitmap = mClipLayout.clip();
                File file = new File(imgDir);
                if (file.exists())
                    file.delete();
                byte[] bytes = compressImageByQuatity(bitmap);
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

                Intent intent = new Intent(ClipImageActivity.this, showImageActivity.class);
                startActivity(intent);
            }
        });
    }

    private byte[] compressImageByQuatity(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>500) {    //<=500k
            baos.reset();	//reset
            options -= 10;
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);

        }
//        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
//        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return baos.toByteArray();
    }
}
