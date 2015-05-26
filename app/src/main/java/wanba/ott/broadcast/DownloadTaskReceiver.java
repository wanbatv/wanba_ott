package wanba.ott.broadcast;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Process;
import android.util.Log;

/**
 * 下载——>广播接收器
 * 
 * @author zhangyus
 *
 */
public class DownloadTaskReceiver extends BroadcastReceiver {

	@Override
	public void onReceive(Context context, Intent intent) {
		// 获取系统下载管理服务
		DownloadManager downloadManager = ((DownloadManager) context
				.getSystemService(Activity.DOWNLOAD_SERVICE));
		String action = intent.getAction();

		Log.e("downId1", intent.getLongExtra("downId", -1) + "");
		Log.e("downId2",
				intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0) + "");
		// 当下载完成后
		if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {
			// 获取当前下载任务的下载id
			long downloadId = intent.getLongExtra(
					DownloadManager.EXTRA_DOWNLOAD_ID, 0);
			Query query = new Query();
			query.setFilterById(downloadId);
			Cursor aCursor = downloadManager.query(query);
			// 使用游标查询该下载任务
			if (aCursor.moveToFirst()) {
				// 获取该任务下载完成后的存放路径
				int fileUriIdx = aCursor
						.getColumnIndex(DownloadManager.COLUMN_LOCAL_URI);
				String fileUri = aCursor.getString(fileUriIdx);
				Log.d("ordertest", "fileUri : " + fileUri);
				installAPK(Uri.parse(fileUri), context);
			}
		}
	}

	/**
	 * 安装apk
	 * 
	 * @param f
	 * @param context
	 */
	public void installAPK(Uri f, Context context) {
		Intent install = new Intent(Intent.ACTION_VIEW);
		install.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		install.setDataAndType(f, "application/vnd.android.package-archive");
		context.startActivity(install);

		// 安装完成后重新启动
		Process.killProcess(Process.myPid());
	}

}
