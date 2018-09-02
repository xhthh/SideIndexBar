package com.xht.indexbar.decoration;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

import com.xht.indexbar.R;
import com.xht.indexbar.bean.CityBean;

import java.util.List;


/**
 * getItemOffsets 是针对每一个 ItemView，而 onDraw 方法却是针对 RecyclerView 本身，
 * 所以在 onDraw 方法中需要遍历屏幕上可见的 ItemView，分别获取它们的位置信息，然后分别的绘制对应的分割线。
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {
    private float dividerHeight;
    private Paint mPaint;
    private Context mContext;
    private List<CityBean> mDatas;

    public DividerItemDecoration(Context context, List<CityBean> datas) {
        this.mContext = context;
        this.mDatas = datas;
        mPaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        mPaint.setColor(mContext.getResources().getColor(R.color.divide));
        dividerHeight = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,
                1f, context.getResources().getDisplayMetrics());
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        outRect.bottom = (int) dividerHeight;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int childCount = parent.getChildCount();
        //int left = parent.getPaddingLeft();
        //int right = parent.getWidth() - parent.getPaddingRight();

        int left = (int) mContext.getResources().getDimension(R.dimen.default_divider_padding);
        int right = parent.getWidth() - (int) mContext.getResources().getDimension(R.dimen.default_divider_padding);

        for (int i = 0; i < childCount - 1; i++) {
            View child = parent.getChildAt(i);
            float top = child.getBottom();
            float bottom = child.getBottom() + dividerHeight;

            final RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();
            int position = params.getViewLayoutPosition();

            if (mDatas != null && !mDatas.isEmpty() && position < mDatas.size() - 1 && position > -1) {
                if (null != mDatas.get(position).getIndexTag()
                        && mDatas.get(position).getIndexTag().equals(mDatas.get(position + 1).getIndexTag())) {
                    c.drawRect(left, top, right, bottom, mPaint);
                }
            }
        }
    }
}
