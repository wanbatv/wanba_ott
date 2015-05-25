package wanba.ott.activity;


import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import wanba.ott.activity.R;
import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.AppUtil;
import android.net.Uri;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.Toast;
import android.widget.VideoView;

/**
 * MikeLing 页
 * 
 * @author zhangyus
 *
 */
public class MikeLingActivity extends FullScreenActivity {
	VideoView mVideoView;
	MediaController mc;
	OpenActivityAction openActivityAction;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mike_ling);
//		LinearLayout linear = (LinearLayout) findViewById(R.id.LinearLayout1);

		// // 显示进度条
		// AppUtil.showProgress(this, "mikeling",
		// new int[] { R.id.LinearLayout1, });
		//
		//
		// // 加载背景图片
		// AppUtil.getInstance(MikeLingActivity.this).loadImageBitmap(linear,
		// getIntent().getStringExtra("background"), true);

		String cateCode = getIntent().getStringExtra("cate_code");
		String url = AppUtil.getAferAddCateArgsUrl(
				getString(R.string.ott_content), cateCode);
		JsonObjectRequest request = new JsonObjectRequest(url, null,

		new Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject response) {
				if (response != null) {
					try {
						JSONArray array = response.getJSONArray("results");
						JSONArray currDataArray = new JSONArray();
						for (int i = 0; i < array.length(); i++) {
							if (array.getJSONObject(i).getInt("code") == getIntent()
									.getIntExtra("index", 0)) {
								currDataArray.put(array.getJSONObject(i));
							}
						}
						ViewGroup grid = (ViewGroup) findViewById(R.id.ml_grid);

						for (int i = 0; i < grid.getChildCount(); i++) {
							// AppUtil.getInstance(MikeLingActivity.this)
							// .loadImageBitmap(
							// grid.getChildAt(i),
							// currDataArray.getJSONObject(i)
							// .getString("icon"), false);
							grid.getChildAt(i)
									.setOnClickListener(
											new OpenActivityAction(
													MikeLingActivity.this,
													SimplePlayerActivity.class,
													AppUtil.toMap(
															MikeLingActivity.this,
															currDataArray
																	.getJSONObject(i))));

						}
						playVod(currDataArray.getJSONObject(0).optString(
								"play_url", getString(R.string.mikeling_url)));

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(MikeLingActivity.this, "读取数据失败,请检查网络连接！",
						Toast.LENGTH_LONG).show();
			}
		});
		AppUtil.getInstance(MikeLingActivity.this).getRequestQueue()
				.add(request);
		// ViewGroup viewGroup = (ViewGroup) findViewById(R.id.ml_grid);
		//
		// super.bindOnClickListener(viewGroup, openActivityAction);
		// playVod();
	}

	protected void playVod(String dataPath) {
		try {
			mVideoView = (VideoView) findViewById(R.id.simple_video_view);
			// String dataPath =
			// getResources().getString(R.string.mikeling_url);
			mVideoView.requestFocus();
			mVideoView.setVideoURI(Uri.parse(dataPath));
			mVideoView.start();
		} catch (Exception e) {
		}
	}
}
