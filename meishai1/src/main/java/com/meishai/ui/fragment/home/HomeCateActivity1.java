package com.meishai.ui.fragment.home;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateResponseData;
import com.meishai.entiy.CateResponseData.CateInfo1;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.adapter.HomeCateAdapter1;
import com.meishai.ui.fragment.home.req.HomeCateReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 分类的activity 2.0
 *
 * @author Administrator yl
 */
public class HomeCateActivity1 extends BaseActivity {

    private Button mBackMain;
    private EditText mSearch_text;
    private ListView mListView;
    private Context mContext;
    private CateResponseData mResponseData;
    private List<CateInfo1> cateinfos;
    private HomeCateAdapter1 mAdapter;
    private int currentPage = 1;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                HomeCateActivity1.class);
        return intent;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_cate1);
        mContext = this;
        this.initView();
    }

    private void initView() {
        // 返回按钮
        mBackMain = (Button) findViewById(R.id.backMain);
        mBackMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

        // 搜索栏
        mSearch_text = (EditText) findViewById(R.id.search_text);
        mSearch_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(HomeCateActivity1.this,
                        HomePostAndTopicSearchActivity.class);
                startActivity(intent);
            }
        });

        // listview 和它的适配器
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new HomeCateAdapter1(mContext, cateinfos, getImageLoader());
        mListView.setAdapter(mAdapter);
        // item的点击事件
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                // DebugLog.w("item的点击事件"+position);
                CateInfo1 info = (CateInfo1) mAdapter.getItem(position);

                Intent intent = TopicShowActivity.newIntent(info.tid);
                mContext.startActivity(intent);

            }
        });
        loadData();
    }

    private void loadData() {
        if (cateinfos != null) {
            return;
        }
        showProgress("", getString(R.string.network_wait));
        sendRequest();
    }

    private void sendRequest() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("typeid", 1 + "");
        HomeCateReq.cate(mContext, data, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                try {
                    JSONObject obj = new JSONObject(response);
                    int success = obj.getInt("success");
                    if (success == 1) {
                        mResponseData = GsonHelper.parseObject(response,
                                CateResponseData.class);
                        cateinfos = mResponseData.list;
                        mAdapter.setDatas(cateinfos);
                        hideProgress();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    hideProgress();
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
                hideProgress();
            }
        });
    }

    @Override
    protected void onResume() {
        loadData();
        super.onResume();
    }
}
