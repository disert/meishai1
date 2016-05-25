package com.meishai.app.widget.layout;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.StratDetailRespData1;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuGoodsDetailActivity;
import com.meishai.util.DebugLog;

/**
 * 文件名：
 * 描    述：美物,tempid=3时跳转到的页面中的item
 * 作    者：
 * 时    间：2016年3月17日13:26:06
 * 版    权：
 */
public class MeiWuStratTempid3temLayout extends RelativeLayout {

    private ImageLoader mImageLoader;
    private Context mContext;
    private StratDetailRespData1.stratDetailItem mData;

    private TextView mTitle;
    private TextView mContent;
    private TextView mPrice;
    private int screenWidth;
    private TextView mCate;
    private TextView mLikeNum;
    private TextView mRanking;
    private ImageView mImage;

    public MeiWuStratTempid3temLayout(Context context) {
        this(context, null);
    }

    public MeiWuStratTempid3temLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeiWuStratTempid3temLayout(Context context, AttributeSet attrs,
                                      int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
        initListenner();
    }

    private void initView(Context context) {

        DisplayMetrics metric = new DisplayMetrics();
        ((Activity) context).getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;     // 屏幕宽度（像素）

        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.meiwu_strat_tempid3_item,
                this, true);

        mTitle = (TextView) convertView.findViewById(R.id.title);
        mContent = (TextView) convertView.findViewById(R.id.content);
        mPrice = (TextView) convertView.findViewById(R.id.price);
        mCate = (TextView) convertView.findViewById(R.id.web_cate);
        mLikeNum = (TextView) convertView.findViewById(R.id.like_num);
        mRanking = (TextView) convertView.findViewById(R.id.ranking);
        mImage = (ImageView) convertView.findViewById(R.id.image);


    }

    private void initListenner() {
        // 自己的
        this.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mContext.startActivity(MeiWuGoodsDetailActivity.newIntent(mData.pid));
            }
        });
    }


    public void setData(StratDetailRespData1.stratDetailItem data, ImageLoader imageLoader) {
        if (data == mData) return;
        mImageLoader = imageLoader;
        mData = data;
        if (mData == null) {
            DebugLog.w("没有数据!");
            return;
        }
        // 初始化数据
        if (!TextUtils.isEmpty(mData.thumb)) {
            mImage.setTag(mData.thumb);
            ListImageListener listener = new ListImageListener(mImage, R.drawable.place_default, R.drawable.place_default, mData.thumb);
            imageLoader.get(mData.thumb, listener);
        }
        if (!TextUtils.isEmpty(mData.rank_text)) {
            mRanking.setVisibility(VISIBLE);
            mRanking.setText(mData.rank_text);
        } else {
            mRanking.setVisibility(GONE);
        }
        if (!TextUtils.isEmpty(mData.rank_bgcolor))
            mRanking.setBackgroundColor(Color.parseColor("#" + mData.rank_bgcolor));

        mTitle.setText(mData.title);
        mContent.setText(mData.content);

        mPrice.setText(mData.tips_text);
        if (!TextUtils.isEmpty(mData.tips_color))
            mPrice.setTextColor(Color.parseColor("#" + mData.tips_color));
        if (mData.tips_is_center == 1) {
            mPrice.setGravity(CENTER_IN_PARENT);
        } else {
            mPrice.setGravity(CENTER_VERTICAL);
        }

        mCate.setText(mData.web_name);
        mLikeNum.setText(mData.count_text);
    }


}
