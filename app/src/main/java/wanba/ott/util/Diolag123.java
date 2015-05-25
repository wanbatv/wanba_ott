package wanba.ott.util;


import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import wanba.ott.activity.R;

public class Diolag123 extends RelativeLayout {

	private Context context;
	private AttributeSet attrs;
	static Bitmap bx;
	 private ImageView member_iv1;
	 static int num;
	 static Diolag123 dio;
	public Diolag123(Context context, AttributeSet attrs) {
		super(context, attrs);
		// TODO Auto-generated constructor stub
		this.context=context;
		this.attrs=attrs;
		
		LayoutInflater.from(context).inflate(R.layout.alert_dialog_layout, this, true);
	
		member_iv1=(ImageView)this.findViewById(R.id.member_iv1);
//		member_iv1.buildDrawingCache();
//		Bitmap bmap = member_iv1.getDrawingCache();
//		refresh();
		
	}
	

	private void refresh() {
		// TODO Auto-generated method stub
//		Bitmap bm= ShopAction.getImage(4, num, null);
		member_iv1.setImageBitmap(bx);
	}

}
