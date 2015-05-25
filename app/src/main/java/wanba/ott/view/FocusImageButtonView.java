package wanba.ott.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.os.HandlerThread;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.ViewPropertyAnimator;
import android.widget.ImageView;

/**
 * 自定义imageView 为了能够在电视盒上使用黄色的选中框
 *
 */
public class FocusImageButtonView extends ImageView {

	public FocusImageButtonView(Context context) {
		super(context);
		this.setFocusable(true);
		this.setClickable(true);
	}

	public FocusImageButtonView(Context context, AttributeSet attrs,
			int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		this.setFocusable(true);
		this.setClickable(true);
	}

	public FocusImageButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);
		this.setFocusable(true);
		this.setClickable(true);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (this.isFocused()) {
			Rect rect1 = getRect(canvas);
			Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
			paint.setARGB(255, 0, 216, 255);
			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			paint.setAlpha(200);
			canvas.drawRect(rect1, paint);

		}
	}

	protected Rect getRect(Canvas canvas) {

		Rect rect = canvas.getClipBounds();
		rect.bottom -= getPaddingBottom();
		rect.right -= getPaddingRight();
		rect.left += getPaddingLeft();
		rect.top += getPaddingTop();
		return rect;
	}

}
