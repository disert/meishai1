package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.StratDetailRespData1;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuStratDetailAdapter1;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuStratTempid3Adapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 文件名：
 * 描    述：美物,tempid=3时跳转到的模块
 * 作    者：
 * 时    间：2016年3月17日13:26:06
 * 版    权：
 */
public class MeiWuStratListActivity extends BaseActivity {

    private Button mBackMain;
    private ImageButton mMore;
    private ImageButton mWechat;
    private ImageButton mFriend;
    private ListView mListView;

    private int mTid;
    private MeiWuStratTempid3Adapter mAdapter;
    private StratDetailRespData1 mData;

    private ShareMorePopupWindow sharePop;
    private LinearLayout mLayRoot;
    private LinearLayout mLikeContainer;
    private ImageView mLikeIcon;
    private TextView mLikeText;
    private RelativeLayout mBottom;
    private TextView mWechatPoint;
    private TextView mFriendPoint;
    private TextView mTitle;

    public static Intent newIntent(int tid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeiWuStratListActivity.class);
        intent.putExtra("tid", tid);
        return intent;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meiwu_strat_detail_activity);
        initView();
        initListener();
//		getRequestData();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (mData == null) {
            getRequestData();
        } else {
            mAdapter.setData(mData);
        }
    }


    private void initView() {
        mBottom = (RelativeLayout) findViewById(R.id.bottom);
        mLayRoot = (LinearLayout) findViewById(R.id.meiwu_strat_detail_root);
        mBackMain = (Button) findViewById(R.id.backMain);
        mTitle = (TextView) findViewById(R.id.title);
        mMore = (ImageButton) findViewById(R.id.more);

        mWechat = (ImageButton) findViewById(R.id.wechat);
        mWechatPoint = (TextView) findViewById(R.id.wechat_point);
        mFriend = (ImageButton) findViewById(R.id.friend);
        mFriendPoint = (TextView) findViewById(R.id.friend_point);
        mLikeContainer = (LinearLayout) findViewById(R.id.like_container);
        mLikeIcon = (ImageView) findViewById(R.id.like_icon);
        mLikeText = (TextView) findViewById(R.id.like_text);


        mListView = (ListView) findViewById(R.id.meiwu_listview);
        mAdapter = new MeiWuStratTempid3Adapter(this, getImageLoader());
        mListView.setAdapter(mAdapter);

        mTid = getIntent().getIntExtra("tid", 0);
    }

    private void initListener() {
        mBackMain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mWechat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 分享到微信
                if (sharePop == null) return;
                sharePop.share(Wechat.NAME);
            }
        });
        mFriend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sharePop == null) return;
                sharePop.share(WechatMoments.NAME);
            }
        });
        mMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPop();
            }
        });

        mLikeContainer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mData == null || mData.topicdata == null) {
                    return;
                }

                if (mData.topicdata.isfollow == 1) {
                    unAttention();
                } else {
                    attention();
                }
            }
        });
    }

    private void showPop() {
        if (sharePop == null) {
            sharePop = new ShareMorePopupWindow(MeiWuStratListActivity.this, mTid);
            sharePop.setHintVisibility(View.GONE);
        }
        if (sharePop.isShowing()) {
            sharePop.dismiss();
        } else {

            sharePop.showAtLocation(mLayRoot, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    protected void getRequestData() {
        if (mTid == 0) {
            throw new RuntimeException("你需要传入tid");
        }
        showProgress("", getString(R.string.network_wait));
        mBottom.setVisibility(View.INVISIBLE);
        MeiWuReq.stratListReq(mTid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
                setNetComplete();
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                setNetComplete();
            }
        });
    }

    protected void setNetComplete() {
        hideProgress();
        mBottom.setVisibility(View.VISIBLE);

    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                mData = GsonHelper.parseObject(response, StratDetailRespData1.class);
                if (mData == null) {
                    DebugLog.w("返回数据错误或json解析错误：" + response);
                    AndroidUtil.showToast("数据异常！请稍后重试！");
                    return;
                }
                sharePop = new ShareMorePopupWindow(MeiWuStratListActivity.this, mTid);
                sharePop.setHintVisibility(View.GONE);
                sharePop.initShareParams(mData.sharedata);

                mTitle.setText(mData.page_title);

                setLikeBtnData();
                int isPoint = mData.sharedata.getIsPoint();
                if (isPoint == 1) {
                    mWechatPoint.setVisibility(View.VISIBLE);
                    mFriendPoint.setVisibility(View.VISIBLE);
                } else {
                    mWechatPoint.setVisibility(View.GONE);
                    mFriendPoint.setVisibility(View.GONE);
                }

                mAdapter.setData(mData);
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
        }
    }


    private void setLikeBtnData() {
        if (mData == null || mData.topicdata == null) {
            return;
        }
        if (mData.topicdata.isfollow == 1) {
            mLikeContainer.setBackgroundResource(R.drawable.has_like_btn_back);
            mLikeIcon.setImageResource(R.drawable.like_yellow);
            mLikeText.setTextColor(0xff999999);
            mLikeText.setText(getString(R.string.has_like));
//			mLikeText.setText("已喜欢");

        } else {
            mLikeContainer.setBackgroundResource(R.drawable.not_like_btn_back);
            mLikeIcon.setImageResource(R.drawable.like_white_);
            mLikeText.setTextColor(Color.WHITE);
            mLikeText.setText(getString(R.string.not_like));
//			mLikeText.setText("喜欢");
        }
    }

    private void attention() {
        showProgress("", "请稍等..");

        MeiWuReq.attention(mTid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                // 处理关注请求的结果
                hideProgress();
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    if (1 == success) {
                        Toast.makeText(MeiWuStratListActivity.this, obj.getString("tips"),
                                Toast.LENGTH_SHORT).show();
                        mData.topicdata.isfollow = 1;
                        mData.topicdata.follow_num++;
                        setLikeBtnData();

                    } else {
                        Toast.makeText(MeiWuStratListActivity.this, "关注失败" + success,
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.e(error.toString());
            }

        });

    }

    private void unAttention() {
        showProgress("", "请稍等..");
        // 发送一个关注请求
        MeiWuReq.unAttention(mTid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                // 处理关注请求的结果
                hideProgress();
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    if (1 == success) {
                        Toast.makeText(MeiWuStratListActivity.this, "取消关注成功",
                                Toast.LENGTH_SHORT).show();
                        mData.topicdata.isfollow = 0;
                        mData.topicdata.follow_num--;
                        setLikeBtnData();

                    } else {
                        Toast.makeText(MeiWuStratListActivity.this, "取消关注失败",
                                Toast.LENGTH_SHORT).show();
                    }

                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.e(error.toString());
            }

        });

    }
}
