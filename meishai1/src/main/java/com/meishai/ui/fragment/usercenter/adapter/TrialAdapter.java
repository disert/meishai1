package com.meishai.ui.fragment.usercenter.adapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.DateUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.TrialInfo;
import com.meishai.entiy.TrialInfo.TrialStatus;
import com.meishai.net.RespData;
import com.meishai.net.VolleyHelper;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.TrialCancelDialog;
import com.meishai.ui.dialog.TrialDetailDialog;
import com.meishai.ui.dialog.TrialOrderDialog;
import com.meishai.ui.dialog.TrialScreenDialog;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.camera.CameraActivity;
import com.meishai.ui.fragment.usercenter.req.MemberReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.StringUtils;

public class TrialAdapter extends BaseAdapter implements OnClickListener {

    private List<TrialInfo> tInfos = null;
    private Context mContext;
    private ImageLoader imageLoader = null;
    private String type = "";
    // 查看详情
    private TrialDetailDialog trialDetailDialog;
    // 放弃试用
    private TrialCancelDialog cancelDialog;
    // 提交订单号
    private TrialOrderDialog orderDialog;
    // 提交评价截图
    private TrialScreenDialog screenDialog;

    public TrialAdapter(Context context, String type, List<TrialInfo> trialInfos) {
        this.mContext = context;
        this.tInfos = trialInfos;
        this.type = type;
        this.initDialog();
        imageLoader = VolleyHelper.getImageLoader(mContext);

    }

    public void settInfos(List<TrialInfo> tInfos) {
        this.tInfos = tInfos;
    }

    private void initDialog() {
        trialDetailDialog = new TrialDetailDialog(mContext);
        cancelDialog = new TrialCancelDialog(mContext);
        orderDialog = new TrialOrderDialog(mContext);
        screenDialog = new TrialScreenDialog(mContext, null);
    }

    @Override
    public int getCount() {
        return tInfos.size();
    }

    @Override
    public Object getItem(int position) {
        return tInfos.get(position);
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (null == convertView) {
            holder = new ViewHolder();
            LayoutInflater inflater = LayoutInflater.from(mContext);
            convertView = inflater.inflate(R.layout.user_trial_item, null);
            holder.tr_title = (TextView) convertView
                    .findViewById(R.id.tr_title);
            holder.thumb = (NetworkImageView) convertView
                    .findViewById(R.id.thumb);
            holder.stype_text = (TextView) convertView
                    .findViewById(R.id.stype_text);
            holder.tv_down_price = (TextView) convertView
                    .findViewById(R.id.tv_down_price);
            holder.price = (TextView) convertView.findViewById(R.id.price);
            holder.fee = (TextView) convertView.findViewById(R.id.fee);
            holder.otype_text = (TextView) convertView
                    .findViewById(R.id.otype_text);
            holder.lay_fp = (LinearLayout) convertView
                    .findViewById(R.id.lay_fp);
            holder.fprice = (TextView) convertView.findViewById(R.id.fprice);
            holder.hprice = (TextView) convertView.findViewById(R.id.hprice);
            holder.lay_oper = (LinearLayout) convertView
                    .findViewById(R.id.lay_oper);
            holder.mBtnDetail = (Button) convertView
                    .findViewById(R.id.btn_detail);
            holder.mBtnCancelTrial = (Button) convertView
                    .findViewById(R.id.btn_cancel_trial);
            holder.mBtnSubmiOrder = (Button) convertView
                    .findViewById(R.id.btn_submit_order);
            holder.mBtnScreen = (Button) convertView
                    .findViewById(R.id.btn_screen);
            holder.btn_dbj = (Button) convertView.findViewById(R.id.btn_dbj);
            holder.reporttext = (TextView) convertView
                    .findViewById(R.id.reporttext);
            holder.rstatus = (Button) convertView.findViewById(R.id.rstatus);
            holder.orderno = (TextView) convertView.findViewById(R.id.orderno);
            holder.ostatus = (Button) convertView.findViewById(R.id.ostatus);
            holder.btn_down_order = (Button) convertView
                    .findViewById(R.id.btn_down_order);
            holder.ordertime = (TextView) convertView
                    .findViewById(R.id.ordertime);
            convertView.setTag(holder);

        } else {
            holder = (ViewHolder) convertView.getTag();
        }

        TrialInfo info = this.tInfos.get(position);
        holder.tr_title.setText(info.getTitle());
        holder.thumb.setDefaultImageResId(R.drawable.head_default);
        holder.thumb.setErrorImageResId(R.drawable.head_default);
        holder.thumb.setImageUrl(info.getThumb(), imageLoader);
        holder.stype_text.setTextColor(Color.parseColor(info.getStypeColor()));
        holder.stype_text.setText(info.getStypeText());
        holder.price.setText(String.format(mContext.getString(R.string.price),
                info.getPrice()));
        holder.fee.setText(info.getFee());
        holder.otype_text.setText(info.getOtype_text());
        holder.fprice.setText(String.format(
                mContext.getString(R.string.fprice), info.getFprice()));
        if (info.getHprice() > 0) {
            holder.hprice.setText(String.format(
                    mContext.getString(R.string.hprice), info.getHprice()));
            holder.hprice.setVisibility(View.VISIBLE);
        } else {
            holder.hprice.setVisibility(View.GONE);
        }
        //
        String noDbj = mContext.getString(R.string.btn_no_dbj);
        String noOrder = mContext.getString(R.string.btn_no_orderno);
        if (info.getOtype() == 2) {
            holder.btn_down_order.setVisibility(View.GONE);
            holder.tv_down_price.setText(mContext
                    .getString(R.string.tv_commdity_price));
            holder.lay_fp.setVisibility(View.GONE);
            holder.mBtnSubmiOrder.setText(mContext
                    .getString(R.string.tv_commdity_price));
            holder.mBtnSubmiOrder.setText(noOrder);
            holder.mBtnSubmiOrder
                    .setBackgroundResource(R.drawable.t_btn_grey_shape_sel);
            holder.mBtnSubmiOrder.setTextColor(mContext.getResources()
                    .getColor(R.color.txt_color));
            holder.btn_dbj.setText(noDbj);
            holder.btn_dbj
                    .setBackgroundResource(R.drawable.t_btn_grey_shape_sel);
            holder.btn_dbj.setTextColor(mContext.getResources().getColor(
                    R.color.txt_color));
            // 提交订单号剩余
            String frOrderTime = mContext.getString(R.string.txt_ordertime);
            holder.ordertime.setText(String.format(frOrderTime,
                    DateUtil.timeFormat(info.getOrdertime())));
        } else {
            if (StringUtils.isBlank(info.getOrderno())
                    || info.getOrderno().equals("0")) {
                if (info.getStype() == 0) {
                    holder.ordertime.setText("提交试用报告");
                } else {
                    String frOrderTime = mContext
                            .getString(R.string.txt_ordertime_screen);
                    holder.ordertime.setText(String.format(frOrderTime,
                            DateUtil.timeFormat(info.getOrdertime())));
                }
            } else {
                // 提交订单号剩余
                String frOrderTime = mContext.getString(R.string.txt_ordertime);
                holder.ordertime.setText(String.format(frOrderTime,
                        DateUtil.timeFormat(info.getOrdertime())));
            }
            holder.btn_down_order.setTag(position);
            holder.btn_down_order.setOnClickListener(this);
            holder.mBtnSubmiOrder.setTag(position);
            holder.mBtnSubmiOrder.setOnClickListener(this);
            holder.btn_dbj.setTag(position);
            holder.btn_dbj.setOnClickListener(this);
        }

        if (type.equals(TrialInfo.TRIAL_NO_PASS)
                || type.equals(TrialInfo.TRIAL_FINISH)) {
            holder.btn_down_order.setVisibility(View.GONE);
            holder.lay_oper.setVisibility(View.GONE);
        } else {
            holder.lay_oper.setVisibility(View.VISIBLE);
            holder.reporttext.setText(info.getReporttext());
            TrialStatus rstatus = TrialStatus.getTrialStatus(info.getRstatus());
            holder.rstatus.setTextColor(Color.parseColor(rstatus.getColor()));
            holder.rstatus.setText(rstatus.getText());
            String frOrderNo = mContext.getString(R.string.txt_orderno);
            if (info.getOtype() == 2) {
                frOrderNo = mContext.getString(R.string.txt_yun_orderno);
            }
            if (StringUtils.isBlank(info.getOrderno())
                    || info.getOrderno().equals("0")) {
                holder.orderno.setText(String.format(frOrderNo, "-"));
            } else {
                holder.orderno.setText(String.format(frOrderNo,
                        info.getOrderno()));
            }
            TrialStatus ostatus = TrialStatus.getTrialStatus(info.getOstatus());
            holder.ostatus.setTextColor(Color.parseColor(ostatus.getColor()));
            holder.ostatus.setText(ostatus.getText());

            // if (info.getOstatus() != TrialStatus.S0.getStatus()) {
            // holder.mBtnSubmiOrder.setVisibility(View.GONE);
            // }
            if (info.getRstatus() != TrialStatus.S0.getStatus()) {
                holder.mBtnScreen.setVisibility(View.GONE);
            }
            if (info.getStype() == 0) {
                // 提交使用报告
                holder.mBtnScreen.setText(R.string.title_submit_try);
            } else {
                // 提交评价截图
                holder.mBtnScreen.setText(R.string.title_submit_screen);
            }
            holder.mBtnDetail.setTag(position);
            holder.mBtnCancelTrial.setTag(position);
            holder.mBtnScreen.setTag(position);

            holder.mBtnDetail.setOnClickListener(this);
            holder.mBtnCancelTrial.setOnClickListener(this);
            holder.mBtnScreen.setOnClickListener(this);
        }

        return convertView;
    }

    @Override
    public void onClick(View v) {
        int position = (Integer) v.getTag();
        TrialInfo info = tInfos.get(position);
        switch (v.getId()) {
            case R.id.btn_detail:
                trialDetailDialog.setTrialInfo(info);
                trialDetailDialog.show();
                break;
            case R.id.btn_cancel_trial:
                cancelDialog.setTrialInfo(info);
                cancelDialog.show();
                break;
            case R.id.btn_down_order:
                mContext.startActivity(MeishaiWebviewActivity.newIntent(info
                        .getWap_url()));
                // tryOrder(info);
                break;
            case R.id.btn_submit_order:
                if (info.getOtype() != 2) {
                    tryOrder(info);
                }
                // orderDialog.setTrialInfo(info);
                // orderDialog.show();
                break;
            case R.id.btn_screen:
                tryReceipt(info);
                break;
            case R.id.btn_dbj:
                if (info.getOtype() != 2) {
                    tryDeposit(info);
                }
                break;
            default:
                break;
        }
    }

    /**
     * 提交订单号检查
     *
     * @param info
     */
    private void tryOrder(TrialInfo info) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("appid", info.getAppid() + "");
        MemberReq.tryOrder(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    TrialInfo trialInfo = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()),
                            TrialInfo.class);
                    orderDialog.setTrialInfo(trialInfo);
                    orderDialog.show();
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

    /**
     * 提交评价截图检查
     *
     * @param info
     */
    private void tryReceipt(final TrialInfo info) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("appid", info.getAppid() + "");
        MemberReq.tryReceipt(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (response.isSuccess()) {
                    if (info.getStype() == 0) {
                        Intent intent = CameraActivity.newTryIntent(info
                                .getSid());
                        mContext.startActivity(intent);
                    } else {
                        screenDialog.setTrialInfo(info);
                        screenDialog.show();
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

    /**
     * 申请担保金
     *
     * @param info
     */
    private void tryDeposit(TrialInfo info) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("appid", info.getAppid() + "");
        MemberReq.tryDeposit(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                AndroidUtil.showToast(response.getTips());
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    class ViewHolder {
        // 试用编号+试用标题
        private TextView tr_title;
        // 试用图片
        private NetworkImageView thumb;
        // 试用类型
        private TextView stype_text;
        // 下单价格text
        private TextView tv_down_price;
        // 下单价格
        private TextView price;
        // 邮费状态
        private TextView fee;
        // 下单类型
        private TextView otype_text;
        private LinearLayout lay_fp;
        // 返还金额
        private TextView fprice;
        // 红包金额
        private TextView hprice;

        private LinearLayout lay_oper;
        // 查看详情
        private Button mBtnDetail;
        // 放弃试用
        private Button mBtnCancelTrial;
        // 提交订单号
        private Button mBtnSubmiOrder;
        // 提交评价截图
        private Button mBtnScreen;
        // 担保金
        private Button btn_dbj;
        // 试用报告文字
        private TextView reporttext;
        // 试用报告状态
        private Button rstatus;
        // 提交订单剩余时间
        private TextView ordertime;
        // 订单号
        private TextView orderno;
        // 订单号状态
        private Button ostatus;
        // 下单流程
        private Button btn_down_order;
    }
}
