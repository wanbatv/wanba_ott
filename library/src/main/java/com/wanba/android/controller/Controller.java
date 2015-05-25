package com.wanba.android.controller;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.view.KeyEvent;

/**
 * Created by Forcs on 15/5/6.
 */
public class Controller {

    private ControllerDelegate mDelegate = null;

    private Handler mHandler = new Handler(Looper.getMainLooper()) {

        @Override
        public void handleMessage(Message msg) {

        }
    };

    public Controller() {
    }

    public void getDelegate(ControllerDelegate delegate) {
        mDelegate = delegate;
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (mDelegate != null) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_DPAD_LEFT:
                    return mDelegate.onDpadLeft(event);
                case KeyEvent.KEYCODE_DPAD_UP:
                    return mDelegate.onDpadUp(event);
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                    return mDelegate.onDpadRight(event);
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    return mDelegate.onDpadDown(event);
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    return mDelegate.onDpadCenter(event);
                case KeyEvent.KEYCODE_BACK:
                    return mDelegate.onBack(event);

            }
        }
        return false;
    }
}
