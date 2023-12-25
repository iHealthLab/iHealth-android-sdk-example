package com.ec.easylibrary.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.GridView;

/**
 * Created by wk on 2017/3/3.
 * 解决ListView中嵌套gridview显示不全问题
 */
public class NbGridView extends GridView {

    public NbGridView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public NbGridView(Context context) {
        super(context);
    }

    public NbGridView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        /**
         * Integer.MAX_VALUE >> 2的含义是Int类型的最大值的二进制向右移动2位的值
         *
         */
        int expandSpec = MeasureSpec.makeMeasureSpec(Integer.MAX_VALUE >> 2,
                MeasureSpec.AT_MOST);
        super.onMeasure(widthMeasureSpec, expandSpec);
    }
}
