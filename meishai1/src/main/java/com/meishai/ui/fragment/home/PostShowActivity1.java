package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.TopBackLayout;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.CommodityInfo;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.CameraDialog;
import com.meishai.ui.fragment.camera.BitmapUtils;
import com.meishai.ui.fragment.home.adapter.PostShowAdapter;
import com.meishai.ui.fragment.home.adapter.PostShowAdapter.OnPostClickListenr;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.home.req.PostReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.CommentPopupWindow;
import com.meishai.ui.popup.CommentPopupWindow.OnCommentSuccessListener;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/***
 * @author li
 * @Description: 晒晒->帖子内容
 */

public class PostShowActivity1 extends BaseActivity {

    // 最大选择图片
    private int maxSelected = 9;
    private CameraDialog cameraDialog;
    private LinearLayout lay_main;
    private PullToRefreshListView mPullListView;
    private PostItem mData;
    private PostShowAdapter mAdapter;

    private CustomProgress mProgressDialog;
    // 赞
    private TextView post_praise_tv;
    // 评论
    private TextView post_review_tv;
    // 收藏
    private TextView post_collect_tv;
    // 分享
    private TextView post_share_tv;

    private CommentPopupWindow commentPopupWindow;

    private LinearLayout post_menu_layout;

    private ShareMorePopupWindow share;

    private final int SIZE_PAGE = 10;
    private int mCommentListOrder = 0;
    private int mLastCommentSize = 0;

    private int mFrom = -1;

    public final static int FROM_POST = 1;
    public final static int FROM_REVIEW = 2;
    public final static int FROM_ADS = 3;
    //分享的回调
    private Listener<String> listener;
    private ErrorListener errorListener;
    private int shareType;//分享的类型


    public static Intent newIntent(PostItem item, int from) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                PostShowActivity1.class);
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
        initDialog();
        registerBoradcastReceiver();

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
                HomeReq.share(PostShowActivity1.this, mData.pid, shareType, 0, listener, errorListener);
            }
        };

        share.initShareParams(shareData);
        share.setHintVisibility(View.GONE);
        share.setActionListener(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                AndroidUtil.showToast("分享成功!");
                HomeReq.share(PostShowActivity1.this, mData.pid, shareType, 0, listener, errorListener);
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                AndroidUtil.showToast("分享失败");
                HomeReq.share(PostShowActivity1.this, mData.pid, shareType, -99, listener, errorListener);
            }

            @Override
            public void onCancel(Platform platform, int i) {
                AndroidUtil.showToast("取消分享");
                HomeReq.share(PostShowActivity1.this, mData.pid, shareType, -1, listener, errorListener);
            }
        });
    }

    //1
    private void initView() {
        lay_main = (LinearLayout) findViewById(R.id.lay_main);
        post_menu_layout = (LinearLayout) findViewById(R.id.post_menu_layout);
        TopBackLayout topBackLayout = (TopBackLayout) findViewById(R.id.back_layout);
        topBackLayout.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        // 赞
        post_praise_tv = (TextView) findViewById(R.id.post_praise_tv);
        post_praise_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                zan();
            }
        });
        // 评论
        post_review_tv = (TextView) findViewById(R.id.post_review_tv);
        post_review_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                commentPopupWindow.setPostItem(mData);
                commentPopupWindow.showPop(lay_main);
            }
        });
        post_collect_tv = (TextView) findViewById(R.id.post_collect_tv);
        post_collect_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                fav();
            }
        });
        post_share_tv = (TextView) findViewById(R.id.post_share_tv);
        post_share_tv.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (null != share) {
                    share.showAtLocation(lay_main, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
            }
        });
        mPullListView = (PullToRefreshListView) findViewById(R.id.post_listview);
        mPullListView.setMode(Mode.PULL_FROM_END);
        PullToRefreshHelper.initIndicator(mPullListView);
        mAdapter = new PostShowAdapter(this, getImageLoader());
        mAdapter.setCommentListener(new PostShowActivity.OnCommentClickListener() {

            @Override
            public void onClick(CommentInfo commentInfo) {
                commentPopupWindow.setPostItem(mData);
                commentPopupWindow.setCommentInfo(commentInfo);
                commentPopupWindow.showPop(lay_main);
            }
        });
        mAdapter.setOnDelLisnter(new OnClickListener() {

            @Override
            public void onClick(View v) {
                getRequestDelPost();
            }
        });
        mAdapter.setClickListener(new OnPostClickListenr() {

            @SuppressLint("NewApi")
            @Override
            public void onClickSort(View v) {
                ImageView iv = (ImageView) v;
                if (mCommentListOrder == 0) {
                    mCommentListOrder = 1;

                    Matrix matrix = new Matrix();
                    matrix.postRotate(180);
                    Bitmap bmpArrow = BitmapFactory.decodeResource(getResources(), R.drawable.down);
                    bmpArrow = Bitmap.createBitmap(bmpArrow, 0, 0, bmpArrow.getWidth(), bmpArrow.getHeight(), matrix, true);
                    iv.setImageBitmap(bmpArrow);

                } else {

                    iv.setImageResource(R.drawable.down);
                    mCommentListOrder = 0;
                }

                getRequestComment(1, true);
            }

            @Override
            public void onClickComment(View v) {
                commentPopupWindow.setPostItem(mData);
                commentPopupWindow.showPop(lay_main);
            }
        });


        mData = getIntent().getParcelableExtra("item");
        //		if(mFrom == FROM_POST || mFrom == FROM_REVIEW) {
        //			mAdapter.setPostItem(mData);
        //		}


        mPullListView.setAdapter(mAdapter);

        mPullListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                getRequestComment(mPage, false);
            }
        });
    }

    //2
    private void initPopup() {
        commentPopupWindow = new CommentPopupWindow(this);
        commentPopupWindow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (v.getId()) {
                    case R.id.close_keybord:
                        break;
                    case R.id.ig_upload:
                        cameraDialog.setMaxSelected(maxSelected
                                - commentPopupWindow.hasSelectCount());
                        cameraDialog.showDialog();
                        break;
                    default:
                        break;
                }
            }
        });

        /**
         * 设置评论成功后的回调
         */
        commentPopupWindow.setOnCommentSuccessListener(new OnCommentSuccessListener() {

            @Override
            public void onCommentSuccess(CommentInfo info) {
                if (mCommentListOrder == 1) {
                    getRequestComment(1, true);
                } else {
                    getRequestComment(mPage, false);
                }

            }
        });
    }

    //3
    private void initDialog() {
        cameraDialog = new CameraDialog(this);
    }

    private void getRequestCommodity() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("item");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(mData.pid));//用到了一个pid
        reqData.setData(dataCate);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            getRequestComment(mPage, false);
                            if (!checkDataCommodity(response)) {
                                DebugLog.d("获取商品信息失败");
                            }
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            getRequestComment(mPage, false);
                            AndroidUtil.showToast(R.string.reqFailed);
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取商品详情
     */
    private void getRequestPost() {
        showProgress("", getString(R.string.network_wait));

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("show");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(mData.pid));
        reqData.setData(dataCate);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            if (!checkDataPost(response)) {
                                DebugLog.d("获取详情失败");
                                showToast("获取详情失败");
                            }
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            hideProgress();
                            AndroidUtil.showToast(R.string.reqFailed);
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private int mPage = 1;

    private void getRequestComment(final int page, final boolean isClear) {
        //		showProgress("", getString(R.string.network_wait));

        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("comment");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(mData.pid));
        dataCate.put("listorder", String.valueOf(mCommentListOrder));
        dataCate.put("page", String.valueOf(mPage));
        dataCate.put("pageSize", String.valueOf(SIZE_PAGE));
        reqData.setData(dataCate);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            //							hideProgress();
                            if (checkDataComment(page, isClear, response)) {
                                mPullListView.onRefreshComplete();
                            } else {
                                mPullListView.onRefreshComplete();
                                //mPullListView.setMode(Mode.DISABLED);
                                // showToast(R.string.drop_down_list_footer_no_more_text);
                            }
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            //							hideProgress();
                            AndroidUtil.showToast(R.string.reqFailed);
                            mPullListView.onRefreshComplete();
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private boolean checkDataCommodity(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                JSONArray array = jsonObj.getJSONArray("data");

                Type type = new TypeToken<Collection<CommodityInfo>>() {
                }.getType();
                Collection<CommodityInfo> items = GsonHelper.parseObject(array.toString(), type);

                mAdapter.setCommodityInfo(items);

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private boolean checkDataPost(String response) {
        //DebugLog.d("post:" + response);
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                JSONArray array = jsonObj.getJSONArray("data");
                Type type = new TypeToken<List<PostItem>>() {
                }.getType();
                List<PostItem> postItems = GsonHelper.parseObject(array.toString(), type);
                if (null != postItems && !postItems.isEmpty()) {
                    //					if(mFrom == FROM_ADS) {
                    //						mData = postItems.get(0);
                    //						mAdapter.setPostItem(mData);
                    //					}
                    mData = postItems.get(0);

                    String content = array.getJSONObject(0).getString("content");
                    mData.description = content;

                    getRequestPic();
                    post_menu_layout.setVisibility(View.VISIBLE);
                    configShareContent(postItems.get(0).getSharedata());

                    getRequestCommodity();


                    return true;
                }

            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        hideProgress();

        return false;
    }

    private boolean checkDataComment(int page, boolean isClear, String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                JSONArray array = jsonObj.getJSONArray("data");
                Type type = new TypeToken<Collection<CommentInfo>>() {
                }.getType();
                List<CommentInfo> items = GsonHelper.parseObject(array.toString(), type);
                int newSize = items.size();
                if (newSize > 0) {
                    if (!isClear) {
                        DebugLog.d("remove :" + mLastCommentSize);
                        for (int i = 0; i < mLastCommentSize; i++) {
                            items.remove(0);
                        }
                    }

                    if (newSize == SIZE_PAGE) {
                        mPage++;
                        mLastCommentSize = 0;
                    } else {
                        mLastCommentSize = newSize;
                    }

                    if (isClear) {
                        mAdapter.clearComments();
                    }
                    mAdapter.addComments(items);
                }
                return true;
            }

        } catch (JSONException e) {
            DebugLog.e("comment:" + response);
            e.printStackTrace();
        }
        return false;
    }

    private void getRequestPic() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("pic");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("pid", String.valueOf(mData.pid));
        reqData.setData(dataCate);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            //DebugLog.d(url);
            getRequestQueue().add(new StringRequest(url, new Listener<String>() {
                @Override
                public void onResponse(String response) {
                    //DebugLog.d(response);
                    if (!checkDataPic(response)) {
                        AndroidUtil.showToast(R.string.reqFailed);
                    }
                    hideProgress();
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AndroidUtil.showToast(R.string.reqFailed);
                    hideProgress();
                }
            }));


        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }


    private boolean checkDataPic(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);

            if (jsonObj.getInt("success") == 1) {
                JSONArray jsonArray = jsonObj.getJSONObject("data").getJSONArray("pics");
                Type type = new TypeToken<Collection<PostItem.PictureInfo>>() {
                }.getType();
                Collection<PostItem.PictureInfo> items = GsonHelper.parseObject(jsonArray.toString(), type);
                mData.pics.clear();//用到了一个pics
                mData.pics.addAll(items);
                mAdapter.setPostItem(mData);

                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    public interface OnCommentClickListener {

        public void onClick(CommentInfo commentInfo);
    }

    private void addImageData(ImageData data) {
        commentPopupWindow.addImageData(data);
    }

    private void addAllImageData(List<ImageData> datas) {
        commentPopupWindow.setImageDatas(datas);
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantSet.ACTION_NAME)) {
                ArrayList<String> imagePaths = intent
                        .getStringArrayListExtra(ConstantSet.CHOOSE_DATA);
                List<ImageData> datas = new ArrayList<ImageData>();
                if (null != imagePaths && !imagePaths.isEmpty()) {
                    for (String path : imagePaths) {
                        String targetPath = context.getCacheDir() + BitmapUtils.toRegularHashCode(path) + ".jpg";
                        BitmapUtils.compress(path, targetPath, 800);  //压缩

                        ImageData data = ImageData.getLocalImage(targetPath);
                        datas.add(data);
                    }
                }
                addAllImageData(datas);
            }
        }
    };

    //4
    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConstantSet.ACTION_NAME);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }
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
        } else if (response.isLogin()) {
            startActivity(LoginActivity.newOtherIntent());
        } else {
            AndroidUtil.showToast(response.getTips());
        }
    }

    ;

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

    private void doPhoto(int requestCode, Intent intent) {
        if (requestCode == ConstantSet.SELECT_PIC_BY_TACK_PHOTO) {
            if (cameraDialog.getPhotoUri() == null) {
                //				AndroidUtil.showToast("拍照图片文件出错");
                return;
            }
            // 将拍照后图片偏差修正
            // String path = AndroidUtil.imageUri2Path(photoUri);
            // int degree = AndroidUtil.readPictureDegree(path);
            // Bitmap bmp = ImageUtil.rotateBitmap(path, degree);
            // File f = ImageUtil.saveBitMap2File(path, bmp);
            // bmp.recycle();
            // bmp = null;
            // cropImage(Uri.fromFile(f));

            // 裁剪
            // cropImage(photoUri);
            String uriPath = AndroidUtil.imageUri2Path(cameraDialog
                    .getPhotoUri());
            final String targetPath = this.getCacheDir() + BitmapUtils.toRegularHashCode(uriPath) + ".jpg";
            BitmapUtils.compressBitmap(uriPath, targetPath, 800);  //压缩
            ImageData data = ImageData.getLocalImage(targetPath);
            addImageData(data);
        }
    }

    // @Override
    // public void onBackPressed() {
    // System.out.println("onBackPressed");
    // return;
    // }
    //
    // @Override
    // public boolean dispatchKeyEvent(KeyEvent event) {
    // System.out.println("dispatchKeyEvent");
    // if (event.getKeyCode() == KeyEvent.KEYCODE_BACK
    // && event.getRepeatCount() == 0
    // && event.getAction() == KeyEvent.ACTION_DOWN) {
    // if (null != commentPopupWindow) {
    // commentPopupWindow.dismissPop();
    // }
    // return false;
    // } else {
    // return super.dispatchKeyEvent(event);
    // }
    // }
    //
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // System.out.println("onKeyDown");
    // if (keyCode == KeyEvent.KEYCODE_BACK) {
    // if (null != commentPopupWindow) {
    // commentPopupWindow.dismissPop();
    // }
    // return true;
    // }
    // return super.onKeyDown(keyCode, event);
    // }
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
