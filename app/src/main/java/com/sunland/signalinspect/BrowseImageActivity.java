package com.sunland.signalinspect;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.CheckBox;
import android.widget.Toast;

import com.sunland.utils.BitmapUtils;
import com.sunland.utils.MyBrowseRecyclerAdapter;
import com.sunland.utils.MyGridDividerItemDecoration;
import com.sunland.utils.RecyclerItemClickListener;

import static com.sunland.signalinspect.DepotActivity.WORK_DIR;

public class BrowseImageActivity extends AppCompatActivity {
	public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String IMAGES_FOLDER = SDCARD_PATH + WORK_DIR;
	public static final int NORMAL_CHOICE = 0;
	public static final int MULTIPLE_CHOICE = 1;
	public static int mode = NORMAL_CHOICE;
	private Context mContext;
	private List<String> datas;
	List<BrowseInfo> mLists;
	private CheckBox mCheckbox;
	private RecyclerView mRecyclerView;
	private MyBrowseRecyclerAdapter mBrowseRecyclerAdapter;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_image);

		mContext = this;
		datas = new ArrayList<>();
		BitmapUtils.getBmpUrl(datas, IMAGES_FOLDER);

		mCheckbox = (CheckBox)findViewById(R.id.action_browse_check_box);
		mLists = getData(datas);
		mBrowseRecyclerAdapter = new MyBrowseRecyclerAdapter(mLists, this);
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
				mode = MULTIPLE_CHOICE;
				mLists.get(position).setChecked(true);
				mBrowseRecyclerAdapter.notifyDataSetChanged();
//				Toast.makeText(mContext, "onItemLongClick", Toast.LENGTH_LONG).show();
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
		Intent intent = new Intent(BrowseImageActivity.this, showImageActivity.class);
		intent.putExtra("path", datas.get(position));
		int[] location = new int[2];
		view.getLocationOnScreen(location);
		intent.putExtra("locationX", location[0]);
		intent.putExtra("locationY", location[1]);

		intent.putExtra("width", view.getWidth());
		intent.putExtra("height", view.getHeight());
		startActivity(intent);
		overridePendingTransition(0, 0);
	}
}
