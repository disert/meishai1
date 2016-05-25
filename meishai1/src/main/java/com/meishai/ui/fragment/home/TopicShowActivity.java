package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.DensityUtils;
import com.meishai.app.widget.CircleImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem.ZanUserInfo;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.TopicItem;
import com.meishai.entiy.TopicRespData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.ui.fragment.home.adapter.HomeTopicAdapter;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 晒晒->话题->详情2.0
 *
 * @author yl
 */
public class TopicShowActivity extends BaseActivity {

    private PullToRefreshListView mListView;
    private HomeTopicAdapter mAdapter;
    private ShareMorePopupWindow share;
    private TextView mTvTitle;

    private TopicItem mInfo;
    private int mTid;

    private int mPage = 1;// 当前页
    private int iswon = 0;// 默认排序
    private TopicRespData mData;
    private LinearLayout mCamer;

    // 弹出窗
    private View mPopView;
    private CircleImageView mAvatar;
    private TextView mFansNum;
    private Button mHandPick;
    private LinearLayout mRankingList;
    private ImageButton mShare;
    private LinearLayout mRankingRoot;
    private PopupWindow mPw;
    private RelativeLayout mTopRoot;

    private LinearLayout mLay_main;
    private boolean isLoading;

    public static Intent newIntent(TopicItem topic) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TopicShowActivity.class);
        intent.putExtra("topic", topic);

        return intent;
    }

    public static Intent newIntent(int tid) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                TopicShowActivity.class);
        intent.putExtra("tid", tid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_topic_show1);

        initView();
//		initPop();
        initData();

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (mData == null) {
            getRequest(1);
        } else {
            mAdapter.setData(mData);
        }
    }

    private void initView() {
        mLay_main = (LinearLayout) findViewById(R.id.root);
        mTopRoot = (RelativeLayout) findViewById(R.id.top_root);
        findViewById(R.id.top_back_btn).setOnClickListener(
                new OnClickListener() {
                    @Override
                    public void onClick(View arg0) {
                        finish();
                    }
                });
        findViewById(R.id.top_share).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                showPop();
            }

        });

//		mAtt = (Button) findViewById(R.id.top_camer_ib);
//		mAtt.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if(mData == null){
//					AndroidUtil.showToast("数据还没加载完成");
//					return;
//				}
//				// 话题 关注
//				if(mData.topicdata1.isattention == 1){
//					delAttentionTopic();
//				}else{
//					addAttentionTopic();
//				}
//			}
//		});
//		mArrow = (ImageView) findViewById(R.id.top_arrow_img);
        mTvTitle = (TextView) findViewById(R.id.top_title_tv);
//		mTitleRoot = (LinearLayout) findViewById(R.id.top_title_root_ll);
//		mTitleRoot.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (mPw == null) {
//					return;
//				}
//				if (!mPw.isShowing()) {
//					mArrow.setImageResource(R.drawable.topic_up_arrow);
//					ShowPop();
//				} else {
//					mArrow.setImageResource(R.drawable.topic_down_arrow);
//					hidePop();
//				}
//			}
//		});
        mCamer = (LinearLayout) findViewById(R.id.top_parti_action);
        mCamer.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                ReleaseData data = new ReleaseData();
                data.setTid(mTid);
                //				Intent intent = ImageChooseActivity1.newIntent(data,0);
                Intent intent = ChooseImageActivity.newIntent(data, ConstantSet.MAX_IMAGE_COUNT);
                startActivity(intent);
            }
        });

        mListView = (PullToRefreshListView) findViewById(R.id.topic_listview);
        mListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mListView);
        PullToRefreshHelper.initIndicator(mListView);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getMode() == Mode.PULL_FROM_START) {
                    getRequest(1);
                } else {
                    if (mData == null) {
                        getRequest(1);
                        return;
                    }
                    if (mPage < mData.pages) {
                        getRequest(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                mListView.onRefreshComplete();
                            }
                        }, 500);
                    }
                }

            }
        });

        mListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (mPage < mData.pages) {
                        getRequest(mPage + 1);
                    } else {
                        mListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                mListView.onRefreshComplete();
                            }
                        }, 500);
                    }
                }
            }
        });

    }


    private void initData() {
        mInfo = getIntent().getParcelableExtra("topic");
        if (null == mInfo) {
            mTid = getIntent().getIntExtra("tid", -1);
        } else {
            mTid = mInfo.tid;
            mTvTitle.setText(mInfo.title);
            // mAdapter.setTopicData(mInfo);
        }
        mAdapter = new HomeTopicAdapter(this, getImageLoader(), mTid);
        mListView.setAdapter(mAdapter);
    }

    private void getRequest(int page) {
//		mCamer.setVisibility(View.GONE);

//		showProgress("", getString(R.string.network_wait));
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("index");
        reqData.setA("lists");
        Map<String, String> dataList = new HashMap<String, String>();
        dataList.put("userid", userInfo.getUserID());
        dataList.put("tid", String.valueOf(mTid)); // 热门
        dataList.put("page", String.valueOf(page));
        dataList.put("iswon", iswon + "");
        reqData.setData(dataList);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d("话题:" + url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgress();
                            isLoading = false;
                            mCamer.setVisibility(View.VISIBLE);
                            // DebugLog.d(response);

                            if (checkData(response)) {
                                mListView.onRefreshComplete();

                            } else {
                                mListView.onRefreshComplete();

                                showToast(R.string.reqDataFailed);
                                mListView.setMode(Mode.DISABLED);
                            }

                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            AndroidUtil.showToast(R.string.reqFailed);

                            mListView.onRefreshComplete();
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private boolean checkData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);

            if (jsonObj.getInt("success") == 1) {
                mData = GsonHelper.parseObject(response, TopicRespData.class);
                if (mData != null) {
                    mPage = mData.page;
                    if (mPage == 1) {
                        if (mData.topicdata != null) {
                            mTvTitle.setText(mData.topicdata.title);
                        }
                        configShareContent(mData.sharedata);
//					initPopData();
//					mAtt.setText(mData.topicdata1.isattention == 0 ? "关注"
//							: "已关注");
                        mAdapter.setData(mData);
                    } else {
                        mAdapter.addData(mData.list);
                    }
                }

                // mAdapter.addCollection(items);

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    /**
     * 初始化弹出窗体
     */
//	private void initPop() {
//		mPw = new PopupWindow(LayoutParams.MATCH_PARENT,
//				LayoutParams.WRAP_CONTENT);
//		// mPw.setOutsideTouchable()
//
//		mPopView = View.inflate(this, R.layout.dialog_topic, null);
//		mAvatar = (CircleImageView) mPopView
//				.findViewById(R.id.topic_pop_avatar);
//		mFansNum = (TextView) mPopView.findViewById(R.id.topic_pop_fans_num);
//		mHandPick = (Button) mPopView.findViewById(R.id.topic_pop_hand_pick);
//		mRankingList = (LinearLayout) mPopView
//				.findViewById(R.id.topic_pop_ranking_list);
//		mShare = (ImageButton) mPopView.findViewById(R.id.topic_pop_share);
//		mRankingRoot = (LinearLayout) mPopView
//				.findViewById(R.id.topic_pop_rangking_root);
//
//		mHandPick.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				if (iswon == 1) {// 只看精选
//					iswon = 0;
//					mHandPick.setText("只看精选");
//				} else {// 查看全部
//					iswon = 1;
//					mHandPick.setText("查看全部");
//				}
//				getRequest(1);
//				hidePop();
//			}
//		});
//		mShare.setOnClickListener(new OnClickListener() {
//
//			@Override
//			public void onClick(View v) {
//				// 2.话题 分享
//				if(null != share){
//					share.showAtLocation(mLay_main, Gravity.BOTTOM
//							| Gravity.CENTER_HORIZONTAL, 0, 0);
//				}
//				hidePop();
//			}
//		});
//	}

    /**
     * 初始化弹出窗体的数据
     */
    private void initPopData() {
        if (mData == null || mData.topicdata2 == null) {
            return;
        }
        mAvatar.setTag(mData.topicdata2.image);
        ListImageListener listener = new ListImageListener(mAvatar,
                R.drawable.head_default, R.drawable.head_default,
                mData.topicdata2.image);
        getImageLoader().get(mData.topicdata2.image, listener);

        mFansNum.setText(mData.topicdata2.fans_num + "个粉丝");

        initRankingList();
    }

    /**
     * 初始化排行榜列表
     */
    private void initRankingList() {
        // mRankingList
        if (mData.topicdata2.userdata == null
                || mData.topicdata2.userdata.size() == 0) {
            mRankingRoot.setVisibility(View.GONE);
            return;
        } else {
            mRankingRoot.setVisibility(View.VISIBLE);
        }

        int gap = 5;
        int dia = DensityUtils.dp2px(this, 30);
        LayoutParams lp = new LayoutParams(dia, dia);
        lp.setMargins(gap, 0, gap, 0);
        mRankingList.removeAllViews();
        // 初始化赞
        for (int i = 0; i < mData.topicdata2.userdata.size(); i++) {
            CircleImageView image = new CircleImageView(this);
            ZanUserInfo info = mData.topicdata2.userdata.get(i);
            image.setLayoutParams(lp);
            final String usrid = info.userid;
            image.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(),
                            HomePageActivity.class);
                    intent.putExtra(ConstantSet.USERID, usrid);
                    TopicShowActivity.this.startActivity(intent);
                }
            });
            image.setTag(info.avatar);
            ListImageListener listener = new ListImageListener(image, 0, 0,
                    info.avatar);
            getImageLoader().get(info.avatar, listener);

            mRankingList.addView(image);
        }
        CircleImageView more = new CircleImageView(this);
        more.setLayoutParams(lp);
        more.setImageResource(R.drawable.more_users);
        mRankingList.addView(more);
    }

//	private void ShowPop() {
//		if (mPw == null) {
//			mPw = new PopupWindow(LayoutParams.MATCH_PARENT,
//					LayoutParams.MATCH_PARENT);
//		}
//		mPw.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//		mPw.setContentView(mPopView);
//		// mPw.setAnimationStyle(R.style.popwin_anim_style);
//		mPw.showAsDropDown(mTopRoot);
//		mPw.setFocusable(true);
//
//	}

    private void hidePop() {
        if (mPw != null && mPw.isShowing()) {
            mPw.dismiss();
        }
    }

    @Override
    protected void onDestroy() {
        hidePop();
        mPopView = null;
        super.onDestroy();
    }

    private void configShareContent(ShareData shareData) {
        if (null == shareData) {
            return;
        }
        share = new ShareMorePopupWindow(this, mTid);
        share.initShareParams(shareData);
        share.setHint1Visibility(View.GONE);
        share.setHintVisibility(View.GONE);

    }

    private void showPop() {
        if (share == null) {
            share = new ShareMorePopupWindow(this, mTid);
            share.setHintVisibility(View.GONE);
        }
        if (share.isShowing()) {
            share.dismiss();
        } else {

            share.showAtLocation(mLay_main, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }
}
