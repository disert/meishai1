package com.meishai.ui.fragment.meiwu;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.GoodsDetailRespData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.meiwu.adapter.GoodsDetailAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * 商品详情
 *
 * @author Administrator yl
 */
public class MeiWuGoodsDetailActivity extends BaseActivity {


    private PullToRefreshListView mRefreshListView;
    private GoodsDetailAdapter mAdapter;
    private long mPid;
    private Button mBackMain;
    private ImageButton mMore;

    private ImageButton mWechat;
    private TextView mWechatPoint;
    private ImageButton mFriend;
    private TextView mFriendPoint;

    // 三个按钮
    private ImageView mCollect;
    private Button mBuy;
    private GoodsDetailRespData mData;

    private ShareMorePopupWindow sharePop;
    private LinearLayout mLayRoot;

    public static Intent newIntent(long pid) {
        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), MeiWuGoodsDetailActivity.class);
        intent.putExtra("pid", pid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meiwu_goods_detail_activity);

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
            mAdapter.notifyDataSetChanged();
        }
    }

    private void initListener() {
        //listview
        mAdapter = new GoodsDetailAdapter(this, getImageLoader());
        mRefreshListView.setAdapter(mAdapter);
        mRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
                //加载数据
                getRequestData();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {

            }
        });

        //返回按钮
        mBackMain.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        //分享到微信
        mWechat.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sharePop != null) {
                    sharePop.share(Wechat.NAME);
                }
            }
        });
        //分享到朋友圈
        mFriend.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (sharePop != null) {
                    sharePop.share(WechatMoments.NAME);
                }
            }
        });
        //更多操作
        mMore.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                showPop();
            }
        });
        //收藏
        mCollect.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                collect();
            }
        });

        //去购买
        mBuy.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                buyReq();
            }
        });
    }

    private void initView() {
        mLayRoot = (LinearLayout) findViewById(R.id.meiwu_goods_detail_root);
        // 标题栏相关
        mBackMain = (Button) findViewById(R.id.backMain);
        mMore = (ImageButton) findViewById(R.id.more);


        //数据区域
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicator(mRefreshListView);
        PullToRefreshHelper.initIndicatorStart(mRefreshListView);


        //底部区域
        mWechat = (ImageButton) findViewById(R.id.wechat);
        mWechatPoint = (TextView) findViewById(R.id.wechat_point);
        mWechatPoint.setVisibility(View.GONE);
        mFriend = (ImageButton) findViewById(R.id.friend);
        mFriendPoint = (TextView) findViewById(R.id.friend_point);
        mFriendPoint.setVisibility(View.GONE);
        mCollect = (ImageView) findViewById(R.id.collect);
        mBuy = (Button) findViewById(R.id.buy);

        mPid = getIntent().getLongExtra("pid", 0);

    }

    protected void getRequestData() {
        if (mPid == 0) {
            throw new RuntimeException("你需要传入pid");
        }
        showProgress("", getString(R.string.network_wait));
        MeiWuReq.commodDetailsReq(mPid, new Listener<String>() {

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
        mRefreshListView.onRefreshComplete();
        hideProgress();
    }

    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                mData = GsonHelper.parseObject(response,
                        GoodsDetailRespData.class);
                initData();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);

            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
        }
    }

    private void initData() {
        if (mData == null || mData.data == null || mData.data.size() == 0) {
            showToast("该商品数据无法获得!");
            return;
        }

        //------------------分享数据------------------------
        sharePop = new ShareMorePopupWindow(MeiWuGoodsDetailActivity.this, 0) {
            @Override
            public void sharePre(String name) {
                MeiWuReq.sharePid(mData.data.get(0).pid, getPlatformType(name), 0);
            }
        };
        sharePop.initShareParams(mData.sharedata);
        sharePop.setHint1Visibility(View.GONE);
        sharePop.setHintVisibility(View.GONE);
        sharePop.setActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Toast.makeText(MeiWuGoodsDetailActivity.this, "分享成功", Toast.LENGTH_SHORT).show();
                MeiWuReq.sharePid(mData.data.get(0).pid, sharePop.getPlatformType(platform.getName()), 1);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Toast.makeText(MeiWuGoodsDetailActivity.this, "分享失败", Toast.LENGTH_SHORT).show();
                MeiWuReq.sharePid(mData.data.get(0).pid, sharePop.getPlatformType(platform.getName()), -99);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Toast.makeText(MeiWuGoodsDetailActivity.this, "分享取消", Toast.LENGTH_SHORT).show();
                MeiWuReq.sharePid(mData.data.get(0).pid, sharePop.getPlatformType(platform.getName()), -1);
            }
        });

        //------------------底部按钮数据----------------------------
        if (mData != null && mData.bottom != null) {
            mBuy.setText(mData.bottom.button_text);
            if (mData.bottom.isfav == 1) {
                mCollect.setImageResource(R.drawable.collected_icon);
                mCollect.setClickable(false);
            } else {
                mCollect.setImageResource(R.drawable.uncollect_icon);
                mCollect.setClickable(true);
            }
        }

        mAdapter.setData(mData);
    }

    private void showPop() {
        if (sharePop == null) {
            sharePop = new ShareMorePopupWindow(this, 0);
        }
        if (sharePop.isShowing()) {
            sharePop.dismiss();
        } else {

            sharePop.showAtLocation(mLayRoot, Gravity.BOTTOM
                    | Gravity.CENTER_HORIZONTAL, 0, 0);
        }
    }

    protected void collect() {

        // 没登陆,去登陆
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            startActivity(LoginActivity.newOtherIntent());
            return;
        }
        // 弹出等待对话框
        String msg = getResources().getString(R.string.network_wait);
        final CustomProgress dlgProgress = CustomProgress.show(this, msg, true,
                null);
        MeiWuReq.collect(mPid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                dlgProgress.dismiss();
                try {
                    if (TextUtils.isEmpty(response)) {
                        mCollect.setImageResource(R.drawable.collected_icon);
                        mCollect.setClickable(false);
                        return;
                    }
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    String tips = obj.getString("tips");
                    Toast.makeText(MeiWuGoodsDetailActivity.this, tips,
                            Toast.LENGTH_SHORT).show();
                    if (1 == success) {
                        mCollect.setImageResource(R.drawable.collected_icon);
                        mCollect.setClickable(false);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                dlgProgress.dismiss();

                DebugLog.w(error.toString());
            }
        });

    }

    protected void buyReq() {
        // 发送购买的请求.在请求中有URL直接跳转到淘宝
        if (mData != null && mData.bottom != null) {
            MeiWuReq.buyReq(this, mPid, mData.bottom.itemurl, mData.bottom.istao);
        }
    }
}
