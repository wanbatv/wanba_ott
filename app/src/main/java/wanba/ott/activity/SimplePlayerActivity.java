package wanba.ott.activity;

import android.app.ProgressDialog;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import wanba.ott.abstracts.activity.FullScreenActivity;

/**
 * 全屏播放器
 * 
 * @author zhangyus
 *
 */
public class SimplePlayerActivity extends FullScreenActivity implements
		OnPreparedListener, OnInfoListener {
	MediaController mc;
	VideoView mVideoView;
	private long exitTime;
	ProgressDialog dialog2;

	String appName;
	String loading;
	String exitInfo;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_simple_player);
		appName = getResources().getString(R.string.app_name);
		loading = getResources().getString(R.string.info_loading);
		exitInfo = getResources().getString(R.string.info_exit);
		
		dialog2 = ProgressDialog.show(this, appName, this.loading);
		dialog2.setIcon(R.drawable.ic_launcher);
		try {
			mVideoView = (VideoView) findViewById(R.id.simple_video_view);
			mc = new MediaController(this);
			mVideoView.setMediaController(mc);
			mVideoView.requestFocus();
			String playUrl = this.getIntent().getStringExtra("play_url");
			if (playUrl == null) {
				playUrl = getString(R.string.mikeling_url);
			}
			mVideoView.setVideoURI(Uri.parse(playUrl));
			mc.show();
			mVideoView.start();
			jsonReport(getResources().getString(R.string.start_report_url));
			mVideoView.setOnPreparedListener(this);
		} catch (Exception e) {

		}
	}

	/**
	 * 向指定的服务器报告视频播放信息
	 * 暂时没有真正发送请求 
	 * 如果需要请将加入队列的代码注释去掉
	 */
	public void jsonReport(String url) {
		JSONObject jsonRequest = new JSONObject();
		try {
			jsonRequest.put("cate_code", getIntent()
					.getStringExtra("cate_code"));
			jsonRequest.put("media_code",
					getIntent().getStringExtra("media_code"));
			jsonRequest.put("play_url", getIntent().getStringExtra("play_url"));

		} catch (JSONException e) {
			e.printStackTrace();
		}
		JsonObjectRequest request = new JsonObjectRequest(url, jsonRequest,
				null, null);
		// AppUtil.getInstance(SimplePlayerActivity.this).getRequestQueue()
		// .add(request);

	}

	public void exitPlay() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(this, exitInfo, Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {

			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// 判断2秒内是否重复点击返回键
			if (System.currentTimeMillis() - exitTime > 2000) {
				System.out.println(Toast.LENGTH_LONG);
				Toast.makeText(this, R.string.info_exit, Toast.LENGTH_LONG)
						.show();
				exitTime = System.currentTimeMillis();
			} else {
				jsonReport(getResources().getString(R.string.end_report_url));
				mVideoView = null;
				finish();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		dialog2.cancel();
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		return true;
	}

}
