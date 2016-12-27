package com.sunland.signalinspect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ActionMode;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sunland.utils.BitmapUtils;
import com.sunland.utils.CustomUtils;
import com.sunland.utils.MyBrowseRecyclerAdapter;
import com.sunland.utils.MyGridDividerItemDecoration;
import com.sunland.utils.RecyclerItemClickListener;

import static com.sunland.signalinspect.ActionSearchActivity.THUMBNAIL_LABEL;
import static com.sunland.signalinspect.ActionSearchActivity.workDir;
import static com.sunland.signalinspect.DepotActivity.WORK_DIR;

public class BrowseImageActivity extends AppCompatActivity {
	public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String IMAGES_FOLDER = SDCARD_PATH + WORK_DIR;
	public static final int NORMAL_CHOICE = 0;
	public static final int MULTIPLE_CHOICE = 1;
	public static int mode = NORMAL_CHOICE;
	private Context mContext;
	private List<String> datas;
	private CheckBox mCheckbox;
	private RecyclerView mRecyclerView;
	private MyBrowseRecyclerAdapter mBrowseRecyclerAdapter;
	private Toolbar mToolbar;
	public ActionBar mActionBar;
	public static ActionMode actionMode;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_image);

		mContext = this;
		datas = new ArrayList<>();
		BitmapUtils.getBmpUrl(datas, IMAGES_FOLDER);

		mToolbar = (Toolbar)findViewById(R.id.action_browse_toolbar);
		setSupportActionBar(mToolbar);
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayHomeAsUpEnabled(true);


		mCheckbox = (CheckBox)findViewById(R.id.action_browse_check_box);
		mBrowseRecyclerAdapter = new MyBrowseRecyclerAdapter(datas, this);
		mRecyclerView = (RecyclerView) findViewById(R.id.action_browse_recyclerview);
		mRecyclerView.setLayoutManager(new GridLayoutManager(this, 4));
		mRecyclerView.addItemDecoration(new MyGridDividerItemDecoration(mContext, R.drawable.recyclerview_divider));
		mRecyclerView.setHasFixedSize(true);
		mRecyclerView.setAdapter(mBrowseRecyclerAdapter);
		mRecyclerView.addOnItemTouchListener(new RecyclerItemClickListener(this, mRecyclerView, new RecyclerItemClickListener.OnItemClickListener() {
			@Override
			public void onItemClick(View view, int position) {
//				Toast.makeText(mContext, "onItemClick", Toast.LENGTH_LONG).show();
			}

			@Override
			public void onItemLongClick(View view, int position) {
				actionMode = startSupportActionMode(mCallback);
				mode = MULTIPLE_CHOICE;
				mBrowseRecyclerAdapter.setItemChecked(position, true);
				mBrowseRecyclerAdapter.notifyDataSetChanged();
				actionMode.setTitle("已选择" + mBrowseRecyclerAdapter.getSelectedItem().size() + "项");
			}
		}));
	}

	private List<BrowseInfo> getData(List<String> datas) {
		List<BrowseInfo> list = new ArrayList<>();
		for(String item : datas) {
			BrowseInfo info = new BrowseInfo(item, false);
			list.add(info);
		}
		return list;
	}

	public void onImageClick(View view) {
		int position = (int) view.getTag(R.id.ivBrowsePositionIndex);
		String path = datas.get(position);
        Uri uri = Uri.fromFile(new File(path));
        Intent intent = new Intent();
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(uri, "image/*");
        startActivity(intent);
//		Intent intent = new Intent(BrowseImageActivity.this, showImageActivity.class);
//		intent.putExtra("path", datas.get(position));
//		int[] location = new int[2];
//		view.getLocationOnScreen(location);
//		intent.putExtra("locationX", location[0]);
//		intent.putExtra("locationY", location[1]);
//
//		intent.putExtra("width", view.getWidth());
//		intent.putExtra("height", view.getHeight());
//		startActivity(intent);
//		overridePendingTransition(0, 0);
	}

	private void onSelectAll() {
		for(int i = 0; i < mBrowseRecyclerAdapter.getItemCount(); i++) {
			mBrowseRecyclerAdapter.setItemChecked(i, true);
		}
		mBrowseRecyclerAdapter.notifyDataSetChanged();
		actionMode.setTitle("已选择" + mBrowseRecyclerAdapter.getSelectedItem().size() + "项");
	}

	private void onSelectOppsite() {
		for(int i = 0; i < mBrowseRecyclerAdapter.getItemCount(); i++) {
			boolean isCheck = mBrowseRecyclerAdapter.isItemChecked(i);
			mBrowseRecyclerAdapter.setItemChecked(i, isCheck ? false:true);
		}
		mBrowseRecyclerAdapter.notifyDataSetChanged();
		actionMode.setTitle("已选择" + mBrowseRecyclerAdapter.getSelectedItem().size() + "项");
	}

	private void onSelectDel() {
		List<String> delList = new ArrayList<>();
		int size = mBrowseRecyclerAdapter.getItemCount();
		int del = 0;
		List<String> tmp = new ArrayList<>();
		tmp.addAll(datas);
		for(int i= 0; i < size; i++){
			if (mBrowseRecyclerAdapter.isItemChecked(i)) {
				delList.add(datas.get(i));
				String title = workDir + datas.get(i);
				File img = new File(title);
				if (img.exists()) {
					img.delete();
				}
				String thumb = workDir + THUMBNAIL_LABEL + datas.get(i);
				File thumbFile = new File(thumb);
				if (thumbFile.exists()) {
					thumbFile.delete();
				}
				tmp.remove(i-del);
				del ++;
			}
		}
		datas.clear();
		datas.addAll(tmp);
		mBrowseRecyclerAdapter.notifyDataSetChanged();
		for (int i = 0; i < mBrowseRecyclerAdapter.getItemCount(); i++) {
			mBrowseRecyclerAdapter.setItemChecked(i, false);
			mBrowseRecyclerAdapter.notifyItemChanged(i);
		}
	}

	private void onSelectCancel() {
		mode = NORMAL_CHOICE;
		for (int i = 0; i < mBrowseRecyclerAdapter.getItemCount(); i++) {
			if(mBrowseRecyclerAdapter.isItemChecked(i)) {
				mBrowseRecyclerAdapter.setItemChecked(i, false);
			}
		}
		mBrowseRecyclerAdapter.notifyDataSetChanged();
		actionMode.finish();
		mActionBar.setTitle(getString(R.string.browse_photo_title));
	}
	private ActionMode.Callback mCallback = new ActionMode.Callback() {
		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			getMenuInflater().inflate(R.menu.multi_select_menu, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			int id = item.getItemId();
			switch (id) {
				case R.id.action_browse_select_all:
					onSelectAll();
					break;
				case R.id.action_browse_select_oppsite:
					onSelectOppsite();
					break;
				case R.id.action_browse_select_delete:
					onSelectDel();
					break;
				case R.id.action_browse_select_cancel:
					onSelectCancel();
					break;
			}
			return false;
		}

		@Override
		public void onDestroyActionMode(ActionMode actMode) {
			mode = NORMAL_CHOICE;
			for (int i = 0; i < mBrowseRecyclerAdapter.getItemCount(); i++) {
				if(mBrowseRecyclerAdapter.isItemChecked(i)) {
					mBrowseRecyclerAdapter.setItemChecked(i, false);
				}
			}
			mBrowseRecyclerAdapter.notifyDataSetChanged();
			actionMode = null;
		}
	};

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int id = item.getItemId();
		switch (id) {
			case android.R.id.home:
				actionMode = null;
				finish();
				break;
		}

		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onDestroy() {
		actionMode = null;
		mode = NORMAL_CHOICE;
		super.onDestroy();
	}
}
