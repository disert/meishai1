package com.meishai.ui.fragment.home;

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
import android.widget.ImageButton;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.dialog.ShareInviteDialog;
import com.meishai.util.DebugLog;
import com.meishai.util.WebViewCtrlUtils;

import java.net.URI;
import java.net.URL;

/**
 * Webview
 *
 * @author sh
 */
public class SellGoodsWebviewActivity extends BaseActivity {

    private Context mContext = SellGoodsWebviewActivity.this;
    private String url = "";
    private String title = "";
    public static final int FIND_MODE = 100;
    private Button backMain;
    private TextView tvTitle;
    private WebView webview;
    private CustomProgress mProgressDialog = null;
    private ImageButton mShare;
    private ShareInviteDialog mSharePop;

    public static Intent newIntent(String url) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                SellGoodsWebviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }


    public static Intent newIntent(String url, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                SellGoodsWebviewActivity.class);
        intent.putExtra("url", url);
        intent.putExtra("title", title);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.sell_goods_webview);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            url = bundle.getString("url");
            title = bundle.getString("title");
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
        }
        initView();

    }

    private void initView() {
        mShare = (ImageButton) this.findViewById(R.id.more);
        mShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        backMain = (Button) this.findViewById(R.id.backMain);
        backMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (webview.canGoBack()) {
                    webview.goBack();
                    //根据当前的URL设置 找到啦 按钮的状态
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
                    if (!WebViewCtrlUtils.ctrl(SellGoodsWebviewActivity.this, url)) {
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


}
