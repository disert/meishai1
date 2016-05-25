package com.meishai.app.widget.layout;


import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.StratDetailRespData.HeadData;
import com.meishai.entiy.StrategyResqData.StratData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;

/**
 * 美物-攻略详情-header
 *
 * @author Administrator yl
 */
public class MeiWuStratDetailHeaderLayout extends RelativeLayout {


    private ImageLoader mImageLoader;
    private Context mContext;
    private HeadData mData;
    private TextView mDesc;
    private MeiWuItemLayout mItem;


    public MeiWuStratDetailHeaderLayout(Context context) {
        this(context, null);
    }

    public MeiWuStratDetailHeaderLayout(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MeiWuStratDetailHeaderLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        mContext = context;
        initView(context);
    }


    private void initView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.meiwu_strat_detail_header, this, true);

        mItem = (MeiWuItemLayout) convertView.findViewById(R.id.meiwu_strat_detail_item);
        mItem.setClickable(false);
        mItem.setRootPadding(0, 0, 0, 0);
        mDesc = (TextView) convertView.findViewById(R.id.meiwu_strat_detail_desc);


    }

    public void setData(HeadData data, ImageLoader imageLoader) {
        if (data == null) return;
        mImageLoader = imageLoader;
        mData = data;
        //初始化数据
        if (TextUtils.isEmpty(mData.content)) {
            mDesc.setVisibility(GONE);
        } else {
            mDesc.setVisibility(VISIBLE);
            mDesc.setText(mData.content);
        }

        mItem.setData(data, imageLoader);


    }


    private void reLayout() {
        //设置viewpager的宽高
//		//获取手机宽高
//        DisplayMetrics dm = new DisplayMetrics();
//        //获取屏幕信息
//        mContext.getWindowManager().getDefaultDisplay().getMetrics(dm);
//        int screenWidth = dm.widthPixels;
//        int screenHeigh = dm.heightPixels;
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT,
                AndroidUtil.dip2px(180));
        //lp.setMargins(0, AndroidUtil.dip2px(3), AndroidUtil.dip2px(4), 0);

//		mViewPager.setLayoutParams(lp);
    }

}
