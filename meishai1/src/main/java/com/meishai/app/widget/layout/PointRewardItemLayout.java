package com.meishai.app.widget.layout;

import u.aly.v;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.PointRewardRespData.PointData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 积分商城item所对应的view
 *
 * @author Administrator yl
 */
public class PointRewardItemLayout extends LinearLayout {

    private ImageLoader mImageLoader;
    private PointData mItem1;
    private PointData mItem2;
    private Context mContext;
    private LinearLayout mRoot;
    private int dip;
    private LinearLayout.LayoutParams lp;

    public PointRewardItemLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView();

    }

    public PointRewardItemLayout(Context context) {
        this(context, null);
    }

    public void setData(PointData item1, PointData item2,
                        ImageLoader imageLoader) {

        mItem1 = item1;
        mItem2 = item2;
        mImageLoader = imageLoader;

        setItem(mItem1, mItem2);

    }

    private void setItem(PointData item1, PointData item2) {
        mRoot.removeAllViews();
        initData(mItem1);
        if (mItem2 == null) {
            View view = new View(mContext);
            view.setLayoutParams(lp);
            mRoot.addView(view);
        } else {
            initData(mItem2);
        }
    }

    private void initView() {
        View.inflate(mContext, R.layout.point_reward_header, this);
        mRoot = (LinearLayout) findViewById(R.id.point_reward_header_root);
        dip = AndroidUtil.dip2px(8);
        mRoot.setPadding(dip, 0, 0, 0);
        lp = new LayoutParams(0,
                LinearLayout.LayoutParams.WRAP_CONTENT, 1);
    }

    private void initData(final PointData item) {

        if (item == null) {
            return;
        }

        View view = View.inflate(mContext, R.layout.point_reward_item, null);
        view.setLayoutParams(lp);

        ImageView mImage = (ImageView) view
                .findViewById(R.id.point_reward_image);
        TextView title = (TextView) view.findViewById(R.id.point_reward_title);
        TextView lowpoint = (TextView) view
                .findViewById(R.id.point_reward_lowpoint);
        TextView typeText = (TextView) view
                .findViewById(R.id.point_reward_typetext);
        final TextView allnum = (TextView) view
                .findViewById(R.id.point_reward_allnum);
        //加空格,使其能够显示在类型textview后面,并且下一行与其对齐
        title.setText("                    " + item.title);
        allnum.setText(item.allnum);
        lowpoint.setText(item.lowpoint);
        typeText.setText(item.type_text);
        // .....设置背景色,有传递颜色过来,使用自定义圆角的textview处理更好,我这个不推荐
        if (item.type_bgcolor.equalsIgnoreCase("F48A7C")) {//红
            typeText.setBackgroundResource(R.drawable.back_cir_red);
        } else if (item.type_bgcolor.equalsIgnoreCase("aada84")) {//绿
            typeText.setBackgroundResource(R.drawable.back_cir_green);
        } else if (item.type_bgcolor.equalsIgnoreCase("68d0d7")) {//蓝
            typeText.setBackgroundResource(R.drawable.back_cir_blue);
        }

        ImageListener listener1 = ImageLoader.getImageListener(mImage,
                R.drawable.place_default, R.drawable.place_default);
        mImageLoader.get(item.thumb, listener1);

        view.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = FuliSheDetailActivity1.newIntent(item.gid, 0);
                mContext.startActivity(intent);
            }
        });

        mRoot.addView(view);

    }

}
