package wanba.ott.activity;

import wanba.ott.activity.R;
import android.app.Dialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

public class WebViewDialog extends Dialog {

	private String url;

	public WebViewDialog(Context context) {
		super(context);
	}

	private void setCustomeDialog() {
		View mView = LayoutInflater.from(getContext()).inflate(
				R.layout.dialog_web_view, null);

		super.setContentView(mView);
	}

}
