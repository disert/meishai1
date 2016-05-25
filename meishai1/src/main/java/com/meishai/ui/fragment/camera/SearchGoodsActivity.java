package com.meishai.ui.fragment.camera;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.EditTextWithDel;
import com.meishai.app.widget.TopBackLayout;
import com.meishai.entiy.SearchGoodsRespData;
import com.meishai.entiy.WebsiteInfo;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名：SearchGoodsActivity
 * 描    述：发布界面点击添加链接时进入的界面
 * 作    者：yl
 * 时    间：2016/1/12
 * 版    权：
 */
public class SearchGoodsActivity extends BaseActivity {

    private TopBackLayout mTopView;
    private EditText mEdit;
    private Button mButton;
    private SearchGoodsRespData mData;
    private LinearLayout mSites;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(), SearchGoodsActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_goods);

        initView();

        initData();
        getRequestData();
    }

    private void initView() {
        mTopView = (TopBackLayout) findViewById(R.id.search_top_view);
        mTopView.setTitle("添加链接");
        mTopView.setOnBackListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mEdit = (EditText) findViewById(R.id.search_edit);
        mButton = (Button) findViewById(R.id.search_button);
        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mData != null) {
                    search(mData.list.get(0));
                }
            }
        });
        mSites = (LinearLayout) findViewById(R.id.search_sites);
    }

    private void initData() {

    }

    private void getRequestData() {
        showProgress("", "加载中..");
        ReleaseReq.linkedSource(this, "", new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                if (!TextUtils.isEmpty(response)) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        if (obj.getInt("success") == 1) {
                            mData = GsonHelper.parseObject(response, SearchGoodsRespData.class);
                            initSite();
                        } else {
                            AndroidUtil.showToast(obj.getString("tips"));
                        }
                    } catch (JSONException e) {
                        AndroidUtil.showToast("json解析失败!");
                    }
                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                DebugLog.w(error.getMessage());
                AndroidUtil.showToast("网络请求失败");
            }
        });
    }

    private void initSite() {

        if (mData != null && mData.list != null && !mData.list.isEmpty()) {

            mSites.removeAllViews();
            LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1);

            for (int i = 0; i < mData.list.size(); i++) {
                WebsiteInfo info = mData.list.get(i);
                View view = View.inflate(this, R.layout.layout_ver_image_text, null);
                TextView tv = (TextView) view.findViewById(R.id.text);
                ImageView iv = (ImageView) view.findViewById(R.id.image);
                tv.setText(info.name);
                iv.setTag(info.icon);
                ListImageListener listener = new ListImageListener(iv, R.drawable.place_default, R.drawable.place_default, info.icon);
                getImageLoader().get(info.icon, listener);

                view.setTag(info);
                view.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        WebsiteInfo tag = (WebsiteInfo) v.getTag();
                        search(tag);
                    }
                });

                mSites.addView(view, lp);

            }
        }

    }

    /**
     * 进行搜索的处理
     *
     * @param tag
     */
    private void search(WebsiteInfo tag) {
        String content = mEdit.getText().toString();
        if (!TextUtils.isEmpty(content)) {
            startActivityForResult(MeishaiWebviewActivity.newIntent(tag.url + content, MeishaiWebviewActivity.FIND_MODE), 100);
        } else {
            AndroidUtil.showToast("请输入关键字");
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == 100) {//寻找链接
                String url = data.getStringExtra("url");
                if (!TextUtils.isEmpty(url)) {
                    Intent intent = new Intent();
                    intent.putExtra("url", url);
                    setResult(RESULT_OK, intent);
                    finish();
                }
            }
        }
    }
}
