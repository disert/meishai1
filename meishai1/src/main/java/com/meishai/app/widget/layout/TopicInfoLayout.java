package com.meishai.app.widget.layout;

import org.json.JSONException;
import org.json.JSONObject;

import com.meishai.R;
import com.meishai.entiy.TopicItem;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.fragment.home.HomeData;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.DebugLog;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

public class TopicInfoLayout extends LinearLayout {

    private ImageView mIvThumb;
    private TextView mTvTitle;
    private TextView mTvUser;
    private TextView mTvTime;
    private TextView mTvDesc;
    private ImageButton mIbAttention;

    private TopicItem mInfo;
    private ImageLoader mImageLoader;

    private boolean mIsShowAll = false;

    public TopicInfoLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    public TopicInfoLayout(Context context) {
        super(context);
        initView(context);
    }


    private void initView(Context context) {
        inflate(context, R.layout.topic_info_item, this);

        mIvThumb = (ImageView) findViewById(R.id.topic_thumb_iv);
        mTvTitle = (TextView) findViewById(R.id.topic_title_tv);
        mTvUser = (TextView) findViewById(R.id.topic_user_tv);
        mTvTime = (TextView) findViewById(R.id.topic_time_tv);
        mTvDesc = (TextView) findViewById(R.id.topic_desc_tv);
        mIbAttention = (ImageButton) findViewById(R.id.topic_attention_ib);

        findViewById(R.id.topic_desc_show_llayout).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mIsShowAll) {
                    mTvDesc.setMaxLines(4);
                    mIsShowAll = false;
                } else {
                    mTvDesc.setMaxLines(Integer.MAX_VALUE);
                    mIsShowAll = true;
                }
            }
        });
    }

    public void setData(TopicItem item, ImageLoader imageLoader) {
        if (item == mInfo) {
            return;
        }

        mInfo = item;
        mImageLoader = imageLoader;

        mIvThumb.setTag(mInfo.thumb);
        ListImageListener listener = new ListImageListener(mIvThumb, R.drawable.photoshow_topic,
                R.drawable.photoshow_topic, mInfo.thumb);
        mImageLoader.get(mInfo.thumb, listener);

        mTvTitle.setText(mInfo.title);
        mTvUser.setText(mInfo.username);
        mTvTime.setText(mInfo.addtime);
        mTvDesc.setText(mInfo.description);

        if (mInfo.isattention == 1) {
            mIbAttention.setImageResource(R.drawable.ic_attention_yes);
        } else {
            mIbAttention.setImageResource(R.drawable.ic_attention_no);

            mIbAttention.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
                    HomeData.getInstance().reqAttention(getContext(), mInfo.userid, new Listener<String>() {

                        @Override
                        public void onResponse(String response) {
                            try {
                                JSONObject obj = new JSONObject(response);
                                int success = obj.getInt("success");
                                if (HomeData.RET_UNLOGIN == success) {

                                    String tips = obj.getString("tips");
                                    Toast.makeText(getContext(), tips, Toast.LENGTH_SHORT).show();
                                    mIbAttention.setClickable(true);

                                    getContext().startActivity(LoginActivity.newOtherIntent());

                                } else {

                                    mIbAttention.setImageResource(R.drawable.ic_attention_yes);
                                    mIbAttention.setClickable(false);
                                    mInfo.isattention = 1;

                                }


                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }

                    }, new ErrorListener() {

                        @Override
                        public void onErrorResponse(VolleyError error) {
                            DebugLog.d(error.toString());
                            mIbAttention.setClickable(true);
                        }

                    });
                }
            });
        }


    }

}
