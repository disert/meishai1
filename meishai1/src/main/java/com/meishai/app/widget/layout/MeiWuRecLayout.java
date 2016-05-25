package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.HomeInfo.MeiWu;
import com.meishai.entiy.HomeInfo.MeiWu.ItemData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.meiwu.FindMasterActivity;
import com.meishai.util.AndroidUtil;

/**
 * 美物推荐对应的view ,问题是listview下面的数据显示不出来,待解决
 *
 * @author Administrator yl
 */
public class MeiWuRecLayout extends LinearLayout {

    private Context mContext;
    private ImageView mMore;
    private TextView mTitle;
    private MeiWu mData;
    private ImageLoader mImageLoader;
    private LinearLayout mMeiwus;
    private LinearLayout mRoot;

    public MeiWuRecLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MeiWuRecLayout(Context context) {
        this(context, null);
    }

    private void initView() {
        View convertView = View.inflate(mContext,
                R.layout.meiwu_recommend_layout, this);
        mMore = (ImageView) convertView.findViewById(R.id.meiwu_more);
        mTitle = (TextView) convertView.findViewById(R.id.meiwu_title);
        mMeiwus = (LinearLayout) convertView.findViewById(R.id.meiwu_container);
        mRoot = (LinearLayout) convertView.findViewById(R.id.meiwu_root);
    }

    /**
     * 设置数据
     */
    public void setData(MeiWu data, ImageLoader imageLoader) {
        if (mData == data) return;
        mData = data;
        mImageLoader = imageLoader;

        if (mData == null || mData.itemdata == null || mData.itemdata.size() < 1) {
            mRoot.setVisibility(View.GONE);
            return;
        }
        mRoot.setVisibility(View.VISIBLE);
        // 设置数据
        mTitle.setText(mData.text);
        mMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                mContext.startActivity(FindMasterActivity.newIntent());
            }
        });
        //初始化达人推荐中的数据
        initData();


    }

    private void initData() {

        mMeiwus.removeAllViews();
        LayoutParams lp = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        for (int i = 0; i < mData.itemdata.size(); i++) {
            View convertView = View.inflate(mContext, R.layout.meiwu_recommend_item, null);
            convertView.setLayoutParams(lp);
            mMeiwus.addView(convertView);

            final ItemData item = mData.itemdata.get(i);

            NetworkImageView image = (NetworkImageView) convertView
                    .findViewById(R.id.meiwu_item_image);
            TextView price = (TextView) convertView
                    .findViewById(R.id.meiwu_item__price);
            if (i != 0) {
                convertView.setPadding(AndroidUtil.dip2px(10), 0, 0, 0);
            }


            //设置价格
            price.setText(String.valueOf(item.price));

            // 加载图片,以及注册它的点击事件
            image.setImageUrl(item.image, mImageLoader);
            image.setScaleType(ImageView.ScaleType.FIT_XY);

            image.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    // 美物推荐点击事件,跳转到webView中
                    getContext().startActivity(MeishaiWebviewActivity.newIntent(item.itemurl));
                }
            });

        }
    }


}
