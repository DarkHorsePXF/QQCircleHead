package com.dk.headsettingdemo.app.adapter;

import java.util.ArrayList;
import java.util.HashMap;

import android.content.Context;
import android.graphics.Bitmap;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dk.headsettingdemo.app.R;
import com.dk.headsettingdemo.app.util.ImageLoaderFactory;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

public class ImageLoaderAdapter extends BaseAdapter implements Adapter {
	Context mContext;
	ArrayList<HashMap<String, Object>> picList;
	ImageLoader imageLoader;

	DisplayImageOptions options;

	public ImageLoaderAdapter(Context context,
			ArrayList<HashMap<String, Object>> picList) {
		this.mContext = context;
		this.picList = picList;

		imageLoader = ImageLoaderFactory.getImageLoader(mContext); // step 1:get
																	// ImageLoader
		options = new DisplayImageOptions.Builder()
				// step2:set options of ImageLoader
				.cacheInMemory(true).cacheOnDisk(true).considerExifParams(true)
				.showImageOnLoading(R.drawable.iv_stub)
				.showImageOnFail(R.drawable.iv_loading_error)
				.showImageForEmptyUri(R.drawable.iv_empty)
				.build();

	}

	@Override
	public int getCount() {
		return picList.size();
	}

	@Override
	public Object getItem(int position) {
		return picList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder holder;
		if (convertView == null) {
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.layout_image_content, null);
			holder = new ViewHolder();
			holder.ivComplete=(ImageView) convertView.findViewById(R.id.iv_scanning_list_item);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}
		String path = (String) picList.get(position).get("path");

		imageLoader.displayImage( // step3:display images
				"file:/" + path, // URL: local files-->"file:/..."
									// Internet files-->"http://..."
									// contentProvider-->"content:/..."
									// drawable files-->"drawable:/..."

				holder.ivComplete, // display on this imageView

				options,           // displayed options

                new ImageLoadingListener() {
                    @Override
                    public void onLoadingStarted(String imageUri, View view) {

                    }

                    @Override
                    public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                    }

                    @Override
                    public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {

                    }

                    @Override
                    public void onLoadingCancelled(String imageUri, View view) {

                    }
                }
                );
		return convertView;
	}

	class ViewHolder {
		ImageView ivComplete;
	}

}
