package com.meishai.app.widget.layout;

import android.content.Context;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TopicRespData;
import com.meishai.entiy.TopicRespData.TopicData1;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AndroidUtil;

import java.util.HashMap;
import java.util.Map;

/**
 * 话题头部所对应的view
 *
 * @author Administrator yl
 */
public class HomeTopicHeader extends LinearLayout {

    private ImageLoader mImageLoader;
    private TopicRespData.TopicData mData;

    private Context mContext;
    private ImageView mImage;
    private TextView mDesc;
    private TextView mTvAtt;
    private TextView mTvTitle;
    private TextView mTvText;
    private int mTid;

    public HomeTopicHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
        initListener();
    }

    public HomeTopicHeader(Context context) {
        this(context, null);
    }


    private void initView(Context context) {
        View.inflate(mContext, R.layout.home_topic_header, this);
        mImage = (ImageView) findViewById(R.id.home_topic_header_img);
        mTvAtt = (TextView) findViewById(R.id.home_topic_header_attention);
        mTvTitle = (TextView) findViewById(R.id.home_topic_header_title);
        mTvText = (TextView) findViewById(R.id.home_topic_header_text);
        mDesc = (TextView) findViewById(R.id.home_topic_header_desc);
    }

    public void setTid(int tid) {
        mTid = tid;
    }

    private void initListener() {
//		this.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				getContext().startActivity(MeishaiWebviewActivity.newIntent(mData.more_url));
//			}
//		});
        mTvAtt.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData == null) return;
                if (mData.isattention == 1) {
                    delAttentionTopic();
                } else {
                    addAttentionTopic();
                }

            }
        });
    }

    /**
     * 为该view设置数据
     *
     * @param userinfo
     * @param imageLoader
     */
    public void setData(TopicRespData.TopicData userinfo, ImageLoader imageLoader) {
        mData = userinfo;
        mImageLoader = imageLoader;
        updateUI();
    }

    /**
     * 当调用setData方法为view设置数据之后调用它来更新界面
     */
    protected void updateUI() {
        if (mData != null) {
            if (TextUtils.isEmpty(mData.image)) {
                mImage.setImageResource(R.drawable.place_default);
            } else {
                mImage.setTag(mData.image);
                ListImageListener listener = new ListImageListener(mImage,
                        R.drawable.image_back_default, R.drawable.image_back_default,
                        mData.image);
                mImageLoader.get(mData.image, listener);
            }

            if (TextUtils.isEmpty(mData.description)) {
                mDesc.setVisibility(GONE);
            } else {
                mDesc.setVisibility(VISIBLE);
                mDesc.setText(mData.description);
            }
            mTvText.setText(mData.text);
            mTvTitle.setText(mData.title);
            setAttention();
        }

    }

    protected void addAttentionTopic() {

        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        data.put("tid", mTid + "");
        PublicReq.addtopic(mContext, data, new Response.Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    mData.isattention = 1;
                    setAttention();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("添加关注失败");
            }
        });

    }

    protected void delAttentionTopic() {
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        data.put("tid", mTid + "");
        PublicReq.deltopic(mContext, data, new Response.Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    mData.isattention = 0;
                    setAttention();
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("取消关注失败");
            }
        });

    }

    protected void setAttention() {
        if (mData.isattention == 1) {
            mTvAtt.setText("已关注");
            mTvAtt.setSelected(true);
        } else {
            mTvAtt.setText("关注");
            mTvAtt.setSelected(false);
        }
    }

}
