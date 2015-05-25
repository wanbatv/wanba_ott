package wanba.ott.util;

import java.io.IOException;

import wanba.ott.activity.R;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Handler;
import android.os.HandlerThread;
import android.view.View;
import android.widget.ImageView;

import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;

/**
 * 自定义的volleyimageListener 为了加入磁盘缓存
 * 
 * @author zhangyus
 *
 */
public class MyImageListener implements ImageListener {
	public View view;
	public int errorImageResId;
	public int defaultImageResId;
	public DiskLruCache mDiskLruCache;
	public String url;
	public boolean isSetBackground;
	Handler handler = new Handler();

	@Override
	public void onErrorResponse(VolleyError arg0) {
		if (errorImageResId != 0) {
			 AppUtil.judgeView(view, isSetBackground, null, errorImageResId);
//			if (isSetBackground) {
//				AppUtil.judgeView(view, isSetBackground, null, errorImageResId);
//			} else {
//				AppUtil.judgeView(view, isSetBackground, null, errorImageResId)
//						.setBackgroundDrawable(null);
//			}

		}
	}

	@SuppressWarnings("deprecation")
	@Override
	public void onResponse(ImageContainer response, boolean arg1) {
		final ImageContainer res = response;
		if (response.getBitmap() != null) {

			 AppUtil.judgeView(view, isSetBackground, response.getBitmap(), 0)
			 .setBackgroundDrawable(null);
//			if (isSetBackground) {
//				AppUtil.judgeView(view, isSetBackground, response.getBitmap(),
//						0);
//			} else {
//				AppUtil.judgeView(view, isSetBackground, response.getBitmap(),
//						0);
//				
//			}

			// 将down下来的bitmap写入磁盘
			new Thread(new Runnable() {

				@Override
				public void run() {
					// TODO Auto-generated method stub
					DiskLruCache.Editor editor = null;
					try {
						editor = mDiskLruCache.edit(AppUtil.hashKeyForDisk(url));

						if (editor != null) {
							if (AppUtil.outPutBitmap(res.getBitmap(),
									editor.newOutputStream(0))) {
								editor.commit();
							} else {
								editor.abort();
							}
						}
						mDiskLruCache.flush();

					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}).start();

		} else if (defaultImageResId != 0) {

			 AppUtil.judgeView(view, isSetBackground, null,
			 defaultImageResId);
//			if (isSetBackground) {
//				AppUtil.judgeView(view, isSetBackground, null,
//						defaultImageResId);
//			} else {
//				AppUtil.judgeView(view, isSetBackground, null,
//						defaultImageResId).setBackgroundDrawable(null);
//			}
		}
		
	}
}