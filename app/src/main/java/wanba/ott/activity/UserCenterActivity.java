package wanba.ott.activity;

import wanba.ott.activity.R;
import wanba.ott.abstracts.activity.FullScreenActivity;
import android.os.Bundle;

public class UserCenterActivity extends FullScreenActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_center);
	}
}
