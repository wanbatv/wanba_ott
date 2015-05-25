package com.wanba.android.internal;

import android.content.Context;

import com.wanba.android.app.WanbaContext;

import java.util.HashMap;

/**
 * Created by Forcs on 15/5/6.
 */
public class WanbaCore {

    private Context mApplicationContext = null;

    private HashMap<String, IService> mServiceMap = null;

    public WanbaCore(Context context) {
        mApplicationContext = context;
    }

    public void run() {
        mServiceMap = new HashMap<>();
    }

    public WanbaContext newContext(Context currContext) {
        return new WanbaContextImpl(currContext, this);
    }

    public IService getService(String key) {
        return mServiceMap.get(key);
    }
}
