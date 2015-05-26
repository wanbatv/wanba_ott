package wanba.ott.activity;

import wanba.ott.activity.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

public class LoadingActivity extends Activity {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.loading);

//		new Handler().postDelayed(new Runnable() {
//			public void run() {
//				LoadingActivity.this.finish();
//				Intent intent=new Intent(LoadingActivity.this,MainActivity.class);
//				startActivity(intent);
//			}
//		}, 5000);
	}
}