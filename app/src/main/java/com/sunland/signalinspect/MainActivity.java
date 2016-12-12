package com.sunland.signalinspect;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Layout;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.Inflater;

public class MainActivity extends AppCompatActivity {

    public static final String DEPOT_LIST = "depot";
    public static final String DEPOT_KEY = "depot_name";
    public static final String DC_NUM = "dc_num";
    public static final String DC_ITEM_MAX = "dc_item_max";

    private ListView lvDepot = null;
    private List<Map<String, String>> mData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);

        lvDepot = (ListView)findViewById(R.id.lv_depot);
        lvDepot.setOnItemClickListener(mItemClickListener);
        SimpleAdapter adapter = new SimpleAdapter(this,getData(),R.layout.depot_item,
                new String[]{"title", "num"},
                new int[]{R.id.tv_depot, R.id.tv_dc});
        lvDepot.setAdapter(adapter);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addDepot(MainActivity.this);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private AdapterView.OnItemClickListener mItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
            HashMap<String,String> map=(HashMap<String,String>)lvDepot.getItemAtPosition(i);
            String depot=map.get("title");
            String dc =map.get("num");
            Intent startDepot = new Intent(MainActivity.this, DepotActivity.class);
            startDepot.putExtra(DepotActivity.DEPOT_KEY, depot);
            startDepot.putExtra(DepotActivity.DC_KEY, dc);
            startActivity(startDepot);
        }
    };
    private List<Map<String, String>> getData() {
        if (mData == null) {
            mData = new ArrayList<>();
        }
        mData.clear();
        SharedPreferences sp = getSharedPreferences(DEPOT_LIST, MODE_PRIVATE);
        Map<String, ?> cfgMap = sp.getAll();
        for (String key : cfgMap.keySet()) {
            Map<String, String> map = new HashMap<String, String>();
            map.put("title", key);
            map.put("num", getString(R.string.dc_num_title) + cfgMap.get(key).toString());
            mData.add(map);
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
                        editor.putString(DEPOT_KEY, depotName);
                        editor.putInt(DC_NUM, Integer.parseInt(dcNum));
                        editor.putInt(DC_ITEM_MAX, Integer.parseInt(dcItemMax));
                        editor.commit();

                        mData = getData();
                        SimpleAdapter adapter = (SimpleAdapter) lvDepot.getAdapter();
                        adapter.notifyDataSetChanged();
                    }
                })
                .create().show();

    }
}
