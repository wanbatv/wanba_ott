package wanba.ott.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.android.volley.Response;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

import wanba.ott.util.AppUtil;
import wanba.ott.util.UrlUtils;

public class OpenActivityAction implements View.OnClickListener {
	@SuppressWarnings("rawtypes")
	private Class activityClass;
	private Map<String, String> params;
	private Context context;

	private String mProductCode = null;
	private String mTranId = null;
	private boolean mNeedAuth = false;
	private Drawable mOrderPrimaryButtonBackground = null;
	private Drawable mOrderSecondaryButtonBackground = null;

	public OpenActivityAction(Context context, Class activityClass,
			Map<String, String> params) {
		this.activityClass = activityClass;
		this.context = context;
		this.params = params;
	}

	public OpenActivityAction(Context context, Class activityClass) {
		this.activityClass = activityClass;
		this.context = context;
	}

	public OpenActivityAction setOrderPrimaryButtonBackground(Drawable drawable) {
		mOrderPrimaryButtonBackground = drawable;
		return this;
	}

	public Drawable getOrderPrimaryButtonBackground() {
		return mOrderPrimaryButtonBackground;
	}

	public OpenActivityAction setOrderSecondaryButtonBackground(Drawable drawable) {
		mOrderSecondaryButtonBackground = drawable;
		return this;
	}

	public Drawable getOrderSecondaryButtonBackground() {
		return mOrderSecondaryButtonBackground;
	}

	public OpenActivityAction setNeedAuth(boolean needAuth) {
		mNeedAuth = needAuth;
		return this;
	}

	public boolean isNeedAuth() {
		return mNeedAuth;
	}

	public OpenActivityAction setProductCode(String productCode) {
		mProductCode = productCode;
		return this;
	}

	public String getProductCode() {
		return mProductCode;
	}

	public OpenActivityAction setTranId(String tranId) {
		mTranId = tranId;
		return this;
	}

	public String getTranId() {
		return mTranId;
	}

	@Override
	public void onClick(final View v) {
		if (!mNeedAuth) {
			Intent intent = new Intent(context, activityClass);

			if (params != null) {
				for (String str : params.keySet()) {
					intent.putExtra(str, params.get(str));
				}
			}
			context.startActivity(intent);
			return;
		}

		String url = UrlUtils.getAuthUrl(mProductCode, mTranId);
		JsonObjectRequest request = new JsonObjectRequest(url, null, new Response.Listener<JSONObject>() {
			@Override
			public void onResponse(JSONObject jsonObject) {
				if (jsonObject != null) {
					if (jsonObject.has("statusCode")) {
						int statusCode = -1;
						try {
							statusCode = jsonObject.getInt("statusCode");
						} catch (JSONException e) {
							e.printStackTrace();
						}
						if (statusCode > -1) {
							if (statusCode == 1) {
								Intent intent = new Intent(context, activityClass);

								if (params != null) {
									for (String str : params.keySet()) {
										intent.putExtra(str, params.get(str));
									}
								}
								context.startActivity(intent);
							} else {


								final PopupWindow ordeLayer = new PopupWindow(context);
								View contentView = LayoutInflater.from(context)
										.inflate(R.layout.channel_order_wizzard_popup_layer, null);
								View backButton = contentView.findViewById(R.id.channel_order_wizzard_back);
								backButton.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										ordeLayer.dismiss();
										String orderUrl = UrlUtils.getOrderUrl(mProductCode, mTranId);
										Intent intent = new Intent("com.wanba.ott.intent.ORDER");

										intent.putExtra(OrderActivity.EXTRA_ORDER_URL, orderUrl);
										Bundle argument = new Bundle();
										if (params != null) {
											for (String str : params.keySet()) {
												argument.putString(str, params.get(str));
											}
										}
										intent.putExtra(OrderActivity.EXTRA_ORDER_ARGUMENT, argument);
										if (context instanceof Activity) {
											((Activity) context).startActivityForResult(intent, OrderActivity.REQUEST_CODE_RESULT);
										} else {
											context.startActivity(intent);
										}
									}
								});

								if (mOrderPrimaryButtonBackground != null) {
									ImageView imgPrimaryBtnBg = (ImageView) contentView.findViewById(
											R.id.channel_order_wizzard_primary_btn_background);
									imgPrimaryBtnBg.setImageDrawable(mOrderPrimaryButtonBackground);
								}

								View primaryButton = contentView.findViewById(R.id.channel_order_wizzard_btn_primary);
								primaryButton.requestFocus();
								primaryButton.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										ordeLayer.dismiss();
										String orderUrl = UrlUtils.getOrderUrl(mProductCode, mTranId);
										Intent intent = new Intent("com.wanba.ott.intent.ORDER");

										intent.putExtra(OrderActivity.EXTRA_ORDER_URL, orderUrl);
										Bundle argument = new Bundle();
										if (params != null) {
											for (String str : params.keySet()) {
												argument.putString(str, params.get(str));
											}
										}
										intent.putExtra(OrderActivity.EXTRA_ORDER_ARGUMENT, argument);
										if (context instanceof Activity) {
											((Activity) context).startActivityForResult(intent, OrderActivity.REQUEST_CODE_RESULT);
										} else {
											context.startActivity(intent);
										}
									}
								});

								if (mOrderSecondaryButtonBackground != null) {
									ImageView imgSecondaryBtnBg = (ImageView) contentView.findViewById(
											R.id.channel_order_wizzard_secondary_btn_background);
									imgSecondaryBtnBg.setImageDrawable(mOrderSecondaryButtonBackground);
								}

								View secondaryButton = contentView.findViewById(R.id.channel_order_wizzard_btn_secondary);
								secondaryButton.setOnClickListener(new View.OnClickListener() {
									@Override
									public void onClick(View v) {
										ordeLayer.dismiss();
									}
								});
								ordeLayer.setBackgroundDrawable(new ColorDrawable(0x99000000));
								ordeLayer.setFocusable(true);
								ordeLayer.setContentView(contentView);
								ordeLayer.setWidth(context.getResources().getDisplayMetrics().widthPixels);
								ordeLayer.setHeight(context.getResources().getDisplayMetrics().heightPixels);
								ordeLayer.showAtLocation(v, Gravity.NO_GRAVITY, 0, 0);
							}
						}
					}
				}
			}
		}, null);
		AppUtil.getInstance(this.context).getRequestQueue().add(request);

	}
}
