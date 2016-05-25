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

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.ui.base.BaseActivity;
import com.meishai.util.AndroidUtil;

/**
 * 美晒达人->达人申请-WebView
 *
 * @author sh
 */
public class FindMasterApplyWebviewActivity extends BaseActivity {

    private Context mContext = FindMasterApplyWebviewActivity.this;
    private Button backMain;
    private WebView webview;
    private UserInfo userInfo;
    private CustomProgress mProgressDialog = null;
    // 要传入当前登陆的会员ID，如果会员未登陆，跳转到登陆页面，先登陆才能进这个页面
    private String applyUrl = "http://www.meishai.com/index.php?m=content&c=daren&a=app&userid=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        setContentView(R.layout.find_master_webview);
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
        webview = (WebView) this.findViewById(R.id.webview);
    }

    private void loadWebview() {
        if (null != userInfo) {
            WebSettings webSettings = webview.getSettings();
            webSettings.setJavaScriptEnabled(true);
            // 浏览器不支持多窗口显示
            webSettings.setSupportMultipleWindows(false);
            // 页面是否可以进行缩放
            webSettings.setSupportZoom(false);
            webview.setWebViewClient(new WebViewClient() {
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
            webview.setWebChromeClient(new WebChromeClient() {

                public void onProgressChanged(WebView view, int progress) {
                    // 加载完成
                    if (progress == 100) {
                    }
                    super.onProgressChanged(view, progress);

                }
            });
            webview.loadUrl(applyUrl + userInfo.getUserID());
        } else {
            AndroidUtil.showToast(R.string.tip_login);
        }
    }

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent event) {
//		if (webview.canGoBack() && keyCode == KeyEvent.KEYCODE_BACK) {
//			// goBack()表示返回webView的上一页面
//			webview.goBack();
//			return true;
//		}
//		return false;
//	}
}
