package com.sunland.signalinspect;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;
import com.sunland.utils.BitmapUtils;
import com.sunland.view.SquareCenterImageView;

import static com.sunland.signalinspect.DepotActivity.WORK_DIR;

public class BrowseImageActivity extends AppCompatActivity {
//	public static DisplayImageOptions mNormalImageOptions;
	public static final String SDCARD_PATH = Environment.getExternalStorageDirectory().toString();
	public static final String IMAGES_FOLDER = SDCARD_PATH + WORK_DIR;
	private GridView mGridView;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_browse_image);
//		initImageLoader(this);
		mGridView = (GridView) findViewById(R.id.multi_photo_grid);
		List<String> datas = new ArrayList<>();
		BitmapUtils.getBmpUrl(datas, IMAGES_FOLDER);
		mGridView.setAdapter(new ImagesInnerGridViewAdapter(datas));
	}
/*
	private void initImageLoader(Context context) {
		int memoryCacheSize = (int) (Runtime.getRuntime().maxMemory() / 5);
		MemoryCacheAware<String, Bitmap> memoryCache;
		if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.GINGERBREAD) {
			memoryCache = new LruMemoryCache(memoryCacheSize);
		} else {
			memoryCache = new LRULimitedMemoryCache(memoryCacheSize);
		}

		mNormalImageOptions = new DisplayImageOptions.Builder().bitmapConfig(Config.RGB_565).cacheInMemory(true).cacheOnDisc(true)
				.resetViewBeforeLoading(true).build();

		// This
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(context).defaultDisplayImageOptions(mNormalImageOptions)
				.denyCacheImageMultipleSizesInMemory().discCache(new UnlimitedDiscCache(new File(IMAGES_FOLDER)))
				// .discCacheFileNameGenerator(new Md5FileNameGenerator())
				.memoryCache(memoryCache)
				// .memoryCacheSize(memoryCacheSize)
				.tasksProcessingOrder(QueueProcessingType.LIFO).threadPriority(Thread.NORM_PRIORITY - 2).threadPoolSize(3).build();

		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
*/
	private class ImagesInnerGridViewAdapter extends BaseAdapter {

		private List<String> datas;

		public ImagesInnerGridViewAdapter(List<String> datas) {
			this.datas = datas;
		}

		@Override
		public int getCount() {
			return datas.size();
		}

		@Override
		public Object getItem(int position) {
			return position;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		@Override
		public View getView(final int position, View convertView, ViewGroup parent) {
			final SquareCenterImageView imageView = new SquareCenterImageView(BrowseImageActivity.this);
			imageView.setScaleType(ScaleType.CENTER_CROP);
			imageView.setImageBitmap(BitmapFactory.decodeFile(datas.get(position)));
//			ImageLoader.getInstance().displayImage(datas.get(position), imageView);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					Intent intent = new Intent(BrowseImageActivity.this, showImageActivity.class);
					intent.putExtra("images", (ArrayList<String>) datas);
					intent.putExtra("position", position);
					int[] location = new int[2];
					imageView.getLocationOnScreen(location);
					intent.putExtra("locationX", location[0]);
					intent.putExtra("locationY", location[1]);

					intent.putExtra("width", imageView.getWidth());
					intent.putExtra("height", imageView.getHeight());
					startActivity(intent);
					overridePendingTransition(0, 0);
				}
			});
			return imageView;
		}

	}
}
