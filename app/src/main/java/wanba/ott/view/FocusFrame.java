package wanba.ott.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Forcs on 15/5/22.
 */
public class FocusFrame extends View {

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public FocusFrame(Context context) {
        super(context);
        init(context);
    }

    public FocusFrame(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FocusFrame(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        setFocusable(true);
        mPaint.setStyle(Paint.Style.STROKE);
        mPaint.setColor(Color.WHITE);
        mPaint.setStrokeWidth(8.0f * context.getResources().getDisplayMetrics().density);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (isFocused()) {
            final int w = getMeasuredWidth();
            final int h = getMeasuredHeight();
            canvas.drawRect(0, 0, w, h, mPaint);
        }
    }
}
