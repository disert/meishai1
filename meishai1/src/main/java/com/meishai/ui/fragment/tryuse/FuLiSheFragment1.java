package com.meishai.ui.fragment.tryuse;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.H5Data;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.ShareInviteDialog;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.sliding.PagerSlidingTabStrip;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.WebViewCtrlUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 福利社主界面 2.0
 *
 * @author yl
 */
public class FuLiSheFragment1 extends BaseFragment {
    private Context mContext;
    private String title = "";
    private TextView tvTitle;
    private WebView webview;
    private CustomProgress mProgressDialog = null;
    private ShareInviteDialog mSharePop;
    private View view;
    private String mUrl;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.meishai_webview, null);
        this.initView();
        loadUrl();
        return view;
    }

    private void loadUrl() {
        H5Data data = new H5Data();
        data.action = "index";
        data.controller = "goods";
        PublicReq.h5Requst(data, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                DebugLog.w("返回结果:" + response);
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj != null) {//解析成功
                            if (obj.getInt("success") == 1) {
                                String url = obj.getString("url");
                                if (!TextUtils.isEmpty(url)) {
                                    mUrl = url;
                                    loadWebview(url);
                                } else {
                                    AndroidUtil.showToast("返回的URL为null!");
                                }
                            } else
                                AndroidUtil.showToast(obj.getString("tips"));

                        } else {//解析失败
                            AndroidUtil.showToast("json解析失败!");
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast("网络请求失败!请检查网络状态");
            }
        });
    }

    private void initView() {
        view.findViewById(R.id.backMain).setVisibility(View.GONE);
        view.findViewById(R.id.close).setVisibility(View.GONE);
        mContext = getActivity();
        tvTitle = (TextView) view.findViewById(R.id.title);
        tvTitle.setText("福利社");
        webview = (WebView) view.findViewById(R.id.webview);
    }


    private void loadWebview(String url) {
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
                    if (!WebViewCtrlUtils.ctrl(mContext, url)) {
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
                    DebugLog.w("链接:" + url);

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

}
