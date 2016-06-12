package com.meishai.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.ShareInviteDialog;
import com.meishai.util.DebugLog;
import com.meishai.util.WebViewCtrlUtils;

/**
 * Webview
 *
 * @author sh
 */
public class MeishaiWebviewActivity extends BaseActivity {

    private Context mContext = MeishaiWebviewActivity.this;
    private String url = "";
    private String title = "";
    private int mode = 0;
    public static final int FIND_MODE = 100;
    private Button backMain;
    private TextView tvTitle;
    private WebView webview;
    private CustomProgress mProgressDialog = null;
    private Button close;
    private Button find;
    private ShareInviteDialog mSharePop;

    public static Intent newIntent(String url) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeishaiWebviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    public static Intent newIntent(String url, int mode) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeishaiWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("mode", mode);
        return intent;
    }

    public static Intent newIntent(String url, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                MeishaiWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meishai_webview);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            url = bundle.getString("url");
            mode = bundle.getInt("mode");
            title = bundle.getString("title");
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
        }
        initView();
        loadWebview();
    }

    private void initView() {
        close = (Button) this.findViewById(R.id.close);
        find = (Button) this.findViewById(R.id.btn_find);
        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //拿着url返回,直接关掉
                Intent intent = new Intent();
                intent.putExtra("url", webview.getUrl());
                setResult(RESULT_OK, intent);
                finish();
            }
        });
        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        backMain = (Button) this.findViewById(R.id.backMain);
        backMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                    //根据当前的URL设置 找到啦 按钮的状态
                    String currentUrl = webview.getUrl();
                    if (currentUrl.startsWith(ConstantSet.TAOBAO_DETAIL) || currentUrl.startsWith(ConstantSet.JD_DETAIL) || currentUrl.startsWith(ConstantSet.TMALL_DETAIL)) {
                        if (mode == FIND_MODE) {
                            find.setVisibility(View.VISIBLE);
                        } else {
                            find.setVisibility(View.GONE);
                        }
                    } else {
                        find.setVisibility(View.GONE);
                    }
                } else {
                    finish();
                }
            }
        });
        tvTitle = (TextView) this.findViewById(R.id.title);
        tvTitle.setText(title);
        webview = (WebView) this.findViewById(R.id.webview);
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

//                    if (url.startsWith(ConstantSet.MEISHAI_HOME_PAGE)) {//个人主页
//                        String[] split = url.split("/");
//                        String userid = split[split.length - 1];
//                        startActivity(HomePageActivity.newIntent(userid));
//                        return true;
//                    } else if (url.startsWith(ConstantSet.MEISHAI_POINT_GOODS)) {//积分商城详情
//                        String[] split = url.split("/");
//                        String gid = split[split.length - 1];
//                        startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(gid), 0));
//                        return true;
//                    } else if (url.startsWith(ConstantSet.MEISHAI_QIANG_GOODS)) {//疯抢详情页面
//                        String[] split = url.split("/");
//                        String gid = split[split.length - 1];
//                        startActivity(FuliSheDetailActivity1.newIntent(Integer.parseInt(gid), 0));
//                        return true;
//                    } else if (url.startsWith(ConstantSet.TMALL_APP) || url.startsWith(ConstantSet.TAOBAO_APP)) {//淘宝的链接,有些会默认的跳转到天猫的客户端,有够坑的,我直接给拦截了
//                        return true;
//                    } else if (url.startsWith(ConstantSet.TMALL_APP) || url.startsWith(ConstantSet.MEISHAI_LOGIN)) {//登陆的链接
//                        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
//                        if (!userInfo.isLogin()) {
//                            startActivity(LoginActivity.newIntent());
//                            return true;
//                        }
//                    } else if (url.startsWith(ConstantSet.TMALL_APP) || url.startsWith(ConstantSet.MEISHAI_SELL_GOODS)) {//商品销售
//                        startActivity(LoginActivity.newIntent());
//                        return true;
//                    } else
                    if (url.startsWith(ConstantSet.MEISHAI_SHARE_FRIEND)) {//邀请好友的链接
                        if (mSharePop == null) {
                            mSharePop = new ShareInviteDialog(MeishaiWebviewActivity.this);
                        }
                        mSharePop.sendShareMsg();

                        return true;

                    }
                    if(!WebViewCtrlUtils.ctrl(MeishaiWebviewActivity.this, url)) {
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
                    if (url.startsWith(ConstantSet.TAOBAO_DETAIL)
                            || url.startsWith(ConstantSet.JD_DETAIL)
                            || url.startsWith(ConstantSet.TMALL_DETAIL)
                            || url.startsWith(ConstantSet.TAOBAO_APP)
                            || url.startsWith(ConstantSet.TAOBAO_APP)) {
                        if (mode == FIND_MODE) {
                            find.setVisibility(View.VISIBLE);
                        } else {
                            find.setVisibility(View.GONE);
                        }
                    } else {
                        find.setVisibility(View.GONE);
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


}
