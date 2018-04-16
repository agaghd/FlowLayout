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
    private int lineSpaceVertical = 0, lineSpaceHorizontal = 0;
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
        //获取行列间距
        index = (int) a.getDimension(R.styleable.FlowLayout_line_space_horizontal, 0);
        lineSpaceHorizontal = index >= 0 ? index : 0;
        index = (int) a.getDimension(R.styleable.FlowLayout_line_space_vertical, 0);
        lineSpaceVertical = index >= 0 ? index : 0;
        a.recycle();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int n = getChildCount();
        for (int i = 0; i < n; i++) {
            View child = getChildAt(i);
            child.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED);
            MarginLayoutParams layoutParams = (MarginLayoutParams) child.getLayoutParams();
            int widthTotal = child.getMeasuredHeight() + layoutParams.topMargin + layoutParams.bottomMargin;
            int heightTotal = child.getMeasuredWidth() + layoutParams.leftMargin + layoutParams.rightMargin;
            maxChildWidth = Math.max(maxChildWidth, heightTotal);
            maxChildHeight = Math.max(maxChildHeight, widthTotal);
        }
        int widthMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        if (mOrientation == VERTICAL) {
            mHeight = MeasureSpec.getSize(heightMeasureSpec);
            switch (widthMode) {
                case MeasureSpec.EXACTLY: {
                    mWidth = MeasureSpec.getSize(widthMeasureSpec);
                    break;
                }
                default: {
                    mWidth = getWidthWhenHeightExactly();
                    break;
                }
            }
        } else {
            mWidth = MeasureSpec.getSize(widthMeasureSpec);
            switch (heightMode) {
                case MeasureSpec.EXACTLY: {
                    mHeight = MeasureSpec.getSize(heightMeasureSpec);
                    break;
                }
                default: {
                    mHeight = getHeightWhenWidthExactly();
                    break;
                }
            }
        }
        setMeasuredDimension(mWidth, mHeight);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        mPaddingLeft = getPaddingLeft();
        mPaddingRight = getPaddingRight();
        mPaddingTop = getPaddingTop();
        mPaddingBottom = getPaddingBottom();
        mHorizontalWidth = mPaddingLeft;
        mVerticalHeight = mPaddingTop;
        if (mOrientation == HORIZONTAL) {
            layoutHorizontalFlow();
        } else if (mOrientation == VERTICAL) {
            layoutVerticalFlow();
        }
    }

    private void layoutVerticalFlow() {
        //竖直方向流式布局
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                int childMarginLeft = layoutParams.leftMargin;
                int childMarginTop = layoutParams.topMargin;
                int childMarginBottom = layoutParams.bottomMargin;
                int totalHeight = childHeight + childMarginTop + childMarginBottom;
                if (mVerticalHeight + totalHeight > mHeight - mPaddingBottom) {
                    //换列
                    mVerticalHeight = mPaddingTop;
                    mHorizontalWidth += maxChildWidth + lineSpaceHorizontal;
                }
                childView.layout(mHorizontalWidth + childMarginLeft,
                        mVerticalHeight + childMarginTop,
                        mHorizontalWidth + childWidth,
                        mVerticalHeight + childHeight + childMarginTop);
                mVerticalHeight += totalHeight + lineSpaceVertical;
            }
        }
    }

    private void layoutHorizontalFlow() {
        //水平方向流式布局
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childWidth = childView.getMeasuredWidth();
                int childHeight = childView.getMeasuredHeight();
                int childMarginLeft = layoutParams.leftMargin;
                int childMarginRight = layoutParams.rightMargin;
                int childMarginBottom = layoutParams.bottomMargin;
                int totalWidth = childWidth + childMarginLeft + childMarginRight;
                if (mHorizontalWidth + totalWidth > mWidth - mPaddingRight) {
                    //换行
                    mHorizontalWidth = mPaddingLeft;
                    mVerticalHeight += maxChildHeight + lineSpaceVertical;
                }
                childView.layout(mHorizontalWidth + childMarginLeft,
                        mVerticalHeight + maxChildHeight - childHeight - childMarginBottom,
                        mHorizontalWidth + totalWidth,
                        mVerticalHeight + maxChildHeight);
                mHorizontalWidth += totalWidth + lineSpaceHorizontal;
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
     * 设置垂直方向间距的方法
     *
     * @param lineSpaceVertical 垂直方向的间距 单位dp
     */
    public void setLineSpaceVertical(int lineSpaceVertical) {
        if (this.lineSpaceVertical != lineSpaceVertical) {
            this.lineSpaceVertical = lineSpaceVertical;
            requestLayout();
        }
    }

    /**
     * 设置水平方向间距的接口
     *
     * @param lineSpaceHorizontal 水平方向的间距 单位dp
     */
    public void setLineSpaceHorizontal(int lineSpaceHorizontal) {
        if (this.lineSpaceHorizontal != lineSpaceHorizontal) {
            this.lineSpaceHorizontal = lineSpaceHorizontal;
            requestLayout();
        }
    }

    /**
     * 高度确定时，获取控件宽度
     *
     * @return 宽度
     */
    private int getWidthWhenHeightExactly() {
        int height = mPaddingTop;
        int width = mPaddingLeft + maxChildWidth;
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childHeight = childView.getMeasuredHeight();
                int childMarginTop = layoutParams.topMargin;
                int childMarginBottom = layoutParams.bottomMargin;
                int totalHeight = childHeight + childMarginTop + childMarginBottom;
                if (height + totalHeight > mHeight - mPaddingBottom) {
                    //换列
                    height = mPaddingTop;
                    width += maxChildWidth + lineSpaceHorizontal;
                }
                height += totalHeight + lineSpaceVertical;
            }
        }
        return width;
    }

    /**
     * 宽度确定时，获取高度
     *
     * @return 控件高度
     */
    private int getHeightWhenWidthExactly() {
        int height = mPaddingTop + maxChildHeight;
        int width = mPaddingLeft;
        int childCount = getChildCount();
        if (childCount > 0) {
            for (int i = 0; i < childCount; i++) {
                View childView = getChildAt(i);
                MarginLayoutParams layoutParams = (MarginLayoutParams) childView.getLayoutParams();
                int childWidth = childView.getMeasuredWidth();
                int childMarginLeft = layoutParams.leftMargin;
                int childMarginRight = layoutParams.rightMargin;
                int totalWidth = childWidth + childMarginLeft + childMarginRight;
                if (width + totalWidth > mWidth - mPaddingRight) {
                    //换行
                    width = mPaddingLeft;
                    height += maxChildHeight + lineSpaceVertical;
                }
                width += totalWidth + lineSpaceHorizontal;
            }
        }
        return height;
    }

    @Override
    public ViewGroup.MarginLayoutParams generateLayoutParams(AttributeSet attrs) {
        return new ViewGroup.MarginLayoutParams(getContext(), attrs);
    }

    @SuppressWarnings("ResourceType")
    @Override
    protected ViewGroup.MarginLayoutParams generateDefaultLayoutParams() {
        return new ViewGroup.MarginLayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
    }

    @Override
    protected ViewGroup.MarginLayoutParams generateLayoutParams(ViewGroup.LayoutParams p) {
        return new ViewGroup.MarginLayoutParams(p);
    }

    @Override
    protected boolean checkLayoutParams(ViewGroup.LayoutParams p) {
        return p instanceof ViewGroup.MarginLayoutParams;
    }

}