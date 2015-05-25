package wanba.ott.abstracts.activity;

import java.io.File;

import org.json.JSONObject;

import wanba.ott.activity.OpenActivityAction;
import wanba.ott.util.AppUtil;
import wanba.ott.util.AppUtil.STB;
import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.VideoView;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

/**
 * app的基类 1.设置整个app的一些界面方面的参数 2.初始化一些app功能性方面的对象。
 * 
 * @author zhangyus
 *
 */
public abstract class FullScreenActivity extends Activity {
	protected OpenActivityAction playAction = null;
	protected OpenActivityAction cardAction = null;
	protected OpenActivityAction commAction = null;
	
	
	// protected ResponseListener[] bitmaps;
	/**
	 * 用于获取json数据之后的回调
	 * 
	 * @author zhangyus
	 *
	 */
	public interface RefreshView {

		public void refresh(JSONObject jsonObject);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// 获取用户和盒子的一些唯一鉴权数据
		STB.INSTANCE.Mac = AppUtil.getValue("STB.Mac", this);
		STB.INSTANCE.Sn = AppUtil.getValue("STB.Sn", this);
		STB.INSTANCE.IP = AppUtil.getValue("STB.IP", this);
		STB.INSTANCE.UserID = AppUtil.getValue("STB.UserID", this);
		STB.INSTANCE.Model = AppUtil.getValue("STB.Model", this);
		STB.INSTANCE.UserGroup = AppUtil.getValue("STB.UserGroup", this);
		
		STB.INSTANCE.UserID = TextUtils.isEmpty(STB.INSTANCE.UserID) ? "00000"
				: STB.INSTANCE.UserID;

		Log.e("FullScreenActivity", STB.INSTANCE.Mac);
		Log.e("FullScreenActivity", STB.INSTANCE.Sn);
		Log.e("FullScreenActivity", STB.INSTANCE.IP);
		Log.e("FullScreenActivity", STB.INSTANCE.UserID);
		Log.e("FullScreenActivity", STB.INSTANCE.Model);
		Log.e("FullScreenActivity", STB.INSTANCE.UserGroup);

		// 检查磁盘缓存文件夹是否存在
		File cacheDir = AppUtil.getDiskCacheDir(FullScreenActivity.this,
				AppUtil.DISK_CACHE_FILENAME);
		if (!cacheDir.exists()) {
			cacheDir.mkdirs();
		}
		this.requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
				WindowManager.LayoutParams.FLAG_FULLSCREEN);

		// playAction = new OpenActivityAction(this,
		// SimplePlayerActivity.class);
		// cardAction = new OpenActivityAction(this, UserCenterActivity.class);
		// commAction = new OpenActivityAction(this, CommentActivity.class);

	}

	public int findIndex(ViewGroup aGroup, View view) {
		int count = aGroup.getChildCount();
		for (int i = 0; i < count; i++) {
			View obj = aGroup.getChildAt(i);
			if (obj.equals(view)) {
				return i;
			}
		}
		return -1;
	}

	public void bindOnClickListener(ViewGroup group,
			View.OnClickListener listener) {
		if (group != null) {
			int count = group.getChildCount();
			for (int i = 0; i < count; i++) {
				View obj = group.getChildAt(i);
				obj.setOnClickListener(listener);
			}
		}
	}

	public void bindOnFocusListener(ViewGroup group,
			View.OnFocusChangeListener listener) {
		int count = group.getChildCount();
		for (int i = 0; i < count; i++) {
			View obj = group.getChildAt(i);
			obj.setOnFocusChangeListener(listener);
		}
	}

	protected void playVod(int id, String url) {
		try {
			VideoView mVideoView = (VideoView) findViewById(id);
			mVideoView.setVideoURI(Uri.parse(url));
			mVideoView.start();
		} catch (Exception e) {
		}
	}

	/**
	 * 访问url获取json数据 设置回调接口
	 * 
	 * @param url
	 * @param refershView
	 */
	protected void volleyJson(String url, final RefreshView refershView) {
		JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						refershView.refresh(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
					}
				});

		// 将 请求放入 请求队列
		AppUtil.getInstance(this).getRequestQueue().add(mJsonObjectRequest);

	}

}
