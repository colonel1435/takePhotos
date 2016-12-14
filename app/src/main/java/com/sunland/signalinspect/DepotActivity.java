package com.sunland.signalinspect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.utils.CustomUtils;
import com.sunland.utils.MyDCRecyclerAdapter;
import com.sunland.utils.MyDividerItemDecoration;
import com.sunland.utils.MyGridDividerItemDecoration;
import com.sunland.view.MyGridLayoutManager;
import com.sunland.view.MyLinearLayoutManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class DepotActivity extends AppCompatActivity {

    public static final String WORK_DIR = "/signalInspect/";
    private static final String DATE_FORMAT_STR = "yyMMddHHmm";
    public static final String DEPOT_KEY = "depot";
    public static final String DC_KEY   = "DC";
    public static final String DC_ITEM_KEY   = "DC_ITEM";
    public static final String DC_ITEM_SEP  = ":";
    public static final String DC_ITEM_MAX_KEY = "max";
    private static final int TAKE_PHOTOS = 0;
    private static final String TAG = "wumin";
    public static final String ZOOM_PHOTO_KEY = "ZOOM";
    public static final String DC_LIST = "dc_item";
    private static final int DC_PHOTO_TYPE = 0;
    private static final int OTHER_PHOTO_TYPE = 1;

    public static String imgName = "";
    public static String imgDir = "";
    private Context mContext = null;
    private String depot = null;
    private int dc_num;
    private int dc_item_max;
    private TextView mTitle;
    private Button btSetPosition;
    private Button btBackPosition;
    private ScrollView mScrollView;
    private RecyclerView mRecyclerView;
    private MyDCRecyclerAdapter myDCRecyclerAdapter;
    private ItemTouchHelper mItemTouchHelper;
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
        imgDir = Environment.getExternalStorageDirectory().toString() + WORK_DIR;
        File dir = new File(imgDir);
        if (!dir.exists()) {
            dir.mkdir();
        }

        mTitle = (TextView)findViewById(R.id.tv_dc_title);
        mTitle.setText(depot);

        mScrollView = (ScrollView)findViewById(R.id.scrollView_dc);
        mScrollView.setSmoothScrollingEnabled(true);
        mScrollView.smoothScrollTo(0,0);

        mRecyclerView = (RecyclerView)this.findViewById(R.id.recyclerview_dc_item);
        mRecyclerView.setLayoutManager(new MyLinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mData = getData();
        myDCRecyclerAdapter = new MyDCRecyclerAdapter(mData);
        mRecyclerView.setAdapter(myDCRecyclerAdapter);

        mItemTouchHelper = new ItemTouchHelper(onItemTouchCallback);
        mItemTouchHelper.attachToRecyclerView(mRecyclerView);

        View itemView = LayoutInflater.from(mContext).inflate(R.layout.dc_item, null);
        btSetPosition = (Button) itemView.findViewById(R.id.dc_set_position);
        btBackPosition = (Button) itemView.findViewById(R.id.dc_back_position);

    }

    private ItemTouchHelper.Callback onItemTouchCallback = new ItemTouchHelper.Callback() {
        @Override
        public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            int dragFlags;
            int swipeFlags;
            if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN
                        | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
                swipeFlags = 0;
            } else {
                dragFlags = 0;
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            return false;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            myDCRecyclerAdapter.notifyItemRemoved(position);
            String delStr = mData.get(position) + DC_ITEM_SEP;
//            Log.i(TAG, "Remove ITEM -> " + position + " " + delStr);
            mData.remove(position);

            SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            String item_name = sp.getString(DC_ITEM_KEY, "");
            item_name = CustomUtils.delStr(item_name, delStr);
//            Log.i(TAG, "Rest -> " + item_name);
            editor.putString(DC_ITEM_KEY, item_name).commit();

        }
    };

    public List<String> getData() {
        File file = new File(Environment.getDataDirectory() + getPackageName().toString()
                + "/shared_prefs", depot + "_" + DC_LIST + ".xml");
        if (file.exists()) {
            file.delete();
        }
        String dc_name;
        String item_name = "";
        List<String> list = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
        item_name = sp.getString(DC_ITEM_KEY, "");
        if (item_name != "") {
            String[] items = item_name.split(DC_ITEM_SEP);
            list = new ArrayList<String>(Arrays.asList(items));
            Log.i(TAG, "DC ITEM -> " + list.toString());
        } else {
            SharedPreferences.Editor editor = sp.edit();
            for (int i = 1; i < dc_num + 1; i++) {
                for (int j = 1; j < dc_item_max + 1; j++) {
                    dc_name = i + "#";
                    dc_name += j + getString(R.string.dc_name_postfix);
                    list.add(dc_name);
                    item_name += dc_name;
                    item_name += DC_ITEM_SEP;
                }
            }
            editor.putString(DC_ITEM_KEY, item_name).commit();
        }

        return list;
    }

    public String createPhotoName(int type, String val, String status) {
        String now = CustomUtils.getCurTime(DATE_FORMAT_STR);
        String jpgName = "";
        switch (type) {
            case DC_PHOTO_TYPE:
                jpgName = imgDir + depot + "_" + DC_KEY + "_" + val + "_"  + status + "_" + now + ".jpg";
                break;
            case OTHER_PHOTO_TYPE:
                jpgName = imgDir + depot + "_"  + val + "_"  + status + "_" + now + ".jpg";
                break;
            default:
                break;
        }

        return jpgName;
    }
    public void onBtSetPositionClick(View view) {
        String dc = view.getTag().toString();
        String fileName = createPhotoName(DC_PHOTO_TYPE, dc, getString(R.string.dc_set_position));
        imgName = fileName;
        Log.i(TAG, "File -> " + imgName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
        startActivityForResult(intent, TAKE_PHOTOS);
    }

    public void onBtBackPositionClick(View view) {
        String dc = (String)view.getTag().toString();
        String fileName = createPhotoName(DC_PHOTO_TYPE, dc, getString(R.string.dc_back_position));
        imgName = fileName;
        Log.i(TAG, "File -> " + imgName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
        startActivityForResult(intent, TAKE_PHOTOS);
    }

    public void onBtDefaultClick(View view) {
        final View popupView =  LayoutInflater.from(mContext).inflate(R.layout.dialog_other_photo, null);
        final EditText etPhoto = (EditText)popupView.findViewById(R.id.et_other_photo);
        AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
        builder.setTitle(R.string.notification_msg_title)
                .setView(popupView)
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String val = etPhoto.getText().toString();
                        if (val.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.msg_photo_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        String fileName = createPhotoName(OTHER_PHOTO_TYPE, val, getString(R.string.dc_misc));
                        imgName = fileName;
                        Log.i(TAG, "Val -> " + val + "File -> " + imgName);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
                        startActivityForResult(intent, TAKE_PHOTOS);
                    }
                })
                .create().show();
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
        imgName = "";
        super.onActivityResult(requestCode, resultCode, data);
    }
}
