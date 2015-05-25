package wanba.ott.activity;

import java.io.IOException;
import java.util.ArrayList;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.android.volley.toolbox.ImageLoader.ImageListener;

import wanba.ott.abstracts.activity.FullScreenActivity;
import wanba.ott.util.AppUtil;
import wanba.ott.util.BitmapCache;
import wanba.ott.util.DiskLruCache;
import wanba.ott.util.MyImageListener;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.Log;
import android.util.LruCache;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 微信相册大图浏览
 * 
 * @author zhangyus
 *
 */
public class AlbumFullScreenActivity extends FullScreenActivity {

	private ViewPager mViewPager;

	private ArrayList<ImageView> imageViews;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_album_full_screen);

		ArrayList<String> urls = getIntent().getStringArrayListExtra("urls");
		int position = getIntent().getIntExtra("position", 0);
		imageViews = new ArrayList<ImageView>();
		mViewPager = (ViewPager) findViewById(R.id.albumVPager);
		mViewPager.setAdapter(new AlbumPagerAdapter(imageViews, urls));
		mViewPager.setCurrentItem(position);

	}

	class AlbumPagerAdapter extends PagerAdapter {
		private ArrayList<ImageView> ivs;
		private ArrayList<String> urls;

		public AlbumPagerAdapter(ArrayList<ImageView> ivs,
				ArrayList<String> urls) {
			super();
			this.ivs = ivs;
			this.urls = urls;
		}

		public AlbumPagerAdapter() {
			super();
		}

		@Override
		public int getCount() {
			// TODO Auto-generated method stub
			return urls.size();
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

			container.removeView((View) object);
		}

		@Override
		public Object instantiateItem(ViewGroup container, int position) {

			ImageView imageView = new ImageView(AlbumFullScreenActivity.this);
			imageView.setLayoutParams(new LayoutParams(
					LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
			imageView.setScaleType(ScaleType.FIT_CENTER);
			imageView.setVisibility(View.VISIBLE);
			imageView.setFocusable(true);

			String url = urls.get(position);
			container.addView(AppUtil.getInstance(AlbumFullScreenActivity.this)
					.loadImageBitmap(imageView, url,false));

			return imageView;
		}
	}

}