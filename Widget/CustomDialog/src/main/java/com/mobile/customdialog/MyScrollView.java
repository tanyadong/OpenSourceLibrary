package com.mobile.customdialog;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.ScrollView;

/**
 * auther : zhouweitong
 * date : 2017/9/14 11:41
 * description :
 */

public class MyScrollView extends ScrollView {

    private Context mContext;

    public MyScrollView(Context context) {
        super(context);
        init(context);
    }

    public MyScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyScrollView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        try {
            heightMeasureSpec = MeasureSpec.makeMeasureSpec((int)(300 * mContext.getResources().getDisplayMetrics().density + 0.5f), MeasureSpec.AT_MOST);
        } catch (Exception e) {
            e.printStackTrace();
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
