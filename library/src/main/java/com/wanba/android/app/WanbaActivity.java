package com.wanba.android.app;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;

import com.wanba.android.internal.WanbaContextImpl;

/**
 * Created by Forcs on 15/5/6.
 */
public class WanbaActivity extends FragmentActivity implements WanbaContext {

    private WanbaContextImpl mWanbaContext = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        WanbaApplication app = (WanbaApplication) getApplication();
        mWanbaContext = (WanbaContextImpl) app.getWanbaCore().newContext(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mWanbaContext.release();
    }

    @Override
    public Object getWanbaService(String key) {
        return mWanbaContext.getWanbaService(key);
    }

    @Override
    public WanbaContext getWanbaApplicationContext() {
        return mWanbaContext.getWanbaApplicationContext();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }
}
