package wanba.ott.activity;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.broadcast.DownloadTaskReceiver;
import wanba.ott.entity.DownloadTask;
import wanba.ott.util.AppUtil;
import wanba.ott.util.ProductInfo;
import wanba.ott.util.UrlUtils;
import wanba.ott.view.FocusImageButtonView;
import wanba.ott.view.FocusImageView;

/**
 * app启动入口
 * 
 * @author zhangyus
 *
 */
public class MainActivity extends FullScreenActivity {

	private static final String TAG = MainActivity.class.getName();

	// apk下载广播接收器
	DownloadTaskReceiver receiver;
	// 用于加载主页面下方的具体内容切换
	private ViewPager viewPager;
	private ArrayList<View> viewContainer;
	private ArrayList<TextView> textViews;
	private TextView recoTextView;
	private TextView sportTextView;
	private TextView personTextView;

	private FocusImageButtonView dailyCard;
	private FocusImageButtonView moreRecord;

	ViewGroup relative;

	// 首页标题下的下划线动画图片
	private ImageView cursor;
	// 移动的距离
	private int offset = 0;
	// 当前页的下标
	private int currIndex = 0;
	// 动画图片的宽度
	private int cursorDrawableWidth = 0;

	private long exitTime;

	boolean isRecommendSendRequest;
	boolean isAllSportsSendRequest;

	boolean isSendUserRequest;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		// final View view = findViewById(R.id.main_h_layout);
		// view.setBackgroundResource(R.drawable.loading);

		int defaultLoadingTime = 5000;
		// 启动时先显示一个loading的进度条
		final ProgressDialog dialog2 = ProgressDialog.show(this,
				this.getString(R.string.app_name),
				this.getString(R.string.loading_text), false, false);
//		dialog2.setIcon(R.drawable.ic_launcher);
		dialog2.setContentView(R.layout.loading);
		// 判断是否第一次进入context界面
		boolean isEnter = getSharedPreferences("main", 0).getBoolean("isEnter",
				false);
		// 不是第一次则将loading时间降低为2000ms
		defaultLoadingTime = isEnter ? 2000 : defaultLoadingTime;

		getWindow().getDecorView().postDelayed(new Runnable() {

			@Override
			public void run() {

				dialog2.dismiss();
				// view.setBackgroundResource(R.drawable.bg);
				// findViewById(R.id.ma).setVisibility(View.VISIBLE);
				// findViewById(R.id.albumVPager).setVisibility(View.VISIBLE);
				getSharedPreferences("main", 0).edit()
						.putBoolean("isEnter", true).commit();

			}
		}, defaultLoadingTime);
		init();

		initProductInfo();
	}

	private void initProductInfo() {
		RequestQueue rq = Volley.newRequestQueue(this);
		JsonObjectRequest request = new JsonObjectRequest(UrlUtils.URL_PRODUCT_INFO, null,
				new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				ProductInfo.getInstance().init(jsonObject);
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}
		});
		rq.add(request);
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (!getSharedPreferences("apk_update", 0).getBoolean("is_update",
				false)) {
			if (receiver != null) {
				this.unregisterReceiver(receiver);
			}
		}
	}

	@Override
	protected void onResume() {
		super.onResume();
		// 检查最新的apk版本并提示是否下载
		if (!getSharedPreferences("apk_update", 0).getBoolean("is_update",
				false)) {
			getSharedPreferences("apk_update", 0).edit()
					.putBoolean("is_update", true).commit();
			updateAPK(getResources().getString(R.string.apk_update_json_url),
					AppUtil.getAppVersion(this));

		}

	}

	/**
	 * 更新apk
	 * 
	 * @param url
	 */
	private void updateAPK(String url, final int currVersion) {
		receiver = new DownloadTaskReceiver();
		JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(url, null,
				new Response.Listener<JSONObject>() {
					@Override
					public void onResponse(JSONObject response) {
						try {
							// 版本是否更新 0否 1是
							if (response.getInt("version_code") > currVersion) {
								// 获取apk下载地址
								final String apkUpdateUrl = response
										.getString("apk_download_url");
								// 是否强制更新 0否 1是
								int isForceUpdate = response
										.getInt("is_force_update");

								AlertDialog.Builder b2 = new AlertDialog.Builder(
										MainActivity.this)
										.setTitle("提示")
										.setMessage("升级到最新版本")
										.setPositiveButton(
												"确定",
												new AlertDialog.OnClickListener() {
													public void onClick(
															DialogInterface dialog,
															int which) {
														Toast.makeText(
																MainActivity.this,
																"开始下载！",
																Toast.LENGTH_LONG)
																.show();
														download("",
																apkUpdateUrl,
																"Wanba2015",
																"", 0);
													}
												});

								if (isForceUpdate == 0) {
									b2.setNegativeButton("取消",
											new AlertDialog.OnClickListener() {

												@Override
												public void onClick(
														DialogInterface dialog,
														int which) {
													dialog.dismiss();
												}
											});
									b2.setCancelable(true);
								} else {
									b2.setCancelable(false);
								}
								b2.create();
								b2.show();
							}

						} catch (NumberFormatException e) {
							e.printStackTrace();
						} catch (JSONException e) {
							e.printStackTrace();
						}

					}
				}, new Response.ErrorListener() {
					@Override
					public void onErrorResponse(VolleyError error) {
						Log.e("apk更新", error.getMessage());
						Toast.makeText(MainActivity.this, "更新访问失败！",
								Toast.LENGTH_LONG).show();
					}
				});

		// 将 请求放入 请求队列
		AppUtil.getInstance(this).getRequestQueue().add(mJsonObjectRequest);

	}

	/**
	 * 下载app具体操作
	 * 
	 * @param appId
	 * @param downloadUrl
	 * @param appName
	 * @param appDesc
	 * @param appSize
	 */
	public void download(String appId, String downloadUrl, String appName,
			String appDesc, long appSize) {
		// 初始化DownloadTask实例
		DownloadTask aDownloadTask = new DownloadTask(appId,
				Uri.parse(downloadUrl), appName, appDesc, appSize);
		// 实例化DownloadManager.Request
		DownloadManager.Request request = new Request(
				aDownloadTask.getDownloadURI());
		// 系统下载路径
		request.setDestinationInExternalFilesDir(MainActivity.this,
				Environment.DIRECTORY_DOWNLOADS, appName + ".apk");

		// request.setShowRunningNotification(true);
		// 向队列加载一个新的下载项
		DownloadManager downloadManager = ((DownloadManager) MainActivity.this
				.getSystemService(Activity.DOWNLOAD_SERVICE));
		long downloadId = downloadManager.enqueue(request);
		// 保存downloadId
		aDownloadTask.setDownloadId(downloadId);
		// 保存DownloadTask对象
		// tasks.add(aDownloadTask);
		// 过滤器 当前下载任务完成
		IntentFilter filter = new IntentFilter(
				DownloadManager.ACTION_DOWNLOAD_COMPLETE);
		// 注册并监听过滤器
		MainActivity.this.registerReceiver(receiver, filter);

	}

	/**
	 * 初始化
	 */
	private void init() {
		viewPager = (ViewPager) findViewById(R.id.vPager);
		viewContainer = new ArrayList<View>();
		textViews = new ArrayList<TextView>();

		LayoutInflater inflater = getLayoutInflater();
		viewContainer.add(inflater.inflate(R.layout.main_recommend, null));
		viewContainer.add(inflater.inflate(R.layout.main_allsports, null));
		viewContainer.add(inflater.inflate(R.layout.main_person_center, null));

		recoTextView = (TextView) findViewById(R.id.reco_text);
		sportTextView = (TextView) findViewById(R.id.sports_text);
		personTextView = (TextView) findViewById(R.id.person_text);

		textViews.add(recoTextView);
		textViews.add(sportTextView);
		textViews.add(personTextView);

		recoTextView.setOnFocusChangeListener(textViewListener);
		sportTextView.setOnFocusChangeListener(textViewListener);
		personTextView.setOnFocusChangeListener(textViewListener);

		cursor = (ImageView) findViewById(R.id.cursor);
		cursorDrawableWidth = BitmapFactory.decodeResource(getResources(),
				R.drawable.line).getWidth();

		// 计算首页下划线动画需要移动的距离
		// 为两个textview的间隔
		offset = sportTextView.getLeft() - recoTextView.getLeft();
		Matrix matrix = new Matrix();
		matrix.postTranslate(cursor.getX(), 0);
		cursor.setImageMatrix(matrix);

		viewPager.setAdapter(new MainPagerAdapter(viewContainer));
		viewPager.setOnPageChangeListener(new OnPageChangeListener() {

			@Override
			public void onPageSelected(int position) {

				// 实际移动的距离是两个textview的间隔加上图片的长度*2
				int one = offset + cursorDrawableWidth * 2;

				Animation animation = new TranslateAnimation(one * currIndex,
						one * position, 0, 0);
				currIndex = position;
				// True:图片停在动画结束位置
				animation.setFillAfter(true);
				animation.setDuration(300);
				cursor.startAnimation(animation);

				for (int i = 0; i < textViews.size(); i++) {
					if (position == i) {
						textViews.get(i).setTextSize(38f);
						textViews.get(i).setAlpha(1.0f);
					} else {
						textViews.get(i).setTextSize(30.5f);
						textViews.get(i).setAlpha(0.6f);
					}
				}
				if (position == 1 && !isAllSportsSendRequest) {
					JsonArrayRequest mViewImageArrayRequest = new JsonArrayRequest(
							AppUtil.getAferAddArgsUrl(getResources().getString(
									R.string.main_json_url)),
							new Response.Listener<JSONArray>() {

								@Override
								public void onResponse(JSONArray array) {
									try {
										if (array == null)
											return;

										for (int i = 0; i < array.length(); i++) {
											if (!array.isNull(i)) {
												JSONObject response = array
														.getJSONObject(i);

												String page_code = response
														.getString("page_code");
												JSONArray items = response
														.getJSONArray("items");
												if ("sports".equals(page_code)) {

													ViewGroup linear = (ViewGroup) findViewById(R.id.ll2);
													if (linear != null) {

														for (int z = 0; z < linear
																.getChildCount(); z++) {

															JSONArray mImageJsonArray = items
																	.getJSONArray(z);

															ViewGroup relative = (ViewGroup) linear
																	.getChildAt(z);
															for (int a = 0; a < mImageJsonArray
																	.length(); a++) {

																JSONObject mImageJson = mImageJsonArray
																		.getJSONObject(a);

																if (mImageJson != null) {

																	FocusImageView image = (FocusImageView) relative
																			.getChildAt(a * 2);
																	// ImageView
																	// image =
																	// (ImageView)
																	// relative
																	// .getChildAt(a
																	// * 2);

																	TextView textView = (TextView) relative
																			.getChildAt(a * 2 + 1);
																	textView.setText(mImageJson
																			.getString("title"));

																	image.setOnClickListener(new MyClickListener(
																			mImageJson
																					.getInt("code"),
																			mImageJson
																					.getInt("cate_code"),
																			mImageJson
																					.getString("icon"),
																			mImageJson
																					.getString("type"),
																			mImageJson
																					.getString("link"),
																			mImageJson
																					.getString("title"),
																			mImageJson
																					.getString("background")));
																	AppUtil.getInstance(
																			MainActivity.this)
																			.loadImageBitmap(
																					image,
																					mImageJson
																							.getString("icon"),
																					false);
																	// if (image
																	// .getOnFocusChangeListener()
																	// == null)
																	//
																	// image.setOnFocusChangeListener(new
																	// MainActivity.FocusChange(textView)
																	// );
																}
															}
														}

													}

												}

											}

										}
										isAllSportsSendRequest = true;
									} catch (JSONException e) {
										e.printStackTrace();
									}
								}

							}, new Response.ErrorListener() {
								@Override
								public void onErrorResponse(VolleyError error) {
									Toast.makeText(MainActivity.this,
											"获取数据失败，请检查网路链接", Toast.LENGTH_LONG)
											.show();
									isAllSportsSendRequest = false;
								}
							});
					AppUtil.getInstance(MainActivity.this).getRequestQueue()
							.add(mViewImageArrayRequest);

				}

				// 取第三页 用户中心的数据
				if (position == 2) {
					SharedPreferences userinfo = getSharedPreferences(
							"userinfo", 0);
					TextView prev_week_hour = (TextView) findViewById(R.id.prev_week_hour);
					TextView prev_week_minute = (TextView) findViewById(R.id.prev_week_minute);
					TextView prev_calorie = (TextView) findViewById(R.id.prev_calorie);
					TextView last_week_hours = (TextView) findViewById(R.id.last_week_hours);
					TextView last_week_calorie = (TextView) findViewById(R.id.last_week_calorie);
					TextView rank = (TextView) findViewById(R.id.rank);
					TextView info = (TextView) findViewById(R.id.info);
					prev_week_hour.setText(userinfo.getInt("curr_time", 0) / 60
							+ "");
					prev_week_minute.setText((int) ((userinfo.getInt(
							"curr_time", 0) / 60.00 - userinfo.getInt(
							"curr_time", 0) / 60) * 60)
							+ "");
					prev_calorie.setText(userinfo.getInt("curr_calorie", 0)
							+ "");
					last_week_hours.setText(userinfo.getInt("prev_time", 0)
							/ 60
							+ "小时"
							+ (int) ((userinfo.getInt("prev_time", 0) / 60.00 - userinfo
									.getInt("prev_time", 0) / 60) * 60) + "分");
					last_week_calorie.setText(userinfo
							.getInt("prev_calorie", 0) + "大卡");
					rank.setText(userinfo.getInt("rank", 0) + "");
					info.setText(userinfo.getInt("info", 0) + "");

					LinearLayout layout = (LinearLayout) findViewById(R.id.person_item_container);
					int[] drawables = { R.drawable.face01, R.drawable.face02,
							R.drawable.face03, R.drawable.face04,
							R.drawable.face05 };
					JSONArray items = null;
					try {
						items = new JSONArray(userinfo.getString("items", null));
						for (int i = 0; i < items.length(); i++) {
							JSONObject json = items.getJSONObject(i);
							if (json != null) {

								LinearLayout person_item = new LinearLayout(
										MainActivity.this);

								LinearLayout.LayoutParams params = new LayoutParams(
										AppUtil.dip2px(MainActivity.this, 210),
										AppUtil.dip2px(MainActivity.this, 300));
								params.gravity = Gravity.TOP;
								person_item
										.setOrientation(LinearLayout.VERTICAL);
								person_item
										.setBackgroundResource(R.drawable.record01_bg);
								params.setMargins(
										AppUtil.dip2px(MainActivity.this, 32),
										0, 0, 0);
								if (i == 0) {
									params.setMargins(AppUtil.dip2px(
											MainActivity.this, 50), 0, 0, 0);
								} else if (i == items.length() - 1) {
									person_item
											.setBackgroundResource(R.drawable.record02_bg);

								}

								person_item.setLayoutParams(params);

								// 动态生成 需要的view
								TextView tx = new TextView(MainActivity.this);
								LayoutParams txParams1 = new LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT);
								txParams1.setMargins(
										AppUtil.dip2px(MainActivity.this, 60),
										20, 0, 0);
								txParams1.gravity = Gravity.START;
								tx.setLayoutParams(txParams1);
								tx.setTextSize(20);
								tx.setText(json.getString("day"));

								person_item.addView(tx);
								//

								ImageView iv = new ImageView(MainActivity.this);
								LayoutParams txParams = new LayoutParams(
										AppUtil.dip2px(MainActivity.this, 45),
										AppUtil.dip2px(MainActivity.this, 45));

								txParams.setMargins(
										AppUtil.dip2px(MainActivity.this, 140),
										AppUtil.dip2px(MainActivity.this, -40),
										0, 0);
								iv.setLayoutParams(txParams);
								iv.setImageResource(drawables[i]);
								person_item.addView(iv);
								JSONArray arraySports = json
										.getJSONArray("sports");
								if (arraySports != null) {
									for (int j = 0; j < arraySports.length(); j++) {
										tx = new TextView(MainActivity.this);
										txParams = new LayoutParams(
												LayoutParams.WRAP_CONTENT,
												LayoutParams.WRAP_CONTENT);
										txParams.setMargins(
												AppUtil.dip2px(
														MainActivity.this, 7),
												j == 0 ? AppUtil.dip2px(
														MainActivity.this, 60)
														: AppUtil
																.dip2px(MainActivity.this,
																		10), 0,
												0);
										txParams.gravity = Gravity.START;
										tx.setTextSize(18);
										tx.setText(arraySports.getString(j));
										tx.setLayoutParams(txParams);
										person_item.addView(tx);
									}
								}

								tx = new TextView(MainActivity.this);
								txParams = new LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT);
								txParams.setMargins(
										AppUtil.dip2px(MainActivity.this, 136),
										AppUtil.dip2px(MainActivity.this, 12),
										0, 0);
								txParams.gravity = Gravity.NO_GRAVITY;
								tx.setTextSize(18);
								tx.setText(json.getInt("time") + "");
								tx.setLayoutParams(txParams);
								person_item.addView(tx);

								tx = new TextView(MainActivity.this);
								txParams = new LayoutParams(
										LayoutParams.WRAP_CONTENT,
										LayoutParams.WRAP_CONTENT);
								txParams.setMargins(
										AppUtil.dip2px(MainActivity.this, 114),
										AppUtil.dip2px(MainActivity.this, 18),
										0, 0);
								tx.setTextSize(18);
								tx.setText(json.getInt("calorie") + "");
								tx.setLayoutParams(txParams);
								person_item.addView(tx);
								layout.addView(person_item);
							}

						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
			}

			@Override
			public void onPageScrolled(int arg0, float arg1, int arg2) {

			}

			@Override
			public void onPageScrollStateChanged(int arg0) {

			}
		});

	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {

		if (event.getAction() == KeyEvent.ACTION_DOWN) {
			if (event.getKeyCode() == KeyEvent.KEYCODE_DPAD_LEFT) {
				if (viewPager.getCurrentItem() == 2) {
					viewPager.setCurrentItem(1);
					findViewById(R.id.first_sport).requestFocus();
				}
			}
		}

		return super.dispatchKeyEvent(event);
	}

	/**
	 * 再按一次退出
	 */
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			// 2秒之内连续点击两次back就退出app
			if (System.currentTimeMillis() - exitTime > 2000) {
				System.out.println(Toast.LENGTH_LONG);
				Toast.makeText(this, R.string.exit, Toast.LENGTH_LONG).show();
				exitTime = System.currentTimeMillis();
			} else {

				finish();
			}
			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	/**
	 * 首页3个标题textView的焦点切换事件
	 * 
	 * @author chances
	 *
	 */
	OnFocusChangeListener textViewListener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			for (int i = 0; i < textViews.size(); i++) {
				if (textViews.get(i).equals(v)) {
					textViews.get(i).setTextSize(38.0f);
					textViews.get(i).setAlpha(1.0f);
					if (hasFocus) {
//						textViews.get(i).setBackgroundDrawable(getResources().getDrawable(
//								R.drawable.main_title_background_focused));
					} else {
//						textViews.get(i).setBackgroundDrawable(null);
					}
					viewPager.setCurrentItem(i, true);
				} else {
					textViews.get(i).setTextSize(30.5f);
					textViews.get(i).setAlpha(0.6f);
//					textViews.get(i).setBackgroundDrawable(null);
				}
			}

		}
	};

	// class FocusChange implements OnFocusChangeListener {
	//
	// View view;
	//
	// public FocusChange(View view) {
	// super();
	// this.view = view;
	// }
	//
	// @Override
	// public void onFocusChange(View v, boolean hasFocus) {
	// if (hasFocus) {
	// v.getParent().getParent()
	// .bringChildToFront((View) v.getParent());
	// v.getParent().bringChildToFront(v);
	// view.getParent().bringChildToFront(view);
	// Animation ani = AnimationUtils.loadAnimation(v.getContext(),
	// R.anim.bar_scale);
	// ani.setFillAfter(true);
	//
	// v.startAnimation(ani);
	// // view.startAnimation(ani);
	//
	// } else {
	// Animation ani = AnimationUtils.loadAnimation(v.getContext(),
	// R.anim.bar_scale_in);
	// ani.setFillAfter(true);
	// v.startAnimation(ani);
	// // view.startAnimation(ani);
	// }
	// }

	// }

	/**
	 * 首页热门推荐 图片获得焦点放大动画
	 */
	OnFocusChangeListener listener = new OnFocusChangeListener() {

		@Override
		public void onFocusChange(View v, boolean hasFocus) {

			if (hasFocus) {
				v.getParent().bringChildToFront(v);

				Animation ani = AnimationUtils.loadAnimation(v.getContext(),
						R.anim.bar_scale);
				ani.setFillAfter(true);

				v.startAnimation(ani);

			} else {
				Animation ani = AnimationUtils.loadAnimation(v.getContext(),
						R.anim.bar_scale_in);
				ani.setFillAfter(true);
				v.startAnimation(ani);
			}

		}
	};

	/**
	 * 为viewPager提供视图数据
	 * 
	 * @author chances
	 *
	 */
	class MainPagerAdapter extends PagerAdapter {
		private List<View> mListViews;

		public MainPagerAdapter() {

		}

		public MainPagerAdapter(List<View> mListViews) {
			super();
			this.mListViews = mListViews;
		}

		@Override
		public int getCount() {
			return mListViews.size();
		}

		@Override
		public int getItemPosition(Object object) {
			return super.getItemPosition(object);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == (View) arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {

			container.removeView(mListViews.get(position));
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			container.addView(mListViews.get(position));
			// 取用户的总数据 存放本地
			if (!isSendUserRequest) {

				JsonObjectRequest mUserinfoRequest = new JsonObjectRequest(
						AppUtil.getAferAddArgsUrl(getResources().getString(
								R.string.user_json_url)), null,
						new Response.Listener<JSONObject>() {
							@Override
							public void onResponse(JSONObject response) {
								if (response != null) {
									try {
										SharedPreferences.Editor editor = getSharedPreferences(
												"userinfo", 0).edit();
										editor.putString("user_id",
												response.getString("user_id"));
										editor.putString("nick_name",
												response.getString("nick_name"));
										editor.putInt("total_time",
												response.getInt("total_time"));
										editor.putInt("total_calorie", response
												.getInt("total_calorie"));
										editor.putInt("rank",
												response.getInt("rank"));
										editor.putInt("info",
												response.getInt("info"));
										editor.putInt("curr_time",
												response.getInt("curr_time"));
										editor.putInt("curr_calorie",
												response.getInt("curr_calorie"));
										editor.putInt("prev_time",
												response.getInt("prev_time"));
										editor.putInt("prev_calorie",
												response.getInt("prev_calorie"));
										editor.putInt("item_count",
												response.getInt("item_count"));
										editor.putInt("page_no",
												response.getInt("page_no"));
										editor.putInt("page_size",
												response.getInt("page_size"));
										editor.putString(
												"latest_sport_item",
												response.getJSONArray(
														"latest_sport_item")
														.toString());
										editor.putString("items", response
												.getJSONArray("items")
												.toString());

										editor.commit();

										TextView allTime = (TextView) findViewById(R.id.all_time);
										TextView allCalorie = (TextView) findViewById(R.id.all_calorie);

										allTime.setText(response
												.getInt("total_time")
												/ 60
												+ "小时"
												+ (int) ((response
														.getInt("total_time") / 60.00 - response
														.getInt("total_time") / 60) * 60)
												+ "分");
										allCalorie.setText(response
												.getInt("total_calorie") + "大卡");

										TextView sport_item1 = (TextView) findViewById(R.id.sport_item1);
										TextView sport_item2 = (TextView) findViewById(R.id.sport_item2);
										TextView sport_item3 = (TextView) findViewById(R.id.sport_item3);

										sport_item1.setText(response
												.getJSONArray(
														"latest_sport_item")
												.getString(0));
										sport_item2.setText(response
												.getJSONArray(
														"latest_sport_item")
												.getString(1));
										sport_item3.setText(response
												.getJSONArray(
														"latest_sport_item")
												.getString(2));

									} catch (JSONException e) {
										e.printStackTrace();
									}

								}
								isSendUserRequest = true;
							}
						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(MainActivity.this,
										"获取数据失败，请检查网路链接", Toast.LENGTH_LONG)
										.show();
								isSendUserRequest = false;
							}
						});

				AppUtil.getInstance(MainActivity.this).getRequestQueue()
						.add(mUserinfoRequest);
			}
			relative = (ViewGroup) findViewById(R.id.rel);

			if (!isRecommendSendRequest) {

				JsonArrayRequest mViewImageArrayRequest = new JsonArrayRequest(
						AppUtil.getAferAddArgsUrl(getResources().getString(
								R.string.main_json_url)),
						new Response.Listener<JSONArray>() {

							@Override
							public void onResponse(JSONArray array) {
								try {
									if (array == null)
										return;

									for (int i = 0; i < array.length(); i++) {
										if (!array.isNull(i)) {
											JSONObject response = array
													.getJSONObject(i);

											String page_code = response
													.getString("page_code");
											JSONArray items = response
													.getJSONArray("items");
											if ("recommend".equals(page_code)) {

												if (relative != null) {
													 dailyCard =
													 (FocusImageButtonView)
													 findViewById(R.id.daily_card);
													// moreRecord =
													// (FocusImageButtonView)
													// findViewById(R.id.more_record);

													 dailyCard.setOnClickListener(
															 new OpenActivityAction(MainActivity.this,
																	 UserCenterActivity.class));
													// moreRecord
													// .setOnClickListener(new
													// MyClickListener(
													// CommentActivity.class));

													for (int j = 0; j < items
															.length(); j++) {

														if (items.length() == relative
																.getChildCount()) {
															ImageView image = (ImageView) relative
																	.getChildAt(j);
															if (items.get(j) != null) {
																JSONObject mImageJson = items
																		.getJSONObject(j);
																// AppUtil.judgeView(
																// image,
																// true,
																// null,
																// R.drawable.logo_gray);
																AppUtil.getInstance(
																		MainActivity.this)
																		.loadImageBitmap(
																				image,
																				mImageJson
																						.getString("icon"),
																				false);

																image.setOnClickListener(new MyClickListener(
																		mImageJson
																				.getInt("code"),
																		mImageJson
																				.getInt("cate_code"),
																		mImageJson
																				.getString("icon"),
																		mImageJson
																				.getString("type"),
																		mImageJson
																				.getString("link"),
																		mImageJson
																				.getString("title"),
																		mImageJson
																				.getString("background")));
																if (image
																		.getOnFocusChangeListener() == null)

																	image.setOnFocusChangeListener(listener);

															}

														}
													}

												}
											}
										}

									}
									isRecommendSendRequest = true;
								} catch (JSONException e) {
									e.printStackTrace();
								}
							}

						}, new Response.ErrorListener() {
							@Override
							public void onErrorResponse(VolleyError error) {
								Toast.makeText(MainActivity.this,
										"获取数据失败，请检查网路链接", Toast.LENGTH_LONG)
										.show();
								isRecommendSendRequest = false;
							}
						});
				AppUtil.getInstance(MainActivity.this).getRequestQueue()
						.add(mViewImageArrayRequest);

			}

			return mListViews.get(position);
		}
	}

	/**
	 * 首页image click事件 添加一些字段存储从远程获取的数据
	 * 
	 * @author zhangyus
	 *
	 */
	class MyClickListener implements View.OnClickListener {

		int index;
		int cate_code;
		String icon;
		String type;
		String link;
		String title;
		String background;

		@Override
		public void onClick(View v) {
			if ("activity".equals(type)) {

				Intent intent = null;
				try {
					intent = new Intent(MainActivity.this,
							Class.forName("wanba.ott.activity." + link));
					intent.putExtra("index", index);
					intent.putExtra("background", background);
					intent.putExtra("cate_code", String.valueOf(cate_code));
					startActivity(intent);
				} catch (ClassNotFoundException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}

		}

		public MyClickListener() {
			super();
		}

		public MyClickListener(int index, int cate_code, String icon,
				String type, String link, String title, String background) {
			super();
			this.index = index;
			this.cate_code = cate_code;
			this.icon = icon;
			this.type = type;
			this.link = link;
			this.title = title;
			this.background = background;
		}

		public MyClickListener(int index, int cate_code, String icon,
				String type, String link, String title) {
			super();
			this.index = index;
			this.cate_code = cate_code;
			this.icon = icon;
			this.type = type;
			this.link = link;
			this.title = title;
		}

		public MyClickListener(int index, int cate_code, String icon,
				String type, String link) {
			super();
			this.index = index;
			this.cate_code = cate_code;
			this.icon = icon;
			this.type = type;
			this.link = link;
		}

	}

}
