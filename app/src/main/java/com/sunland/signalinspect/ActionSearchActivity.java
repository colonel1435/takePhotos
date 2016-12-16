package com.sunland.signalinspect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.text.Spanned;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.sunland.utils.CustomUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ActionSearchActivity extends AppCompatActivity {

    public static final String WORK_DIR_KEY = "WORK_DIR";
    public static final String HISTORY_LIST = "search_history";
    private SearchView mSearchView;
    private ListView mListView;
    private ListView mHistoryView;
    private TextView mHistoryMsg;
    private LinearLayout mHistoryLayout;
    private MyOnQueryTextListener mOnQueryTextListener;
    private Context mContext;
    private List<String> mItems;
    private List<String> mItemsBak;
    private List<String> mHistoryItems;
    private MyAdapter mAdapter;
    private ArrayAdapter<String> mHistoryAdapter;
    private static String workDir = "";
    private String usrInput = "";


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
        mItemsBak = mItems;
        mListView = (ListView) findViewById(R.id.listview_search);
        mListView.setTextFilterEnabled(true);
        mAdapter = new MyAdapter();
        mListView.setAdapter(mAdapter);

        mHistoryLayout = (LinearLayout)findViewById(R.id.ll_history);

        mHistoryItems = getHistory();
        mHistoryView = (ListView) findViewById(R.id.listview_search_history);
        mHistoryAdapter = new ArrayAdapter<String>(mContext, android.R.layout.simple_expandable_list_item_1, mHistoryItems);
        mHistoryView.setAdapter(mHistoryAdapter);

        mHistoryMsg = (TextView) findViewById(R.id.history_msg);
        if (mHistoryItems.size() < 1) {
            mHistoryMsg.setVisibility(View.VISIBLE);
        }

    }

    public void initEvent() {
        mOnQueryTextListener = new MyOnQueryTextListener();
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);

        mListView.setOnItemClickListener(onItemClickListener);
        mHistoryView.setOnItemClickListener(onHistoryItemClickListener);

    }

    public List<String> getDatas() {
        List<String> lists = new ArrayList<>();
        CustomUtils.getFileList(lists, workDir);
        return lists;
    }

    public List<String> getHistory() {
        List<String> lists = new ArrayList<>();
        SharedPreferences sp = getSharedPreferences(HISTORY_LIST, MODE_PRIVATE);
        Map<String, ?> historyMap = sp.getAll();
        for (String key : historyMap.keySet()) {
            lists.add(key);
        }
        return lists;
    }

    public void clearHistory(View view) {
        SharedPreferences sp = getSharedPreferences(HISTORY_LIST, MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.clear().commit();
        mHistoryItems.clear();
        mHistoryAdapter.notifyDataSetChanged();

        mHistoryMsg.setVisibility(View.VISIBLE);

    }

    private AdapterView.OnItemClickListener onHistoryItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//            String item = mHistoryItems.get(position);

        }
    };
    private AdapterView.OnItemClickListener onItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            CustomUtils.hideKeyboard(view);
            if (!usrInput.isEmpty()) {
                SharedPreferences sp = getSharedPreferences(HISTORY_LIST, MODE_PRIVATE);
                SharedPreferences.Editor editor = sp.edit();
                editor.putString(usrInput, usrInput).commit();
                mHistoryItems.add(usrInput);
                mHistoryAdapter.notifyDataSetChanged();
            }
//            Toast.makeText(mContext, "ITEM -> " + mItems[position], Toast.LENGTH_LONG).show();
            Uri uri = Uri.fromFile(new File(workDir + mItemsBak.get(position)));
            Intent intent = new Intent();
//            intent.setAction(android.content.Intent.ACTION_GET_CONTENT);
//        intent.setAction(Intent.ACTION_PICK);
            intent.setAction(Intent.ACTION_VIEW);
            intent.setDataAndType(uri, "image/*");
            startActivity(intent);
        }
    };
    public class MyOnQueryTextListener implements SearchView.OnQueryTextListener {

        @Override
        public boolean onQueryTextSubmit(String query) {
            if (mItems.size() < 1) {
                Toast.makeText(mContext, getString(R.string.search_null), Toast.LENGTH_LONG).show();
                return false;
            }
            SharedPreferences sp = getSharedPreferences(HISTORY_LIST, MODE_PRIVATE);
            SharedPreferences.Editor editor = sp.edit();
            editor.putString(query, query).commit();
            mHistoryItems.add(query);
            mHistoryAdapter.notifyDataSetChanged();
            return false;
        }

        @Override
        public boolean onQueryTextChange(String newText) {
            if (newText.length() > 0) {
//                mListView.setFilterText(newText.toString());
                usrInput = newText;
                mListView.setVisibility(View.VISIBLE);
                mHistoryLayout.setVisibility(View.INVISIBLE);
                mAdapter.getFilter().filter(newText);
            } else {
                mListView.setVisibility(View.INVISIBLE);
                mHistoryLayout.setVisibility(View.VISIBLE);
                if (mHistoryItems.size() < 1) {
                    mHistoryMsg.setVisibility(View.VISIBLE);
                } else {
                    mHistoryMsg.setVisibility(View.GONE);
                }
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

    private class MyAdapter extends BaseAdapter implements Filterable {
        private MyFilter mFilter;
        @Override
        public int getCount() {
            return mItems.size();
        }

        @Override
        public Object getItem(int position) {
            return mItems.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (null == convertView) {
                convertView = View.inflate(mContext, R.layout.action_search_item, null);
            }
            TextView item = (TextView) convertView.findViewById(R.id.tv_action_search_item);
            String text = mItems.get(position);
            if (!text.isEmpty() && text.contains(usrInput)) {
                int index = text.indexOf(usrInput);
                int len = usrInput.length();
                Spanned tmp = Html.fromHtml(text.substring(0, index)
                                + "<font color=#FF0000>"
                                + text.substring(index, index + len) + "</font>"
                                + text.substring(index + len, text.length()));
                item.setText(tmp);
            } else {
                item.setText(text);
            }
//            item.setText(mItems.get(position));
            return convertView;
        }

        @Override
        public Filter getFilter() {
            if (null == mFilter) {
                mFilter = new MyFilter();
            }
            return mFilter;
        }

        class MyFilter extends Filter {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();

                List<String> newValues = new ArrayList<String>();
                String filterString = constraint.toString().trim().toLowerCase();

                // 如果搜索框内容为空，就恢复原始数据
                if (TextUtils.isEmpty(filterString)) {
                    newValues = mItemsBak;
                } else {
                    // 过滤出新数据
                    for (String str : mItemsBak) {
                        if (-1 != str.toLowerCase().indexOf(filterString)) {
                            newValues.add(str);
                        }
                    }
                }

                results.values = newValues;
                results.count = newValues.size();

                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {
                mItems = (List<String>) results.values;

                if (results.count > 0) {
                    mAdapter.notifyDataSetChanged();  // 通知数据发生了改变
                } else {
                    mAdapter.notifyDataSetInvalidated(); // 通知数据失效
                }
            }
        }
    }
}
