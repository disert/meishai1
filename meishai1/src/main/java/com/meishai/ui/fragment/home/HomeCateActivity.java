package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CatalogInfo;
import com.meishai.entiy.CateTopic;
import com.meishai.entiy.CateTopic.Topics;
import com.meishai.entiy.ChannelData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.home.adapter.HomeCatalogAdapter;
import com.meishai.ui.fragment.home.adapter.HomeCateAdapter;
import com.meishai.ui.fragment.home.adapter.HomeCateTopicAdapter;
import com.meishai.ui.fragment.home.req.HomeCateReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

public class HomeCateActivity extends BaseActivity {

    private Context mContext = HomeCateActivity.this;
    private Button backMain;
    private ListView cate_list;
    private List<ChannelData> channelDatas = null;
    private HomeCateAdapter cateAdapter;
    private List<CatalogInfo> catalogInfos = null;
    private HomeCatalogAdapter catalogAdapter = null;
    private ListView right_list;
    private HomeCateTopicAdapter topicAdapter = null;
    private ListView right_topic_list;
    private List<CateTopic> cateTopics = null;
    private List<Topics> topics = null;
    private EditText search_text;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                HomeCateActivity.class);
        return intent;
    }

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_cate);
        channelDatas = new ArrayList<ChannelData>();
        catalogInfos = new ArrayList<CatalogInfo>();
        cateTopics = new ArrayList<CateTopic>();
        topics = new ArrayList<CateTopic.Topics>();
        this.initView();
    }

    @Override
    protected void onResume() {
        this.loadData();
        super.onResume();
    }

    private void initView() {
        search_text = (EditText) this.findViewById(R.id.search_text);
        search_text.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mContext,
                        HomePostAndTopicSearchActivity.class);
                startActivity(intent);
            }
        });
        backMain = (Button) this.findViewById(R.id.backMain);
        backMain.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });
        cate_list = (ListView) this.findViewById(R.id.cate_list);
        right_list = (ListView) this.findViewById(R.id.right_list);
        right_topic_list = (ListView) this.findViewById(R.id.right_topic_list);
        cateAdapter = new HomeCateAdapter(mContext, channelDatas);
        cateAdapter.setListener(new LeftItemClickListener() {

            @Override
            public void onClick() {
                catalogs();
            }
        });
        cate_list.setAdapter(cateAdapter);
        catalogAdapter = new HomeCatalogAdapter(mContext, catalogInfos);
        // 分类添加或是删除成功之后 刷新数据
        catalogAdapter.setRigthCatalogListener(new RigthCatalogListener() {

            @Override
            public void onClick() {
                catalogs();
            }
        });
        right_list.setAdapter(catalogAdapter);

        topicAdapter = new HomeCateTopicAdapter(mContext, cateTopics, topics);
        right_topic_list.setAdapter(topicAdapter);
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        HomeCateReq.channel(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {

                    Type type = new TypeToken<List<ChannelData>>() {
                    }.getType();
                    List<ChannelData> resultChannelDatas = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);

                    if (null != resultChannelDatas
                            && !resultChannelDatas.isEmpty()) {
                        channelDatas = resultChannelDatas;
                        notifyAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void notifyAdapter() {
        cateAdapter.setChannelDatas(channelDatas);
        cateAdapter.setCurrentChannel(channelDatas.get(0));
        catalogs();
    }

    private void catalogs() {
        ChannelData channelData = cateAdapter.getCurrentChannel();
        // 分类
        if (channelData.getType().equals(
                ChannelData.ChannelType.catalog.toString())) {
            loadCatalogs(channelData);
        } else if (channelData.getType().equals(
                ChannelData.ChannelType.topic.toString())) {
            loadTopic(channelData);
        }
    }

    /**
     * 美晒右侧分类数据
     *
     * @param channelData
     */
    private void loadCatalogs(ChannelData channelData) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("type", channelData.getType());
        data.put("chid", String.valueOf(channelData.getChid()));
        HomeCateReq.catalogs(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {

                    Type type = new TypeToken<List<CatalogInfo>>() {
                    }.getType();
                    List<CatalogInfo> resultCatalogInfos = GsonHelper
                            .parseObject(GsonHelper.toJson(response.getData()),
                                    type);

                    if (null != resultCatalogInfos
                            && !resultCatalogInfos.isEmpty()) {
                        catalogInfos = resultCatalogInfos;
                    } else {
                        catalogInfos = new ArrayList<CatalogInfo>();
                    }
                    notifyCateInfoAdapter();
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void notifyCateInfoAdapter() {
        right_topic_list.setVisibility(View.GONE);
        right_list.setVisibility(View.VISIBLE);
        catalogAdapter.setCatalogInfos(catalogInfos);
        catalogAdapter.notifyDataSetChanged();
    }

    private void loadTopic(ChannelData channelData) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("type", channelData.getType());
        data.put("chid", String.valueOf(channelData.getChid()));
        HomeCateReq.catalogs(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<CateTopic>>() {
                    }.getType();
                    List<CateTopic> resultCateTopics = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    boolean hasData = false;
                    if (null != resultCateTopics && !resultCateTopics.isEmpty()) {
                        hasData = true;
                        cateTopics = resultCateTopics;
                    }
                    Type topicType = new TypeToken<List<Topics>>() {
                    }.getType();
                    List<Topics> resultTopics = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getTopics()), topicType);
                    if (null != resultTopics && !resultTopics.isEmpty()) {
                        hasData = true;
                        topics = resultTopics;
                    }
                    if (hasData) {
                        notifyTopicAdapter();
                    }
                } else {
                    AndroidUtil.showToast(response.getTips());
                }

            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    private void notifyTopicAdapter() {
        right_list.setVisibility(View.GONE);
        right_topic_list.setVisibility(View.VISIBLE);
        topicAdapter.setCateTopics(cateTopics);
        topicAdapter.setTopics(topics);
        topicAdapter.notifyDataSetChanged();
    }

    public interface LeftItemClickListener {
        public void onClick();
    }

    public interface RigthCatalogListener {
        public void onClick();
    }
}
