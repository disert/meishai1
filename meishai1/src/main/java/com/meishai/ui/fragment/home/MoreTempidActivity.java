package com.meishai.ui.fragment.home;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.entiy.BaseResp;
import com.meishai.entiy.BaseRespData;
import com.meishai.entiy.CateResponseData;
import com.meishai.entiy.HomePageDatas;
import com.meishai.entiy.MoreStarItemData;
import com.meishai.entiy.MoreTempid1Data;
import com.meishai.entiy.MoreTempid2Data;
import com.meishai.entiy.PostItem;
import com.meishai.entiy.StarItemData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.fragment.home.adapter.ListItemAdapter;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * 文件名：
 * 描    述：美晒 - 发现 - tempid 的更多页面
 * 作    者：yl
 * 时    间：2016/2/18
 * 版    权：
 */
public class MoreTempidActivity extends BaseActivity {

    private TextView mTitle;
    private PullToRefreshListView mRefreshListView;
    private int mPage = 1;
    private boolean isLoading;
    private BaseResp mData;

    private ListItemAdapter mAdapter;
    private int padding;
    private int width;
    private int mTemid;
    private int mCid;


    public static Intent newIntent(int tempid, int cid, String title) {
        Intent intent = new Intent(GlobalContext.getInstance(), MoreTempidActivity.class);
        intent.putExtra("tempid", tempid);
        intent.putExtra("cid", cid);
        intent.putExtra("title", title);

        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.common_listview_activity);

        initView();

        initData();


    }

    private void initData() {
        mTitle.setText(getIntent().getStringExtra("title"));
        mTemid = getIntent().getIntExtra("tempid", 0);
        mCid = getIntent().getIntExtra("cid", 0);

        if (mTemid == 1) {
            mAdapter = initTempid1Adapter();
        } else if (mTemid == 2) {
            mAdapter = initTempid2Adapter();

        }
        mRefreshListView.setAdapter(mAdapter);

        //加载数据
        loadData();

    }

    public void loadData() {
        showProgress("", "加载中...");
        getRequestData(1);

    }

    private void initView() {
        DisplayMetrics dm = GlobalContext.getInstance().getApplicationContext().getResources().getDisplayMetrics();
        int screenWidth = dm.widthPixels;
        padding = AndroidUtil.dip2px(4);
        width = (screenWidth - padding) / 3;
        findViewById(R.id.back).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        mTitle = (TextView) findViewById(R.id.title);
        mRefreshListView = (PullToRefreshListView) findViewById(R.id.listview);
        mRefreshListView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mRefreshListView);


        mRefreshListView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener<ListView>() {

            @Override
            public void onRefresh(PullToRefreshBase<ListView> refreshView) {
                if (refreshView.getMode() == PullToRefreshBase.Mode.PULL_FROM_START) {
                    getRequestData(1);
                }

            }
        });

        mRefreshListView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                //倒数第二个的时候加载新数据
                if (totalItemCount - (firstVisibleItem + visibleItemCount) == 1 && !isLoading) {
                    isLoading = true;
                    DebugLog.w("总数item:" + totalItemCount + "当前item:" + (firstVisibleItem + visibleItemCount));
                    if (mPage < mData.pages) {
                        getRequestData(mPage + 1);
                    } else {
                        mRefreshListView.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                AndroidUtil
                                        .showToast(R.string.drop_down_list_footer_no_more_text);
                                mRefreshListView.onRefreshComplete();
                            }
                        }, 500);
                    }
                }
            }
        });
    }

    private ListItemAdapter<MoreTempid1Data.Tempid1ItemData> initTempid1Adapter() {
        return new ListItemAdapter<MoreTempid1Data.Tempid1ItemData>() {
            @Override
            public View getConvertView(int position, View convertView, ViewGroup parent, final MoreTempid1Data.Tempid1ItemData item, ImageLoader imageLoader) {
                Holder holder;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = View.inflate(MoreTempidActivity.this, R.layout.layout_more_star, null);
                    holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                    holder.username = (TextView) convertView.findViewById(R.id.username);
                    holder.lastTime = (TextView) convertView.findViewById(R.id.lasttime);
                    holder.vipGrade = (TextView) convertView.findViewById(R.id.vip_grade);
                    holder.vipGrade.setVisibility(View.GONE);
                    holder.attent = (TextView) convertView.findViewById(R.id.attention);
                    holder.grid = (GridView) convertView.findViewById(R.id.images_grid);
                    holder.adapter = new ListItemAdapter<HomePageDatas.PostInfo>() {

                        @Override
                        public View getConvertView(int position, View convertView, ViewGroup parent, final HomePageDatas.PostInfo item, ImageLoader imageLoader) {
                            if (convertView == null) {
                                convertView = new ImageView(MoreTempidActivity.this);
                                convertView.setLayoutParams(new AbsListView.LayoutParams(width, width));
                                convertView.setPadding(0, padding, padding, 0);
                            }
                            convertView.setTag(item.image);
                            ListImageListener listener1 = new ListImageListener((ImageView) convertView, R.drawable.place_default, R.drawable.place_default, item.image);
                            imageLoader.get(item.image, listener1);
                            convertView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    PostItem postItem = new PostItem();
                                    postItem.pid = item.pid;
                                    startActivity(PostShowActivity.newIntent(postItem, PostShowActivity.FROM_POST));
                                }
                            });
                            return convertView;
                        }
                    };

                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }

                holder.username.setText(item.title);
                holder.lastTime.setText(item.text);
                if (item.isattention == 1) {
                    holder.attent.setText("已关注");
                    holder.attent.setSelected(true);
                    holder.attent.setClickable(false);
                } else {
                    holder.attent.setText("+关注");
                    holder.attent.setClickable(true);
                    holder.attent.setSelected(false);
                }

                holder.avatar.setTag(item.image);
                ListImageListener listener = new ListImageListener(holder.avatar, R.drawable.place_default, R.drawable.place_default, item.image);
                imageLoader.get(item.image, listener);

                holder.grid.setPadding(padding, 0, 0, 0);
                holder.grid.setAdapter(holder.adapter);
                holder.adapter.setData(item.pics, imageLoader);

                holder.avatar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(TopicShowActivity.newIntent(item.tid));
                    }
                });
                holder.attent.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        attent(v, item);
                    }
                });
                return convertView;
            }
        };
    }

    private ListItemAdapter<CateResponseData.CateInfo1> initTempid2Adapter() {
        return new ListItemAdapter<CateResponseData.CateInfo1>() {
            @Override
            public View getConvertView(int position, View convertView, ViewGroup parent, final CateResponseData.CateInfo1 item, ImageLoader imageLoader) {
                Holder holder;
                if (convertView == null) {
                    holder = new Holder();
                    convertView = View.inflate(MoreTempidActivity.this, R.layout.home_cate_catalog_item1, null);
                    holder.avatar = (ImageView) convertView.findViewById(R.id.image);
                    holder.username = (TextView) convertView.findViewById(R.id.name);

                    convertView.setTag(holder);
                } else {
                    holder = (Holder) convertView.getTag();
                }
                holder.avatar.setTag(item.image);
                ListImageListener listener = new ListImageListener(holder.avatar, R.drawable.place_default, R.drawable.place_default, item.image);
                imageLoader.get(item.image, listener);

                holder.username.setText(item.title);

                convertView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(TopicShowActivity.newIntent(item.tid));
                    }
                });

                return convertView;
            }
        };
    }

    private void attent(final View v, MoreTempid1Data.Tempid1ItemData item) {
        v.setClickable(false);
        HomeData.getInstance().reqAttentionTopic(this, item.tid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                v.setClickable(true);
                // 处理关注请求的结果
                if (!TextUtils.isEmpty(response)) {
                    BaseRespData data = GsonHelper.parseObject(response, BaseRespData.class);
                    if (data != null) {
                        AndroidUtil.showToast(data.tips);
                        if (data.success == 1) {
                            ((TextView) v).setText("已关注");
                            v.setSelected(true);
                            v.setClickable(false);
                        }
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                v.setClickable(true);
                AndroidUtil.showToast("添加关注失败!");
            }
        });
    }

    private void getRequestData(int page) {
        HomeReq.moreOther(this, page, mTemid, mCid, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                mRefreshListView.onRefreshComplete();
                hideProgress();
                isLoading = false;
                BaseResp data = null;
                if (!TextUtils.isEmpty(response)) {
                    if (mTemid == 1) {
                        data = GsonHelper.parseObject(response, MoreTempid1Data.class);
                    } else if (mTemid == 2) {
                        data = GsonHelper.parseObject(response, MoreTempid2Data.class);
                    }
                    if (data != null) {
                        if (data.page == 1) {
                            if (!TextUtils.isEmpty(data.page_title))
                                mTitle.setText(data.page_title);
                            mData = data;
                        } else if (data.page > 1) {
                            if (mData == null || mData.list == null) {
                                return;
                            }

                            if (!mData.list.containsAll(data.list)) {
                                mData.list.addAll(data.list);
                                mData.page = data.page;
                            }
                        } else {
                            return;
                        }
                        mPage = data.page;
                        mAdapter.setData(mData.list, getImageLoader());
                    } else {
                        AndroidUtil.showToast("json解析失败!");
                    }
                } else {
                    AndroidUtil.showToast("返回数据为null!");
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mRefreshListView.onRefreshComplete();
                AndroidUtil.showToast("数据加载失败!");
                hideProgress();
                isLoading = false;
            }
        });
    }

    class Holder {

        ImageView avatar;
        TextView username;
        TextView lastTime;
        TextView vipGrade;
        TextView attent;
        GridView grid;
        ListItemAdapter<HomePageDatas.PostInfo> adapter;
    }
}
