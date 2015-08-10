package com.dk.headsettingdemo.app.activity;


import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.Window;
import com.dk.headsettingdemo.app.R;
import com.dk.headsettingdemo.app.common.BaseActivity;
import com.dk.headsettingdemo.app.util.BitmapUtil;
import com.dk.headsettingdemo.app.widget.HeadCutView;


public class HeadCuttingActivity extends BaseActivity {
	public static final int RESULT_DONE = 1;
	public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_HEAD_DATA="change_head";
    public static final int INTENT_FLAG=0x1;
	public static Bitmap newHeadBitmap;

	public static int PHOTO_FLAG = 0;
	public static int CAMERA_FLAG = 1;

    private final int MSG_CHANGE_IMG=0x11;

    private int screenWidth;
    private int screenHeight;

	private int intentFlag;
	private Bitmap oldBitmap;


	private HeadCutView ivHeader;

    private Handler mHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case MSG_CHANGE_IMG:{
                    ivHeader.setImageFromBitmap(oldBitmap);
                    oldBitmap.recycle();
                }
            }
            super.handleMessage(msg);
        }
    };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.activity_header_cut);
		initData();

	}

	private void initData() {
        screenHeight=getWindowManager().getDefaultDisplay().getHeight();
        screenWidth=getWindowManager().getDefaultDisplay().getWidth();

		Intent intent = getIntent();
		intentFlag = intent.getFlags();
		ivHeader= (HeadCutView) findViewById(R.id.iv_cut_header);
		// 判断是否是拍照后跳转过来的
		if (intentFlag == PHOTO_FLAG) {
			String path = intent.getStringExtra(EXTRA_IMAGE_INDEX);
			getLocalBitmap(path);
			ivHeader.setImageFromBitmap(oldBitmap);
		} else {
			oldBitmap = (Bitmap) intent.getExtras().get("data");
			ivHeader.setImageFromBitmap(oldBitmap);

		}

	}

	//从本地获取显示图片的Bitmap
	private void getLocalBitmap(final String path) {
        oldBitmap=BitmapUtil.decodeSampledBitmapFromPath(path,screenWidth,screenHeight);
	}

	public void clickBack(View v) {
		this.finish();
	}

	public void clickFinish(View v) {
		newHeadBitmap = ivHeader.clipImage();
		Intent intent = new Intent(this, MainActivity.class);
		Bundle bundle = new Bundle();
		bundle.putParcelable("bitmap", newHeadBitmap);
		intent.putExtra(EXTRA_HEAD_DATA, bundle);
        intent.setFlags(INTENT_FLAG);
		startActivity(intent);
		this.finish();

	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		if (oldBitmap != null)
			oldBitmap.recycle();
		System.gc();
	}

}
