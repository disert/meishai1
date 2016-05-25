package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.entiy.HomeInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialShowActivity;
import com.meishai.ui.fragment.meiwu.MeiWuStratDetailActivity1;
import com.meishai.util.AndroidUtil;

import java.util.List;

/**
 * 美物推荐图片对应的view
 *
 * @author Administrator yl
 */
public class MeiWuRecView extends LinearLayout {

    private Context mContext;
    private LinearLayout mRoot;
    private ImageLoader mImageLoader;
    private List<HomeInfo.MeiWuItem> mData;

    public MeiWuRecView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();
    }

    public MeiWuRecView(Context context) {
        this(context, null);
    }

    private void initView() {
        View convertView = View.inflate(mContext,
                R.layout.meiwu_rec_view_layout, this);
        mRoot = (LinearLayout) convertView.findViewById(R.id.root);
    }

    /**
     * 设置数据
     */
    public void setData(List<HomeInfo.MeiWuItem> data, ImageLoader imageLoader) {
        if (mData == data) return;
        mData = data;
        mImageLoader = imageLoader;

        if (mData == null || mData.size() == 0) {
            mRoot.setVisibility(View.GONE);
            return;
        } else {
            mRoot.setVisibility(View.VISIBLE);
        }
        //初始化数据
        initData();


    }

    private void initData() {

        mRoot.removeAllViews();
        LayoutParams lp = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        int margin = AndroidUtil.dip2px(7);
        lp.setMargins(0, margin, 0, 0);
        for (int i = 0; i < mData.size(); i++) {
            NetworkImageView convertView = new NetworkImageView(mContext);
            convertView.setLayoutParams(lp);
            convertView.setAdjustViewBounds(true);
            mRoot.addView(convertView);

            HomeInfo.MeiWuItem item = mData.get(i);


            // 加载图片,以及注册它的点击事件
            convertView.setTag(item);
            convertView.setImageUrl(item.image, mImageLoader);
            convertView.setScaleType(ImageView.ScaleType.FIT_XY);

            convertView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HomeInfo.MeiWuItem item = (HomeInfo.MeiWuItem) v.getTag();
//					typeid: 0,//typeid=0为广告，跳转到url；typeid=4为专场，跳转到专场详情；typeid=5为攻略，跳转到攻略详情
                    switch (item.typeid) {
                        case 0://广告--跳转到webview
                            mContext.startActivity(MeishaiWebviewActivity.newIntent(item.url));
                            break;
                        case 1:
                            break;
                        case 2:
                            break;
                        case 3:
                            break;
                        case 4://专场 - 专场详情
                            mContext.startActivity(MeiWuSpecialShowActivity.newIntent(item.tid));
                            break;
                        case 5://攻略 - 攻略详情
                            mContext.startActivity(MeiWuStratDetailActivity1.newIntent(item.tid));
                            break;
                        default:

                    }
                }
            });

        }
    }


}
