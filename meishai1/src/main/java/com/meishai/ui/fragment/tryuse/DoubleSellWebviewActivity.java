package com.meishai.ui.fragment.tryuse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CheckBuyRespBean;
import com.meishai.entiy.DoubleSellRespBean;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.message.MessageReq;
import com.meishai.ui.popup.ShareMorePopupWindow;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.ToastUtlis;
import com.meishai.util.WebViewCtrlUtils;
import com.nimbusds.jose.JOSEException;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Webview
 *
 * @author sh
 */
public class DoubleSellWebviewActivity extends BaseActivity implements View.OnClickListener{

    private Context mContext = DoubleSellWebviewActivity.this;
    private String url = "";
    private String title = "";
    public static final int FIND_MODE = 100;
    private Button backMain;
    private TextView tvTitle;
    private WebView webview;
    private CustomProgress mProgressDialog = null;
    private ImageButton mShare;
    private String mGroupid;
    private DoubleSellRespBean mData;

    private ShareMorePopupWindow mSharePopupWindow;


    private View mLayRoot;
    private Button mBtnMore,mBtnPing;

    public static Intent newIntent(String url) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                DoubleSellWebviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }


    public static Intent newIntent(String url, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                DoubleSellWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.double_sell_webview);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            url = bundle.getString("url");
            title = bundle.getString("title");
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
            String[] split = url.split("=");
            mGroupid = split[split.length-1];

        }
        initView();
        getRequestData(1);
        loadWebview();



    }

    private void initView() {
        mShare = (ImageButton) this.findViewById(R.id.more);
        backMain = (Button) this.findViewById(R.id.backMain);
        tvTitle = (TextView) this.findViewById(R.id.title);
        tvTitle.setText(title);
        webview = (WebView) this.findViewById(R.id.webview);


        mLayRoot = this.findViewById(R.id.root);
        mBtnMore = (Button) this.findViewById(R.id.double_more_btn);
        mBtnPing = (Button) this.findViewById(R.id.double_ping_btn);
        mShare.setOnClickListener(this);
        backMain.setOnClickListener(this);
        mBtnMore.setOnClickListener(this);
        mBtnPing.setOnClickListener(this);
    }

    private void loadWebview() {
        if (!TextUtils.isEmpty(url)) {
            DebugLog.w("URL:" + url);
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            // 浏览器不支持多窗口显示
            webSettings.setSupportMultipleWindows(false);
            // 页面是否可以进行缩放
            webSettings.setSupportZoom(false);
            // 监听 所有点击的链接，如果拦截到我们需要的，就跳转到相对应的页面。
            webview.setWebViewClient(new WebViewClient() {

                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (!WebViewCtrlUtils.ctrl(DoubleSellWebviewActivity.this, url)) {
                        view.loadUrl(url);
                    }
                    return true;

                }

                // 载入页面完成的事件
                @Override
                public void onPageFinished(WebView view, String url) {
                    if (null != mProgressDialog) {
                        mProgressDialog.hide();
                    }
                    super.onPageFinished(view, url);
                }

                // 载入页面开始的事件
                @Override
                public void onPageStarted(WebView view, String url,
                                          Bitmap favicon) {
                    if (null == mProgressDialog) {
                        mProgressDialog = CustomProgress.show(mContext,
                                mContext.getString(R.string.network_wait),
                                true, null);
                    }
                    super.onPageStarted(view, url, favicon);
                }

            });
            webview.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String titl) {

                    if (!TextUtils.isEmpty(titl)) {
                        title = titl;
                        tvTitle.setText(title);
                    }
                }

            });
            webview.loadUrl(url);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            // goBack()表示返回webView的上一页面
            if (webview.canGoBack()) {
                webview.goBack();
            } else {
                finish();
            }
            return true;
        }
        return false;
    }

    private void getRequestData(int page) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("mall");
        reqData.setA("group");
        Map<String, String> data = new HashMap<String, String>();
        data.put("groupid", mGroupid);
        data.put("userid", userInfo.getUserID());

        reqData.setData(data);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            sendMsg(url);

        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private boolean checkRequestData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);
            if (jsonObj.getInt("success") == 1) {
                // 把数据封装成bean
                mData = GsonHelper.parseObject(response, DoubleSellRespBean.class);
                initData();

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

    private void initData() {
        if(mData != null){
            mBtnPing.setBackgroundColor(Color.parseColor("#"+mData.getButton().getBgcolor()));
            mBtnPing.setTextColor(Color.parseColor("#" + mData.getButton().getText_color()));
            mBtnPing.setText(mData.getButton().getText());

            if(mSharePopupWindow == null){
                mSharePopupWindow = new ShareMorePopupWindow(mContext,0);
            }
            mSharePopupWindow.initShareParams(mData.getSharedata());
        }
    }

    @Override
    public void updateUI(Object obj) {
        if (obj == null || TextUtils.isEmpty(obj.toString())) return;
        String response = obj.toString();
        //检查数据
        checkRequestData(response);

    }

    @Override
    public void failt(Object ojb) {
        super.failt(ojb);
    }

    @Override
    public void onClick(View v) {
        if(mData == null){
            return;
        }
        switch (v.getId()){
            //TODO 为按钮注册事件
            case R.id.double_more_btn:
                break;
            case R.id.double_ping_btn:
                ctrlPing();
                break;
            case R.id.backMain:
                if (webview.canGoBack()) {
                    webview.goBack();
                    //根据当前的URL设置 找到啦 按钮的状态
                } else {
                    finish();
                }
                break;
            case R.id.more:
                if(mSharePopupWindow != null){
                    mSharePopupWindow.showAtLocation(mLayRoot, Gravity.BOTTOM
                            | Gravity.CENTER_HORIZONTAL, 0, 0);
                }
                break;
        }
    }

    private void ctrlPing() {
        MessageReq.checkBuy(this, mData.getButton().getGid(), mData.getButton().getType(), mData.getButton().getGroupid(), new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    CheckBuyRespBean buyRespBean = GsonHelper.parseObject(response, CheckBuyRespBean.class);
                    switch (buyRespBean.success) {
                        case 0:
                            ToastUtlis.showToast(mContext, buyRespBean.tips);
                            break;
                        case 1:
                            startActivity(MeishaiWebviewActivity.newIntent(buyRespBean.redirect_url));
                            break;
                        case -1:
                            if (mSharePopupWindow != null) {
                                mSharePopupWindow.showAtLocation(mLayRoot, Gravity.BOTTOM
                                        | Gravity.CENTER_HORIZONTAL, 0, 0);
                            }
                            break;
                    }
                }
            }
        });
    }

}
