package wanba.ott.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.AppUtil;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.TextView;

/**
 * 瑜伽 item
 * 
 * @author zhangyus
 *
 */
public class YujiaActivity extends FullScreenActivity {
	// 用来存放显示 4个imageView选择框
	GridView gridview;

	// 存放从服务器读取的title数据
	private List<String> titles = new ArrayList<String>();
	private TextView myj_textView1;
	private TextView myj_textView2;
	private TextView myj_textView3;

	JSONArray currDataArray = new JSONArray();

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		 int[] bgid = new int[] {
		
		 R.drawable.bg_4, R.drawable.bg_5, R.drawable.bg_6, R.drawable.bg_7
		
		 };
		setContentView(R.layout.activity_yujia);
		View mainView = findViewById(R.id.main_h_layout);
		
		// 根据json中定义的code值来对应哪个栏目
		switch (getIntent().getIntExtra("index", 1)) {
		case 1:
//			AppUtil.showProgress(this, "guangchangwu",
//					new int[] { R.id.main_h_layout });
			mainView.setBackgroundResource(bgid[1]);
			break;
		case 3:
//			AppUtil.showProgress(this, "jianshencao",
//					new int[] { R.id.main_h_layout });
			mainView.setBackgroundResource(bgid[2]);
			break;
		case 5:
//			AppUtil.showProgress(this, "dupiwu",
//					new int[] { R.id.main_h_layout });
			mainView.setBackgroundResource(bgid[3]);
			break;
		case 6:
//			AppUtil.showProgress(this, "yujia",
//					new int[] { R.id.main_h_layout });
			mainView.setBackgroundResource(bgid[0]);
			break;
		}

		myj_textView1 = (TextView) findViewById(R.id.yj_textView1);
		myj_textView2 = (TextView) findViewById(R.id.yj_textView2);
		myj_textView3 = (TextView) findViewById(R.id.yj_textView3);

		myj_textView1.setText(getSharedPreferences("userinfo", 0).getString(
				"user_id", ""));
		myj_textView2
				.setText(getSharedPreferences("userinfo", 0).getInt(
						"total_time", 0)
						/ 60
						+ "小时"
						+ (int) (getSharedPreferences("userinfo", 0).getInt(
								"total_time", 0) / 60.00 - getSharedPreferences(
								"userinfo", 0).getInt("total_time", 0) / 60)
						* 60 + "分");

		myj_textView3.setText(getSharedPreferences("userinfo", 0).getInt(
				"total_calorie", 0)
				+ "大卡");


//		// 加载背景图片
//		AppUtil.getInstance(YujiaActivity.this).loadImageBitmap(mainView,
//				getIntent().getStringExtra("background"), true);

		gridview = (GridView) findViewById(R.id.yj_gridView);
		gridview.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				try {
					new OpenActivityAction(YujiaActivity.this,
							SimplePlayerActivity.class, AppUtil.toMap(YujiaActivity.this,currDataArray
									.getJSONObject(position))).onClick(view);
				} catch (JSONException e) {
					e.printStackTrace();
				}

			}
		});

		init(getIntent().getIntExtra("index", 0));

		// super.playVod(R.id.yj_trailer, getString(R.string.play_url));
	}

	

	public void init(int idx) {
		// + "?id=" + idx

		String cateCode = getIntent().getStringExtra("cate_code");
		String url = AppUtil.getAferAddCateArgsUrl(
				getString(R.string.ott_content), cateCode);
		super.volleyJson(url, new RefreshView() {

			@Override
			public void refresh(JSONObject jsonObject) {
				titles.clear();
				try {
					JSONArray jsonArray = jsonObject.getJSONArray("results");
					JSONObject jsonObj = null;
					for (int i = 0; i < jsonArray.length(); i++) {
						jsonObj = jsonArray.getJSONObject(i);
						if (jsonObj.getInt("code") == getIntent().getIntExtra(
								"index", 1)) {
							currDataArray.put(jsonObj);
						}
					}
					// json解析完毕页面开始播放视频数据
					playVod(R.id.yj_trailer,
							currDataArray.getJSONObject(0).optString(
									"play_url", getString(R.string.play_url)));
					for (int i = 0; i < 10; i++) {
						if (i < currDataArray.length()) {
							JSONObject obj = (JSONObject) currDataArray.get(i);
							titles.add(obj.getString("title"));
						}
					}
				} catch (JSONException e) {

					e.printStackTrace();
				}
				ArrayAdapter<String> saImageItems = new ArrayAdapter(
						YujiaActivity.this, R.layout.text_line_layout, titles);
				gridview.setAdapter(saImageItems);

			}
		});
	}

}
