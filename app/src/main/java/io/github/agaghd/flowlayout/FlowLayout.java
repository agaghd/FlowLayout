package io.github.agaghd.flowlayout;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.support.annotation.IntRange;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

/**
 * author : wjy
 * time   : 2018/04/13
 * desc   : 流式布局，支持从左到右、从上到下的布局和反向布局
 */

public class FlowLayout extends ViewGroup {

    public static final int HORIZONTAL = 0;
    public static final int VERTICAL = 1;

    private int mOrientation = HORIZONTAL;
    private int mHorizontalWidth;
    private int mVerticalHeight;
    private int mWidth = 0, mHeight = 0;
    private int maxChildHeight = 0, maxChildWidth = 0;
    private int mPaddingLeft = 0, mPaddingRight = 0, mPaddingTop = 0, mPaddingBottom = 0;

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs, defStyleAttr, 0);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FlowLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs, defStyleAttr, defStyleRes);

    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        final TypedArray a = context.obtainStyledAttributes(
                attrs, R.styleable.FlowLayout, defStyleAttr, defStyleRes);
        //获取orientation
        int index = a.getInt(R.styleable.FlowLayout_orientation, -1);
        if (index >= 0) {
            setOrientation(index);
        }
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        final int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        switch (widthMode) {
            default: {
                break;
            }
        }
        switch (heightMode) {
            default: {
                break;
            }
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        mHorizontalWidth += mPaddingLeft;
        mVerticalHeight += mPaddingTop;
        findMaxChildWidthAndHeight();
        if (mOrientation == HORIZONTAL) {
            layoutHorizontalFlow(changed, l, t, r, b);
        } else if (mOrientation == VERTICAL) {
            layoutVerticalFlow(changed, l, t, r, b);
        }
    }

    private void layoutVerticalFlow(boolean changed, int l, int t, int r, int b) {
        // TODO: 2018/4/13 竖直方向流式布局
    }

    private void layoutHorizontalFlow(boolean changed, int l, int t, int r, int b) {
        // TODO: 2018/4/13 水平方向流式布局
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childWidth = childView.getWidth();
                int childMarginLeft = layoutParams.leftMargin;
                int childMarginRight = layoutParams.rightMargin;
                int totalWidth = childWidth + childMarginLeft + childMarginRight;
                if (mHorizontalWidth + totalWidth > mWidth) {
                    //换行
                    mHorizontalWidth = mPaddingLeft;
                    mVerticalHeight += maxChildHeight;
                }
                childView.layout(mHorizontalWidth + childMarginLeft, mVerticalHeight, mHorizontalWidth + totalWidth, mVerticalHeight + maxChildHeight);
            }
        }
    }

    /**
     * 参考Linerlayout的orientation
     *
     * @param orientation 方向
     */
    public void setOrientation(@IntRange(from = HORIZONTAL, to = VERTICAL) int orientation) {
        if (mOrientation != orientation) {
            mOrientation = orientation;
            requestLayout();
        }
    }

    /**
     * 找寻最大的子控件宽高，在onMeasure()之后调用
     */
    private void findMaxChildWidthAndHeight() {
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            maxChildHeight = Math.max(maxChildHeight, child.getMeasuredHeight());
            maxChildWidth = Math.max(maxChildWidth, child.getMeasuredWidth());
        }
        //子控件宽高超过父控件宽高时，按父控件宽高处理
        maxChildHeight = Math.min(maxChildHeight, mHeight);
        maxChildWidth = Math.min(maxChildWidth, mWidth);
    }

    @Override
    public LayoutParams generateLayoutParams(AttributeSet attrs) {
        return new LayoutParams(getContext(), attrs);
    }

    @Override
    protected LayoutParams generateDefaultLayoutParams() {
        return new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected LayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new LayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof LayoutParams;
    }

    public static class LayoutParams extends ViewGroup.MarginLayoutParams {

        public LayoutParams(Context c, AttributeSet attrs) {
            super(c, attrs);
        }

        public LayoutParams(int width, int height) {
            super(width, height);
        }

        public LayoutParams(MarginLayoutParams source) {
            super(source);
        }

        public LayoutParams(ViewGroup.LayoutParams source) {
            super(source);
        }

    }
}