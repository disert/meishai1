package com.meishai.app.widget.layout;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.util.AttributeSet;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.RoundCornerImageView;
import com.meishai.entiy.HomeInfo.Subject;
import com.meishai.entiy.HomeInfo.Subject.SubjectInfo;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialListActivity;
import com.meishai.ui.fragment.meiwu.MeiWuSpecialShowActivity;
import com.meishai.ui.fragment.home.HomeTopicListActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.SkipUtils;

/**
 * 主页专题对应的view 2.0 在美物品质专场中被复用,
 *
 * @author Administrator yl
 */
public class HomeTopicLayout extends LinearLayout {

    private Context mContext;
    private View mRoot;
    private TextView mTvMore;
    private TextView mTvText;
    private Subject mTopic;
    private ImageLoader mImageLoader;
    private LinearLayout mTopics;

    private int type;
    public static final int TYPE_TOPIC = 0;// 晒晒 话题
    public static final int TYPE_SPECAIL = 1;// 攻略 专场
    private LinearLayout mTitleRoot;
    private LinearLayout mDivideLine;
    private View mCenterLine;

    public HomeTopicLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        mRoot = View.inflate(mContext, R.layout.home_topic_layout, this);

        initView();
    }

    public void setType(int type) {
        this.type = type;
        if (type == TYPE_SPECAIL) {
            mDivideLine.setVisibility(View.VISIBLE);
        } else {
            mDivideLine.setVisibility(View.GONE);
        }
    }

    public HomeTopicLayout(Context context) {
        this(context, null);
    }

    private void initView() {

        mTopics = (LinearLayout) mRoot.findViewById(R.id.htl_topics);
        mDivideLine = (LinearLayout) mRoot.findViewById(R.id.divide_line);
        mTitleRoot = (LinearLayout) mRoot.findViewById(R.id.htl_title_root);
        mTvMore = (TextView) mRoot.findViewById(R.id.htl_tv_more);
        mTvText = (TextView) mRoot.findViewById(R.id.htl_tv_text);
        mCenterLine = mRoot.findViewById(R.id.line_center);

        mTvMore.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                if (type == TYPE_TOPIC) {
                    // 分类页面
                    Intent intent = new Intent(mContext, HomeTopicListActivity.class);
                    mContext.startActivity(intent);
                } else {
                    // 进入品质专场
                    Intent intent = MeiWuSpecialListActivity.newIntent(0);
                    mContext.startActivity(intent);
                }
            }
        });
    }

    public void setData(Subject item, ImageLoader imageLoader) {
        if (item == mTopic) {
            return;
        }
        if (item == null || imageLoader == null) {
            return;
        }
        if (item.topicdata == null || item.topicdata.isEmpty()) {
            setVisibility(GONE);
            return;
        } else {
            setVisibility(VISIBLE);
        }
        mTitleRoot.setVisibility(View.GONE);
        mCenterLine.setVisibility(GONE);
//		if (item.text == null || item.more == null) {
//		} else {
//			mTitleRoot.setVisibility(View.VISIBLE);
//			mCenterLine.setVisibility(VISIBLE);
//		}
        mTopic = item;
        mImageLoader = imageLoader;

        mTvMore.setText(mTopic.more);

        mTvText.setText(mTopic.text);

        initTopicsData(mTopic.topicdata);
    }

    /**
     * 初始化专题页面的数据
     *
     * @param datas
     */
    private void initTopicsData(List<SubjectInfo> datas) {
        if (datas == null || datas.size() < 1) {
            return;
        }
        mTopics.removeAllViews();
        for (int i = 0; i < datas.size(); i++) {
            final SubjectInfo info = datas.get(i);
            View convertView = View.inflate(mContext,
                    R.layout.home_topic_layout_item, null);

            convertView.setPadding(AndroidUtil.dip2px(6), 0, 0, 0);
            RoundCornerImageView image = (RoundCornerImageView) convertView
                    .findViewById(R.id.htli_iv);

            image.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (type == TYPE_TOPIC) {
                        // 去分类页面
//						Intent intent = TopicShowActivity.newIntent(info.tid);
//						mContext.startActivity(intent);
                        SkipUtils.skip(mContext, info.type, info.value, info.tempid, info.h5data);
                    } else {// 进入专场详情
                        Intent intent = MeiWuSpecialShowActivity
                                .newIntent(info.tid);
                        mContext.startActivity(intent);
                    }
                }
            });
            // 专题的图片加载
            image.setTag(info.image);
            ListImageListener listener = new ListImageListener(image,
                    R.drawable.image_cate_back, R.drawable.image_cate_back,
                    info.image);
            int width = AndroidUtil.dip2px(100);
            mImageLoader.get(info.image, listener, width, width);

            mTopics.addView(convertView);
        }

    }

}
