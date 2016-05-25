package com.meishai.ui.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
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

/**
 * Webview
 *
 * @author sh
 */
public class UpgradeWebviewActivity extends BaseActivity {

    private Context mContext = UpgradeWebviewActivity.this;
    private String url = "";
    private String title = "";
    private Button backMain;
    private TextView tvTitle;
    private WebView webview;
    private CustomProgress mProgressDialog = null;

    public static Intent newIntent(String url) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UpgradeWebviewActivity.class);
        intent.putExtra("url", url);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.meishai_webview);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            url = bundle.getString("url");
            title = bundle.getString("title");
            if (TextUtils.isEmpty(title)) {
                title = "";
            }
        }
        initView();
        loadWebview();
    }

    private void initView() {
        backMain = (Button) this.findViewById(R.id.backMain);
        backMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = (TextView) this.findViewById(R.id.title);
        tvTitle.setText(title);
        webview = (WebView) this.findViewById(R.id.webview);
    }

    private void loadWebview() {
        if (!TextUtils.isEmpty(url)) {
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            // 浏览器不支持多窗口显示
            webSettings.setSupportMultipleWindows(false);
            // 页面是否可以进行缩放
            webSettings.setSupportZoom(false);
            webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    if (url.endsWith(".apk")) {
                        Uri uri = Uri.parse(url);
                        Intent viewIntent = new Intent(Intent.ACTION_VIEW, uri);
                        UpgradeWebviewActivity.this.startActivity(viewIntent);
                        return true;
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

                public void onProgressChanged(WebView view, int progress) {
                    // 加载完成
                    if (progress == 100) {
                    }
                    super.onProgressChanged(view, progress);

                }

            });
            webview.loadUrl(url);
        }
    }

    // @Override
    // public boolean onKeyDown(int keyCode, KeyEvent event) {
    // if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
    // // goBack()表示返回webView的上一页面
    // webview.goBack();
    // return true;
    // }
    // return false;
    // }
}
