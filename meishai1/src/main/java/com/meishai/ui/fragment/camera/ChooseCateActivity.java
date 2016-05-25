package com.meishai.ui.fragment.camera;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.adapter.ChooseCateAdapter;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 发布-选择分类
 *
 * @author sh
 */
public class ChooseCateActivity extends BaseActivity {

    private Context mContext = ChooseCateActivity.this;
    private Button btn_cancel;
    private PullToRefreshGridView grid;
    private ChooseCateAdapter cateAdapter;
    private List<CateInfo> cateInfos;

    private CateInfo cateInfo = null;

    public static Intent newIntent(CateInfo cateInfo) {
        Intent intent = new Intent(GlobalContext.getInstance(),
                ChooseCateActivity.class);
        intent.putExtra("cate", cateInfo);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.release_cate);
        Bundle bundle = getIntent().getExtras();
        if (null != bundle) {
            try {
                cateInfo = (CateInfo) bundle.getSerializable("cate");
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        cateInfos = new ArrayList<CateInfo>();
        this.initView();
    }

    @Override
    protected void onResume() {
        this.loadData();
        super.onResume();
    }

    private void initView() {
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        grid = (PullToRefreshGridView) this.findViewById(R.id.grid);
        grid.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                cateAdapter.setCate(null);
                // 当前选中的Item改变背景颜色
                view.setBackgroundColor(mContext.getResources().getColor(
                        R.color.grid_item_press));
                for (int i = 0; i < parent.getCount(); i++) {
                    View v = parent.getChildAt(i);
                    if (position != i) {
                        v.setBackgroundColor(mContext.getResources().getColor(
                                R.color.white));
                    }
                }
                CateInfo chkCateInfo = (CateInfo) parent.getAdapter().getItem(
                        position);
                Intent mIntent = new Intent(ConstantSet.ACTION_CATE);
                mIntent.putExtra(ConstantSet.CHOOSE_CATE, chkCateInfo);
                // 发送广播
                sendBroadcast(mIntent);
                finish();
            }

        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                finish();
            }
        });

    }

    private void notifyCateAdapter() {
        if (null == cateAdapter) {
            cateAdapter = new ChooseCateAdapter(mContext, cateInfos);
            grid.setAdapter(cateAdapter);
        } else {
            cateAdapter.setCate(cateInfo);
            cateAdapter.setCateInfos(cateInfos);
            cateAdapter.notifyDataSetChanged();
        }
    }

    private void loadData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        ReleaseReq.choosecatalog(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<CateInfo>>() {
                    }.getType();
                    List<CateInfo> resultCateInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);

                    if (null != resultCateInfos && !resultCateInfos.isEmpty()) {
                        cateInfos = resultCateInfos;
                        notifyCateAdapter();
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
}
