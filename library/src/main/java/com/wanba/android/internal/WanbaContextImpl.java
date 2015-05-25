package com.wanba.android.internal;

import android.content.Context;

import com.wanba.android.app.WanbaContext;

/**
 * Created by Forcs on 15/5/6.
 */
public class WanbaContextImpl implements WanbaContext {

    private Context mContext = null;
    private Context mAppContext = null;
    private WanbaCore mWanbaCore = null;

    public WanbaContextImpl(Context context, WanbaCore core) {
        mContext = context;
        mAppContext = context.getApplicationContext();
        mWanbaCore = core;
    }

    public void release() {
        mContext = null;
    }

    @Override
    public Object getWanbaService(String key) {
        return mWanbaCore.getService(key).getProxy(mContext);
    }

    @Override
    public WanbaContext getWanbaApplicationContext() {
        return (WanbaContext) mContext.getApplicationContext();
    }
}
