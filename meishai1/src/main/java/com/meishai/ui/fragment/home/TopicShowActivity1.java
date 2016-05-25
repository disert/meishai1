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
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.layout.TopicShowAdapter;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.TopicItem;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.camera.CameraActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 晒晒->话题->讨论
 *
 * @author sh
 */
public class TopicShowActivity1 extends BaseActivity {

    private PullToRefreshListView mListView;
    private TopicShowAdapter mAdapter;

    private TextView mTvTitle;

    private TopicItem mInfo;
    private int mTid;

    private int mPage = 1;

    public static Intent newIntent(TopicItem topic) {
        Intent intent = new Intent(GlobalContext.getInstance(), TopicShowActivity1.class);
        intent.putExtra("topic", topic);

        return intent;
    }

    public static Intent newIntent(int tid) {
        Intent intent = new Intent(GlobalContext.getInstance(), TopicShowActivity1.class);
        intent.putExtra("tid", tid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.home_topic_show);

        initView();
        initData();
        getRequest();
    }

    private void initView() {
        findViewById(R.id.top_back_btn).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });

        findViewById(R.id.top_camer_ib).setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(CameraActivity.newIntent(mTid));
            }
        });

        mTvTitle = (TextView) findViewById(R.id.top_title_tv);

        mListView = (PullToRefreshListView) findViewById(R.id.topic_listview);
        mListView.setMode(Mode.PULL_FROM_END);
        PullToRefreshHelper.initIndicator(mListView);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                mPage++;
                getRequest();

            }
        });

    }

    private void initData() {
        mAdapter = new TopicShowAdapter(this, getImageLoader());
        mInfo = getIntent().getParcelableExtra("topic");
        if (null == mInfo) {
            mTid = getIntent().getIntExtra("tid", -1);
        } else {
            mTid = mInfo.tid;
            mTvTitle.setText(mInfo.title);
            mAdapter.setTopicData(mInfo);
        }
        mListView.setAdapter(mAdapter);
    }

    private void getRequest() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();

        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("topicview");
        Map<String, String> dataList = new HashMap<String, String>();
        dataList.put("userid", userInfo.getUserID());
        dataList.put("tid", String.valueOf(mTid)); // 热门
        dataList.put("page", String.valueOf(mPage));
        dataList.put("pagesize", String.valueOf(10));
        reqData.setData(dataList);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            hideProgress();
                            // DebugLog.d(response);

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

                if (null == mInfo) {
                    mInfo = GsonHelper.parseObject(
                            jsonObj.getJSONArray("topicinfo").getJSONObject(0)
                                    .toString(), TopicItem.class);
                    mTvTitle.setText(mInfo.title);
                    mAdapter.setTopicData(mInfo);
                    DebugLog.d("des:" + mInfo.description);
                }

                JSONArray jsonArray = jsonObj.getJSONArray("data");
                Type type = new TypeToken<Collection<PostItem>>() {
                }.getType();
                Collection<PostItem> items = GsonHelper.parseObject(
                        jsonArray.toString(), type);

                // DebugLog.d("add PostItem:" + items.size());

                mAdapter.addCollection(items);

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }

}
