package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.TopBackLayout;
import com.meishai.app.widget.layout.LatestPostAdapter;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.nimbusds.jose.JOSEException;


/**
 * @ClassName: ChannelActivity
 * @Description: 晒晒->精选->频道内容
 */
public class ChannelShowActivity extends BaseActivity {

    private PullToRefreshListView mListView;
    private LatestPostAdapter mPostListAdapter;

    private int mId;
    private int mPage = 0;

    public static Intent newIntent(int id, String name) {
        Intent intent = new Intent(GlobalContext.getInstance(), ChannelShowActivity.class);
        intent.putExtra("id", id);
        intent.putExtra("name", name);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_channel_show);

        initView();
        getRequest();
    }

    private void initView() {
        TopBackLayout layout = (TopBackLayout) findViewById(R.id.back_layout);
        layout.setOnBackListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        mId = getIntent().getIntExtra("id", 0);
        String title = getIntent().getStringExtra("name");
        layout.setTitle(title);

        mListView = (PullToRefreshListView) findViewById(R.id.channel_show_listview);
        mListView.setMode(Mode.PULL_FROM_END);
        mPostListAdapter = new LatestPostAdapter(this, getImageLoader());
        mListView.setAdapter(mPostListAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                mPage++;
                getRequest();
            }
        });

    }


    private ReqData mReqData;
    private Map<String, String> mMap = new HashMap<String, String>();

    private void getRequest() {
        if (null == mReqData) {
            mReqData = new ReqData();
            mReqData.setC("post");
            mReqData.setA("lists");

            UserInfo user = MeiShaiSP.getInstance().getUserInfo();

            mMap.put("userid", user.getUserID());
            mMap.put("cid", String.valueOf(mId));
            mMap.put("listorder", "1"); // 1：最新发布；2：最新回复；3：精彩
            mMap.put("pagesize", String.valueOf(10));
        }
        mMap.put("page", String.valueOf(mPage));
        mReqData.setData(mMap);


        try {
            String url = getString(R.string.base_url) + mReqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(new StringRequest(url, new Listener<String>() {
                @Override
                public void onResponse(String response) {
                    hideProgress();
                    //DebugLog.d(response);

                    if (checkData(response)) {
                        mListView.onRefreshComplete();
                    } else {
                        mListView.onRefreshComplete();

                        showToast(R.string.drop_down_list_footer_no_more_text);
                        mListView.setMode(Mode.DISABLED);
                    }
                }
            }, new ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    hideProgress();
                    AndroidUtil.showToast(R.string.reqFailed);
                    mPage--;
                    mListView.onRefreshComplete();
                }
            }));


        } catch (JOSEException e) {
            e.printStackTrace();
        }

    }


    private boolean checkData(String response) {
        try {
            JSONObject jsonObj = new JSONObject(response);

            if (jsonObj.getInt("success") == 1) {
                JSONArray jsonArray = jsonObj.getJSONArray("data");

                Type type = new TypeToken<Collection<PostItem>>() {
                }.getType();
                Collection<PostItem> items = GsonHelper.parseObject(jsonArray.toString(), type);

                //DebugLog.d("add PostItem:" + items.size());

                mPostListAdapter.addCollection(items);


                return true;
            }


        } catch (JSONException e) {
            e.printStackTrace();
        }
        //DebugLog.d(response);

        return false;
    }
}
