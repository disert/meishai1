package com.meishai.ui.fragment.usercenter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.RelativeLayout;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.list.IOnRefreshListener;
import com.meishai.app.widget.list.RefreshListView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserPoint;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.meiwu.FindPointActivity;
import com.meishai.ui.fragment.usercenter.adapter.PointAdapter;
import com.meishai.ui.fragment.usercenter.req.PointReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 我的-我的积分
 *
 * @ClassName: UserPointActivity
 */
public class UserPointActivity extends BaseActivity implements OnClickListener {

    private Context mContext = UserPointActivity.this;
    private int currentPage = 1;
    private Button mBtnBack;
    private RelativeLayout lay_point;
    private RefreshListView mListview;
    private PointAdapter pointAdapter;
    private List<UserPoint> points = new ArrayList<UserPoint>();
    private View mTopLine;
    private View mBottomLine;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserPointActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_point);
        currentPage = 1;
        points = new ArrayList<UserPoint>();
        initView();
        loadData();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        lay_point = (RelativeLayout) this.findViewById(R.id.lay_point);
        lay_point.setOnClickListener(this);
        mTopLine = (View) this.findViewById(R.id.top_line);
        mBottomLine = (View) this.findViewById(R.id.bottom_line);
        mListview = (RefreshListView) this.findViewById(R.id.listview);
        // 上拉刷新
        mListview.setPullRefreshEnable(true);
        // 下拉加载更多
        mListview.setPullLoadEnable(false);
        mListview.setRefreshListListener(new IOnRefreshListener() {

            @Override
            public void onRefresh() {
                mListview.stopRefresh();
                currentPage = 1;
                points.clear();
                loadData();
            }

            @Override
            public void onLoadMore() {
                mListview.stopLoadMore();
                currentPage++;
                loadData();
            }
        });
    }

    private void toggleView() {
        if (points.isEmpty()) {
            mTopLine.setVisibility(View.GONE);
            mBottomLine.setVisibility(View.GONE);
        } else {
            mTopLine.setVisibility(View.VISIBLE);
            mBottomLine.setVisibility(View.VISIBLE);
        }
    }

    private void notifyTrialAdapter() {
        if (null == pointAdapter) {
            pointAdapter = new PointAdapter(mContext, points);
            mListview.setAdapter(pointAdapter);
        } else {
            pointAdapter.setPoints(points);
            mListview.setAdapter(pointAdapter);
        }
    }

    private void loadData() {
        showProgress(null, mContext.getString(R.string.network_wait));
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        PointReq.point(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    Type type = new TypeToken<List<UserPoint>>() {
                    }.getType();
                    List<UserPoint> resultPoints = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultPoints && !resultPoints.isEmpty()) {
                        points.addAll(resultPoints);
                        toggleView();
                        notifyTrialAdapter();
                    } else if (points.isEmpty()) {
                        AndroidUtil.showToast(response.getTips());
                    }
                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    /**
     * <p>
     * Title: onClick
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.lay_point:
                mContext.startActivity(FindPointActivity.newIntent());
                break;
            default:
                break;
        }
    }
}
