package com.meishai.ui.fragment.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.app.widget.TopBackLayout;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.PostDetailRespData;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.ShareData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.CameraDialog;
import com.meishai.ui.fragment.camera.BitmapUtils;
import com.meishai.ui.fragment.home.adapter.PostShowAdapter1;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.home.req.PostReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.CommentPopupWindow;
import com.meishai.ui.popup.CommentPopupWindow.OnCommentSuccessListener;
import com.meishai.ui.popup.CommentPopupWindow1;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 文件名：
 * 描    述：晒晒详情
 * 作    者： yl
 * 时    间：2016/1/27
 * 版    权：
 */

public class PostShowActivity extends BaseActivity {

    private LinearLayout lay_main;
    private PullToRefreshListView mPullListView;
    private PostItem mData;
    private PostShowAdapter1 mAdapter;

    private CustomProgress mProgressDialog;

    private CommentPopupWindow1 commentPopupWindow;


    private ShareMorePopupWindow share;


    private int mFrom = -1;

    public final static int FROM_POST = 1;
    public final static int FROM_REVIEW = 2;
    public final static int FROM_ADS = 3;
    //分享的回调
    private Listener<String> listener;
    private ErrorListener errorListener;
    private int shareType;//分享的类型
    private LinearLayout mCommentContainer;
    private ImageView mPraise;
    private ImageView mCollect;
    private EditTextWithDel mCommentEdit;


    public static Intent newIntent(PostItem item, int from) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                PostShowActivity.class);
        intent.putExtra("item", item);
        intent.putExtra("from", from);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_post_show);

        mFrom = getIntent().getIntExtra("from", -1);

        initView();
        initPopup();

        getRequestPost();
        //		getRequestCommodity();
        //		getRequestComment(mPage, false);
    }

    @Override
    public void onAttachedToWindow() {
        super.onAttachedToWindow();

        if (FROM_REVIEW == mFrom) {
            commentPopupWindow.setPostItem(mData);
            commentPopupWindow.showPop(lay_main);
        }
    }

    private void configShareContent(ShareData shareData) {
        if (null == shareData) {
            return;
        }

        listener = new Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.d("分享请求发送成功");
                try {
                    JSONObject obj = new JSONObject(response);
                    String tips = obj.getString("tips");
                    if (!TextUtils.isEmpty(tips)) {
                        AndroidUtil.showToast(tips);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        };

        errorListener = new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.w("分享请求发送出错");
                error.printStackTrace();
            }
        };

        share = new ShareMorePopupWindow(this, 0) {
            @Override
            public void sharePre(String name) {
                shareType = getPlatformType(name);
                HomeReq.share(PostShowActivity.this, mData.pid, shareType, 0, listener, errorListener);
            }
        };

        share.initShareParams(shareData);
        share.setHintVisibility(View.GONE);
        share.setActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                AndroidUtil.showToast("分享成功!");
                HomeReq.share(PostShowActivity.this, mData.pid, shareType, 0, listener, errorListener);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                AndroidUtil.showToast("分享失败");
                HomeReq.share(PostShowActivity.this, mData.pid, shareType, -99, listener, errorListener);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                AndroidUtil.showToast("取消分享");
                HomeReq.share(PostShowActivity.this, mData.pid, shareType, -1, listener, errorListener);
            }
        });
    }

    //1
    private void initView() {
        lay_main = (LinearLayout) findViewById(R.id.lay_main);
        findViewById(R.id.post_menu_layout).setVisibility(View.GONE);
        TopBackLayout topBackLayout = (TopBackLayout) findViewById(R.id.back_layout);
        topBackLayout.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        topBackLayout.setOnMoreListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (null != share) {
                    share.showAtLocation(lay_main, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
        //底部
        mCommentContainer = (LinearLayout) findViewById(R.id.comment);
        mCommentContainer.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                    return;
                }
                if (mData != null) {
                    commentPopupWindow.setPostItem(mData);
                    commentPopupWindow.showPop(lay_main);
                }
            }
        });
        mPraise = (ImageView) findViewById(R.id.praise);
        mPraise.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                    return;
                }
                zan();
            }
        });
        mCollect = (ImageView) findViewById(R.id.collect);
        mCollect.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                    return;
                }
                fav();
            }
        });
        mCommentEdit = (EditTextWithDel) findViewById(R.id.comment_bar);
        mCommentEdit.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                    return;
                }
                if (mData != null) {
                    commentPopupWindow.setPostItem(mData);
                    commentPopupWindow.showPop(lay_main);
                }
            }
        });
        mPullListView = (PullToRefreshListView) findViewById(R.id.post_listview);
        mPullListView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicator(mPullListView);
        PullToRefreshHelper.initIndicatorStart(mPullListView);
        mAdapter = new PostShowAdapter1(this, getImageLoader());
        /**
         * 设置评论中某楼被点击后的事件
         */
        mAdapter.setCommentListener(new OnCommentClickListener() {

            @Override
            public void onClick(CommentInfo commentInfo) {
                if (!MeiShaiSP.getInstance().getUserInfo().isLogin()) {
                    startActivity(LoginActivity.newIntent());
                    return;
                }
                commentPopupWindow.setPostItem(mData);
                commentPopupWindow.setCommentInfo(commentInfo);
                commentPopupWindow.showPop(lay_main);
            }
        });
        /**
         * 设置点击删除后的事件
         */
        mAdapter.setOnDelLisnter(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getRequestDelPost();
            }
        });
//        mAdapter.setClickListener(new OnPostClickListenr() {
//
//            @SuppressLint("NewApi")
//            @Override
//            public void onClickSort(View v) {
//                ImageView iv = (ImageView) v;
//                if (mCommentListOrder == 0) {
//                    mCommentListOrder = 1;
//
//                    Matrix matrix = new Matrix();
//                    matrix.postRotate(180);
//                    Bitmap bmpArrow = BitmapFactory.decodeResource(getResources(), R.drawable.down);
//                    bmpArrow = Bitmap.createBitmap(bmpArrow, 0, 0, bmpArrow.getWidth(), bmpArrow.getHeight(), matrix, true);
//                    iv.setImageBitmap(bmpArrow);
//
//                } else {
//
//                    iv.setImageResource(R.drawable.down);
//                    mCommentListOrder = 0;
//                }
//
//                getRequestComment(1, true);
//            }
//
//            @Override
//            public void onClickComment(View v) {
//                commentPopupWindow.setPostItem(mData);
//                commentPopupWindow.showPop(lay_main);
//            }
//        });


        mData = getIntent().getParcelableExtra("item");
        //		if(mFrom == FROM_POST || mFrom == FROM_REVIEW) {
        //			mAdapter.setPostItem(mData);
        //		}


        mPullListView.setAdapter(mAdapter);

        mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getRequestPost();
            }
        });
    }

    //2
    private void initPopup() {
        commentPopupWindow = new CommentPopupWindow1(this);
//        commentPopupWindow.setOnClickListener(new OnClickListener() {
//
//            @Override
//            public void onClick(View v) {
//                switch (v.getId()) {
//                case R.id.close_keybord:
//                    break;
//                case R.id.ig_upload:
//                    cameraDialog.setMaxSelected(maxSelected
//                            - commentPopupWindow.hasSelectCount());
//                    cameraDialog.showDialog();
//                    break;
//                default:
//                    break;
//                }
//            }
//        });

        /**
         * 设置评论成功后的回调
         */
        commentPopupWindow.setOnCommentSuccessListener(new OnCommentSuccessListener() {

            @Override
            public void onCommentSuccess(CommentInfo info) {
                getRequestPost(true);

            }
        });
    }

    /**
     * 获取商品详情
     */
    private void getRequestPost() {
        getRequestPost(false);
    }

    /**
     * 当刷新模式为true时,表示刷新的是评论的数据
     *
     * @param refreshMode
     */
    private void getRequestPost(final boolean refreshMode) {
        showProgress("", getString(R.string.network_wait));
        mPraise.setClickable(false);
        mCollect.setClickable(false);

        HomeReq.postDetail(this, mData.pid, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                mPullListView.onRefreshComplete();
                mPraise.setClickable(true);
                mCollect.setClickable(true);
                if (!checkDataPost(response, refreshMode)) {
                    showToast("获取详情失败");
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                mPullListView.onRefreshComplete();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });
    }

    private boolean checkDataPost(String response, boolean refreshMode) {
        if (!TextUtils.isEmpty(response)) {
            PostDetailRespData respData = GsonHelper.parseObject(response, PostDetailRespData.class);
            if (respData != null) {

                if (refreshMode) {
                    mAdapter.clearComments();
                    mAdapter.addComments(respData);
                } else {
                    if (respData.data != null && !respData.data.isEmpty()) {
                        mData = respData.data.get(0);

                        configShareContent(mData.getSharedata());
                        //初始化数据
                        if (mData.iszan == 1) {
                            mPraise.setSelected(true);
                            mPraise.setClickable(false);
                        } else {
                            mPraise.setSelected(false);
                            mPraise.setClickable(true);
                        }
                        if (mData.isfav == 1) {
                            mCollect.setSelected(true);
                            mCollect.setClickable(false);
                        } else {
                            mCollect.setSelected(false);
                            mCollect.setClickable(true);
                        }
                    }
                    mAdapter.setData(respData);
                }
                return true;
            } else {
                return false;
            }
        }


        return false;
    }

    public interface OnCommentClickListener {

        public void onClick(CommentInfo commentInfo);
    }

    /**
     * 收藏
     */
    private void fav() {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(this,
                    getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("pid", String.valueOf(mData.pid));
        PostReq.fav(this, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                onReqResult(response);
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(getString(R.string.reqFailed));
            }
        });
    }

    // 请求成功之后 调用
    private void onReqResult(RespData response) {
        if (response.isSuccess()) {
            AndroidUtil.showToast(response.getTips());
            mCollect.setSelected(true);
            mCollect.setClickable(false);
        } else if (response.isLogin()) {
            startActivity(LoginActivity.newOtherIntent());
        } else {
            AndroidUtil.showToast(response.getTips());
        }
    }

    /**
     * 赞
     */
    private void zan() {
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(this,
                    getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("pid", String.valueOf(mData.pid));
        PostReq.zan(this, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    mPraise.setSelected(true);
                    mPraise.setClickable(false);
                    AndroidUtil.showToast(response.getTips());
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(getString(R.string.reqFailed));
            }
        });
    }

    private void getRequestDelPost() {


        PostReq.deletePost(this, mData.pid, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    if (obj.getInt("success") == 1) {
                        AndroidUtil.showToast("删除成功!");
                        //TODO 发送一个删除数据的广播
                        Intent intent = new Intent(ConstantSet.POST_DELETE);
                        intent.putExtra("pid", mData.pid);
                        sendBroadcast(intent);

                        finish();
                        return;
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                AndroidUtil.showToast("删除失败!");
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
            }
        });

    }

}
