package com.dk.headsettingdemo.app.util;

import android.content.Context;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

public class ImageLoaderFactory {
	
	public static ImageLoader getImageLoader(Context context) {
		ImageLoader imageLoader = ImageLoader.getInstance();
		imageLoader.init(ImageLoaderConfiguration.createDefault(context));
		return imageLoader;
	}
}
