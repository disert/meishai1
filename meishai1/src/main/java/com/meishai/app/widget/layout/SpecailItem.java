package com.meishai.app.widget.layout;

import android.content.Context;
import android.content.Intent;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.entiy.CateInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuCateDetailActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ImageAdapter;

/**
 * 文件名：SpecailItem
 * 描    述：美物 专辑 下面的item
 * 作    者：yl
 * 时    间：2016/1/21
 * 版    权：
 */
public class SpecailItem extends LinearLayout {

    private CateInfo mItem1;
    private CateInfo mItem2;
    private ImageLoader mImageLoader;
    private LinearLayout mRoot;

    public SpecailItem(Context context) {
        this(context, null);
    }

    public SpecailItem(Context context, AttributeSet attrs) {
        super(context, attrs);
        initData(context);
    }

    private void initData(Context context) {
        View.inflate(context, R.layout.view_specail_item, this);
        mRoot = (LinearLayout) findViewById(R.id.root);
    }

    public void setData(CateInfo item1, CateInfo item2, ImageLoader imageLoader) {
        mItem1 = item1;
        mItem2 = item2;
        mImageLoader = imageLoader;
        initPic(item1, item2);
    }

    private void initPic(final CateInfo item1, final CateInfo item2) {
        mRoot.removeAllViews();
        int margin = AndroidUtil.dip2px(4);
        Point point = ImageAdapter.getViewRealWH(2, margin, 750, 380);
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(point.x, point.y);
        lp.setMargins(0, margin, margin, 0);
        //第一个
        SampleRoundImageView view1 = new SampleRoundImageView(getContext()).hideRound();
        ImageView imageView1 = view1.getImageView();

        if (item1 != null) {
            view1.setVisibility(VISIBLE);
            imageView1.setTag(item1.image);
//            imageView1.setMaxHeight(AndroidUtil.dip2px(150));
            ListImageListener listener = new ListImageListener(imageView1, R.drawable.image_back_default, R.drawable.image_back_default, item1.image);
            mImageLoader.get(item1.image, listener);

            imageView1.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = MeiWuCateDetailActivity.newIntent(item1.cid, item1.name);
                    getContext().startActivity(intent);
                }
            });

        } else {
            view1.setVisibility(INVISIBLE);
        }
        mRoot.addView(view1, lp);

        //第二个
        SampleRoundImageView view2 = new SampleRoundImageView(getContext()).hideRound();
        ImageView imageView2 = view2.getImageView();
        if (item2 != null) {
            view2.setVisibility(VISIBLE);
            imageView2.setTag(item2.image);
            ListImageListener listener = new ListImageListener(imageView2, R.drawable.image_back_default, R.drawable.image_back_default, item2.image);
            mImageLoader.get(item2.image, listener);
            imageView2.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = MeiWuCateDetailActivity.newIntent(item2.cid, item2.name);
                    getContext().startActivity(intent);
                }
            });

        } else {
            view2.setVisibility(INVISIBLE);
        }
        mRoot.addView(view2, lp);


    }


}
