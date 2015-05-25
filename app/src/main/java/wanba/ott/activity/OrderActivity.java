package wanba.ott.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import wanba.ott.abstracts.activity.FullScreenActivity;

/**
 * Created by Forcs on 15/5/13.
 */
public class OrderActivity extends FullScreenActivity {

    public static final int REQUEST_CODE_RESULT = 1000;

    public static final String EXTRA_ORDER_URL = "order_url";
    public static final String EXTRA_ORDER_ARGUMENT = "order_argument";

    private WebView mWbvOrderPage = null;

    private Bundle mUserArgument = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        mWbvOrderPage = (WebView) findViewById(R.id.order_page);
        mWbvOrderPage.getSettings().setJavaScriptEnabled(true);
        mWbvOrderPage.addJavascriptInterface(new JsObject(), "orderHandle");
        mWbvOrderPage.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (event.getKeyCode() == KeyEvent.KEYCODE_BACK) {
                        Intent data = new Intent();
                        data.putExtra(EXTRA_ORDER_ARGUMENT, mUserArgument);
                        setResult(-1, data);
                        finish();
                    } else {
                        mWbvOrderPage.loadUrl("javascript:orderKeyHandle(" + keyCode + ");");
                    }
                    return true;
                }
                return false;
            }
        });

        mWbvOrderPage.setWebViewClient(new MyWebViewClient());

        Intent intent = getIntent();
        String url = null;
        if (intent != null) {
            url = intent.getStringExtra(EXTRA_ORDER_URL);
            mUserArgument = intent.getBundleExtra(EXTRA_ORDER_ARGUMENT);
        }

        if (!TextUtils.isEmpty(url)) {
            mWbvOrderPage.loadUrl(url);
        }

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    private class MyWebViewClient extends WebViewClient {

        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            super.onPageStarted(view, url, favicon);
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
        }
    }

    public class JsObject {

        @JavascriptInterface
        public void orderResult(final String resultCode, final String resultUrl) {
            Log.d("OrderActivity", "@@@ orderResult code:" + resultCode + ", url:" + resultUrl);
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    final int rc = Integer.parseInt(resultCode);
                    Intent data = new Intent();
                    data.putExtra(EXTRA_ORDER_ARGUMENT, mUserArgument);
                    setResult(rc, data);
                    finish();
                }
            });
        }
    }
}
