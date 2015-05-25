package wanba.ott.activity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.AppUtil;
import wanba.ott.util.BitmapCache;
import wanba.ott.view.FocusImageView;

/**
 * 微信相册
 * 
 * @author zhangyus
 *
 */
@SuppressWarnings("deprecation")
public class AlbumActivity extends FullScreenActivity {
	JSONArray jsonArray;

	// Gallery g ;
	// HorizontalScrollView scrollView;
	LinearLayout relative;
	private LinearLayout mUserListContainer;

	private ImageLoader mImageLoader = null;
	private RequestQueue mRequestQueue = null;

	private static class UserItem {
		String id;
		String name;
		String portraitUrl;
	}

	private static class PhotoItem {
		String userId;
		String iconUrl;
		String imageUrl;
		String datetime;
		String userPortraitUrl;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album);

		relative = (LinearLayout) findViewById(R.id.album);

		mUserListContainer = (LinearLayout) findViewById(R.id.weixin_user_list_container);

		mRequestQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());

		// g=(Gallery) findViewById(R.id.gallery1);

		// // 显示进度条
		// AppUtil.showProgress(this, "album", new int[] { R.id.gallery1, });
		// // 加载背景图片
		// AppUtil.getInstance(AlbumActivity.this).loadImageBitmap(
		// findViewById(R.id.album),
		// getIntent().getStringExtra("background"), true);
		// HorizontalScrollView scrollView=new HorizontalScrollView(this);
		// final RequestQueue queue =
		// AppUtil.getInstance(this).getRequestQueue();
		JsonObjectRequest request = new JsonObjectRequest(
				AppUtil.getAferAddArgsUrl(getResources().getString(
						R.string.weixin_pic_json_url)), null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						update(response);
					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Toast.makeText(AlbumActivity.this, "获取数据失败，请检查网路链接",
								Toast.LENGTH_LONG).show();
					}
				});
		AppUtil.getInstance(AlbumActivity.this).getRequestQueue().add(request);

		// this.volleyJson(
		// AppUtil.getAferAddArgsUrl(getResources().getString(
		// R.string.weixin_pic_json_url)),
		//
		// new RefreshView() {
		//
		// @Override
		// public void refresh(JSONObject jsonObject) {
		// try {
		// jsonArray = jsonObject.getJSONArray("photos");
		// String[] urls = new String[jsonArray.length()];
		// final ArrayList<String> listUrls = new ArrayList<String>();
		// for (int i = 0; i < urls.length; i++) {
		// JSONObject obj = jsonArray.getJSONObject(i);
		// urls[i] = (String) obj.getString("img");
		// listUrls.add(urls[i]);
		// }
		// VolleyListAdapter adpter = new VolleyListAdapter(
		// AlbumActivity.this, urls, queue);
		//
		// g.setOnItemClickListener(new OnItemClickListener() {
		//
		// @Override
		// public void onItemClick(AdapterView<?> arg0,
		// View arg1, int arg2, long arg3) {
		// Intent intent = new Intent(
		// AlbumActivity.this,
		// AlbumFullScreenActivity.class);
		// intent.putExtra("position", arg2);
		// intent.putStringArrayListExtra("urls",
		// listUrls);
		// startActivity(intent);
		//
		// }
		//
		// });
		//
		// g.setAdapter(adpter);
		// // g.measure(0, 0);
		// // g.scrollBy(550, 0);
		// // int count=g.getChildCount();
		// // g.setSelection(2, true);
		// g.getPaddingLeft();
		// MarginLayoutParams margin = (MarginLayoutParams) g
		// .getLayoutParams();
		// } catch (JSONException e) {
		// e.printStackTrace();
		// }
		//
		// }
		// });

	}

	/**
	 * 监听 遥控器按0时刷新数据
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_0) {

			// getSharedPreferences("album", 0).edit().clear();
			// 显示进度条
			AppUtil.showProgress(this, "recommend",
					new int[] { R.id.hsview_album });

			JsonObjectRequest request = new JsonObjectRequest(
					AppUtil.getAferAddArgsUrl(getResources().getString(
							R.string.weixin_pic_json_url)), null,
					new Response.Listener<JSONObject>() {
						@Override
						public void onResponse(JSONObject response) {
							relative.removeAllViews();
							try {
								jsonArray = response.getJSONArray("photos");

								String[] urls = new String[jsonArray.length()];
								final ArrayList<String> listUrls = new ArrayList<String>();

								for (int i = 0; i < urls.length; i++) {
									final int position = i;
									JSONObject obj = jsonArray.getJSONObject(i);
									urls[i] = (String) obj.getString("img");
									if(i==0){
										listUrls.add(urls[i]);
									}else{
										listUrls.add(urls[i].replace("icon_", ""));
									}
									FocusImageView imageView = new FocusImageView(
											AlbumActivity.this);

									LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
											LayoutParams.MATCH_PARENT,
											LayoutParams.MATCH_PARENT);
									params.setMargins(20, params.topMargin, 20,
											params.bottomMargin);
									imageView.setPadding(4, 4, 4, 4);
									imageView.setLayoutParams(params);
									imageView
											.setImageResource(R.drawable.logo_gray);
									imageView
											.setBackgroundResource(R.drawable.imageview_shape);
									imageView
											.setScaleType(ScaleType.FIT_CENTER);
									imageView.setFocusable(true);
									imageView.setClickable(true);
									imageView
											.setOnClickListener(new View.OnClickListener() {

												@Override
												public void onClick(View v) {
													Intent intent = new Intent(
															AlbumActivity.this,
															AlbumFullScreenActivity.class);
													intent.putExtra("position",
															position);
													intent.putStringArrayListExtra(
															"urls", listUrls);
													startActivity(intent);
												}
											});

									AppUtil.getInstance(AlbumActivity.this)
											.loadImageBitmap(
													imageView,
													(String) obj
															.getString("img"),
													false);
									relative.addView(imageView);
									// listUrls.add(urls[i]);
								}
								if (relative.getChildAt(0) != null)
									relative.getChildAt(0).requestFocus();
								/*
								 * 重用view 避免重复加载布局
								 */

								// holder.imageView
								// .setLayoutParams(new
								// Gallery.LayoutParams(240,
								// 350));

								// String url = urlArrays[position];

							} catch (JSONException e) {
								e.printStackTrace();
							}

						}
					}, new Response.ErrorListener() {
						@Override
						public void onErrorResponse(VolleyError error) {
							Toast.makeText(AlbumActivity.this,
									"获取数据失败，请检查网路链接", Toast.LENGTH_LONG).show();
						}
					});
			AppUtil.getInstance(AlbumActivity.this).getRequestQueue()
					.add(request);

			// this.volleyJson(
			// AppUtil.getAferAddArgsUrl(getResources().getString(
			// R.string.weixin_pic_json_url)),
			//
			// new RefreshView() {
			//
			// @Override
			// public void refresh(JSONObject jsonObject) {
			// try {
			// jsonArray = jsonObject.getJSONArray("photos");
			// String[] urls = new String[jsonArray.length()];
			// final ArrayList<String> listUrls = new ArrayList<String>();
			// for (int i = 0; i < urls.length; i++) {
			// JSONObject obj = jsonArray.getJSONObject(i);
			// urls[i] = (String) obj.getString("img");
			// listUrls.add(urls[i]);
			// }
			// VolleyListAdapter adpter = new VolleyListAdapter(
			// AlbumActivity.this, urls,
			// AppUtil.getInstance(AlbumActivity.this)
			// .getRequestQueue());
			// Gallery g = (Gallery) findViewById(R.id.gallery1);
			//
			// g.setOnItemClickListener(new OnItemClickListener() {
			//
			// @Override
			// public void onItemClick(
			// AdapterView<?> arg0, View arg1,
			// int arg2, long arg3) {
			// Intent intent = new Intent(
			// AlbumActivity.this,
			// AlbumFullScreenActivity.class);
			// intent.putExtra("position", arg2);
			// intent.putStringArrayListExtra("urls",
			// listUrls);
			// startActivity(intent);
			//
			// }
			//
			// });
			//
			// // g.setSelection(2, true);
			// } catch (JSONException e) {
			// e.printStackTrace();
			// }
			//
			// }
			// });
		}

		return super.onKeyDown(keyCode, event);
	}

	// /**
	// * 复用imageview
	// *
	// * @author chances
	// *
	// */
	// static class ViewHolder {
	// ImageView imageView;
	// }
	//
	// /**
	// * 为微信相册图片下载显示准备的适配器
	// *
	// * @author zhangyus
	// *
	// */
	// class VolleyListAdapter extends BaseAdapter {
	//
	// private static final String TAG = "VolleyListAdapter";
	//
	// private Context mContext;
	// private String[] urlArrays;
	//
	// public VolleyListAdapter(Context context, String[] url,
	// RequestQueue mQueue) {
	// this.mContext = context;
	// urlArrays = url;
	// }
	//
	// @Override
	// public int getCount() {
	// return urlArrays.length;
	// }
	//
	// @Override
	// public Object getItem(int position) {
	// return position;
	// }
	//
	// @Override
	// public long getItemId(int position) {
	// return position;
	// }
	//
	// @Override
	// public View getView(int position, View convertView, ViewGroup parent) {
	// /*
	// * 重用view 避免重复加载布局
	// */
	// ViewHolder holder = null;
	// if (convertView == null) {
	// convertView = LayoutInflater.from(mContext).inflate(
	// R.layout.a_photo_layout, null);
	// holder = new ViewHolder();
	// holder.imageView = (ImageView) convertView;
	// convertView.setTag(holder);
	// } else {
	// holder = (ViewHolder) convertView.getTag();
	// convertView = holder.imageView;
	// }
	// holder.imageView
	// .setLayoutParams(new Gallery.LayoutParams(240, 350));
	//
	// String url = urlArrays[position];
	//
	// return AppUtil.getInstance(AlbumActivity.this).loadImageBitmap(
	// holder.imageView, url, false);
	//
	// }
	//
	// }

	private void update(JSONObject response) {
		relative.removeAllViews();
		mUserListContainer.removeAllViews();
		try {
			JSONArray userList = response.getJSONArray("userList");
			final int userSize = userList.length();
			UserItem[] userItems = null;
			if (userSize > 0) {
				userItems = new UserItem[userSize];
				for (int i = 0; i < userSize; i++) {
					UserItem ui = new UserItem();
					JSONObject userObject = userList.getJSONObject(i);
					ui.id = userObject.getString("userId");
					ui.name = userObject.getString("userName");
					ui.portraitUrl = userObject.getString("portraitUrl");
					userItems[i] = ui;

					View userItemView = LayoutInflater.from(this).inflate(R.layout.weixin_album_user_item, null);
					ImageView userPortraitView = (ImageView) userItemView.findViewById(R.id.weixin_user_portrait);
					TextView userNameView = (TextView) userItemView.findViewById(R.id.weixin_user_name);
					userNameView.setText(ui.name);
					mImageLoader.get(ui.portraitUrl, ImageLoader.getImageListener(
							userPortraitView, R.drawable.logo_gray, R.drawable.logo_gray));

					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
					lp.rightMargin = (int) (16 * getResources().getDisplayMetrics().density);
					mUserListContainer.addView(userItemView, lp);
				}
			}

			jsonArray = response.getJSONArray("photos");
			PhotoItem[] photoItems = null;
			final int photoSize = jsonArray.length();
			if (photoSize > 0) {
				photoItems = new PhotoItem[photoSize];
				String[] urls = new String[photoSize];
				final ArrayList<String> listUrls = new ArrayList<String>();
				for (int i = 0; i < urls.length; i++) {
					final int position = i;
					JSONObject obj = jsonArray.getJSONObject(i);
					PhotoItem pi = new PhotoItem();
					if (obj.has("userId")) {
						pi.userId = obj.getString("userId");
					}
					if (obj.has("datetime")) {
						pi.datetime = obj.getString("datetime");
					}
					if (obj.has("imageUrl")) {
						pi.iconUrl = obj.getString("imageUrl");
					}
					if (!TextUtils.isEmpty(pi.iconUrl)) {
						pi.imageUrl = pi.iconUrl.replace("icon_", "");
					}

					listUrls.add(pi.imageUrl);

					if (userItems != null && userItems.length > 0) {
						final int tempUserSize = userItems.length;
						for (int j = 0; j < tempUserSize; j++) {
							UserItem ui = userItems[j];
							if (ui.id.equals(pi.userId)) {
								pi.userPortraitUrl = ui.portraitUrl;
								break;
							}
						}
					}

					photoItems[i] = pi;

					View imageItemView = LayoutInflater.from(this).inflate(R.layout.weixin_album_image_item, null);
					View focusFrame = imageItemView.findViewById(R.id.weixin_image_focus_frame);
					ImageView imageView = (ImageView) imageItemView.findViewById(R.id.weixin_image);
					if (position > 0) {
						focusFrame.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								Intent intent = new Intent(
										AlbumActivity.this,
										AlbumFullScreenActivity.class);
								intent.putExtra("position",
										position);
								intent.putStringArrayListExtra(
										"urls", listUrls);
								startActivity(intent);
							}
						});
					} else {
						imageView.setFocusable(false);
						focusFrame.setFocusable(false);
						focusFrame.setVisibility(View.GONE);
					}
					ImageView imageUserPortraitView = (ImageView) imageItemView.findViewById(R.id.weixin_image_user_portrait);
					TextView datetimeView = (TextView) imageItemView.findViewById(R.id.weixin_image_datetime);

					mImageLoader.get(pi.iconUrl, ImageLoader.getImageListener(
							imageView, R.drawable.logo_gray, R.drawable.logo_gray));

					if (!TextUtils.isEmpty(pi.userPortraitUrl)) {
						mImageLoader.get(pi.userPortraitUrl, ImageLoader.getImageListener(
								imageUserPortraitView, R.drawable.logo_gray, R.drawable.logo_gray));
						datetimeView.setText(pi.datetime);
					} else {
						((View) imageUserPortraitView.getParent()).setVisibility(View.GONE);
					}

					LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
							LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.MATCH_PARENT);
					final int margin = (int) (8 * getResources().getDisplayMetrics().density);
					lp.setMargins(margin, margin, margin, margin);

					relative.addView(imageItemView, lp);

//					urls[i] = (String) obj.getString("img");
//					if (i == 0) {
//						listUrls.add(urls[i]);
//					} else {
//						listUrls.add(urls[i].replace("icon_", ""));
//					}
//					FocusImageView imageView = new FocusImageView(
//							AlbumActivity.this);
//
//					LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
//							LayoutParams.MATCH_PARENT,
//							LayoutParams.MATCH_PARENT);
//					params.setMargins(10, params.topMargin, 10,
//							params.bottomMargin);
//					imageView.setPadding(4, 4, 4, 4);
//					imageView.setLayoutParams(params);
//					imageView
//							.setImageResource(R.drawable.logo_gray);
//					imageView
//							.setBackgroundResource(R.drawable.imageview_shape);
//					imageView.setScaleType(ScaleType.FIT_CENTER);
//					imageView.setAdjustViewBounds(true);
//					imageView
//							.setOnClickListener(new View.OnClickListener() {
//
//								@Override
//								public void onClick(View v) {
//									Intent intent = new Intent(
//											AlbumActivity.this,
//											AlbumFullScreenActivity.class);
//									intent.putExtra("position",
//											position);
//									intent.putStringArrayListExtra(
//											"urls", listUrls);
//									startActivity(intent);
//								}
//							});

//					AppUtil.getInstance(AlbumActivity.this)
//							.loadImageBitmap(imageView,
//									(String) obj.getString("img"),
//									false);
//					relative.addView(imageView);
					// listUrls.add(urls[i]);
				}
			}
			if (relative.getChildAt(1) != null)
				relative.getChildAt(1).requestFocus();
							/*
							 * 重用view 避免重复加载布局
							 */

			// holder.imageView
			// .setLayoutParams(new Gallery.LayoutParams(240,
			// 350));

			// String url = urlArrays[position];

		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

}
