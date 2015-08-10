package com.dk.headsettingdemo.app.activity;

import java.util.ArrayList;
import java.util.HashMap;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ListAdapter;

import com.dk.headsettingdemo.app.R;
import com.dk.headsettingdemo.app.adapter.ImageLoaderAdapter;
import com.nostra13.universalimageloader.core.ImageLoader;

public class PhotoScanningActivity extends Activity {
	public static String IMAGE_PATH = "path";
	public static String IMAGE_NAME = "name";
	private GridView gvImages;
	private Context context;
	private ImageLoaderAdapter adapter;
	private ArrayList<HashMap<String, Object>> listPhotos;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_scan_photos);
		initData();

	}

	private void initData() {
		context = this;
		listPhotos = getListPhotos();

		gvImages = (GridView) findViewById(R.id.gvImages);
		gvImages.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				showImage(position);
			}

			private void showImage(int position) {
				ImageLoader.getInstance().destroy();
				Intent intent = new Intent(context, HeadCuttingActivity.class);
				intent.setFlags(HeadCuttingActivity.PHOTO_FLAG);
				intent.putExtra(HeadCuttingActivity.EXTRA_IMAGE_INDEX,
						listPhotos.get(position).get(IMAGE_PATH).toString());
				startActivity(intent);
				finish();
			}
		});

		adapter = new ImageLoaderAdapter(context, listPhotos);
		gvImages.setAdapter((ListAdapter) adapter);

	}

	private ArrayList<HashMap<String, Object>> getListPhotos() { // get images data
		ArrayList<HashMap<String, Object>> list = new ArrayList<HashMap<String, Object>>();
		Cursor cursor = getContentResolver().query(
				MediaStore.Images.Media.EXTERNAL_CONTENT_URI // you have to give permission at AndroidManifest
				, null, null, null,MediaStore.Images.Media.DATE_ADDED+" DESC"); 	//按时间逆序
		while (cursor.moveToNext()) {
			String name = cursor.getString(cursor
					.getColumnIndex(MediaStore.Video.Media.DISPLAY_NAME)); // get file name
			String path = cursor.getString(cursor
					.getColumnIndex(MediaStore.Images.Media.DATA)); // get file path
			HashMap<String, Object> map = new HashMap<String, Object>();
			map.put(IMAGE_NAME, name == null ? "" : name);
			map.put(IMAGE_PATH, path == null ? "" : path);
			list.add(map);
		}
		cursor.close();
		return list;
	}
	
	public void click_back(View v) {
		this.finish();
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if(gvImages != null) {
			gvImages.destroyDrawingCache();
			gvImages = null;
		}
	}
}
