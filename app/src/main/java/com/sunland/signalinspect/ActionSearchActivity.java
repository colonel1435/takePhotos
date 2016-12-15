package com.sunland.signalinspect;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.utils.CustomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ActionSearchActivity extends AppCompatActivity {

    public static final String WORK_DIR_KEY = "WORK_DIR";
    private SearchView mSearchView;
    private ListView mListView;
    private MyOnQueryTextListener mOnQueryTextListener;
    private Context mContext;
    private Object[] mItems;
    private ArrayAdapter<Object> mAdapter;
    private static String workDir = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_action_search);

        Bundle bundle = getIntent().getExtras();
        workDir = bundle.getString(WORK_DIR_KEY);
        mContext = this;
        initView();
        initEvent();
    }

    public void initView() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.action_search_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mSearchView = (SearchView)findViewById(R.id.action_search);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("搜索");

        mItems = getDatas();
        mListView = (ListView) findViewById(R.id.listview_search);
        mListView.setTextFilterEnabled(true);
        mAdapter = new ArrayAdapter<Object>(mContext, android.R.layout.simple_expandable_list_item_1, mItems);
        mListView.setAdapter(mAdapter);

    }

    public void initEvent() {
        mOnQueryTextListener = new MyOnQueryTextListener();
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);

        mListView.setOnItemClickListener(onItemClickListener);

    }

    public Object[] getDatas() {
        List<String> lists = new ArrayList<>();
        CustomUtils.getFileList(lists, workDir);
        return lists.toArray();
    }

    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            Toast.makeText(mContext, "ITEM -> " + mItems[position], Toast.LENGTH_LONG).show();
            Uri uri = Uri.fromFile(new File(workDir + mItems[position]));
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
//        intent.setAction(Intent.ACTION_PICK);
//            intent.setAction(Intent.ACTION_GET_CONTENT);
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        }
    };
    public class MyOnQueryTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.length() > 0) {
//                mListView.setFilterText(newText.toString());
                mAdapter.getFilter().filter(newText);
            } else {
                mListView.clearTextFilter();
            }
            return true;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case android.R.id.home:
                finish();
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

//    private class MyClickListener implements View.OnClickListener {
//        @Override
//        public void onClick(View v) {
//            int id = v.getId();
//            Intent startSearchItem = new Intent(ActionSearchActivity.this, ActionSearchItemActivity.class);
//            startSearchItem.putExtra(ACTION_SEARCH_ITEM, id);
//            startActivity(startSearchItem);
//        }
//    }
}
