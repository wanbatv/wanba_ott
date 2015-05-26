package wanba.ott.activity;

import java.io.IOException;

import wanba.ott.activity.R;
import wanba.ott.abstracts.activity.FullScreenActivity;
import android.media.MediaPlayer;
import android.media.MediaPlayer.OnCompletionListener;
import android.media.MediaPlayer.OnErrorListener;
import android.media.MediaPlayer.OnInfoListener;
import android.media.MediaPlayer.OnPreparedListener;
import android.media.MediaPlayer.OnSeekCompleteListener;
import android.media.MediaPlayer.OnVideoSizeChangedListener;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.MediaController;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

public class PlayerActivity extends FullScreenActivity implements
		OnCompletionListener, OnErrorListener, OnInfoListener,
		OnPreparedListener, OnSeekCompleteListener, OnVideoSizeChangedListener,
		SurfaceHolder.Callback {
	private SurfaceView surface1;
	private Button start, stop, pre;
	private TextView info;
	private MediaController MediaController;
	private MediaPlayer player;
	private ProgressBar progBar;
	private SurfaceHolder holder;
	private int postion = 0;
	private int vWidth, vHeight;
	private Display currDisplay;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_player);
		surface1 = (SurfaceView) findViewById(R.id.surfaceView1);
		// start = (Button) findViewById(R.id.button1);
		progBar = (ProgressBar) findViewById(R.id.progressBar1);

		holder = surface1.getHolder();
		holder.addCallback(this);
		surface1.getHolder().setKeepScreenOn(true);
		holder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		player = new MediaPlayer();
		player.setOnCompletionListener(this);
		player.setOnErrorListener(this);
		player.setOnInfoListener(this);
		player.setOnPreparedListener(this);
		player.setOnSeekCompleteListener(this);
		player.setOnVideoSizeChangedListener(this);
		try {

			String dataPath = getResources().getString(R.string.play_url);
			player.setDataSource(PlayerActivity.this, Uri.parse(dataPath));
			Log.v("Next:::", "surfaceDestroyed called");
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalStateException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		currDisplay = this.getWindowManager().getDefaultDisplay();
	}

	@Override
	protected void onPause() {
		if (player.isPlaying()) {
			postion = player.getCurrentPosition();
			player.stop();
		}
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		if (player.isPlaying())
			player.stop();
		player.release();
		super.onDestroy();
	}

	@Override
	public void surfaceChanged(SurfaceHolder holder, int format, int width,
			int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void surfaceCreated(SurfaceHolder holder) {
		player.setDisplay(holder);
		player.prepareAsync();
	}

	@Override
	public void surfaceDestroyed(SurfaceHolder holder) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onVideoSizeChanged(MediaPlayer mp, int width, int height) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onSeekComplete(MediaPlayer mp) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onPrepared(MediaPlayer mp) {
		vWidth = player.getVideoWidth();

		vHeight = player.getVideoHeight();

		if (vWidth > currDisplay.getWidth()
				|| vHeight > currDisplay.getHeight()) {

			// ���video�Ŀ���߸߳����˵�ǰ��Ļ�Ĵ�С����Ҫ��������

			float wRatio = (float) vWidth / (float) currDisplay.getWidth();

			float hRatio = (float) vHeight / (float) currDisplay.getHeight();
			// ѡ����һ����������
			float ratio = Math.max(wRatio, hRatio);
			vWidth = (int) Math.ceil((float) vWidth / ratio);
			vHeight = (int) Math.ceil((float) vHeight / ratio);
			// ����surfaceView�Ĳ��ֲ���
			surface1.setLayoutParams(new LinearLayout.LayoutParams(vWidth,
					vHeight));
			// Ȼ��ʼ������Ƶ

		}
		player.start();
	}

	private long exitTime = 0;

	public void ExitApp() {
		if ((System.currentTimeMillis() - exitTime) > 2000) {
			Toast.makeText(this, "�ٰ�һ���˳�����", Toast.LENGTH_SHORT).show();
			exitTime = System.currentTimeMillis();
		} else {
			this.finish();
		}
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if ((keyCode == KeyEvent.KEYCODE_BACK)) {
			if (System.currentTimeMillis() - exitTime > 2000) // 2s���ٴ�ѡ��back����Ч
			{
				System.out.println(Toast.LENGTH_LONG);
				Toast.makeText(this, "���ڰ�һ�η����˳�", Toast.LENGTH_LONG)
						.show();
				exitTime = System.currentTimeMillis();
			} else {
				finish();
				System.exit(0); // ���Ƿ��㶼��ʾ�쳣�˳�!0��ʾ���˳�!
			}

			return true;
		} else {
			return super.onKeyDown(keyCode, event);
		}
	}

	@Override
	public boolean onInfo(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_INFO_BAD_INTERLEAVING:
			break;
		case MediaPlayer.MEDIA_INFO_METADATA_UPDATE:
			break;
		case MediaPlayer.MEDIA_INFO_VIDEO_TRACK_LAGGING:
			break;
		case MediaPlayer.MEDIA_INFO_NOT_SEEKABLE:
			break;
		}
		return false;
	}

	@Override
	public boolean onError(MediaPlayer mp, int what, int extra) {
		switch (what) {
		case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
			Log.v("Play Error:::", "MEDIA_ERROR_SERVER_DIED");
			break;
		case MediaPlayer.MEDIA_ERROR_UNKNOWN:
			Log.v("Play Error:::", "MEDIA_ERROR_UNKNOWN");
			break;
		default:
			break;
		}
		return false;
	}

	@Override
	public void onCompletion(MediaPlayer mp) {
		Log.v("Play Over:::", "onComletion called");
		this.finish();
	}
}
