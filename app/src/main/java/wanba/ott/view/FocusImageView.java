package wanba.ott.view;

import wanba.ott.activity.R;
import android.content.Context;
import android.content.res.TypedArray;
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
public class FocusImageView extends ImageView {

	Paint paint;

	public FocusImageView(Context context) {
		super(context);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		paint.setARGB(255, 0, 216, 255);
		this.setFocusable(true);
		this.setClickable(true);
	}

	public FocusImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);
		this.setFocusable(true);
		this.setClickable(true);
	}

	public FocusImageView(Context context, AttributeSet attrs) {
		super(context, attrs);
		paint = new Paint(Paint.ANTI_ALIAS_FLAG);

		// 读取自定义属性
		TypedArray array = context.obtainStyledAttributes(attrs,
				R.styleable.FocusImageView);
		// 0xFFFF00 黄色
		//0xFF00D8FF 淡蓝色
		int color = array.getColor(0, 0xFF00D8FF);
		paint.setColor(color);

		array.recycle();
		this.setFocusable(true);
		this.setClickable(true);
	}

	@Override
	public void draw(Canvas canvas) {
		super.draw(canvas);
		if (this.isFocused()) {
			Rect rect1 = getRect(canvas);
			RectF rectF = new RectF(rect1.left, rect1.top, rect1.right,
					rect1.bottom);
			// #FF00D8FF

			paint.setStyle(Paint.Style.STROKE);
			paint.setStrokeWidth(4);
			paint.setAlpha(200);
			canvas.drawRoundRect(rectF, 20, 20, paint);

		}
	}

	protected Rect getRect(Canvas canvas) {
		Rect rect = null;
		if (this.getBackground() != null) {
			rect = this.getBackground().copyBounds();

		} else {
			rect = canvas.getClipBounds();
		}

		rect.bottom -= getPaddingBottom();
		rect.right -= getPaddingRight();
		rect.left += getPaddingLeft();
		rect.top += getPaddingTop();
		return rect;
	}

}
