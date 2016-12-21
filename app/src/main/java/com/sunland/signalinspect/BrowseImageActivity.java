package com.sunland.signalinspect;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView.ScaleType;

import com.bumptech.glide.Glide;
import com.nostra13.universalimageloader.cache.disc.impl.UnlimitedDiskCache;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.cache.memory.impl.LRULimitedMemoryCache;
import com.nostra13.universalimageloader.cache.memory.impl.LruMemoryCache;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;
import com.sunland.utils.BitmapUtils;
import com.sunland.view.SquareCenterImageView;

import static com.sunland.signalinspect.ActionSearchActivity.THUMBNAIL_LABEL;
import static com.sunland.signalinspect.DepotActivity.WORK_DIR;

public class BrowseImageActivity extends AppCompatActivity {
	public static DisplayImageOptions mNormalImageOptions;
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

	private void initImageLoader(Context context) {

		// Initialize ImageLoader with configuration.
		ImageLoaderConfiguration.Builder config = new ImageLoaderConfiguration.Builder(context);
		config.memoryCacheExtraOptions(1080,1920);//设置缓存每个图片的最大宽长
		config.threadPoolSize(5);//设置缓存池的大小
		config.threadPriority(Thread.NORM_PRIORITY - 2);//设置线程运行优先级为3 低于普通线程优先级
		config.denyCacheImageMultipleSizesInMemory();
		config.diskCacheFileNameGenerator(new Md5FileNameGenerator());//硬盘缓存文件名采用MD5加密策略 HashCodeFileNameGenerator()还可以用hashcode进行加密
		config.diskCacheSize(50 * 1024 * 1024); // 默认设置50M硬盘缓存空间
		config.memoryCacheSize(2*1024*1024);//设置内存缓存大小
		config.diskCacheFileCount(50);//设置硬盘缓存文件数目
		config.tasksProcessingOrder(QueueProcessingType.LIFO);
		File cacheDir = new File(Environment.getExternalStorageDirectory()+"/signalInspect/cache");
		config.diskCache(new UnlimitedDiskCache(cacheDir));//自定义缓存路径
		ImageLoader.getInstance().init(config.build());
	}

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
//			imageView.setImageBitmap(BitmapFactory.decodeFile(datas.get(position)));
			File photoFile = new File(datas.get(position));
			Glide.with(BrowseImageActivity.this).load(photoFile).crossFade().into(imageView);
//			ImageLoader.getInstance().displayImage("file://" + datas.get(position), imageView);
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
