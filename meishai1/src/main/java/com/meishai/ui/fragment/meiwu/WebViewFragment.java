package com.meishai.ui.fragment.meiwu;

import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import com.meishai.R;
import com.meishai.entiy.UrlData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.ui.fragment.tryuse.FuliSheDetailActivity1;
import com.meishai.util.DebugLog;

import org.json.JSONException;
import org.json.JSONObject;


/**
 * Created by chenupt@gmail.com on 1/30/15.
 * Description :
 */
public class WebViewFragment extends BaseFragment {

    private WebView webview;
    private UrlData urldata;
    private String url;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.meiwu_shops_web_fragment, container, false);
        webview = (WebView) view.findViewById(R.id.web_view);
        getUrl();
        return view;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initData();
    }

    private void getUrl() {
        DebugLog.w("getUrl:" + (urldata == null));
        if (urldata != null) {
            PublicReq.h5Req(urldata.controller, urldata.action, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    DebugLog.w("url请求成功:" + response);
                    if (!TextUtils.isEmpty(response)) {
                        try {
                            JSONObject obj = new JSONObject(response);
                            url = obj.getString("url");
                            loadWebview();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    DebugLog.w("url请求失败");
                }
            });
        }
    }


    private void initData() {
        Bundle args = getArguments();
        urldata = args.getParcelable("urldata");

    }

    private void loadWebview() {
        if (!TextUtils.isEmpty(url)) {
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
                    DebugLog.w("链接:" + url);
                    if (url.startsWith("http://www.meishai.com/u/")) {//个人主页
                        String[] split = url.split("/");
                        String userid = split[split.length - 1];
                        startActivity(HomePageActivity.newIntent(userid));
                        //						DebugLog.w(userid);
                        return true;
                    } else if (url.startsWith("http://www.meishai.com/goods/point/")) {//积分商城详情
                        String[] split = url.split("/");
                        String gid = split[split.length - 1];
                        startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(gid), 0));
                        return true;
                    } else if (url.startsWith("http://www.meishai.com/goods/qiang/")) {//疯抢详情页面
                        String[] split = url.split("/");
                        String gid = split[split.length - 1];
                        startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(gid), 0));
                        return true;
                    } else if (url.startsWith("tmall://") || url.startsWith("taobao://")) {//淘宝的链接,有些会默认的跳转到天猫的客户端,有够坑的,我直接给拦截了
                        return true;
                    }


                    //					DebugLog.w("跳转的链接:"+url);
                    view.loadUrl(url);
                    return true;

                }

                // 载入页面完成的事件
                @Override
                public void onPageFinished(WebView view, String url) {
                    hideProgress();
                    super.onPageFinished(view, url);
                }

                // 载入页面开始的事件
                @Override
                public void onPageStarted(WebView view, String url,
                                          Bitmap favicon) {
                    showProgress("", mContext.getString(R.string.network_wait));
                    super.onPageStarted(view, url, favicon);
                }

            });

            webview.loadUrl(url);
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            if (urldata == null) {
                urldata = getArguments().getParcelable("urldata");
            }
            if (urldata == null) {
                urldata = ((MeiWuShopsShowActivity) getActivity()).getData().urldata;
            }
            getUrl();
        } else {
            // 相当于Fragment的onPause
        }
    }

    public void setUrlData(UrlData urldata) {
        this.urldata = urldata;
    }
}
