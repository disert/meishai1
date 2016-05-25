package com.meishai.ui.fragment.home;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.TopBackLayout;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CatalogInfo;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.nimbusds.jose.JOSEException;

/**
 * @ClassName: ChannelActivity
 * @Description: 晒晒->精选->频道选择
 */
public class ChannelPickActivity extends BaseActivity {

    private PullToRefreshListView mListView;
    private PickAdapter mAdapter;

    private int mPage = 0;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(), ChannelPickActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_channel_pick);
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

        mListView = (PullToRefreshListView) findViewById(R.id.channel_pick_listivew);
        mListView.setMode(Mode.PULL_FROM_END);
        mAdapter = new PickAdapter(this, getImageLoader());
        mListView.setAdapter(mAdapter);

        mListView.setOnRefreshListener(new OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {

                mPage++;
                getRequest();
            }
        });
    }

    private void getRequest() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("catalog");
        Map<String, String> dataCate = new HashMap<String, String>();
        dataCate.put("userid", userInfo.getUserID());
        dataCate.put("type", "1"); // 晒晒
        dataCate.put("page", String.valueOf(mPage));
        dataCate.put("pageSize", "10");
        reqData.setData(dataCate);

        try {
            String url = getString(R.string.base_url) + reqData.toReqString();
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

                    mListView.onRefreshComplete();
                    mPage--;
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
                JSONArray array = jsonObj.getJSONArray("data");

                Type type = new TypeToken<Collection<CatalogInfo>>() {
                }.getType();
                Collection<CatalogInfo> items = GsonHelper.parseObject(array.toString(), type);

                mAdapter.addCollection(items);

                return true;
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return false;
    }


    private static class PickAdapter extends BaseAdapter {

        private List<CatalogInfo> mList;

        private LayoutInflater mInflater;
        private ImageLoader mImageLoader;


        public PickAdapter(Context context, ImageLoader imageLoader) {
            mInflater = LayoutInflater.from(context);
            mImageLoader = imageLoader;
            mList = new ArrayList<CatalogInfo>();
        }

        public void addCollection(Collection<CatalogInfo> items) {
            mList.addAll(items);
            notifyDataSetChanged();
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public Object getItem(int position) {
            return mList.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (null == convertView) {
                convertView = mInflater.inflate(R.layout.home_channel_pick_item, parent, false);
                holder = new ViewHolder();
                holder.thumb = (ImageView) convertView.findViewById(R.id.thumb_niv);
                holder.name = (TextView) convertView.findViewById(R.id.name_tv);
                holder.desc = (TextView) convertView.findViewById(R.id.desc_tv);
                holder.operator = (ImageButton) convertView.findViewById(R.id.operator_ib);

                convertView.setTag(holder);

            } else {
                holder = (ViewHolder) convertView.getTag();
            }


            CatalogInfo item = (CatalogInfo) getItem(position);
            holder.thumb.setTag(item.image);
            ListImageListener listener = new ListImageListener(holder.thumb, 0, 0, item.image);
            mImageLoader.get(item.image, listener);

            holder.name.setText(item.name);
            holder.desc.setText(item.desc);

            if (item.isadd == 1) {
                holder.operator.setImageResource(R.drawable.ic_round_remove);

            } else {
                holder.operator.setImageResource(R.drawable.ic_round_add);
            }


            return convertView;
        }

        private static class ViewHolder {
            ImageView thumb;
            TextView name;
            TextView desc;
            ImageButton operator;
        }
    }
}
