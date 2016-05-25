package com.meishai.ui.fragment.tryuse;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.layout.SampleRoundImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.FuLiSheDetailResq;
import com.meishai.entiy.TrialDetailRespData.Picture;
import com.meishai.entiy.TrialDetailRespData.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ImageLoader.ImageListener;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.tryuse.req.FuLiSheReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PackageManagerUtils;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;

/**
 * 试用详情对应的Activity
 *
 * @author Administrator yl
 */
public class FuliSheDetailActivity1 extends BaseActivity implements
        OnClickListener {

    private int mType = 0;

    private int mGid;

    private ImageView mImage;
    private TextView mImageTitle;

    private TextView mNotice;
    private TextView mNoticeContent;
    private TextView mEndday;
    private Button mApplyBtn;
    private LinearLayout mPics;
    private FuLiSheDetailResq mResqData;
    private ShareMorePopupWindow sharePop;


    private int mCurrentOper;// 当前选择的操作,0 普通分享 ,1 申请试用 ,2 积分兑换,3 抢购 ,
    private RelativeLayout mLayRoot;
    private TextView mTitle;

    // 发送分享请求时用到的监听器
    private Listener<String> mShareListener;
    private ErrorListener mShareErrorListener;
    private View mDataArea;


    private TextView mPrice;
    private TextView mLimitNum;
    private TextView mApplyNum;
    private TextView mPriceText;
    private TextView mLimitText;
    private TextView mApplyText;

    private LinearLayout mChains;

    private ImageView mProcessImage;
    private TextView mProcessText;
    private TextView mGoodsContent;
    private TextView mGoodsTitle;
    private Button mBuyBtn;
    private int screenWidth;

    @Deprecated
    public static Intent newIntent(int gid, int type) {

        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), FuliSheDetailActivity1.class);
        intent.putExtra("gid", gid);
//        intent.putExtra("type", type);
        return intent;
    }

    public static Intent newIntent(int gid) {

        Intent intent = new Intent(GlobalContext.getInstance()
                .getApplicationContext(), FuliSheDetailActivity1.class);
        intent.putExtra("gid", gid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.trial_detail_activity1);

        DisplayMetrics metric = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(metric);
        screenWidth = metric.widthPixels;

        initView();

    }

    @Override
    protected void onResume() {
        super.onResume();
        getRequestData();
    }

    private void initView() {

        mType = getIntent().getIntExtra("type", 0);
        mGid = getIntent().getIntExtra("gid", 0);
        if (mGid == 0) {
            throw new RuntimeException("gid不能为空");
        }
        mDataArea = findViewById(R.id.fulishe_detail_data_area);

        mLayRoot = (RelativeLayout) findViewById(R.id.main);
        mTitle = (TextView) findViewById(R.id.title);
        findViewById(R.id.backMain).setOnClickListener(this);
        findViewById(R.id.more).setOnClickListener(this);
        //头部
        mImage = (ImageView) findViewById(R.id.fulishe_detail_image);
        mImageTitle = (TextView) findViewById(R.id.fulishe_detail_content);

        // 试用商品信息
        findViewById(R.id.fulishe_detail_price_container).setOnClickListener(this);
        findViewById(R.id.fulishe_detail_limit_container).setOnClickListener(this);
        findViewById(R.id.fulishe_detail_apply_container).setOnClickListener(this);
        mPrice = (TextView) findViewById(R.id.fulishe_detail_price_num);
        mPriceText = (TextView) findViewById(R.id.fulishe_detail_price);
        mLimitNum = (TextView) findViewById(R.id.fulishe_detail_limit_num);
        mLimitText = (TextView) findViewById(R.id.fulishe_detail_limit);
        mApplyNum = (TextView) findViewById(R.id.fulishe_detail_apply_num);
        mApplyText = (TextView) findViewById(R.id.fulishe_detail_apply);

        //链接
        mChains = (LinearLayout) findViewById(R.id.fulishe_detail_chains);

        //体验流程信息
        mProcessText = (TextView) findViewById(R.id.fulishe_detail_process_title);
        mProcessImage = (ImageView) findViewById(R.id.fulishe_detail_process_image);

        // 试用商品信息
        mGoodsTitle = (TextView) findViewById(R.id.fulishe_detail_goods_title);
        mGoodsContent = (TextView) findViewById(R.id.fulishe_detail_goods_content);
        mPics = (LinearLayout) findViewById(R.id.fulishe_detail_pics);

        // 提示信息
        mNotice = (TextView) findViewById(R.id.fulishe_detail_notice);
        mNoticeContent = (TextView) findViewById(R.id.fulishe_detail_notice_content);

        // 底部按钮栏信息
        mEndday = (TextView) findViewById(R.id.fulishe_detail_endday);
        mBuyBtn = (Button) findViewById(R.id.fulishe_detail_buy_btn);//直接购买
        mBuyBtn.setOnClickListener(this);
        mApplyBtn = (Button) findViewById(R.id.fulishe_detail_apply_btn);//申请按钮
        mApplyBtn.setOnClickListener(this);

        mShareListener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                hideProgress();
                try {
                    JSONObject obj = new JSONObject(response);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };
        mShareErrorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                hideProgress();
            }
        };

    }

    String pre = "";

    private void initData() {

        if (mResqData == null || mResqData.data == null) {
            AndroidUtil.showToast("没有获取到有效数据");
            return;
        }


        if (!mResqData.data.appnum_color.startsWith("#")) {
            pre = "#FF";
        } else {
            pre = "";
        }
        //---------------------分享对话框的初始化-------------------
        sharePop = new ShareMorePopupWindow(this, 0) {
            @Override
            public void sharePre(String name) {
                // 分享前请求
                FuLiSheReq.share(mGid, getPlatformType(name), 0,
                        mShareListener, mShareErrorListener);
            }
        };
        // 设置分享页面的分享数据
        sharePop.initShareParams(mResqData.sharedata);
//        // 设置分享页面的提示
//        sharePop.setHint("分享就可以参加" + mResqData.data.page_title + "咯~");
        // 分享后发送请求 0 普通分享 ,1 申请试用 ,2 积分兑换,3 抢购 ,
        sharePop.setActionListener(new PlatformActionListener() {

            @Override
            public void onError(Platform arg0, int arg1, Throwable arg2) {
                FuLiSheReq.share(mGid,
                        sharePop.getPlatformType(arg0.getName()), -99,
                        mShareListener, mShareErrorListener);
            }

            @Override
            public void onComplete(Platform arg0, int arg1,
                                   HashMap<String, Object> arg2) {
                // 分享请求
                FuLiSheReq.share(mGid,
                        sharePop.getPlatformType(arg0.getName()), 1,
                        mShareListener, mShareErrorListener);
                mResqData.data.isshare = 1;
                // 提交
                if (mResqData.data.isorder == 0) {//不填地址
                    submit();

                } else {//填地址
                    Intent intent2 = FindPointOrderActivity1.newIntent(mGid);
                    startActivity(intent2);
                }
            }

            @Override
            public void onCancel(Platform arg0, int arg1) {
                AndroidUtil.showToast("您取消了分享!");
                int type = sharePop.getPlatformType(arg0.getName());
                FuLiSheReq.share(mGid, type, -1, mShareListener,
                        mShareErrorListener);
            }
        });
        //------------------标题-------------
        mTitle.setText(mResqData.data.page_title);
        //-----------------头----------------

        mImageTitle.setText(mResqData.data.title);
        if (!TextUtils.isEmpty(mResqData.data.image)) {
            mImage.setTag(mResqData.data.image);
            ImageListener listener = ImageLoader.getImageListener(mImage,
                    R.drawable.place_default, R.drawable.place_default);
            getImageLoader().get(mResqData.data.image, listener);
        } else {
            mImage.setImageResource(R.drawable.place_default);
        }
        // -------------活动信息---------------
        //价格
        mPriceText.setText(mResqData.data.price_text);
        if (!TextUtils.isEmpty(mResqData.data.price_color)) {
            mPrice.setTextColor(Color.parseColor(pre + mResqData.data.price_color));
        }
        mPrice.setText(mResqData.data.price);
        mPrice.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG); //删除线
        mPrice.getPaint().setAntiAlias(true);//抗锯齿
        //限量
        mLimitText.setText(mResqData.data.number_text);
        mLimitNum.setText(mResqData.data.number);
        if (!TextUtils.isEmpty(mResqData.data.number)) {
            mLimitNum.setTextColor(Color.parseColor(pre + mResqData.data.number_color));
        }
        //申请
        mApplyText.setText(mResqData.data.appnum_text);
        mApplyNum.setText(mResqData.data.appnum);
        if (!TextUtils.isEmpty(mResqData.data.appnum_color)) {
            mApplyNum.setTextColor(Color.parseColor(pre + mResqData.data.appnum_color));
        }
        mApplyNum.setText(mResqData.data.appnum);
        //--------------------链接信息----------------------
        if (mResqData.h5data != null && !mResqData.h5data.isEmpty()) {
            //初始化链接信息
            initChains();
        }

        //--------------------体验流程------------------
        mProcessText.setText(mResqData.process.title);
//        mProcessText.getPaint().setFakeBoldText(true);//加粗
        if (!TextUtils.isEmpty(mResqData.process.image)) {
            mProcessImage.setTag(mResqData.process.image);
            ImageListener listener1 = ImageLoader.getImageListener(mProcessImage, R.drawable.place_default, R.drawable.place_default);
            getImageLoader().get(mResqData.process.image, listener1);
        } else {
            mProcessImage.setImageResource(R.drawable.place_default);
        }

        //-------------------商品图片和描述信息-------------------------
        if (mResqData.details.pics != null && mResqData.details.pics.size() > 0) {
            mGoodsTitle.setText(mResqData.details.title);
//            mGoodsTitle.getPaint().setFakeBoldText(true);//加粗
            if (TextUtils.isEmpty(mResqData.details.content)) {
                mGoodsContent.setVisibility(View.GONE);
            } else {
                mGoodsContent.setVisibility(View.VISIBLE);
                mGoodsContent.setText(mResqData.details.content);
            }
            mGoodsTitle.setVisibility(View.VISIBLE);
            mPics.setVisibility(View.VISIBLE);
            initPics();
        } else {
            mGoodsTitle.setVisibility(View.GONE);
            mGoodsContent.setVisibility(View.GONE);
            mPics.setVisibility(View.GONE);
        }

        // --------------------提示----------------
        mNotice.setText(mResqData.notice.title);
        mNoticeContent.setText(mResqData.notice.content);

        // -----------------底部按钮栏----------------
        mEndday.setText(mResqData.bottom.endday);
        mBuyBtn.setText(mResqData.bottom.item_button_text);
        mApplyBtn.setText(mResqData.bottom.app_button_text);
        //购买按钮是否显示
        if (mResqData.bottom.item_button == 1) {
            mBuyBtn.setVisibility(View.VISIBLE);
        } else {
            mBuyBtn.setVisibility(View.GONE);
        }
        //申请按钮是否可点
        if (mResqData.bottom.app_button == 1) {
            mApplyBtn.setSelected(false);
            mApplyBtn.setClickable(true);
        } else {
            mApplyBtn.setSelected(true);
            mApplyBtn.setClickable(false);
        }


    }

    private void submit() {
        FuLiSheReq.submit(mGid, 0, 0, 0, 0, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    if (success == 1) {
                        mApplyBtn.setSelected(true);
                        mApplyBtn.setClickable(false);
                        mApplyBtn.setText("已经申请");
                    } else {
                        mApplyBtn.setSelected(false);
                        mApplyBtn.setClickable(true);
                        mApplyBtn.setText("申请体验");
                    }
                    AndroidUtil.showToast(obj.getString("tips"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.w(error.getMessage());
            }
        });
    }

    /**
     * 初始化h5链接的UI
     */
    private void initChains() {
        mChains.removeAllViews();

        for (FuLiSheDetailResq.SkipInfo info : mResqData.h5data) {

            View view = View.inflate(this, R.layout.home_cate_catalog_item1, null);
            view.setTag(info);
            view.findViewById(R.id.image).setVisibility(View.GONE);
            TextView tv = (TextView) view.findViewById(R.id.name);
            tv.setText(info.title);
            tv.setTextColor(Color.parseColor(pre + info.color));
            view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    FuLiSheDetailResq.SkipInfo tag = (FuLiSheDetailResq.SkipInfo) v.getTag();
                    startActivity(MeishaiWebviewActivity.newIntent(tag.url));
                }
            });
            mChains.addView(view);
        }
    }

    /**
     * 初始化商品图片的UI
     */
    private void initPics() {
        mPics.removeAllViews();


        double width = screenWidth - AndroidUtil.dip2px(12) * 2;

        for (Picture pic : mResqData.details.pics) {
            double widthScale = width / pic.pic_width;
            int height = (int) (widthScale * pic.pic_height);

            SampleRoundImageView view = new SampleRoundImageView(this);
            ImageView image = view.getImageView();
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams((int) width, height);
            layoutParams.setMargins(0, AndroidUtil.dip2px(5), 0, 0);
            view.setLayoutParams(layoutParams);
            if (!TextUtils.isEmpty(pic.pic_url)) {
                ImageListener listener = ImageLoader.getImageListener(image,
                        R.drawable.place_default, R.drawable.place_default);
                getImageLoader().get(pic.pic_url, listener);
            } else {
                image.setImageResource(R.drawable.place_default);
            }
            mPics.addView(view);
        }
    }

    /**
     * 加载网络数据
     */
    private void getRequestData() {
        mDataArea.setVisibility(View.GONE);
        showProgress("", getString(R.string.network_wait));

        FuLiSheReq.fulisheDetail(mGid, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                checkRespData(response);
                mDataArea.setVisibility(View.VISIBLE);
                hideProgress();
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                hideProgress();
            }
        });
    }

    /**
     * 检查加载到的网络数据
     *
     * @param response
     */
    protected void checkRespData(String response) {
        try {
            JSONObject obj = new JSONObject(response);
            int success = obj.getInt("success");
            if (success == 1) {
                mResqData = GsonHelper.parseObject(response,
                        FuLiSheDetailResq.class);

                initData();
            } else {
                AndroidUtil.showToast(R.string.reqFailed);
            }

        } catch (JSONException e) {
            DebugLog.e(e.getMessage());
            e.printStackTrace();
        }
    }

    @Override
    public void onClick(View v) {
        com.meishai.entiy.UserInfo userInfo = MeiShaiSP.getInstance()
                .getUserInfo();
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.more:// 分享
                if (sharePop == null) {
                    sharePop = new ShareMorePopupWindow(this, 0);
                }
                sharePop.setHint1Visibility(View.GONE);
                sharePop.setHintVisibility(View.GONE);
                if (!userInfo.isLogin()) {
                    startActivity(LoginActivity.newOtherIntent());
                    return;
                }

                showPop();
                break;
            case R.id.fulishe_detail_price_container:// 价格

                break;
            case R.id.fulishe_detail_limit_container:// 限量

                break;
            case R.id.fulishe_detail_apply_container:// 申请

                break;
            case R.id.fulishe_detail_buy_btn:// 直接购买
                if (mResqData != null)
                    buy(mResqData.bottom);
//            startActivity(MeishaiWebviewActivity.newIntent(mResqData.bottom.item_button_url));
                break;

            case R.id.fulishe_detail_apply_btn:// 底部控制按钮
                apply();
                break;
            default:
                break;
        }
    }

    private void buy(FuLiSheDetailResq.ButtonData data) {
        //发送数据给服务器,不需要返回
        FuLiSheReq.buy(mResqData.data.gid, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.d("服务端返回:" + response);
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        //进行处理 判断是去到淘宝还是webView
        boolean install = PackageManagerUtils.isInstall(
                this, "com.taobao.taobao");
        if (data.item_button_istao == 1 && install) {
            // TODO 处理url 启动淘宝
            //对URL进行处理
            String[] split = data.item_button_url.split(":");
            String url1 = "taobao:" + split[1];
            Intent intent = new Intent();
            intent.setAction("android.intent.action.VIEW");
            Uri content_url = Uri.parse(url1);
            intent.setData(content_url);
            startActivity(intent);
        } else {

            Intent intent = MeishaiWebviewActivity
                    .newIntent(data.item_button_url);
            startActivity(intent);
        }
    }

    private void apply() {
        //----------设置分享对话框-----------
        if (sharePop == null) {
            sharePop = new ShareMorePopupWindow(this, 0);
        }
        sharePop.setHint1Visibility(View.VISIBLE);
        sharePop.setHintVisibility(View.VISIBLE);
        //-----------非空判断---------------
        if (mResqData == null || mResqData.data == null) {
            return;
        }
        //-----------登录判断---------------
        com.meishai.entiy.UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            startActivity(LoginActivity.newOtherIntent());
            return;
        }
        //-----------具体操作---------------
        if (mResqData.data.isshare == 1) {//已经分享

            if (mResqData.data.isshare == 1) {//需要填地址
                Intent intent2 = FindPointOrderActivity1.newIntent(mGid);
                startActivity(intent2);
            } else {//不需要填地址,并且已经分享
                submit();
            }
        } else {
            showPop();
        }
    }

    private void showPop() {
        if (sharePop == null) {
            sharePop = new ShareMorePopupWindow(this, 0);
        }
        if (sharePop.isShowing()) {
            sharePop.dismiss();
        }

        sharePop.showAtLocation(mLayRoot, Gravity.BOTTOM
                | Gravity.CENTER_HORIZONTAL, 0, 0);
    }

    private class MyAdapter extends BaseAdapter {

        private Context context;
        private List<UserInfo> users;

        public MyAdapter(Context context, List<UserInfo> users) {
            this.context = context;
            this.users = users;
        }

        @Override
        public int getCount() {
            if (users != null && users.size() > 0) {
                return users.size();
            }
            return 0;
        }

        @Override
        public Object getItem(int position) {
            return users.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            UserInfo item = (UserInfo) getItem(position);
            if (convertView == null) {
                int gap = AndroidUtil.dip2px(26);
                int marg = AndroidUtil.dip2px(5);
                LayoutParams lp = new LayoutParams(gap, gap);
                // 初始化赞
                CircleImageView image = new CircleImageView(context);
                image.setPadding(marg, marg, marg, marg);

                image.setLayoutParams(lp);
                convertView = image;

            }
            CircleImageView image = (CircleImageView) convertView;
            // 加载图片
            image.setTag(item.avatar);
            ListImageListener listener = new ListImageListener(image, 0, 0,
                    item.avatar);

            getImageLoader().get(item.avatar, listener);
            return convertView;
        }

    }

}
