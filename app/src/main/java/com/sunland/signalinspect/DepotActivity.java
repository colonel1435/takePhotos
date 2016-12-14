package com.sunland.signalinspect;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.utils.MyDCRecyclerAdapter;
import com.sunland.utils.MyDividerItemDecoration;
import com.sunland.utils.MyGridDividerItemDecoration;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class DepotActivity extends AppCompatActivity {

    public static final String DEPOT_KEY = "depot";
    public static final String DC_KEY   = "dc";
    public static final String DC_ITEM_MAX_KEY = "max";
    private static final int TAKE_PHOTOS = 0;
    private static final String TAG = "wumin";
    public static final String ZOOM_PHOTO_KEY = "ZOOM";
    public static String imgName = null;
    private Context mContext = null;
    private String depot = null;
    private int dc_num;
    private int dc_item_max;
    private TextView mTitle;
    private LinearLayout mLayout;
    private RecyclerView mRecyclerView;
    private MyDCRecyclerAdapter myDCRecyclerAdapter;
    List<String> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_depot);

        mContext = this;
        Bundle bundle = getIntent().getExtras();
        depot = bundle.getString(DEPOT_KEY);
        dc_num = Integer.parseInt(bundle.getString(DC_KEY).split(":")[1]);
        dc_item_max = bundle.getInt(DC_ITEM_MAX_KEY);

        initView();
    }

    private void initView() {
//        String msg = depot + " -> " + dc_num;
//        Toast.makeText(mContext, msg, Toast.LENGTH_LONG).show();

        imgName = Environment.getExternalStorageDirectory().toString() + "/test" + ".jpg";
        mTitle = (TextView)findViewById(R.id.tv_dc_title);
        mTitle.setText(depot);
        mLayout = (LinearLayout) findViewById(R.id.listview_dc_item);
        mRecyclerView = (RecyclerView)this.findViewById(R.id.recyclerview_dc_item);
        mRecyclerView.setLayoutManager(new GridLayoutManager(mContext, 1));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
//        mRecyclerView.addItemDecoration(new MyDividerItemDecoration(mContext, MyDividerItemDecoration.VERTICAL_LIST));
        mRecyclerView.addItemDecoration(new MyGridDividerItemDecoration(mContext));
        mRecyclerView.setHasFixedSize(true);
        mData = getData();
        myDCRecyclerAdapter = new MyDCRecyclerAdapter(mData);
        mRecyclerView.setAdapter(myDCRecyclerAdapter);

    }

    public List<String> getData() {
        String dc_name;
        List<String> list = new ArrayList<>();
        for(int i = 1; i < dc_num+1; i++ ) {
            for(int j = 1; j < dc_item_max+1; j++) {
                dc_name = i + "#";
                dc_name += j + getString(R.string.dc_name_postfix);
                list.add(dc_name);
            }
        }
        return list;
    }

    public void onBtClick(View v) {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
        startActivityForResult(intent, TAKE_PHOTOS);
    }

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
