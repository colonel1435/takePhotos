package com.sunland.signalinspect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.media.session.PlaybackStateCompat;
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
import android.view.Menu;
import android.view.MenuItem;
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

public class DepotActivity extends AppCompatActivity implements MediaScannerConnection.MediaScannerConnectionClient{

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
    private static final String FILE_TYPE = "image/*";
    private static String SCAN_PATH = "";

    public static String imgName = "";
    public static String imgDir = "";
    private static String fileName = "";
    private static Context mContext = null;
    private static String depot = null;
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

    private MediaScannerConnection conn;
    public String[] allFiles;
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
                jpgName = depot + "_" + DC_KEY + "_" + val + "_"  + status + "_" + now + ".jpg";
                break;
            case OTHER_PHOTO_TYPE:
                jpgName = depot + "_"  + val + "_"  + status + "_" + now + ".jpg";
                break;
            default:
                break;
        }

        return jpgName;
    }
    public void onBtSetPositionClick(View view) {
        String dc = view.getTag().toString();
        fileName = createPhotoName(DC_PHOTO_TYPE, dc, getString(R.string.dc_set_position));
        imgName = imgDir + fileName;
        Log.i(TAG, "File -> " + fileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
        startActivityForResult(intent, TAKE_PHOTOS);
    }

    public void onBtBackPositionClick(View view) {
        String dc = (String)view.getTag().toString();
        fileName = createPhotoName(DC_PHOTO_TYPE, dc, getString(R.string.dc_back_position));
        imgName = imgDir + fileName;
        Log.i(TAG, "File -> " + fileName);
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(imgName)));
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
                        imgName = imgDir + fileName;
                        Log.i(TAG, "Val -> " + val + "File -> " + imgName);
                        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(fileName)));
                        startActivityForResult(intent, TAKE_PHOTOS);
                    }
                })
                .create().show();
    }

    private void showFileBrowser() {
//        Toast.makeText(mContext, "浏览文件", Toast.LENGTH_LONG).show();
        Uri uri = Uri.fromFile(new File(imgDir));
//        Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
//        scanIntent.setData(uri);
//        mContext.sendBroadcast(scanIntent);

        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setAction(Intent.ACTION_GET_CONTENT);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
/*
        File folder = new File(imgDir);
        allFiles = folder.list();
        for(int i=0;i<allFiles.length;i++)
        {
            Log.d(TAG, allFiles[i]+allFiles.length);
        }

        SCAN_PATH = imgDir + allFiles[0];
        if(conn!=null)
        {
            conn.disconnect();
        }
        conn = new MediaScannerConnection(this, this);
        conn.connect();
        */
    }

    private void showFileSearch() {
//        Toast.makeText(mContext, "搜索", Toast.LENGTH_LONG).show();
        Intent intent = new Intent(DepotActivity.this, ActionSearchActivity.class);
        intent.putExtra(ActionSearchActivity.WORK_DIR_KEY, imgDir);
        startActivity(intent);
    }

    public static void onSaveFinished() {
        Toast.makeText(mContext, mContext.getString(R.string.save_finished), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onMediaScannerConnected() {
        Log.d(TAG,"success "+conn);
        Log.i(TAG, "path -> " + SCAN_PATH);
        conn.scanFile(SCAN_PATH, FILE_TYPE);
    }
    @Override
    public void onScanCompleted(String path, Uri uri) {
        try {
            Log.d(TAG,uri + "success"+conn);
            if (uri != null)
            {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(uri);
                startActivity(intent);
            }
        } finally
        {
            conn.disconnect();
            conn = null;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_search:
                showFileSearch();
                return true;
            case R.id.action_browse:
                showFileBrowser();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == TAKE_PHOTOS) {
                Log.i(TAG, "Take photo finished...");
                Intent intent = new Intent(DepotActivity.this, ClipImageActivity.class);
                intent.putExtra(ClipImageActivity.PHOTONAME_KEY, fileName);
                intent.putExtra(ClipImageActivity.PHOTOFILE_KEY, imgName);
                intent.putExtra(ClipImageActivity.DEPOT_KEY, depot);
                startActivity(intent);
            }
        }
        imgName = "";
        super.onActivityResult(requestCode, resultCode, data);
    }
}
