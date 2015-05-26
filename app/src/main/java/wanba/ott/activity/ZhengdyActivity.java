package wanba.ott.activity;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.BitmapCache;
import wanba.ott.util.ProductInfo;

/**
 * 郑多燕 页
 * 
 * @author zhangyus
 *
 */
public class ZhengdyActivity extends FullScreenActivity {

	View cardview;

	private ViewPager mViewPager = null;
	private ItemAdapter mItemAdapter = null;

	private TextView z_user_info;
	private TextView z_play_time;
	private TextView z_user_k;

	private ChannelItem[] mChannelItem = null;

	private RequestQueue mRequestQueue = null;
	private ImageLoader mImageLoader = null;

	private static class ChannelItem {
		String id;
		String title;
		String videoUrl;
		String coverUrl;
		String productCode;
		String tranId;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_zhengdy);
		cardview = findViewById(R.id.z_card);
		cardview.setOnClickListener(new OpenActivityAction(this,
				UserCenterActivity.class));

		mViewPager = (ViewPager) findViewById(R.id.channel_zdy_view_pager);
		mItemAdapter = new ItemAdapter(this);
		mViewPager.setAdapter(mItemAdapter);
		
//		//显示加载进度框
//		AppUtil.showProgress(this, "zdy", new int[] { R.id.zdy_layout });
//
//		// 加载背景图片
//		AppUtil.getInstance(ZhengdyActivity.this).loadImageBitmap(
//				findViewById(R.id.zdy_layout),
//				getIntent().getStringExtra("background"), true);

//		String cateCode = getIntent().getStringExtra("cate_code");
//		String url = AppUtil.getAferAddCateArgsUrl(
//				getString(R.string.ott_content), cateCode);
//		JsonObjectRequest request = new JsonObjectRequest(url, null,
//
//		new Listener<JSONObject>() {
//
//			@Override
//			public void onResponse(JSONObject response) {
//				if (response != null) {
//					try {
//						JSONArray array = response.getJSONArray("results");
//						JSONArray currDataArray = new JSONArray();
//						for (int i = 0; i < array.length(); i++) {
//							if (array.getJSONObject(i).getInt("code") == getIntent()
//									.getIntExtra("index", 0)) {
//								currDataArray.put(array.getJSONObject(i));
//							}
//						}
//						ViewGroup grid = (ViewGroup) findViewById(R.id.photo_layout);
//
//						for (int i = 0; i < grid.getChildCount(); i++) {
////							AppUtil.getInstance(ZhengdyActivity.this)
////									.loadImageBitmap(
////											grid.getChildAt(i),
////											currDataArray.getJSONObject(i)
////													.getString("icon"), false);
//							grid.getChildAt(i).setOnClickListener(
//									new OpenActivityAction(
//											ZhengdyActivity.this,
//											SimplePlayerActivity.class,
//											AppUtil.toMap(ZhengdyActivity.this,currDataArray
//													.getJSONObject(i))));
//						}
//
//					} catch (JSONException e) {
//						e.printStackTrace();
//					}
//
//				}
//			}
//
//		}, new ErrorListener() {
//
//			@Override
//			public void onErrorResponse(VolleyError arg0) {
//				Toast.makeText(ZhengdyActivity.this, "读取数据失败,请检查网络连接！",
//						Toast.LENGTH_LONG).show();
//			}
//		});
//		AppUtil.getInstance(ZhengdyActivity.this).getRequestQueue()
//				.add(request);

		// ViewGroup viewGroup = (ViewGroup) findViewById(R.id.photo_layout);
		// super.bindOnClickListener(viewGroup, new OpenActivityAction(this,
		// SimplePlayerActivity.class));

		final JsonObjectRequest request = new JsonObjectRequest(
				"http://121.201.7.173:8080/wanba_shzg/ott_channel_zdy.jsp",
				null,
				new Response.Listener<JSONObject>() {

			@Override
			public void onResponse(JSONObject jsonObject) {
				if (jsonObject.has("result")) {
					try {
						JSONArray result = jsonObject.getJSONArray("result");
						if (result != null) {
							final int length = result.length();
							if (length > 0) {
								mChannelItem = new ChannelItem[length];
								ProductInfo.Product p = ProductInfo.getInstance().getProductWithId("3");
								for (int i = 0; i < length; i++) {
									JSONObject itemObject = result.getJSONObject(i);
									ChannelItem ci = new ChannelItem();
									ci.id = itemObject.getString("id");
									ci.title = itemObject.getString("title");
									ci.coverUrl = itemObject.getString("icon");
									ci.videoUrl = itemObject.getString("play_url");
									ci.tranId = itemObject.getString("tranId");
									ci.productCode = p.code;
									mChannelItem[i] = ci;
								}
							}
						}
					} catch (JSONException e) {
						e.printStackTrace();
					}
				}
				mItemAdapter.notifyDataSetChanged();
			}
		}, new Response.ErrorListener() {
			@Override
			public void onErrorResponse(VolleyError volleyError) {

			}
		});

		z_user_info = (TextView) findViewById(R.id.z_user_info);
		z_play_time = (TextView) findViewById(R.id.z_play_time);
		z_user_k = (TextView) findViewById(R.id.z_user_k);
		// 加载用户数据
		z_user_info.setText(getSharedPreferences("userinfo", 0).getString(
				"user_id", ""));
		z_play_time
				.setText(getSharedPreferences("userinfo", 0).getInt(
						"total_time", 0)
						/ 60
						+ "小时"
						+ (int) (getSharedPreferences("userinfo", 0).getInt(
						"total_time", 0) / 60.00 - getSharedPreferences(
						"userinfo", 0).getInt("total_time", 0) / 60)
						* 60 + "分");

		z_user_k.setText(getSharedPreferences("userinfo", 0).getInt(
				"total_calorie", 0)
				+ "大卡");

		mRequestQueue = Volley.newRequestQueue(this);
		mImageLoader = new ImageLoader(mRequestQueue, new BitmapCache());
		mRequestQueue.add(request);

	}

	private class ItemAdapter extends PagerAdapter {

		private static final int PAGE_SIZE = 4;

		private Context mContext = null;

		ItemAdapter(Context context) {
			mContext = context;
		}

		@Override
		public int getCount() {
			int count = 0;
			if (mChannelItem != null && mChannelItem.length > 0) {
				final int len = mChannelItem.length;
				if ((len % PAGE_SIZE) == 0) {
					count = len / PAGE_SIZE;
				} else {
					count = (len / PAGE_SIZE) + 1;
				}
			}
			return count;
		}

		@Override
		public boolean isViewFromObject(View view, Object object) {
			return view == object;
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			int offset = position * PAGE_SIZE;
			LinearLayout itemContentView = new LinearLayout(mContext);
			for (int i = offset; i < offset + PAGE_SIZE; i++) {
				View itemView = createItemView(mContext, i);
				LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
						LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
				itemContentView.addView(itemView, lp);
			}
			container.addView(itemContentView);
			return itemContentView;
		}

		private View createItemView(Context context, int position) {
			FrameLayout frame = new FrameLayout(context);
			final int padding = (int) (4 * context.getResources().getDisplayMetrics().density);
			frame.setPadding(padding, padding, padding, padding);
			FocusImageView imageView = new FocusImageView(context);
			final ChannelItem item = mChannelItem[position];
			mImageLoader.get(item.coverUrl, ImageLoader.getImageListener(
					imageView, R.drawable.logo_gray, R.drawable.logo_gray));
			FrameLayout.LayoutParams imageViewLp = new FrameLayout.LayoutParams(
					context.getResources().getDimensionPixelSize(R.dimen.channel_zdy_item_width),
					context.getResources().getDimensionPixelSize(R.dimen.channel_zdy_item_height));
			frame.addView(imageView, imageViewLp);
			imageView.setOnClickListener(new View.OnClickListener() {
				@Override
				public void onClick(View v) {
					new OpenActivityAction(ZhengdyActivity.this,
							SimplePlayerActivity.class, null)
							.setProductCode(item.productCode)
							.setTranId(item.tranId)
							.onClick(v);
				}
			});

			if (position == 0) {
				imageView.requestFocus();
			}
			return frame;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
	}

	private static class FocusImageView extends ImageView {

		private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
		private RectF mRect = new RectF();
		private float mR = 0;

		public FocusImageView(Context context) {
			super(context);
			init(context);
		}

		private void init(Context context) {
			setFocusable(true);
			setClickable(true);

			mPaint.setStyle(Paint.Style.STROKE);
			mPaint.setColor(Color.YELLOW);
			mPaint.setStrokeWidth(4 * getResources().getDisplayMetrics().density);
			mR = 24 * getResources().getDisplayMetrics().density;
		}

		@Override
		protected void onDraw(Canvas canvas) {
			super.onDraw(canvas);

			if (isFocused()) {
				final int w = getMeasuredWidth();
				final int h = getMeasuredHeight();
				final float density = getResources().getDisplayMetrics().density;
				mRect.left = getResources().getDimensionPixelSize(R.dimen.channel_zdy_item_border_offset_left);
				mRect.top = getResources().getDimensionPixelSize(R.dimen.channel_zdy_item_border_offset_top);
				mRect.right = w - mPaint.getStrokeWidth() / 2;
				mRect.bottom = h - getResources().getDimensionPixelSize(R.dimen.channel_zdy_item_border_offset_bottom);

				canvas.drawRoundRect(mRect, mR, mR, mPaint);
			}
		}
	}

}
