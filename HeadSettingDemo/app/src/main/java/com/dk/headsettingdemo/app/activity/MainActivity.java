package com.dk.headsettingdemo.app.activity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import com.dk.headsettingdemo.app.R;
import com.dk.headsettingdemo.app.common.BaseActivity;
import com.dk.headsettingdemo.app.util.BitmapUtil;


public class MainActivity extends BaseActivity implements View.OnClickListener{
    private final int REQ_CODE_CAMERA=2;
    private final int MSG_CHANGE_HEAD=0x11;

    Button btnSelectFromPhotos;
    Button btnSelectFromCamera;
    ImageView ivHeader;

    Context mContext=this;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (msg.what == MSG_CHANGE_HEAD) {
                // 更新头像
                Log.v("dk", "change head");
                Bitmap bm = BitmapUtil.getInstance().toRoundBitmap(
                        (Bitmap) msg.obj);
                ivHeader.setImageBitmap(bm);
            }
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_head_setting);
        init();

    }

    private void init() {
        btnSelectFromCamera= (Button) findViewById(R.id.btn_select_from_camera);
        btnSelectFromPhotos= (Button) findViewById(R.id.btn_select_from_photo);
        ivHeader= (ImageView) findViewById(R.id.iv_header);

        btnSelectFromPhotos.setOnClickListener(this);
        btnSelectFromCamera.setOnClickListener(this);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_head_setting, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent.hasExtra(HeadCuttingActivity.EXTRA_HEAD_DATA)) {
            Bitmap bm = intent.getBundleExtra(HeadCuttingActivity.EXTRA_HEAD_DATA).getParcelable(
                    "bitmap");
            Message msg = new Message();
            msg.what = MSG_CHANGE_HEAD;
            msg.obj = bm;
            mHandler.sendMessage(msg);
            super.onNewIntent(intent);
        }
    }
    @Override
    public void onClick(View view) {
        switch(view.getId()){
            case R.id.btn_select_from_camera:{
                Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent,REQ_CODE_CAMERA);
                break;
            }
            case R.id.btn_select_from_photo:{
                Intent intent=new Intent(mContext,PhotoScanningActivity.class);
                startActivity(intent);
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1) {
            Bundle bundle = data.getExtras();
            Intent intent = new Intent(this, HeadCuttingActivity.class);
            intent.putExtras(bundle);
            intent.setFlags(HeadCuttingActivity.CAMERA_FLAG);
            startActivity(intent);
        }
    }
}
