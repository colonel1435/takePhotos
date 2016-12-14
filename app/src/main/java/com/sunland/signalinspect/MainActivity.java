package com.sunland.signalinspect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import com.sunland.utils.CustomUtils;
import com.sunland.utils.MyDepotRecyclerAdapter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    public static final String DEPOT_LIST = "depot";
    public static final String DEPOT_KEY = "depot_name";
    public static final String DEPOT_TIME_KEY = "time";
    public static final String DC_NUM = "dc_num";
    public static final String DC_ITEM_MAX = "dc_item_max";
    private static final String formatter = "yyyy-MM-dd HH:mm";

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
        setSupportActionBar(toolbar);

        mContext = this;
        initView();
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
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
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
                dragFlags = 0;
                swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
            }
            return makeMovementFlags(dragFlags, swipeFlags);
        }

        @Override
        public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
//            int fromPosition = viewHolder.getAdapterPosition();
//            int toPosition = target.getAdapterPosition();
//            if (fromPosition < toPosition) {
//                for(int i = fromPosition; i < toPosition; i++) {
//                    Collections.swap(mData, i, i+1);
//                }
//            }else {
//                for(int i = fromPosition; i > toPosition; i--) {
//                    Collections.swap(mData, i, i -1);
//                }
//            }
//            myRecyclerAdapter.notifyItemMoved(fromPosition, toPosition);
            return false;
        }

        @Override
        public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
//            if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
//                viewHolder.itemView.setBackgroundColor(Color.LTGRAY);
//            }
            super.onSelectedChanged(viewHolder, actionState);
        }

        @Override
        public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
            super.clearView(recyclerView, viewHolder);
//            viewHolder.itemView.setBackgroundColor(0);
        }

        @Override
        public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
            int position = viewHolder.getAdapterPosition();
            myRecyclerAdapter.notifyItemRemoved(position);
            mData.remove(position);

//            LinearLayoutManager manager = new LinearLayoutManager(mContext);
//            manager.findViewByPosition(position);
//            DepotInfo item = manager.get
            DepotInfo item = mData.get(position);
            String depot = item.getDepot();
            SharedPreferences sp = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
            SharedPreferences.Editor editor =sp.edit();
            editor.remove(depot);
            editor.commit();

//            /** 删除SharedPreferences文件 **/
//            File file = new File(DATA_URL + getPackageName().toString()
//                    + "/shared_prefs", depot + ".xml");
//            if (file.exists()) {
//                file.delete();
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
        SharedPreferences sp = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
        Map<String, ?> cfgMap = sp.getAll();
        for (String key : cfgMap.keySet()) {
            SharedPreferences spDepot = getSharedPreferences(key, MODE_PRIVATE);
            String time = spDepot.getString(DEPOT_TIME_KEY, "");
            DepotInfo depot = new DepotInfo();
            depot.setImg(R.drawable.depot_icon);
            depot.setDepot(key);
            depot.setDcNum(getString(R.string.dc_num_title) + cfgMap.get(key).toString());
            depot.setDate(time);

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
                        SharedPreferences spList = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
                        SharedPreferences.Editor editorList = spList.edit();
                        editorList.putInt(depotName, Integer.parseInt(dcNum));
                        editorList.commit();

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
