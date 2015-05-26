package wanba.ott.activity;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.volley.VolleyError;
import com.android.volley.Response.ErrorListener;
import com.android.volley.Response.Listener;
import com.android.volley.toolbox.JsonObjectRequest;

import wanba.ott.activity.R;
import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.AppUtil;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

/**
 * BBtiao 页
 * 
 * @author zhangyus
 *
 */
public class BBTiaoActivity extends FullScreenActivity {
	private TextView bb_textView0;
	private TextView bb_textView1;
	private TextView bb_textView2;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bbtiao);

		// 显示进度条
		// AppUtil.showProgress(this, "bbtiao", new int[] { R.id.bbtiao_layout,
		// });

		// // 加载背景图片
		// AppUtil.getInstance(BBTiaoActivity.this).loadImageBitmap(
		// findViewById(R.id.bbtiao_layout),
		// getIntent().getStringExtra("background"), true);

		bb_textView0 = (TextView) findViewById(R.id.bb_textView0);
		bb_textView1 = (TextView) findViewById(R.id.bb_textView1);
		bb_textView2 = (TextView) findViewById(R.id.bb_textView2);
		// 加载用户数据
		bb_textView0.setText(getSharedPreferences("userinfo", 0).getString(
				"user_id", ""));
		bb_textView1
				.setText(getSharedPreferences("userinfo", 0).getInt(
						"total_time", 0)
						/ 60
						+ "小时"
						+ (int) (getSharedPreferences("userinfo", 0).getInt(
								"total_time", 0) / 60.00 - getSharedPreferences(
								"userinfo", 0).getInt("total_time", 0) / 60)
						* 60 + "分");

		bb_textView2.setText(getSharedPreferences("userinfo", 0).getInt(
				"total_calorie", 0)
				+ "大卡");

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
						ViewGroup grid = (ViewGroup) findViewById(R.id.photo_layout);

						for (int i = 0; i < grid.getChildCount(); i++) {
//							AppUtil.getInstance(BBTiaoActivity.this)
//									.loadImageBitmap(
//											grid.getChildAt(i),
//											currDataArray.getJSONObject(i)
//													.getString("icon"), false);
							grid.getChildAt(i).setOnClickListener(
									new OpenActivityAction(BBTiaoActivity.this,
											SimplePlayerActivity.class,
											AppUtil.toMap(BBTiaoActivity.this,currDataArray
													.getJSONObject(i))));
						}

					} catch (JSONException e) {
						e.printStackTrace();
					}

				}
			}

		}, new ErrorListener() {

			@Override
			public void onErrorResponse(VolleyError arg0) {
				Toast.makeText(BBTiaoActivity.this, "读取数据失败,请检查网络连接！",
						Toast.LENGTH_LONG).show();
			}
		});
		AppUtil.getInstance(BBTiaoActivity.this).getRequestQueue().add(request);

		// ViewGroup viewGroup = (ViewGroup) findViewById(R.id.photo_layout);
		// super.bindOnClickListener(viewGroup, new OpenActivityAction(this,
		// SimplePlayerActivity.class));
	}

}
