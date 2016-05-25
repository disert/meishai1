package com.meishai.app.widget.layout;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.entiy.StratDetailRespData.Advertisement;
import com.meishai.entiy.StratDetailRespData.Welfare;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.MeishaiWebviewActivity;

/**
 * 攻略详情的广告和奖品item
 *
 * @author Administrator
 */
public class PicLayout extends LinearLayout {

    private ImageLoader mImageLoader;
    private PicInfo mPicInfo;

    private LinearLayout mWelfare;
    private ImageView mWelImage;

    private LinearLayout mAdv;
    private ImageView mAdvImage;

    public PicLayout(Context context) {
        super(context);
        View.inflate(context, R.layout.pic_layout, this);
        intiView();

    }


    private void intiView() {
        mWelfare = (LinearLayout) findViewById(R.id.welfare);
        mWelImage = (ImageView) findViewById(R.id.wel_image);


        mAdv = (LinearLayout) findViewById(R.id.adv);
        mAdvImage = (ImageView) findViewById(R.id.adv_image);

        mAdvImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPicInfo != null && mPicInfo.mAdvertisement != null) {
                    getContext().startActivity(
                            MeishaiWebviewActivity.newIntent(mPicInfo.mAdvertisement.ad_url));
                }
            }
        });

        mWelImage.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mPicInfo != null && mPicInfo.mWelfare != null) {
                    getContext().startActivity(
                            MeishaiWebviewActivity.newIntent(mPicInfo.mWelfare.welfare_url));
                }
            }
        });
    }

    public void setData(PicInfo info, ImageLoader imageLoader) {
        mImageLoader = imageLoader;
        mPicInfo = info;
        UpdateUI();

    }

    private void UpdateUI() {
        setAdvUI();
        setWelfareUI();
    }

    public void setAdvUI() {
        if (mPicInfo.mAdvertisement == null || mPicInfo.mAdvertisement.is_ad == 0) {
            mAdv.setVisibility(View.GONE);
        } else {
            if (mImageLoader == null) {
                mAdv.setVisibility(View.GONE);
                return;
            }
            mAdv.setVisibility(View.VISIBLE);
            mAdvImage.setTag(mPicInfo.mAdvertisement.ad_image);
            ListImageListener listener = new ListImageListener(mAdvImage,
                    R.drawable.image_back_default, R.drawable.image_back_default,
                    mPicInfo.mAdvertisement.ad_image);
            mImageLoader.get(mPicInfo.mAdvertisement.ad_image, listener);
        }
    }

    public void setWelfareUI() {
        if (mPicInfo.mWelfare == null || mPicInfo.mWelfare.is_welfare == 0) {
            mWelfare.setVisibility(View.GONE);
        } else {
            if (mImageLoader == null) {
                mWelfare.setVisibility(View.GONE);
                return;
            }
            mWelfare.setVisibility(View.VISIBLE);
            mWelImage.setTag(mPicInfo.mWelfare.welfare_image);
            ListImageListener listener = new ListImageListener(mWelImage,
                    R.drawable.image_back_default, R.drawable.image_back_default,
                    mPicInfo.mWelfare.welfare_image);
            mImageLoader.get(mPicInfo.mWelfare.welfare_image, listener);
        }
    }

    public static class PicInfo {
        public Welfare mWelfare;
        public Advertisement mAdvertisement;
    }

}
