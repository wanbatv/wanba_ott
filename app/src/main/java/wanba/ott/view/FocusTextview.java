package wanba.ott.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by ÓáÑÇéª on 2015/5/25.
 */
public class FocusTextview extends TextView {
    public FocusTextview(Context context) {
        super(context);
        this.setFocusable(true);
        this.setClickable(true);
    }
    public FocusTextview(Context context, AttributeSet attrs,
                                int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.setFocusable(true);
        this.setClickable(true);
    }

    public FocusTextview(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.setFocusable(true);
        this.setClickable(true);
    }

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
