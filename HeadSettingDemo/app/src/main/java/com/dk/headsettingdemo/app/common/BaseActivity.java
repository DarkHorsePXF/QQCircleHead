package com.dk.headsettingdemo.app.common;

import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;

/**
 * Created by feng on 2015/8/10.
 */
public class BaseActivity extends ActionBarActivity{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
