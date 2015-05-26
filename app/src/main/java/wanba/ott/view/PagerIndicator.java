package wanba.ott.view;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

/**
 * Created by Forcs on 15/5/19.
 */
public class PagerIndicator extends View {

    private String mCurrentPage = null;
    private String mTotalPage = null;

    private Rect mTextBounds = new Rect();

    private float mOffset = 0;

    private Paint mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

    public PagerIndicator(Context context) {
        super(context);
        init(context);
    }

    public PagerIndicator(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public PagerIndicator(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mPaint.setStyle(Paint.Style.FILL);
        mPaint.setTextAlign(Paint.Align.CENTER);
        mPaint.setColor(Color.WHITE);
        mPaint.setTextSize(32.0f * context.getResources().getDisplayMetrics().density);
    }

    public void offset(float offset) {
        mOffset = offset;
        postInvalidate();
    }

    public void setCurrentPage(String currPage) {
        mCurrentPage = currPage;
        requestLayout();
    }

    public void setTotalPage(String totalPage) {
        mTotalPage = totalPage;
        requestLayout();
    }

    public void setTextColor(int color) {
        mPaint.setColor(color);
        postInvalidate();
    }

    public void setTextSize(float size) {
        mPaint.setTextSize(size);
        requestLayout();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        final int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int widthMeasured = widthSize;
        int heightMeasured = heightSize;

        String currPage = mCurrentPage != null ? mCurrentPage : "0";
        String totalPage = mTotalPage != null ? mTotalPage : "0";
        String txt = currPage + " / " + totalPage;

        mPaint.getTextBounds(txt, 0, txt.length(), mTextBounds);

        switch (widthMode) {
            case MeasureSpec.AT_MOST:
                widthMeasured = Math.min(mTextBounds.width(), widthSize);
                break;
            case MeasureSpec.EXACTLY:
                widthMeasured = widthSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                widthMeasured = mTextBounds.width();
                break;
        }

        switch (heightMode) {
            case MeasureSpec.AT_MOST:
                heightMeasured = Math.min(mTextBounds.height(), heightSize);
                break;
            case MeasureSpec.EXACTLY:
                heightMeasured = heightSize;
                break;
            case MeasureSpec.UNSPECIFIED:
                heightMeasured = mTextBounds.height();
                break;
        }

        setMeasuredDimension(widthMeasured + getPaddingLeft() + getPaddingRight(),
                heightMeasured + getPaddingTop() + getPaddingBottom());
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        String currPage = mCurrentPage != null ? mCurrentPage : "0";
        String totalPage = mTotalPage != null ? mTotalPage : "0";
        String txt = currPage + " / " + totalPage;

        mPaint.getTextBounds(txt, 0, txt.length(), mTextBounds);

        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        canvas.drawText(txt, width / 2 + mOffset, (height + mTextBounds.height()) / 2, mPaint);
    }
}
