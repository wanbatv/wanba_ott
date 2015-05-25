package com.wanba.android.app;

/**
 * Created by Forcs on 15/5/6.
 */
public interface WanbaContext {

    public static final String CONTROLLER = "wanba.service.controller";

    public Object getWanbaService(String key);

    public WanbaContext getWanbaApplicationContext();
}
