package com.meishai.app.widget.layout;

import android.content.Context;
import android.view.View;
import android.widget.LinearLayout;

import com.meishai.R;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.entiy.HomeInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.ui.fragment.meiwu.SearchActivity;

/**
 * 文件名：
 * 描    述：首页幻灯片的自定义view
 * 作    者：
 * 时    间：2016/1/25
 * 版    权：
 */
public class HomeHeadLayout extends LinearLayout {

    private Context context;
    private AutoScrollLayout mAutoScroll;
    private EditTextWithDel mEdit;

    public HomeHeadLayout(Context context) {
        super(context);
        this.context = context;
        initData();

    }

    private void initData() {
        View.inflate(context, R.layout.view_home_search, this);

        mAutoScroll = (AutoScrollLayout) findViewById(R.id.autoScrollLayout);
        mEdit = (EditTextWithDel) findViewById(R.id.search);
//        mEdit.setOnFocusChangeListener(new OnFocusChangeListener() {
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus){
//                    context.startActivity(new Intent(context, SearchActivity.class));
//                }
//            }
//        });
        mEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(SearchActivity.newIntent(SearchActivity.TYPE_SHAISHAI));
            }
        });
    }


    public void setData(HomeInfo.SlideInfo[] slides, ImageLoader imageLoader) {
        mAutoScroll.setData(slides, imageLoader);
    }
}
