package com.meishai.ui.fragment.meiwu;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.entiy.Bargains;
import com.meishai.ui.base.BaseActivity;

/**
 * 喜折特价-WebView
 *
 * @author sh
 */
public class FindSaleWebviewActivity extends BaseActivity {

    private Context mContext = FindSaleWebviewActivity.this;
    private Bargains bargains = null;
    private Button backMain;
    private TextView title;
    private WebView sale_webview;
    private CustomProgress mProgressDialog = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.find_sale_webview);
        bargains = getIntent().getParcelableExtra("bargains");
        initView();
        refreshUI();
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
        title = (TextView) this.findViewById(R.id.title);
        sale_webview = (WebView) this.findViewById(R.id.sale_webview);
    }

    private void refreshUI() {
        if (null != bargains) {
            title.setText(bargains.getTitle());
        }
    }

    private void loadWebview() {
        if (null != bargains) {
            WebSettings webSettings = sale_webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            // 浏览器不支持多窗口显示
            webSettings.setSupportMultipleWindows(false);
            // 页面是否可以进行缩放
            webSettings.setSupportZoom(false);
            sale_webview.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    view.loadUrl(url);
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
            sale_webview.setWebChromeClient(new WebChromeClient() {

                public void onProgressChanged(WebView view, int progress) {
                    // if (null == mProgressDialog) {
                    // mProgressDialog.show(mContext,
                    // mContext.getString(R.string.network_wait),
                    // true, null);
                    // } else {
                    // if (!mProgressDialog.isShowing()) {
                    // mProgressDialog.show();
                    // }
                    // }
                    // 加载完成
                    if (progress == 100) {
                        // if (null != mProgressDialog) {
                        // mProgressDialog.hide();
                        // }
                    }
                    super.onProgressChanged(view, progress);

                }
            });
            sale_webview.loadUrl(bargains.getItemurl());
        }
    }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (sale_webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
//			// goBack()表示返回webView的上一页面
//			sale_webview.goBack();
//			return true;
//		}
//		return false;
//	}
}
