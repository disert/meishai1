package com.meishai.app.widget.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.GoodsDetailRespData;
import com.meishai.entiy.StratDetailRespData;
import com.meishai.entiy.StrategyResqData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.ViewListUtils;

import java.util.List;

/**
 * 文件名：
 * 描    述：
 * 作    者：
 * 时    间：2015/12/28
 * 版    权：
 */
public class GoodsDetailHeader extends LinearLayout {

    private Context mContext;
    private ImageView mImage;
    private TextView mTitle;
    private TextView mPrice;
    private TextView mDesc;
    private LinearLayout mPics;
    private GoodsDetailRespData mData;
    private ImageLoader mImageLoader;
    private TextView mPriceText;
    private double mScreenWidth;

    public GoodsDetailHeader(Context context) {
        this(context, null);
    }

    public GoodsDetailHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    private void initView() {
        View.inflate(mContext, R.layout.view_goods_detail_header, this);
        mImage = (ImageView) findViewById(R.id.goods_image);
        mTitle = (TextView) findViewById(R.id.goods_title);
        mPrice = (TextView) findViewById(R.id.goods_price);
        mPriceText = (TextView) findViewById(R.id.goods_price_text);
        mDesc = (TextView) findViewById(R.id.goods_desc);
        mPics = (LinearLayout) findViewById(R.id.goods_pics);

        WindowManager manager = (WindowManager) mContext
                .getSystemService(Context.WINDOW_SERVICE);
        Display display = manager.getDefaultDisplay();
        mScreenWidth = display.getWidth();
    }

    public void setData(GoodsDetailRespData data, ImageLoader imageLoader) {
        mData = data;
        mImageLoader = imageLoader;
        initData();
    }

    private void initData() {
        if (mData != null && mData.data != null) {
            GoodsDetailRespData.GoodsDetail goodsInfo = mData.data.get(0);
            if (goodsInfo != null) {
                if (TextUtils.isEmpty(goodsInfo.image)) {
                    mImage.setImageResource(R.drawable.place_default);
                } else {
                    mImage.setTag(goodsInfo.image);
                    ListImageListener listener = new ListImageListener(mImage, R.drawable.place_default, R.drawable.place_default, goodsInfo.image);
                    mImageLoader.get(goodsInfo.image, listener);
                }

                mTitle.setText(goodsInfo.title);
                mPriceText.setText(goodsInfo.price_text);
                mPrice.setText(goodsInfo.price + goodsInfo.price_unit);
                mDesc.setText(goodsInfo.content);

                setGoodsPics(goodsInfo.pics);


            }

        }
    }

    //    private void setGoodsPics(List<StratDetailRespData.Picture> pics) {
//
//        ViewListUtils.addImageViews(mContext, mPics, pics, mImageLoader);
//    }
    private void setGoodsPics(List<StratDetailRespData.Picture> pics) {
        if (pics == null || pics.isEmpty()) {
            DebugLog.w("没有图片");
//            mPics.removeAllViews();
            return;
        }
        mPics.removeAllViews();

//        LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams(
//                RelativeLayout.LayoutParams.MATCH_PARENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
//        int px = AndroidUtil.dip2px(5);
//        pl.setMargins(0, px, 0, 0);
        for (StratDetailRespData.Picture pic : pics) {

            SampleRoundImageView view = new SampleRoundImageView(mContext);
            ImageView iv = view.getImageView();
            double widht = mScreenWidth - AndroidUtil.dip2px(8) * 2;
            double sampleSize = widht / pic.pic_width;
            int height = (int) (pic.pic_height * sampleSize);

            LinearLayout.LayoutParams pl = new LinearLayout.LayoutParams((int) widht, height);
            int px = AndroidUtil.dip2px(5);
            pl.setMargins(0, px, 0, 0);
            view.setLayoutParams(pl);

            if (TextUtils.isEmpty(pic.pic_path)) {
                iv.setImageResource(R.drawable.place_default);
            } else {
                iv.setTag(pic.pic_path);
                ListImageListener listener = new ListImageListener(iv,
                        R.drawable.place_default, R.drawable.place_default,
                        pic.pic_path);
                mImageLoader.get(pic.pic_path, listener);
            }
            mPics.addView(view);

        }
    }

}
