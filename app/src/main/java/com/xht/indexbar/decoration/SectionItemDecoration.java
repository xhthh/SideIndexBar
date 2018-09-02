package com.xht.indexbar.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.xht.indexbar.R;
import com.xht.indexbar.bean.CityBean;

import java.util.List;

public class SectionItemDecoration extends RecyclerView.ItemDecoration {
    private List<CityBean> mData;
    private Paint mBgPaint;
    private TextPaint mTextPaint;
    private Rect mBounds;

    private int mSectionHeight;
    private int mBgColor;
    private int mTextColor;
    private int mTextSize;

    private Context mContext;

    public SectionItemDecoration(Context context, List<CityBean> data) {
        this.mData = data;
        this.mContext = context;

        mBgColor = mContext.getResources().getColor(R.color.section_bg);
        mSectionHeight = (int) mContext.getResources().getDimension(R.dimen.section_height);
        mTextSize = mContext.getResources().getDimensionPixelSize(R.dimen.section_text_size);
        mTextColor = Color.BLACK;

        mBgPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mBgPaint.setColor(mBgColor);

        mTextPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG);
        mTextPaint.setTextSize(mTextSize);
        mTextPaint.setColor(mTextColor);

        mBounds = new Rect();
    }

    public void setData(List<CityBean> data) {
        this.mData = data;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        super.onDraw(c, parent, state);
        final int left = parent.getPaddingLeft();
        final int right = parent.getWidth() - parent.getPaddingRight();
        final int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            final View child = parent.getChildAt(i);
            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();
            if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
                if (position == 0) {
                    // 第一个section位置画出分类标题
                    drawSection(c, left, right, child, params, position);
                } else {
                    // 其它位置和上一个进行比较，如果不同就在该section绘制分类标题
                    if (null != mData.get(position).getIndexTag() && !mData.get(position).getIndexTag().equals(mData.get(position - 1).getIndexTag())) {
                        drawSection(c, left, right, child, params, position);
                    }
                }
            }
        }
    }

    /**
     * 绘制分类标题背景和文字
     *
     * @param c
     * @param left
     * @param right
     * @param child
     * @param params
     * @param position
     */
    private void drawSection(Canvas c, int left, int right, View child, RecyclerView.LayoutParams params, int position) {
        //Log.i("xht", "onDraw()---child.getTop==" + child.getTop() + "  params.topMargin==" + params.topMargin + " mSectionHeight==" + mSectionHeight);
        // 绘制矩形背景
        c.drawRect(left, child.getTop() - params.topMargin - mSectionHeight, right,
                child.getTop() - params.topMargin, mBgPaint);
        mTextPaint.getTextBounds(mData.get(position).getIndexTag(), 0, mData.get(position).getIndexTag().length(),
                mBounds);
        c.drawText(mData.get(position).getIndexTag(),
                child.getPaddingLeft(),// child.getPaddingLeft()获取的是每个item的父布局中的paddingLeft属性
                child.getTop() - params.topMargin - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
    }

    /**
     * 实现悬停效果
     *
     * @param c
     * @param parent
     * @param state
     */
    @Override
    public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int pos = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
        if (pos < 0)
            return;
        if (mData == null || mData.isEmpty())
            return;
        String section = mData.get(pos).getIndexTag();
        View child = parent.findViewHolderForLayoutPosition(pos).itemView;

        boolean flag = false;//定义一个flag，Canvas是否位移过的标志
        if ((pos + 1) < mData.size()) {
            if (null != section && !section.equals(mData.get(pos + 1).getIndexTag())) {
                if (child.getHeight() + child.getTop() < mSectionHeight) {
                    c.save();//每次绘制前 保存当前Canvas状态
                    flag = true;
                    //上滑时，将canvas上移 （y为负数） ,所以后面canvas 画出来的Rect和Text都上移了，有种切换的“动画”感觉
                    c.translate(0, child.getHeight() + child.getTop() - mSectionHeight);
                }
            }
        }
        c.drawRect(parent.getPaddingLeft(), parent.getPaddingTop(), parent.getRight() - parent.getPaddingRight(),
                parent.getPaddingTop() + mSectionHeight, mBgPaint);
        mTextPaint.getTextBounds(section, 0, section.length(), mBounds);
        c.drawText(section, child.getPaddingLeft(),
                parent.getPaddingTop() + mSectionHeight - (mSectionHeight / 2 - mBounds.height() / 2),
                mTextPaint);
        if (flag)
            c.restore();
    }

    /**
     * 设置四个方向上 需要为itemView设置padding的值。
     *
     * @param outRect
     * @param view
     * @param parent
     * @param state
     */
    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        int position = ((RecyclerView.LayoutParams) view.getLayoutParams()).getViewLayoutPosition();
        if (mData != null && !mData.isEmpty() && position <= mData.size() - 1 && position > -1) {
            if (position == 0) {
                // 将第一个正常的item距离顶部一个sectionHeight距离
                outRect.set(0, mSectionHeight, 0, 0);
            } else {
                if (null != mData.get(position).getIndexTag()
                        && !mData.get(position).getIndexTag().equals(mData.get(position - 1).getIndexTag())) {
                    // 判断如果下一个正常的item和上一个正常item的section不同，则距离顶部一个sectionHeight距离
                    outRect.set(0, mSectionHeight, 0, 0);
                }
            }
        }
    }
}
