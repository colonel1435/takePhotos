package com.sunland.signalinspect;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import android.app.IntentService;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Bitmap.Config;
import android.util.Log;

public class SaveImgService extends IntentService {

    private static final String PHOTO_TABLE = "Photos";
	private static final String LOG_TAG = "wumin SaveImgService";
	
	public SaveImgService() {
		super("SaveImgService");
		// TODO Auto-generated constructor stub
	}

	@Override
	protected void onHandleIntent(Intent intent) {
		// TODO Auto-generated method stub
		if (intent != null) {
			final String action = intent.getAction();
			if (ClipImageActivity.ACTION_SAVE_IMG == action) {
				Log.i(LOG_TAG, "Start handle intent");
				String dbDir = intent.getStringExtra(ClipImageActivity.PHOTODB_KEY);
				String photoFile = intent.getStringExtra(ClipImageActivity.PHOTOFILE_KEY);
				String photoName = intent.getStringExtra(ClipImageActivity.PHOTONAME_KEY);
				save2db(dbDir, photoFile, photoName);
			}
		}
	}
	
	private void save2db(String dbDir, String photoFile, String photoName) {
        SQLiteDatabase db = null;
        File dbFile = new File(dbDir);
        if(!dbFile.exists()) {
            Log.i(LOG_TAG, "Create new database");
            db = SQLiteDatabase.openOrCreateDatabase(dbDir, null);
            String photosTable = "create table Photos(pid integer primary key autoincrement,name text,data BLOB)";
            db.execSQL(photosTable);

        } else {
            Log.i(LOG_TAG, "Open database");
            db = SQLiteDatabase.openDatabase(dbDir, null, SQLiteDatabase.OPEN_READWRITE);
        }

		byte[] inByte = compressImageByQuatity(BitmapFactory.decodeFile(photoFile));
		
		db.execSQL("insert into Photos (name, data) values(?,?)",new 
				Object[]{photoName, inByte});
		db.close();
        DepotActivity.onSaveFinished();
		Log.i(LOG_TAG, "Save To database finished!");
	}
	
	private byte[] compressImageByQuatity(Bitmap bmp) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        int options = 100;
        while ( baos.toByteArray().length / 1024>500) {    //<=500k       
            baos.reset();	//reset
            options -= 10; 
            bmp.compress(Bitmap.CompressFormat.JPEG, options, baos);

        }
       // ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
       // Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        return baos.toByteArray();
    }
	
	private byte[] compressImageBySize(Bitmap bmp) {

        ByteArrayOutputStream baos = new ByteArrayOutputStream();        
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        if( baos.toByteArray().length / 1024>1024) {// <= 1M    
            baos.reset();
            bmp.compress(Bitmap.CompressFormat.JPEG, 50, baos);//����ѹ��50%����ѹ��������ݴ�ŵ�baos��
        }
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        //��ʼ����ͼƬ����ʱ��options.inJustDecodeBounds ���true��
        newOpts.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        newOpts.inJustDecodeBounds = false;
        int w = newOpts.outWidth;
        int h = newOpts.outHeight;
        //���������ֻ��Ƚ϶���800*480�ֱ��ʣ����ԸߺͿ���������Ϊ
        float hh = 800f;//�������ø߶�Ϊ800f
        float ww = 480f;//�������ÿ��Ϊ480f
        //���űȡ������ǹ̶��������ţ�ֻ�ø߻��߿�����һ�����ݽ��м��㼴��
        int be = 1;//be=1��ʾ������
        if (w > h && w > ww) {//�����ȴ�Ļ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outWidth / ww);
        } else if (w < h && h > hh) {//����߶ȸߵĻ����ݿ�ȹ̶���С����
            be = (int) (newOpts.outHeight / hh);
        }
        if (be <= 0)
            be = 1;
        newOpts.inSampleSize = be;//�������ű���
        newOpts.inPreferredConfig = Config.RGB_565;//����ͼƬ��ARGB888��RGB565
        //���¶���ͼƬ��ע���ʱ�Ѿ���options.inJustDecodeBounds ���false��
        isBm = new ByteArrayInputStream(baos.toByteArray());
        bitmap = BitmapFactory.decodeStream(isBm, null, newOpts);
        return compressImageByQuatity(bitmap);//ѹ���ñ�����С���ٽ�������ѹ��
    }

}
