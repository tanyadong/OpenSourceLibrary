package com.mobile.photoview;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.annotation.DrawableRes;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.bumptech.glide.Glide;

/**
 * @author yuanxueyuan
 * @Title: ImageDetailFragment
 * @Description: 图片详情类
 * @date 2017/9/13  11:44
 * ${tags}
 */
public class ImageDetailFragment extends Fragment {
    private String mImageUrl;
    private PhotoViewUI mImageView;
    private
    @DrawableRes
    int imageId = 0;//图片资源id

    public static ImageDetailFragment newInstance(String imageUrl) {
        final ImageDetailFragment f = new ImageDetailFragment();
        final Bundle args = new Bundle();
        args.putString("url", imageUrl);
        f.setArguments(args);
        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mImageUrl = getArguments() != null ? getArguments().getString("url") : null;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View v = inflater.inflate(R.layout.image_detail_fragment, container, false);
        mImageView = (PhotoViewUI) v.findViewById(R.id.image_show);
        mImageView.setOnPhotoTapListener(new PhotoViewAttacher.OnPhotoTapListener() {
            @Override
            public void onPhotoTap(View arg0, float arg1, float arg2) {
                getActivity().finish();
            }
        });
        return v;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (imageId == 0) {
            Glide.with(getContext()).load(mImageUrl).into(mImageView);
        } else {
            Glide.with(getContext()).load(mImageUrl).placeholder(imageId).into(mImageView);
        }
    }

    /**
     * @param imageId 图片资源id
     * @author yuanxueyuan
     * @Title: setLoadingImage
     * @Description: 设置加载中图片
     * @date 2017/9/13 14:26
     */
    public void setLoadingImage(@DrawableRes int imageId) {
        this.imageId = imageId;
    }

}
