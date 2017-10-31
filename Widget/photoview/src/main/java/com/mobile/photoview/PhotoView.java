package com.mobile.photoview;

import android.content.Context;
import android.support.annotation.DrawableRes;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.List;


/**
 * @author yuanxueyuan
 * @Title: PhotoView
 * @Description: 1)支持图片预览
 * 2)支持多图片左右滑动切换到下一张
 * 3)支持图片放大功能
 * 4)支持显示图片进度使用n/m形式
 * @date 2017/9/13  9:58
 */
public class PhotoView extends RelativeLayout {

    private final String TAG = "PhotoView";

    private TextView indicator;
    private HackyViewPager mPager;
    private Context context;
    private ImagePagerAdapter mAdapter;
    private PhotoViewDelegate delegate;

    /**
     * @param context 上下文
     * @author yuanxueyuan
     * @Title: PhotoView
     * @Description: 用于用户浏览图片
     * @date 2017/9/13 9:57
     */
    public PhotoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        View view = LayoutInflater.from(context).inflate(R.layout.image_detail_pager, this);
        initView(view);
    }

    /**
     * @param view 界面
     * @author yuanxueyuan
     * @Title: initView
     * @Description: 初始化界面
     * @date 2017/9/13 11:45
     */
    private void initView(View view) {
        mPager = (HackyViewPager) view.findViewById(R.id.pager);
        indicator = (TextView) view.findViewById(R.id.text_number);
    }

    /**
     * @param activity 当前的activity
     * @param urls     图片的路径 数组类型
     * @author yuanxueyuan
     * @Title: setAdapter
     * @Description: 设置adapter
     * @date 2017/9/13 10:36
     */
    public void setAdapter(FragmentActivity activity, String[] urls) {
        if (activity == null || urls == null || urls.length <= 0) {
            Log.i(TAG, "activity == null");
            return;
        }
        if (mPager == null ) {
            Log.i(TAG, "mPager == null");
            return;
        }
        mAdapter = new ImagePagerAdapter(activity.getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        initValues();
    }

    /**
     * @param activity 当前的activity
     * @param urls     图片的路径 集合类型
     * @author yuanxueyuan
     * @Title: setAdapter
     * @Description: 设置adapter
     * @date 2017/9/13 10:36
     */
    public void setAdapter(FragmentActivity activity, List<String> urls) {
        if (activity == null || urls == null || urls.size() <= 0) {
            Log.i(TAG, "activity == null");
            return;
        }
        if (mPager == null) {
            Log.i(TAG, "mPager == null");
            return;
        }
        mAdapter = new ImagePagerAdapter(activity.getSupportFragmentManager(), urls);
        mPager.setAdapter(mAdapter);
        initValues();
    }

    private void initValues(){
        CharSequence text = context.getString(R.string.viewpager_indicator, 1, mAdapter.getCount());
        indicator.setText(text);
        // 更新下标
        if (delegate instanceof PhotoViewDelegate) {
            delegate.onSelectPhoto(0);
        }
        //监听pager的改变事件
        mPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                //重新设置下标
                CharSequence text = context.getString(R.string.viewpager_indicator, arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
                if (delegate instanceof PhotoViewDelegate) {
                    delegate.onSelectPhoto(arg0);
                }
            }
        });
        mPager.setCurrentItem(0);//设置当前页数
    }

    /**
     * @param currentPosition 当前下标
     * @author yuanxueyuan
     * @Title: setCurrentPosition
     * @Description: 设置当前的位置
     * @date 2017/9/13 11:56
     */
    public void setCurrentPosition(int currentPosition) {
        if (mPager == null) {
            Log.i(TAG, "mPager == null");
        }
        mPager.setCurrentItem(currentPosition);//设置当前页数
    }


    /**
     * @param delegate 代理类
     * @author yuanxueyuan
     * @Title: setDelegate
     * @Description: 设置代理
     * @date 2017/9/13 11:49
     */
    public void setDelegate(PhotoViewDelegate delegate) {
        this.delegate = delegate;
    }

    /**
     * @param imageId 图片资源id
     * @author yuanxueyuan
     * @Title: setLoadingImage
     * @Description: 设置加载中图片
     * @date 2017/9/13 14:26
     */
    public void setLoadingImage(@DrawableRes int imageId){
        if (mAdapter == null) {
            Log.i(TAG, "mAdapter == null");
            return;
        }
        mAdapter.setLoadingImage(imageId);
    }


    /**
     * @author yuanxueyuan
     * @Title: PhotoView
     * @Description: 图片显示的代理
     * @date 2017/9/13  11:46
     */
    public interface PhotoViewDelegate {
        void onSelectPhoto(int position);//当前选择的图片
    }
}
