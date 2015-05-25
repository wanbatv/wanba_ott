package com.wanba.android.app;

import android.app.Application;

import com.wanba.android.internal.WanbaContextImpl;
import com.wanba.android.internal.WanbaCore;

/**
 * Created by Forcs on 15/5/6.
 */
public class WanbaApplication extends Application implements WanbaContext {

    private WanbaContextImpl mWanbaContext = null;
    private WanbaCore mWanbaCore = null;

    @Override
    public void onCreate() {
        super.onCreate();
        mWanbaCore = new WanbaCore(this);
        mWanbaCore.run();
        mWanbaContext = (WanbaContextImpl) mWanbaCore.newContext(this);
    }

    @Override
    public Object getWanbaService(String key) {
        return mWanbaContext.getWanbaService(key);
    }

    @Override
    public WanbaContext getWanbaApplicationContext() {
        return mWanbaContext.getWanbaApplicationContext();
    }

    WanbaCore getWanbaCore() {
        return mWanbaCore;
    }
}
