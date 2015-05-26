package wanba.ott.util;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import wanba.ott.activity.R;

import com.android.volley.Network;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.RequestQueue.RequestFinishedListener;
import com.android.volley.toolbox.BasicNetwork;
import com.android.volley.toolbox.DiskBasedCache;
import com.android.volley.toolbox.HttpClientStack;
import com.android.volley.toolbox.HttpStack;
import com.android.volley.toolbox.HurlStack;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.Volley;

import android.app.Activity;
import android.app.DevInfoManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.http.AndroidHttpClient;
import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;

/**
 * 自己定义的一些常用的工具方法
 * 
 * @author chances
 *
 */
public final class AppUtil {

	private static AppUtil mInstance;
	// 全局的网络队列
	private RequestQueue mRequestQueue;
	// 全局imageLoader
	private ImageLoader mImageLoader;
	private BitmapCache mBitmapCache;
	private static Context mCtx;
//	private HttpStack stack;

	// 磁盘缓存对象
	private DiskLruCache mDiskLruCache;

	// bitmap磁盘缓存文件夹名称
	public static final String DISK_CACHE_FILENAME = "bitmap";

	/**
	 * 用来存放盒子和用户的一些唯一的鉴权信息
	 * 
	 * @author zhangyus
	 *
	 */
	public static class STB {
		public static final STB INSTANCE = new STB();
		public String Mac;
		public String Sn; // 终端串号（序列号）
		public String IP;
		public String UserID; // IPTV业务账号
		public String Model; // 机顶盒型号
		public String UserGroup; // IPTV分组ID，如中兴平台UserGroupNMB=3022，该参数为3022.
	}

	private AppUtil(Context context) {
		mCtx = context;
		mRequestQueue = getRequestQueue();
		mBitmapCache = new BitmapCache();
		mImageLoader = new ImageLoader(mRequestQueue, mBitmapCache);
		try {
			// 设定磁盘缓存初始化
			mDiskLruCache = DiskLruCache.open(AppUtil.getDiskCacheDir(context,
					AppUtil.DISK_CACHE_FILENAME), AppUtil
					.getAppVersion(context), 1, 50 * 1024 * 1024);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}

	}

	public static AppUtil getInstance(Context context) {
		if (mInstance == null) {
			synchronized (AppUtil.class) {
				if (mInstance == null)
					mInstance = new AppUtil(context);
			}

		}
		return mInstance;
	}

	/**
	 * 根据volley源码初始化 requestqueue
	 * 
	 * @return
	 */
	public RequestQueue getRequestQueue() {
		if (mRequestQueue == null) {
			// getApplicationContext() is key, it keeps you from leaking the
			// Activity or BroadcastReceiver if someone passes one in.
			mRequestQueue = Volley
					.newRequestQueue(mCtx.getApplicationContext());
			//
			// File cacheDir = new File(mCtx.getCacheDir(), "volley");
			//
			// String userAgent = "volley/0";
			// try {
			// String packageName = mCtx.getPackageName();
			// PackageInfo info = mCtx.getPackageManager().getPackageInfo(
			// packageName, 0);
			// userAgent = packageName + "/" + info.versionCode;
			// } catch (NameNotFoundException e) {
			// }
			//
			// if (stack == null) {
			// if (Build.VERSION.SDK_INT >= 9) {
			// stack = new HurlStack();
			// } else {
			// // Prior to Gingerbread, HttpUrlConnection was unreliable.
			// // See:
			// //
			// http://android-developers.blogspot.com/2011/09/androids-http-clients.html
			// stack = new HttpClientStack(
			// AndroidHttpClient.newInstance(userAgent));
			// }
			// }
			//
			// Network network = new BasicNetwork(stack);
			//
			// // 将原来源码中 线程池的数量从4 调高至10
			// mRequestQueue = new RequestQueue(new DiskBasedCache(cacheDir),
			// network, 10);
			// mRequestQueue.start();

			return mRequestQueue;

		}
		return mRequestQueue;
	}

	public ImageLoader getImageLoader() {
		return mImageLoader;
	}

	public BitmapCache getBitmapCache() {
		return mBitmapCache;
	}

	/**
	 * 简单的一个显示progressdialog
	 * 
	 * @param context
	 *            需要显示进度条的activity
	 * @param contextTag
	 *            用来判断进度条时间的tag
	 * @param resIds
	 *            activity在进度条之后需要设置visible的view的res_id
	 */
	public static ProgressDialog showProgress(final Activity context,
			final String contextTag, final int[] resIds) {
		int defaultLoadingTime = 5000;
		// 启动时先显示一个loading的进度条
		final ProgressDialog dialog2 = ProgressDialog.show(context,
				context.getString(R.string.app_name),
				context.getString(R.string.loading_text), false, false);
		dialog2.setIcon(R.drawable.ic_launcher);
		// 判断是否第一次进入context界面
		boolean isEnter = context.getSharedPreferences(contextTag, 0)
				.getBoolean("isEnter", false);
		// 不是第一次则将loading时间降低为2000ms
		defaultLoadingTime = isEnter ? 2000 : defaultLoadingTime;

		context.getWindow().getDecorView().postDelayed(new Runnable() {

			@Override
			public void run() {

				dialog2.dismiss();
				for (int i = 0; i < resIds.length; i++) {
					context.findViewById(resIds[i]).setVisibility(View.VISIBLE);
				}
				context.getSharedPreferences(contextTag, 0).edit()
						.putBoolean("isEnter", true).commit();

			}
		}, defaultLoadingTime);
		return dialog2;
	}

	/**
	 * 判断当前view的背景和src设置 并设置error时的默认图片
	 * 
	 * @param view
	 * @param isSetBackground
	 * @param bitmap
	 * @param imageResId
	 */
	public static View judgeView(View view, boolean isSetBackground,
			Bitmap bitmap, int imageResId) {

		if (bitmap == null && imageResId == 0) {
			throw new IllegalArgumentException("bitmap和imageResId都为空");
		}

		if (bitmap == null) {
			if (isSetBackground) {
				view.setBackgroundResource(imageResId);
			} else {
				if (view instanceof ImageView) {
					((ImageView) view).setImageResource(imageResId);
				} else {
					throw new IllegalArgumentException("当前view无法设定src值");
				}
			}
		} else {
			if (isSetBackground) {
				view.setBackgroundDrawable(new BitmapDrawable(null, bitmap));
			} else {
				if (view instanceof ImageView) {
					((ImageView) view).setImageBitmap(bitmap);
				} else {
					throw new IllegalArgumentException("当前view无法设定src值");
				}
			}
		}
		return view;

	}

	/**
	 * 根据指定的url 为指定的View从 内存||磁盘||网络 加载bitmap 可选设置为background还是src 默认src
	 * 
	 * @param imageView
	 * @param url
	 * @param isSetBackground
	 *            是否设置为背景
	 * @return
	 */
	public View loadImageBitmap(View view, String url, boolean isSetBackground) {
		Bitmap bitmap = null;

		// 如果当前bitmap在内存中存在则直接显示
		if (mBitmapCache.getBitmap(url) != null) {
			bitmap = mBitmapCache.getBitmap(url);
			judgeView(view, isSetBackground, bitmap, 0).setBackgroundDrawable(null);
			//如果 isSetBackground 为真 表示load的图是用来为当前的view设置背景的  无需去除默认的背景
//			if(isSetBackground){
//				judgeView(view, isSetBackground, bitmap, 0);
//			}else{
//				judgeView(view, isSetBackground, bitmap, 0).setBackgroundDrawable(null);
//			}
			
		} else {

			// 初始化自定义imageListener
			final MyImageListener listener = new MyImageListener();
			// listener.defaultImageResId = android.R.drawable.ic_menu_rotate;
			// listener.errorImageResId = android.R.drawable.ic_delete;
			listener.defaultImageResId = R.drawable.logo_gray;
			listener.errorImageResId = R.drawable.logo_gray;
			listener.view = view;
			listener.mDiskLruCache = mDiskLruCache;
			listener.url = url;
			listener.isSetBackground = isSetBackground;
			
			
			// 根据url取出该url磁盘缓存的bitmap
			DiskLruCache.Snapshot snapshot = null;
			try {

				snapshot = mDiskLruCache.get(AppUtil.hashKeyForDisk(url));

				if (snapshot != null) {
					// 存在则读取该bitmap
					bitmap = BitmapFactory.decodeStream(snapshot
							.getInputStream(0));

					// 如果读取失败 可能出现未知错误
					if (bitmap == null) {

						// 将这个url从缓存中移除
						mDiskLruCache.remove(AppUtil.hashKeyForDisk(url));
						// 同时为这个url重新下载写入缓存
						mImageLoader.get(url, listener, 1280, 720,
								ScaleType.CENTER_CROP);

					} else {
						// 磁盘读取成功直接存入内存缓存
						judgeView(view, isSetBackground, bitmap, 0).setBackgroundDrawable(null);;
//						if(isSetBackground){
//							judgeView(view, isSetBackground, bitmap, 0);
//						}else{
//							judgeView(view, isSetBackground, bitmap, 0).setBackgroundDrawable(null);;
//						}
						mBitmapCache.putBitmap(url, bitmap);
					}

				} else {
					// 如果bitmap磁盘缓存不存在
					mImageLoader.get(url, listener, 1280, 720,
							ScaleType.CENTER_CROP);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return view;
	}

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 */
	public static int dip2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/**
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 */
	public static int px2dip(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}

	/**
	 * 为url添加盒子的基本信息值
	 * 
	 * @param url
	 * @return
	 */
	public static String getAferAddArgsUrl(String url) {

		String params = "mac=" + STB.INSTANCE.Mac + "&sn=" + STB.INSTANCE.Sn
				+ "&ip=" + STB.INSTANCE.IP + "&userId=" + STB.INSTANCE.UserID
				+ "&model=" + STB.INSTANCE.Model + "&group="
				+ STB.INSTANCE.UserGroup;

		if (url.contains("?")) {

			return url + "&" + params;

		} else {

			return url + "?" + params;
		}

	}

	/**
	 * 为Url添加cateCode
	 * 
	 * @param url
	 * @param cateCode
	 * @return
	 */
	public static String getAferAddCateArgsUrl(String url, String cateCode) {

		String params = "cate_code=" + cateCode;
		String aUrl = url;
		if (url.contains("?")) {

			aUrl = url + "&" + params;

		} else {

			aUrl = url + "?" + params;
		}
		return getAferAddArgsUrl(aUrl);
	}

	public static Map<String, String> toMap(Activity context, JSONObject obj) {
		Map<String, String> result = new HashMap<String, String>();
		result.put("cate_code", context.getIntent().getStringExtra("cate_code"));
		Iterator<String> iter = obj.keys();
		for (; iter.hasNext();) {
			String aKey = iter.next();
			try {
				result.put(aKey, obj.getString(aKey));
			} catch (JSONException e) {
				e.printStackTrace();
			}
		}
		return result;

	}

	/**
	 * 检查是否已连接上网
	 * 
	 * @return
	 */
	public static boolean isNetOnline(Context context) {
		// 获取网络信息管理
		ConnectivityManager manager = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		// 查看当前活动的网络链接
		NetworkInfo networkInfo = manager.getActiveNetworkInfo();

		return networkInfo != null && networkInfo.isConnected();
	}

	/**
	 * 判断当前sd卡是否存在 有无被取出 根据输入的uniqueName拼接出一个缓存文件夹的路径
	 * 
	 * @param context
	 * @param uniqueName
	 * @return
	 */
	public static File getDiskCacheDir(Context context, String uniqueName) {
		String cachePath = context.getCacheDir().getPath();
		try {

			if (Environment.MEDIA_MOUNTED.equals(Environment
					.getExternalStorageState())
					|| !Environment.isExternalStorageRemovable()) {
				File cache = context.getExternalCacheDir();
				if (cache != null) {
					cachePath = context.getExternalCacheDir().getPath();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new File(cachePath + File.separator + uniqueName);
	}

	/**
	 * 获取当前app版本号
	 * 
	 * @param context
	 * @return
	 */
	public static int getAppVersion(Context context) {
		try {
			PackageInfo info = context.getPackageManager().getPackageInfo(
					context.getPackageName(), 0);
			return info.versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
		}
		return 1;
	}

	/**
	 * 将key转换为MD5加密的key
	 * 
	 * @param key
	 * @return
	 */
	public static String hashKeyForDisk(String key) {
		String cacheKey;
		try {
			final MessageDigest mDigest = MessageDigest.getInstance("MD5");
			mDigest.update(key.getBytes());
			cacheKey = bytesToHexString(mDigest.digest());
		} catch (NoSuchAlgorithmException e) {
			cacheKey = String.valueOf(key.hashCode());
		}
		return cacheKey;
	}

	private static String bytesToHexString(byte[] bytes) {
		StringBuilder sb = new StringBuilder();
		for (int i = 0; i < bytes.length; i++) {
			String hex = Integer.toHexString(0xFF & bytes[i]);
			if (hex.length() == 1) {
				sb.append('0');
			}
			sb.append(hex);
		}
		return sb.toString();
	}

	/**
	 * 将bitmap写入磁盘
	 * 
	 * @param bitmap
	 * @return
	 */
	public static boolean outPutBitmap(Bitmap bitmap, OutputStream out) {
		if (bitmap == null)
			return false;
		BufferedOutputStream bos = new BufferedOutputStream(out, 8 * 1024);
		bitmap.compress(Bitmap.CompressFormat.PNG, 100, bos);
		try {
			bos.flush();
			bos.close();
			return true;
		} catch (IOException e) {
			e.printStackTrace();
		}
		return false;
	}

	/*
	 * DevInfoManager 获取设备信息jar的方法
	 */

	/**
	 * 取值
	 * 
	 * @param name
	 *            待查询参数名称
	 * @return 参数值
	 */
	public static String getValue(String name, Context context) {
		DevInfoManager mDevInfoManager = (DevInfoManager) context
				.getSystemService("devinfo_data");
		if (mDevInfoManager != null) {
			return mDevInfoManager.getValue(name);

		} else {
			return "";
		}

	}

	/**
	 * 更新
	 * 
	 * @param name
	 *            待修改的参数名称
	 * @param value
	 *            修改值
	 * @param attribute
	 *            代表所改动的项的读写权限(1读写，0只读)，无特殊要求一律用DevInfoManager
	 *            .Default_Attribute = 0
	 * @param context
	 * @return 成功 0 失败 -1
	 */
	public static boolean update(String name, String value, Context context) {
		DevInfoManager mDevInfoManager = (DevInfoManager) context
				.getSystemService("devinfo_data");
		if (mDevInfoManager == null)
			return false;

		int result = -1;
		try {
			result = mDevInfoManager.update(name, value,
					DevInfoManager.Default_Attribute);
			if (result == 0)
				return true;
			return false;
		} catch (Exception e) {
			result = -1;
			return false;
		}
	}

	/*
	 * DevInfoManager 获取设备信息jar的方法
	 */

}
