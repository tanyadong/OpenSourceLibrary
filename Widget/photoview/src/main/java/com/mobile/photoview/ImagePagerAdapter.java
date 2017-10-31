package com.mobile.photoview;

import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;

import java.util.List;

/**
 * @author yuanxueyuan
 * @Title: ImagePagerAdapter
 * @Description: 图片浏览器的适配器
 * @date 2017/9/13  14:31
 */
public class ImagePagerAdapter extends FragmentStatePagerAdapter {

    private ImageDetailFragment fragment;
    private @DrawableRes int imageId = 0;

    public String[] files;
    private List<String> fileList;

    public ImagePagerAdapter(FragmentManager fm, String[] fileList) {
        super(fm);
        this.files = fileList;
    }

    public ImagePagerAdapter(FragmentManager fm, List<String> fileList) {
        super(fm);
        this.fileList = fileList;
    }

    @Override
    public Fragment getItem(int position) {
        String url = "";
        if (files != null && files.length > 0) {
            url = files[position];
        } else if (fileList != null && fileList.size() > 0) {
            url = fileList.get(position);
        }
        fragment = ImageDetailFragment.newInstance(url);
        fragment.setLoadingImage(imageId);
        return fragment;
    }

    @Override
    public int getCount() {
        if (files != null && files.length > 0) {
            return files == null ? 0 : files.length;
        } else if (fileList != null && fileList.size() > 0) {
            return fileList == null ? 0 : fileList.size();
        }
        return 0;
    }

    /**
     * @param imageId 图片资源id
     * @author yuanxueyuan
     * @Title: setLoadingImage
     * @Description: 设置加载中图片
     * @date 2017/9/13 14:26
     */
    public void setLoadingImage(@DrawableRes int imageId){
        this.imageId = imageId;
    }
}
