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
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.widget.list.IOnRefreshListener;
import com.meishai.app.widget.list.RefreshListView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserMoney;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.MoneyCashDialog;
import com.meishai.ui.fragment.usercenter.adapter.MoneyAdapter;
import com.meishai.ui.fragment.usercenter.req.MoneyReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;

/**
 * 我的-我的资金
 *
 * @ClassName: UserMoneyActivity
 */
public class UserMoneyActivity extends BaseActivity implements OnClickListener {

    private Context mContext = UserMoneyActivity.this;
    private int currentPage = 1;
    private Button mBtnBack;
    private RelativeLayout lay_payinfo;
    private TextView amount;
    private TextView alipay;
    private RefreshListView mListview;
    private MoneyAdapter moneyAdapter;
    private List<UserMoney> moneys = null;
    // 申请提现
    private RelativeLayout mLayApplyCash;
    private MoneyCashDialog cashDialog;

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserMoneyActivity.class);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_money);
        currentPage = 1;
        moneys = new ArrayList<UserMoney>();
        initView();
        fillData(null);
        loadData();
        initDialog();
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
        lay_payinfo = (RelativeLayout) this.findViewById(R.id.lay_payinfo);
        lay_payinfo.setOnClickListener(this);
        amount = (TextView) this.findViewById(R.id.amount);
        alipay = (TextView) this.findViewById(R.id.alipay);
        mLayApplyCash = (RelativeLayout) this.findViewById(R.id.lay_apply_cash);
        mLayApplyCash.setOnClickListener(this);
        mListview = (RefreshListView) this.findViewById(R.id.listview);
        // 上拉刷新
        mListview.setPullRefreshEnable(true);
        // 下拉加载更多
        mListview.setPullLoadEnable(true);
        mListview.setRefreshListListener(new IOnRefreshListener() {

            @Override
            public void onRefresh() {
                mListview.stopRefresh();
                currentPage = 1;
                moneys.clear();
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

    private void fillData(RespData data) {
        if (null != data) {
            amount.setText(String.format(getString(R.string.txt_amount),
                    data.getAmount()));
            alipay.setText(data.getAlipay());
        } else {
            amount.setText(String.format(getString(R.string.txt_amount), ""));
            alipay.setText("");
        }
    }

    private void initDialog() {
        cashDialog = new MoneyCashDialog(mContext);
    }

    private void loadData() {
        showProgress(null, mContext.getString(R.string.network_wait));
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        MoneyReq.money(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    fillData(response);
                    Type type = new TypeToken<List<UserMoney>>() {
                    }.getType();
                    List<UserMoney> resultMoneys = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultMoneys && !resultMoneys.isEmpty()) {
                        moneys.addAll(resultMoneys);
                        notifyMoneyAdapter();
                    } else if (moneys.isEmpty()) {
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

    private void notifyMoneyAdapter() {
        if (null == moneyAdapter) {
            moneyAdapter = new MoneyAdapter(mContext, moneys);
            mListview.setAdapter(moneyAdapter);
        } else {
            moneyAdapter.setMoneys(moneys);
            mListview.setAdapter(moneyAdapter);
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
            case R.id.lay_apply_cash:
                cashDialog.show();
                break;
            case R.id.lay_payinfo:
                mContext.startActivity(UserPayActivity.newIntent());
                break;
            default:
                break;
        }
    }
}
