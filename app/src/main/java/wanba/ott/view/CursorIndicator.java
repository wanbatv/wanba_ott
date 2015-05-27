package wanba.ott.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import wanba.ott.activity.R;

/**
 * Created by Forcs on 15/5/27.
 */
public class CursorIndicator extends View {

    private static final int DEF_NORMAL_COLOR = Color.DKGRAY;
    private static final int DEF_HIGHLIGHT_COLOR = Color.LTGRAY;
    private static final float DEF_MARGIN_SIZE = 8.0f;
    private static final float DEF_RADIUS = 4.0f;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private int mNormalColor = 0;
    private int mHighlightColor = 0;
    private int mCount = 0;
    private int mCurrent = 0;
    private float mOffset = 0;
    private float mMarginSize = 0.0f;
    private float mRadius = 0.0f;

    public CursorIndicator(Context context) {
        super(context);
        init(context, null);
    }

    public CursorIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public CursorIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        mNormalColor = DEF_NORMAL_COLOR;
        mHighlightColor = DEF_HIGHLIGHT_COLOR;

        final float density = getResources().getDisplayMetrics().density;
        mMarginSize = DEF_MARGIN_SIZE * density;
        mRadius = DEF_RADIUS * density;

        final TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CursorIndicator);
        if (array != null) {
            final int N = array.length();
            for (int i = 0; i < N; i++) {
                final int index = array.getIndex(i);
                switch (index) {
                    case R.styleable.CursorIndicator_ci_radius:
                        mRadius = array.getDimension(index, DEF_RADIUS * density);
                        break;
                    case R.styleable.CursorIndicator_ci_margin:
                        mMarginSize = array.getDimension(index, DEF_MARGIN_SIZE * density);
                        break;
                    case R.styleable.CursorIndicator_ci_normalColor:
                        mNormalColor = array.getColor(index, DEF_NORMAL_COLOR);
                        break;
                    case R.styleable.CursorIndicator_ci_highlightColor:
                        mHighlightColor = array.getColor(index, DEF_HIGHLIGHT_COLOR);
                        break;
                }
            }
        }
    }

    /**
     * 设置正常游标的颜色
     * @param color 颜色
     */
    public void setNormalColor(int color) {
        mNormalColor = color;
        postInvalidate();
    }

    /**
     * 设置当前高亮游标的颜色
     * @param color 颜色
     */
    public void setHighlightColor(int color) {
        mHighlightColor = color;
        postInvalidate();
    }

    /**
     * 设置总共的游标个数
     * @param count 总数
     */
    public void setCount(int count) {
        mCount = count;
        requestLayout();
    }

    /**
     * 设置当前高亮的游标位置
     * @param current 当前位置
     */
    public void setCurrent(int current) {
        mCurrent = current;
        postInvalidate();
    }

    /**
     * 偏移当前游标位置
     * @param offset 偏移量
     */
    public void offsetCurrent(float offset) {
        mOffset = offset;
        postInvalidate();
    }

    /**
     * 设置游标的边距
     * @param margin 边距
     */
    public void setCursorMargin(float margin) {
        mMarginSize = margin;
        requestLayout();
    }

    /**
     * 设置游标的半径
     * @param radius 半径
     */
    public void setCurrsorRadius(float radius) {
        mRadius = radius;
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (mCount <= 0) {
            return;
        }

        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);

        int measuredWidth = widthSize;
        int measuredHeight = heightSize;

        if (widthMode != MeasureSpec.EXACTLY) {
            final float r = mRadius;
            final float margin = mMarginSize;
            final int n = mCount;
            float width = r * 2 * n + margin * (n - 1);
            switch (widthMode) {
                case MeasureSpec.AT_MOST:
                    measuredWidth = (int) Math.min(measuredWidth, width);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    measuredWidth = (int) width;
                    break;
            }
        }

        if (heightMode != MeasureSpec.EXACTLY) {
            final float r = mRadius;
            float height = r * 2;
            switch (heightMode) {
                case MeasureSpec.AT_MOST:
                    measuredHeight = (int) Math.min(measuredHeight, height);
                    break;
                case MeasureSpec.UNSPECIFIED:
                    measuredHeight = (int) height;
                    break;
            }
        }

        setMeasuredDimension(measuredWidth, measuredHeight);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (mCount <= 0) { //没有游标，不画内容
            return;
        }

        final int count = mCount;
        final float r = mRadius;
        final float margin = mMarginSize;
        final float offset = mOffset;
        final int width = getMeasuredWidth();
        final int height = getMeasuredHeight();

        final float cy = height / 2;
        float cx;
        float cursorWidth = r * 2 * count + margin * (count - 1);
        float startX = (width - cursorWidth) / 2;

        for (int i = 0; i < count; i++) {
            cx = startX + r;
            int color = (offset == 0 && i == mCurrent) ? mHighlightColor : mNormalColor;
            mPaint.setColor(color);
            canvas.drawCircle(cx, cy, r, mPaint);

            if (offset > 0) {
                canvas.drawCircle(cx + offset, cy, r, mPaint);
            }

            startX += r * 2 + margin;
        }

     }
}
