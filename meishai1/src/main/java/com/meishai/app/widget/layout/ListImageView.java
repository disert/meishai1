package com.meishai.app.widget.layout;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.entiy.StrategyResqData;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.util.DebugLog;

import java.util.List;

/**
 * 文件名：ListImageView
 * 描    述：猜你喜欢对应的view
 * 作    者：yl
 * 时    间：2015/12/26
 * 版    权：
 */
public class ListImageView extends LinearLayout {


    private final View view;
    private ImageLoader mImageLoader;
    private List<StrategyResqData.StratData> mData;
    private LinearLayout mRoot;
    private Context mContext;

    public ListImageView(Context context) {
        this(context, null);
    }

    public ListImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
//        WindowManager manager = (WindowManager) context
//                .getSystemService(Context.WINDOW_SERVICE);
//        Display display = manager.getDefaultDisplay();
//        Point point = new Point();
//        display.getSize(point);
//        mScreenWidth = point.x;
        view = View.inflate(context, R.layout.list_image_view, this);
        initView();
    }

    private void initView() {
        mRoot = (LinearLayout) view.findViewById(R.id.list_image_root);
    }


    public void setData(List<StrategyResqData.StratData> correlation, ImageLoader imageLoader) {
        mData = correlation;
        mImageLoader = imageLoader;
        setPics();
    }

    private void setPics() {
        if (mData == null || mData.size() == 0) {
            DebugLog.w("没有图片");
            mRoot.removeAllViews();
            return;
        }
        mRoot.removeAllViews();

        for (StrategyResqData.StratData pic : mData) {

            MeiWuItemLayout view = new MeiWuItemLayout(mContext, MeiWuItemLayout.TYPE_OTHER);

            view.setData(pic, mImageLoader);

            mRoot.addView(view);
        }
    }
}
