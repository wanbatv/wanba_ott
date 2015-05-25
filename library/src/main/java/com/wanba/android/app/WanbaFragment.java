package com.wanba.android.app;

import android.support.v4.app.Fragment;

/**
 * Created by Forcs on 15/5/6.
 */
public class WanbaFragment extends Fragment implements WanbaContext {
    @Override
    public Object getWanbaService(String key) {
        return null;
    }

    @Override
    public WanbaContext getWanbaApplicationContext() {
        return null;
    }
}
