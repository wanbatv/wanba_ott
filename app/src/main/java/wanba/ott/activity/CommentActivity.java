package wanba.ott.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wanba.ott.activity.R;
import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.AppUtil.STB;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class CommentActivity extends FullScreenActivity {
	SimpleAdapter adapter;
	ListView listView;
	ArrayList<Map<String, Object>> mData = new ArrayList<Map<String, Object>>();
	final String[] attrNames = new String[] { "username", "public_date",
			"content" };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_comment);
		listView = (ListView) findViewById(R.id.comm_listview);

		super.volleyJson(getResources().getString(R.string.ott_content)
						+"?mac="
						+STB.INSTANCE.Mac 
						+"&sn="
						+STB.INSTANCE.Sn 
						+"&ip="
						+STB.INSTANCE.IP 
						+"&userid="
						+STB.INSTANCE.UserID 
						+"&model="
						+STB.INSTANCE.Model 
						+"&group="
						+STB.INSTANCE.UserGroup ,
				new RefreshView() {

					@Override
					public void refresh(JSONObject jsonObject) {
						mData.clear();
						try {
							JSONArray jsonArray = jsonObject
									.getJSONArray("results");
							for (int i = 0; i < 5; i++) {
								JSONObject obj = (JSONObject) jsonArray.get(i);
								HashMap<String, Object> value = new HashMap<String, Object>();
								for (String attrName : attrNames) {
									value.put(attrName, obj.get(attrName));
								}
								mData.add(value);

							}
						} catch (JSONException e) {
							e.printStackTrace();
						}
						adapter = new SimpleAdapter(CommentActivity.this,
								mData, R.layout.comment_layout, attrNames,
								new int[] { R.id.c_list_username,
										R.id.c_list_time, R.id.c_list_content });
						listView.setAdapter(adapter);

					}
				});
	}
}
