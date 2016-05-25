package com.meishai.ui.fragment.meiwu;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.SpecialDetailResqData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.meiwu.adapter.MeiWuSpecialAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * 美物-专场详情 2.0
 *
 * @author Administrator yl
 */
public class MeiWuSpecialShowActivity extends BaseActivity {

    private Button mBackMain;
    private ImageButton mMore;
    private PullToRefreshListView mRefreshListView;
    private MeiWuSpecialAdapter mAdapter;
    private int mTid;
    private int mPage = 1;
    private SpecialDetailResqData mData;

    private ShareMorePopupWindow sharePop;
    private LinearLayout mLayRoot;

    private ImageButton mWechat;
    private ImageButton mFriend;
    private LinearLayout mLikeContainer;
    private ImageView mLikeIcon;
    private TextView mLikeText;
    private RelativeLayout mBottom;
    private TextView mWechatPoint;
    private TextView mFriendPoint;
    private TextView mTitle;

    public static final int TYPE_DEFAULT = 0;//默认的 专场
    public static final int TYPE_SHOPS = 1;//好店
    private int mType = TYPE_DEFAULT;
    private boolean isLoading;

    /**
     * 获取启动该activity的intent,
     *
     * @param tid
     * @param type 默认是0,表示专场详情,1表示好店详情
     * @return
     */
    public static Intent newIntent(int tid, int type) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeiWuSpecialShowActivity.class);
        intent.putExtra("tid", tid);
        intent.putExtra("type", type);

        return intent;
    }

    public static Intent newIntent(int tid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeiWuSpecialShowActivity.class);
        intent.putExtra("tid", tid);

        return intent;
    }

    public static Intent newIntent(int tid, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeiWuSpecialShowActivity.class);
        intent.putExtra("tid", tid);
        intent.putExtra("title", title);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.meiwu_topic_activity);
        mPage = 1;
        initView();
        initListenner();
//		getRequestData(1);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mData == null) {
            showProgress("", getString(R.string.network_wait));
            getRequestData(mPage);
        } else {
            mAdapter.setData(mData);
        }
    }


    private void initView() {

        mLayRoot = (LinearLayout) findViewById(R.id.meiwu_topic_root);

        mBackMain = (Button) findViewById(R.id.backMain);
        mTitle = (TextView) findViewById(R.id.title);
        mMore = (ImageButton) findViewById(R.id.more);
        // 底部
        mBottom = (RelativeLayout) findViewById(R.id.bottom);
        mWechat = (ImageButton) findViewById(R.id.wechat);
        mWechatPoint = (TextView) findViewById(R.id.wechat_point);
        mFriend = (ImageButton) findViewById(R.id.friend);
        mFriendPoint = (TextView) findViewById(R.id.friend_point);
        mLikeContainer = (LinearLayout) findViewById(R.id.like_container);
        mLikeIcon = (ImageView) findViewById(R.id.like_icon);
        mLikeText = (TextView) findViewById(R.id.like_text);

        mRefreshListView = (PullToRefreshListView) findViewById(R.id.meiwu_listview);
        mRefreshListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mRefreshListView);
        PullToRefreshHelper.initIndicator(mRefreshListView);

        mAdapter = new MeiWuSpecialAdapter(this, getImageLoader());
        mRefreshListView.setAdapter(mAdapter);

        mTid = getIntent().getIntExtra("tid", 0);
        mType = getIntent().getIntExtra("type", 0);
        if (mType == TYPE_DEFAULT) {
            mTitle.setText("专场详情");
        } else {
            mTitle.setText("好店详情");
        }
    }

    private void initListenner() {
        mRefreshListView
                .setOnRefreshListener(new OnRefreshListener<ListView>() {

                    @Override
                    public void onRefresh(
                            PullToRefreshBase<ListView> refreshView) {
                        if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                            getRequestData(1);
                        } else if (refreshView.getCurrentMode() == Mode.PULL_FROM_END) {
                            if (mData == null) {
                                getRequestData(1);
                                return;
                            }
                            if (mPage < mData.pages) {
                                getRequestData(mPage + 1);
                            } else {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }

                    }
                });
        mRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (mPage < mData.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mRefreshListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
//								AndroidUtil
//										.showToast(R.string.drop_down_list_footer_no_more_text);
                                setNetComplete();
                            }
                        }, 500);
                    }
                }
            }
        });
        mBackMain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPop();
            }
        });
        mWechat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // 分享到微信
                sharePop.share(Wechat.NAME);
            }
        });
        mFriend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                sharePop.share(WechatMoments.NAME);
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

    private Listener<String> listener = new Listener<String>() {

        @Override
        public void onResponse(String response) {
            checkRespData(response);
            setNetComplete();
        }
    };
    private ErrorListener errorListener = new ErrorListener() {

        @Override
        public void onErrorResponse(VolleyError error) {
            AndroidUtil.showToast(R.string.reqFailed);
            setNetComplete();
        }
    };

    protected void getRequestData(int page) {
        if (mTid == 0) {
            throw new RuntimeException("你需要传入tid");
        }

//		showProgress("", getString(R.string.network_wait));
        if (mType == TYPE_DEFAULT) {//专题详情
            MeiWuReq.specialDetailsReq(page, mTid, listener, errorListener);
        } else {//好店详情
            MeiWuReq.shopsDetailsReq(page, mTid, listener, errorListener);
        }
    }

    protected void setNetComplete() {
        // mRefreshListView.setVisibility(View.VISIBLE);
        mRefreshListView.onRefreshComplete();
        hideProgress();
        isLoading = false;

    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                SpecialDetailResqData resqData = GsonHelper.parseObject(
                        response, SpecialDetailResqData.class);
                if (resqData.page == 1) {
                    mData = resqData;
                    mTitle.setText(mData.page_title);
                } else {
                    mData.list.addAll(resqData.list);
                    mData.page = resqData.page;
                }

                //分享数据
                sharePop = new ShareMorePopupWindow(
                        MeiWuSpecialShowActivity.this, mTid);
                sharePop.setHintVisibility(View.GONE);
                sharePop.initShareParams(mData.sharedata);


                int isPoint = mData.sharedata.getIsPoint();
                if (isPoint == 1) {
                    mWechatPoint.setVisibility(View.VISIBLE);
                    mFriendPoint.setVisibility(View.VISIBLE);
                } else {
                    mWechatPoint.setVisibility(View.GONE);
                    mFriendPoint.setVisibility(View.GONE);
                }

                setLikeBtnData();

                mPage = mData.page;
                mAdapter.setData(mData);
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    private void showPop() {
        if (sharePop == null) {
            sharePop = new ShareMorePopupWindow(this, mTid);
            sharePop.setHintVisibility(View.GONE);
        }
        if (sharePop.isShowing()) {
            sharePop.dismiss();
        } else {

            sharePop.showAtLocation(mLayRoot, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
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
                        Toast.makeText(MeiWuSpecialShowActivity.this, obj.getString("tips"),
                                Toast.LENGTH_SHORT).show();
                        mData.topicdata.isfollow = 1;
                        mData.topicdata.follow_num++;
                        setLikeBtnData();

                    } else {
                        Toast.makeText(MeiWuSpecialShowActivity.this, "关注失败" + success,
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
                        Toast.makeText(MeiWuSpecialShowActivity.this, "取消关注成功",
                                Toast.LENGTH_SHORT).show();
                        mData.topicdata.isfollow = 0;
                        mData.topicdata.follow_num--;
                        setLikeBtnData();

                    } else {
                        Toast.makeText(MeiWuSpecialShowActivity.this, "取消关注失败",
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

}
