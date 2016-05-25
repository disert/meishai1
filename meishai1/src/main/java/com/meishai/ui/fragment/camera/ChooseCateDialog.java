package com.meishai.ui.fragment.camera;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshGridView;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CateInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.adapter.ChooseCateAdapter;
import com.meishai.ui.fragment.camera.req.ReleaseReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 拍照-选择晒晒分类
 *
 * @author sh
 */
public class ChooseCateDialog extends Dialog {

    private Context mContext;
    private Button btn_cancel;
    private PullToRefreshGridView grid;
    private ChooseCateAdapter cateAdapter;
    private List<CateInfo> cateInfos;
    private OnCateCheckedListener listener;

    private CateInfo cateInfo = null;

    // private CateInfo chkCateInfo = null;

    public ChooseCateDialog(Context context, CateInfo cateInfo,
                            OnCateCheckedListener listener) {
        super(context, R.style.dialog_transparent);
        this.mContext = context;
        this.listener = listener;
        this.cateInfo = cateInfo;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setContentView(R.layout.release_cate);
        cateInfos = new ArrayList<CateInfo>();
        this.initView();
        this.loadData();
    }

    public void setCateInfo(CateInfo cateInfo) {
        this.cateInfo = cateInfo;
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public interface OnCateCheckedListener {
        public void checkedCate(CateInfo cateInfo);
    }

    private void initView() {
        btn_cancel = (Button) this.findViewById(R.id.btn_cancel);
        grid = (PullToRefreshGridView) this.findViewById(R.id.grid);
        cateAdapter = new ChooseCateAdapter(mContext, cateInfos);
        grid.setAdapter(cateAdapter);
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
                listener.checkedCate(chkCateInfo);
                dismiss();
            }

        });
        btn_cancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

    }

    private void notifyCateAdapter() {
        cateAdapter.setCate(cateInfo);
        cateAdapter.setCateInfos(cateInfos);
        cateAdapter.notifyDataSetChanged();

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
