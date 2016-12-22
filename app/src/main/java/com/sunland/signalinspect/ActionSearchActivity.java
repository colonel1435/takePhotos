package com.sunland.signalinspect;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.sunland.utils.CustomUtils;

import org.w3c.dom.Text;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static android.view.View.GONE;

public class ActionSearchActivity extends AppCompatActivity {

    public static final String WORK_DIR_KEY = "WORK_DIR";
    public static final String HISTORY_LIST = "search_history";
    public static final int THUMBNAIL_WIDTH = 48;
    public static final int THUMBNAIL_HEIGHT = 48;

    private static final String TAG = "wumin";
    private Toolbar toolbar;
    private LinearLayout mMulSelectLayout;
    private TextView mSearchResult;
    private SearchView mSearchView;
    private ListView mListView;
    private ListView mHistoryView;
    private TextView mHistoryMsg;
    private LinearLayout mHistoryLayout;
    private MyOnQueryTextListener mOnQueryTextListener;
    private Context mContext;
    private List<SearchInfo> mItems;
    private List<SearchInfo> mItemsBak;
    private List<String> mHistoryItems;
    private MySearchAdapter mAdapter;
    private ArrayAdapter<String> mHistoryAdapter;
    public static String workDir = "";
    private String usrInput = "";
    private int NORMAL_CHOICE = 0;
    private int MULTIPLE_CHOICE = 1;
    public static String THUMBNAIL_LABEL = "thumbnail/";

    private int mode = NORMAL_CHOICE;


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

        toolbar = (Toolbar) findViewById(R.id.action_search_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

//        actionMenuView = (ActionMenuView) findViewById(R.id.action_search_menu_item);
//        actionMenuView.getMenu().clear();
//        getMenuInflater().inflate(R.menu.multi_select_menu, actionMenuView.getMenu());

        mSearchResult = (TextView) findViewById(R.id.action_search_result);
        mSearchView = (SearchView)findViewById(R.id.action_search);
        mSearchView.setIconifiedByDefault(false);
        mSearchView.setSubmitButtonEnabled(false);
        mSearchView.setQueryHint("搜索");
//        CustomUtils.hideKeyboard(mSearchView);

        mItems = getDatas();
        mItemsBak = new ArrayList<>();
        mItemsBak.addAll(mItems);
        mListView = (ListView) findViewById(R.id.listview_search);
        mListView.setTextFilterEnabled(true);
        mAdapter = new MySearchAdapter(mContext, mItems);
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

        mMulSelectLayout = (LinearLayout)findViewById(R.id.llayout_multiple_select);
    }

    public void initEvent() {
        mOnQueryTextListener = new MyOnQueryTextListener();
        mSearchView.setOnQueryTextListener(mOnQueryTextListener);
        mListView.setOnItemClickListener(onItemClickListener);
        mListView.setOnItemLongClickListener(onItemLongClickListener);


//        mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
//        if (multiChioceCallback == null) {
//            multiChioceCallback = new MultiChioceCallback();
//        }
//        mListView.setMultiChoiceModeListener(multiChioceCallback);
//        mListView.clearChoices();
        mHistoryView.setOnItemClickListener(onHistoryItemClickListener);

    }

    public List<SearchInfo> getDatas() {
        List<SearchInfo> lists = new ArrayList<>();
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

    private AdapterView.OnItemLongClickListener onItemLongClickListener = new AdapterView.OnItemLongClickListener() {
        @Override
        public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
            mSearchResult.setVisibility(View.VISIBLE);
            mSearchView.setVisibility(View.INVISIBLE);
            mMulSelectLayout.setVisibility(View.VISIBLE);
            mode = MULTIPLE_CHOICE;
            mItems.get(position).setChecked(true);
            mAdapter.notifyDataSetChanged();
            return true;
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
                usrInput = newText;
                mListView.setVisibility(View.VISIBLE);
                mHistoryLayout.setVisibility(View.GONE);
                mAdapter.getFilter().filter(newText);
            } else {
                mListView.setVisibility(View.GONE);
                mHistoryLayout.setVisibility(View.VISIBLE);
                if (mHistoryItems.size() < 1) {
                    mHistoryMsg.setVisibility(View.VISIBLE);
                } else {
                    mHistoryMsg.setVisibility(GONE);
                }
                mListView.clearTextFilter();
            }
            return true;
        }
    }

    public void onSelectAll(View view){
        for(int i= 0; i< mAdapter.getCount(); i++){
//            mListView.setItemChecked(i, true);
            mItems.get(i).setChecked(true);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onSelectOpposite(View view){
        for(int i= 0; i< mAdapter.getCount(); i++){
//            mListView.setItemChecked(i, !mListView.isItemChecked(i));
            mItems.get(i).setChecked(mItems.get(i).getChecked()? false:true);
            mAdapter.notifyDataSetChanged();
        }
    }

    public void onSelectDelete(View view){
        List<SearchInfo> delList = new ArrayList<>();
        int size = mAdapter.getCount();
        int del = 0;
        List<SearchInfo> tmp = new ArrayList<>();
        tmp.addAll(mItems);
        for(int i= 0; i < size; i++){
            if (mItems.get(i).getChecked()) {
                delList.add(mItems.get(i));
                String title = workDir + mItems.get(i).getTitle();
                File img = new File(title);
                if (img.exists()) {
                    img.delete();
                }
                String thumb = workDir + THUMBNAIL_LABEL + mItems.get(i).getTitle();
                File thumbFile = new File(thumb);
                if (thumbFile.exists()) {
                    thumbFile.delete();
                }
                tmp.remove(i-del);
                del ++;
            }
        }
        if (delList.size() > 0) {
            mItemsBak.removeAll(delList);
        }
        mItems.clear();
        mItems.addAll(tmp);
        mAdapter.notifyDataSetChanged();

    }

    public void onSelectCancle(View view) {
        for(int i= 0; i< mAdapter.getCount(); i++){
//            mListView.setItemChecked(i, false);
            mItems.get(i).setChecked(false);
            mode = NORMAL_CHOICE;
            mAdapter.notifyDataSetChanged();
            mMulSelectLayout.setVisibility(GONE);
        }
        mSearchResult.setVisibility(View.GONE);
        mSearchView.setVisibility(View.VISIBLE);
        toolbar.setTitle("");
    }
    public void onCheckClicked(View view) {
        int position = (int)view.getTag();
        if(mItems.get(position).getChecked()) {
            mItems.get(position).setChecked(false);
        } else {
            mItems.get(position).setChecked(true);
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

    private class MySearchAdapter extends BaseAdapter implements Filterable {
        private MyFilter mFilter;
        private List<SearchInfo> mItems;
        private Context mContext;

        public MySearchAdapter (Context context, List<SearchInfo> list) {
            this.mContext = context;
            this.mItems = list;
        }
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

            String text = mItems.get(position).getTitle();
            ImageView ivItem = (ImageView) convertView.findViewById(R.id.iv_action_search_item);
            String thumbDir = workDir + THUMBNAIL_LABEL;
            File dir = new File(thumbDir);
            if (!dir.exists()) {
                dir.mkdir();
            }
            String thumbFile = thumbDir + text;
            File thumb = new File(thumbFile);
            Bitmap bmp = null;
            if (!thumb.exists()) {
                bmp = CustomUtils.getImageThumbnail(text, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT);
            }
            Glide.with(mContext).load(thumb).crossFade().into(ivItem);
//            ivItem.setImageBitmap(bmp);

            TextView item = (TextView) convertView.findViewById(R.id.tv_action_search_item);
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

            CheckBox check = (CheckBox) convertView.findViewById(R.id.cb_action_search_item);
            check.setChecked(mItems.get(position).getChecked()? true:false);
            check.setTag(position);
            if(mode == MULTIPLE_CHOICE) {
                check.setVisibility(View.VISIBLE);
            } else {
                check.setVisibility(GONE);
            }

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

                List<SearchInfo> newValues = new ArrayList<SearchInfo>();
                String filterString = constraint.toString().trim().toLowerCase();

                // 如果搜索框内容为空，就恢复原始数据
                if (TextUtils.isEmpty(filterString)) {
                    newValues = mItemsBak;
                } else {
                    // 过滤出新数据
                    for (SearchInfo item : mItemsBak) {
                        if (-1 != item.getTitle().toLowerCase().indexOf(filterString)) {
                            newValues.add(item);
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
                mItems.clear();
                mItems.addAll((List<SearchInfo>) results.values);
//                mItems = (List<SearchInfo>) results.values;
                if (results.count > 0) {
                    mAdapter.notifyDataSetChanged();  // 通知数据发生了改变
                } else {
                    mAdapter.notifyDataSetInvalidated(); // 通知数据失效
                }
            }
        }
    }
}
