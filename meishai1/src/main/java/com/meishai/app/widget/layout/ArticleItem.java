package com.meishai.app.widget.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.entiy.StratDetailRespData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.util.AndroidUtil;

import java.util.List;

/**
 * Created by Administrator on 2015/11/14.
 * 美物 攻略详情 article的view
 */
public class ArticleItem extends LinearLayout {

    private final Context context;
    private View mConvertView;
    private LinearLayout mRoot;
    private List<StratDetailRespData.Article> mData;
    private LayoutParams mlp;
    private ImageLoader mImageLoader;

    public ArticleItem(Context context) {
        this(context, null);
    }

    public ArticleItem(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        mConvertView = View.inflate(context, R.layout.article_item, this);

        mRoot = (LinearLayout) mConvertView.findViewById(R.id.article_root);
        mlp = new LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        int margin = AndroidUtil.dip2px(7);
        mlp.setMargins(0, margin, 0, 0);
    }

    public void setData(List<StratDetailRespData.Article> data, ImageLoader imageLoader) {

        mData = data;
        mImageLoader = imageLoader;

        upDateUI();
    }

    private void upDateUI() {
        mRoot.removeAllViews();
        if (mData == null || mData.size() == 0) {
            mRoot.setVisibility(GONE);
            return;
        } else {
            mRoot.setVisibility(VISIBLE);
        }
        for (StratDetailRespData.Article article : mData) {
            View view = View.inflate(context, R.layout.article_item_sub, null);

            TextView tvTitle = (TextView) view.findViewById(R.id.article_title);
            TextView tvContent = (TextView) view.findViewById(R.id.article_content);
            LinearLayout llPics = (LinearLayout) view.findViewById(R.id.article_pics);

            if (TextUtils.isEmpty(article.title)) {
                tvTitle.setVisibility(GONE);
            } else {
                tvTitle.setVisibility(VISIBLE);
                tvTitle.setText(article.title);
            }
            if (TextUtils.isEmpty(article.content)) {
                tvContent.setVisibility(GONE);
            } else {
                tvContent.setVisibility(VISIBLE);
                tvContent.setText(article.content);
            }
            List<StratDetailRespData.Picture> pics = article.pics;
            llPics.removeAllViews();
            for (StratDetailRespData.Picture pic : pics) {

                ImageView iv = new ImageView(context);
                iv.setLayoutParams(mlp);
                iv.setAdjustViewBounds(true);

                iv.setTag(pic.pic_path);
                ListImageListener listener = new ListImageListener(iv, R.drawable.image_back_default, R.drawable.image_back_default, pic.pic_path);
                mImageLoader.get(pic.pic_path, listener);
                llPics.addView(iv);
            }


            mRoot.addView(view);
        }
    }


}
