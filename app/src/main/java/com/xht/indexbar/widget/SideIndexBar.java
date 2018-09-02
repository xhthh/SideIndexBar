package com.xht.indexbar.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import com.xht.indexbar.R;


/**
 * Created by xht on 2018/3/1.
 */

public class SideIndexBar extends View {
    private static final String[] mLetters = {"A", "B", "C", "D", "E", "F", "G", "H",
            "I", "J", "K", "L", "M", "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z", "#"};
    private Paint mPaint;
    private Paint mTouchPaint;
    private int mItemHeight;// 每个字母所占位置的高度
    private int mCurrentTouchIndex = -1;// 当前触摸的位置
    private int mWidth;
    private int mHeight;
    private int maxHeight;
    private int mTopMargin;

    private TextView mOverlayTextView;// 显示的当前触摸字母的控件
    private OnLetterTouchListener mOnLetterTouchListener;
    private int mTextSize;
    private int mTextColor;
    private int mTextTouchedColor;

    public SideIndexBar(Context context) {
        super(context, null);
    }

    public SideIndexBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SideIndexBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.SideIndexBar);

        mTextSize = typedArray.getDimensionPixelSize(R.styleable.SideIndexBar_indexBarTextSize, (int) sp2Px(16));
        mTextColor = typedArray.getColor(R.styleable.SideIndexBar_indexBarNormalTextColor, getResources().getColor(R.color.colorAccent));
        mTextTouchedColor = typedArray.getColor(R.styleable.SideIndexBar_indexBarTouchedTextColor, getResources().getColor(R.color.colorPrimary));
        Log.i("xht", "SideIndexBar--init()--mTextSize==" + mTextSize + "  mTextColor==" + mTextColor + "  mTextTouchedColor==" + mTextTouchedColor);

        typedArray.recycle();

        mPaint = new Paint();
        mPaint.setAntiAlias(true);
        // 自定义属性：颜色、大小
        mPaint.setTextSize(mTextSize);
        mPaint.setColor(mTextColor);// 默认字母颜色

        mTouchPaint = new Paint();
        mTouchPaint.setAntiAlias(true);
        mTouchPaint.setTextSize(mTextSize);
        mTouchPaint.setColor(mTextTouchedColor);// 选中的字母颜色
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        // 计算宽度 = 左右的padding + 字母的宽度(取决于画笔)
        int textWidth = (int) mPaint.measureText("A");
        int width = getPaddingLeft() + getPaddingRight() + textWidth;

        // 获取高度
        int height = MeasureSpec.getSize(heightMeasureSpec);

        setMeasuredDimension(width, height);
    }

    /**
     * 使用EditText控件时，调起软键盘会将布局上顶，高度会发生变化，取前后的最大值为高度
     * <p>
     * bug:切换中英文时oldh和h均不为最大值
     * 这里用一个maxHeight来记录最大值
     *
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mWidth = getWidth();
        if (maxHeight == 0) {
            maxHeight = Math.max(h, oldh);
        }
        mHeight = Math.max(maxHeight, Math.max(h, oldh));
        mItemHeight = mHeight / mLetters.length;
        mTopMargin = (mHeight - mItemHeight * mLetters.length) / 2;
        Log.i("xht", "maxHeight==" + maxHeight + "  onSizeChanged()--w==" + w + " h==" + h + "  oldw==" + oldw + "  oldh==" + oldh
                + "  mWidth==" + mWidth + " mHeight==" + mHeight + "  mItemHeight==" + mItemHeight + "  mTopMargin==" + mTopMargin);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // 画26个字母
        mItemHeight = (mHeight - getPaddingTop() - getPaddingBottom()) / mLetters.length;
        for (int i = 0; i < mLetters.length; i++) {
            // 知道每个字母的中心位置 1 字母的高度一半 2 字母高度一半 + 前面字母的高度
            int letterCenterY = i * mItemHeight + mItemHeight / 2 + getPaddingTop() + mTopMargin;
            Paint.FontMetrics fontMetrics = mPaint.getFontMetrics();
            //top表示基线到文字最上面的位置的距离 是个负值 bottom为基线到最下面的距离，为正值
            int dy = (int) ((fontMetrics.bottom - fontMetrics.top) / 2 - fontMetrics.bottom);
            // 基线，基于中心位置
            int baseLine = letterCenterY + dy;
            // x 绘制在最中间 = 宽度 / 2 - 文字 / 2
            int textWidth = (int) mPaint.measureText(mLetters[i]);
            int x = mWidth / 2 - textWidth / 2;

            // 当前字母 高亮 用两个画笔 判断是否是当前触摸的字母
            canvas.drawText(mLetters[i], x, baseLine, mCurrentTouchIndex == i ? mTouchPaint : mPaint);
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
            case MotionEvent.ACTION_MOVE:
                // 计算出当前触摸的字母
                float currentY = event.getY();
                int indexSize = mLetters.length;
                int touchedIndex = (int) (currentY / mItemHeight);
                if (touchedIndex < 0) {
                    touchedIndex = 0;
                } else if (touchedIndex > indexSize - 1) {
                    touchedIndex = indexSize - 1;
                }

                if (mOnLetterTouchListener != null && touchedIndex >= 0 && touchedIndex < indexSize) {
                    if (touchedIndex != mCurrentTouchIndex) {
                        mCurrentTouchIndex = touchedIndex;
                        if (mOverlayTextView != null) {
                            mOverlayTextView.setVisibility(View.VISIBLE);
                            mOverlayTextView.setText(mLetters[touchedIndex]);
                        }
                        mOnLetterTouchListener.onIndexChanged(mLetters[touchedIndex], touchedIndex);
                        // 重新绘制
                        invalidate();
                    }
                }

                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mCurrentTouchIndex = -1;
                if (mOverlayTextView != null) {
                    mOverlayTextView.setVisibility(GONE);
                }
                invalidate();
                break;
        }
        return true;

    }

    /**
     * 设置TextView显示当前触摸到的字母
     *
     * @param overlay
     * @return
     */
    public SideIndexBar setOverlayTextView(TextView overlay) {
        this.mOverlayTextView = overlay;
        return this;
    }

    public SideIndexBar setOnLetterTouchListener(OnLetterTouchListener listener) {
        this.mOnLetterTouchListener = listener;
        return this;
    }

    public interface OnLetterTouchListener {
        void onIndexChanged(String letter, int position);
    }

    /**
     * sp转px
     *
     * @param sp
     * @return
     */
    private float sp2Px(int sp) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, sp, getResources().getDisplayMetrics());
    }
}
