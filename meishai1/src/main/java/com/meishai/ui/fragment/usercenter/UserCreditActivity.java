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
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.list.IOnRefreshListener;
import com.meishai.app.widget.list.RefreshListView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserCredit;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.adapter.CreditAdapter;
import com.meishai.ui.fragment.usercenter.req.CreditReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 我的-我的信用
 *
 * @ClassName: UserCreditActivity
 */
public class UserCreditActivity extends BaseActivity implements OnClickListener {

    private Context mContext = UserCreditActivity.this;
    // 初始页(第一页数据)
    private int currentPage = 1;
    private Button mBtnBack;
    private TextView mMyCredit;
    private RefreshListView mListview;
    private CreditAdapter creditAdapter;
    private List<UserCredit> credits = null;
    private View mTopLine;
    private View mBottomLine;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserCreditActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_credit);
        currentPage = 1;
        credits = new ArrayList<UserCredit>();
        this.initView();
        this.loadData();
        this.fillCredit(null);
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     */
    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mMyCredit = (TextView) this.findViewById(R.id.my_credit);
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
                credits.clear();
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
        if (credits.isEmpty()) {
            mTopLine.setVisibility(View.GONE);
            mBottomLine.setVisibility(View.GONE);
        } else {
            mTopLine.setVisibility(View.VISIBLE);
            mBottomLine.setVisibility(View.VISIBLE);
        }
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
            default:
                break;
        }
    }

    private void notifyTrialAdapter() {
        if (null == creditAdapter) {
            creditAdapter = new CreditAdapter(mContext, credits);
            mListview.setAdapter(creditAdapter);
        } else {
            creditAdapter.setCredits(credits);
            mListview.setAdapter(creditAdapter);
        }
    }

    private void loadData() {
        showProgress(null, mContext.getString(R.string.network_wait));
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        CreditReq.credit(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    fillCredit(response.getCredit());
                    Type type = new TypeToken<List<UserCredit>>() {
                    }.getType();
                    List<UserCredit> resultCredits = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultCredits && !resultCredits.isEmpty()) {
                        credits.addAll(resultCredits);
                        toggleView();
                        notifyTrialAdapter();
                    } else if (credits.isEmpty()) {
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

    private void fillCredit(String credit) {

        mMyCredit.setText(String.format(
                mContext.getString(R.string.txt_credit),
                StringUtil.isBlank(credit) ? "0" : credit));

    }
}
