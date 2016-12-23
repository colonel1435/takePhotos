package com.sunland.signalinspect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

import com.readystatesoftware.systembartint.SystemBarTintManager;
import com.sunland.utils.CustomUtils;
import com.sunland.utils.MyDepotRecyclerAdapter;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "wumin";
    public static final String DEPOT_LIST = "depot";
    public static final String DEPOT_KEY = "depot_name";
    public static final String DEPOT_TIME_KEY = "time";
    public static final String DC_NUM = "dc_num";
    public static final String DC_ITEM_MAX = "dc_item_max";
    public static final String formatter = "yyyy-MM-dd HH:mm";


    private RecyclerView mRecyclerView;
    private MyDepotRecyclerAdapter myRecyclerAdapter;
    private ItemTouchHelper itemTouchHelper;
    private List<DepotInfo> mData;
    private Context mContext;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
//        setSupportActionBar(toolbar);

        mContext = this;
        initView();
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.i(TAG, "onRestart");
        mData = getData();
        myRecyclerAdapter.notifyDataSetChanged();
    }


    void initView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_depot) ;
        mRecyclerView.setLayoutManager(new LinearLayoutManager(mContext));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);

        mData = getData();
        myRecyclerAdapter = new MyDepotRecyclerAdapter(mData);
        mRecyclerView.setAdapter(myRecyclerAdapter);
        myRecyclerAdapter.setOnItemClickListener(onItemClickListener);

        itemTouchHelper = new ItemTouchHelper(onItemTouchCallback);
        itemTouchHelper.attachToRecyclerView(mRecyclerView);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDepot(MainActivity.this);
            }
        });
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
                dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
                swipeFlags = 0;
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
            int old = viewHolder.getLayoutPosition();
            int newPos = target.getLayoutPosition();
            myRecyclerAdapter.notifyItemMoved(old, newPos);
            return true;
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            final RecyclerView.ViewHolder vHolder = viewHolder;
            AlertDialog.Builder builder = new AlertDialog.Builder(mContext);
            builder.setTitle(R.string.notification_msg_title)
                    .setMessage(getString(R.string.del_depot_msg))
                    .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            myRecyclerAdapter.notifyDataSetChanged();
                        }
                    })
                    .setPositiveButton(getString(R.string.msg_ok), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            int position = vHolder.getAdapterPosition();
                            myRecyclerAdapter.notifyItemRemoved(position);
                            DepotInfo item = mData.get(position);
                            mData.remove(position);

                            String depot = item.getDepot();
                            SharedPreferences sp = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
                            SharedPreferences.Editor editor =sp.edit();
                            editor.remove(depot).commit();

                            SharedPreferences spItem = getSharedPreferences(depot, MODE_PRIVATE);
                            SharedPreferences.Editor editorItem = spItem.edit();
                            editorItem.clear().commit();
                        }
                    })
                    .create().show();

//            File file = new File(Environment.getDataDirectory() + File.separator + getPackageName().toString()
//                    + "/shared_prefs", depot + ".xml");
//            if (file.exists()) {
//                file.delete();
//            }

//            File depotFile = new File(Environment.getDataDirectory() + getPackageName().toString()
//                    + "/shared_prefs", DepotActivity.DC_LIST + ".xml");
//            if (depotFile.exists()) {
//                depotFile.delete();
//            }

        }
    };
    private MyDepotRecyclerAdapter.OnItemClickListener onItemClickListener = new MyDepotRecyclerAdapter.OnItemClickListener() {
        @Override
        public void OnItemClick(View view, int position) {
            DepotInfo item = mData.get(position);
            String depot = item.getDepot();
            String dc = item.getDcNum();
            SharedPreferences sp = getSharedPreferences(depot, MODE_PRIVATE);
            int dc_item_max = sp.getInt(DC_ITEM_MAX, 0);
            Intent startDepot = new Intent(MainActivity.this, DepotActivity.class);
            startDepot.putExtra(DepotActivity.DEPOT_KEY, depot);
            startDepot.putExtra(DepotActivity.DC_KEY, dc);
            startDepot.putExtra(DepotActivity.DC_ITEM_MAX_KEY, dc_item_max);
            startActivity(startDepot);
        }
    };

    private List<DepotInfo> getData() {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.clear();
        String time;
        SharedPreferences sp = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
        Map<String, ?> cfgMap = sp.getAll();
        for (String key : cfgMap.keySet()) {
            SharedPreferences spDepot = getSharedPreferences(key, MODE_PRIVATE);
            time = spDepot.getString(DEPOT_TIME_KEY, "");
            DepotInfo depot = new DepotInfo();
            depot.setImg(R.drawable.depot_icon);
            depot.setDepot(key);
            depot.setDcNum(getString(R.string.dc_num_title) + cfgMap.get(key).toString());
            depot.setDate(time);

            Log.i(TAG, "Depot -> " + key + " Num -> " + cfgMap.get(key) + " Time -> " + time);
            mData.add(depot);
        }
        return mData;
    }
    public void addDepot(Context context) {
        final View popupView =  LayoutInflater.from(context).inflate(R.layout.depot_main, null);
        final EditText etDepot = (EditText)popupView.findViewById(R.id.et_depot_name);
        final EditText etDcNum = (EditText)popupView.findViewById(R.id.et_dc_num);
        final EditText etDcItemMax = (EditText)popupView.findViewById(R.id.et_dc_item_max);
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder .setCancelable(false)
                .setView(popupView)
                .setNegativeButton(getString(R.string.depot_cancle), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                    }
                })
                .setPositiveButton(getString(R.string.depot_add), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String depotName = etDepot.getText().toString();
                        String dcNum = etDcNum.getText().toString();
                        String dcItemMax = etDcItemMax.getText().toString();

                        if (depotName.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.msg_depot_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (dcNum.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.msg_dc_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        if (dcItemMax.length() == 0) {
                            Toast.makeText(mContext, getString(R.string.msg_item_null), Toast.LENGTH_LONG).show();
                            return;
                        }
                        SharedPreferences spList = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
                        int tmp = spList.getInt(depotName, -1);
                        if(tmp != -1) {
                            Toast.makeText(mContext, getString(R.string.msg_depot_again), Toast.LENGTH_LONG).show();
                            return;
                        }
                        SharedPreferences.Editor editorList = spList.edit();
                        editorList.putInt(depotName, Integer.parseInt(dcNum)).commit();

                        SharedPreferences spDepot = getSharedPreferences(depotName, MODE_PRIVATE);
                        SharedPreferences.Editor editor = spDepot.edit();
                        String now = CustomUtils.getCurTime(formatter);
                        editor.putString(DEPOT_KEY, depotName);
                        editor.putInt(DC_NUM, Integer.parseInt(dcNum));
                        editor.putInt(DC_ITEM_MAX, Integer.parseInt(dcItemMax));
                        editor.putString(DEPOT_TIME_KEY, now);

                        DepotInfo depot = new DepotInfo(R.drawable.depot_icon, depotName, getString(R.string.dc_num_title) + dcNum, now);
                        editor.commit();

                        myRecyclerAdapter.addData(0, depot);
                    }
                })
                .create().show();

    }
}
