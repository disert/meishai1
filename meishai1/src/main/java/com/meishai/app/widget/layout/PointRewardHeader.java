package com.meishai.app.widget.layout;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.entiy.PointRewardRespData.TypeData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.fragment.tryuse.PointRewardCateActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 积分商城头部所对应的view
 *
 * @author Administrator yl
 */
public class PointRewardHeader extends LinearLayout {

    private ImageLoader mImageLoader;
    private List<TypeData> mData;

    private Context mContext;
    private LinearLayout mRoot;

    public PointRewardHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
    }

    public PointRewardHeader(Context context) {
        this(context, null);
    }


    private void initView(Context context) {
        View.inflate(mContext, R.layout.point_reward_header, this);
        mRoot = (LinearLayout) findViewById(R.id.point_reward_header_root);
        mRoot.setBackgroundColor(0xffffffff);
    }


    /**
     * 为该view设置数据
     *
     * @param userinfo
     * @param imageLoader
     */
    public void setData(List<TypeData> userinfo, ImageLoader imageLoader) {
        mData = userinfo;
        mImageLoader = imageLoader;
        updateUI();
    }

    /**
     * 当调用setData方法为view设置数据之后调用它来更新界面
     */
    protected void updateUI() {
        if (mData == null || mData.size() == 0) {
            return;
        }
        mRoot.removeAllViews();

        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
        int dip = AndroidUtil.dip2px(7);
        lp.setMargins(0, dip, dip, 0);

        for (TypeData data : mData) {
            ImageView image = new ImageView(mContext);
            image.setLayoutParams(lp);
            image.setAdjustViewBounds(true);
            image.setTag(data.image);
            ImageListener listener1 = ImageLoader.getImageListener(
                    image, R.drawable.place_default,
                    R.drawable.place_default);
            mImageLoader.get(data.image, listener1);
            final TypeData data1 = data;
            image.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    DebugLog.e(data1.typeid + " typeid ");
                    mContext.startActivity(PointRewardCateActivity.newIntent(data1.typeid));
                }
            });

            mRoot.addView(image);
        }

    }

}
