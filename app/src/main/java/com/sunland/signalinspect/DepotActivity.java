package com.sunland.signalinspect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DepotActivity extends AppCompatActivity {

    public static final String DEPOT_KEY = "depot";
    public static final String DC_KEY   = "dc";
    private static final int TAKE_PHOTOS = 0;
    private static final String TAG = "wumin";
    public static final String ZOOM_PHOTO_KEY = "ZOOM";
    public static String imgName = null;
    private Context mContext = null;
    private String depot = null;
    private String dc_num = null;
    private TextView mMsg;
    private Button mSetPosition;
    private Button mBackPosition;
    private LinearLayout mLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot);

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        depot = bundle.getString(DEPOT_KEY);
        dc_num = bundle.getString(DC_KEY);
//        dc_num = Integer.parseInt(bundle.getString(DC_KEY));

        initView();
    }

    private void initView() {
        String msg = depot + " -> " + dc_num;
        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

        imgName = Environment.getExternalStorageDirectory().toString() + "/test" + ".jpg";
        Log.i(TAG, "IMAGE " + imgName);

        mLayout = (LinearLayout)findViewById(R.id.ll_dc_item);
        List<String> list = new ArrayList<>();
        list.add("道岔1");
        list.add("道岔2");
        list.add("道岔3");
        list.add("道岔4");

        createContentView(mContext, mLayout, list);
    }


    public void  createContentView(Context context, LinearLayout contentLayout, List<String> dcItem) {

        View childView;
        TextView mDc;
        LayoutInflater layoutInflater = LayoutInflater.from (context);

        for  ( int  i = 0; i < dcItem.size(); i++) {
            childView  = layoutInflater.inflate(R.layout.dc_item, null);
            mDc = (TextView)childView.findViewById(R.id.tv_dc_name);
            mDc.setText(dcItem.get(i));
            mSetPosition = (Button)childView.findViewById(R.id.dc_set_position);
            mBackPosition = (Button)childView.findViewById(R.id.dc_back_position);
            mSetPosition.setOnClickListener(mClickListener);
            mBackPosition.setOnClickListener(mClickListener);
            contentLayout.addView(childView);
        }
    }

    private View.OnClickListener mClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
            startActivityForResult(intent, TAKE_PHOTOS);
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTOS) {
                Log.i(TAG, "Take photo finished...");

                Intent intent = new Intent(DepotActivity.this, ClipImageActivity.class);
                intent.putExtra(ZOOM_PHOTO_KEY, imgName);
                startActivity(intent);
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
