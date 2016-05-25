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
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.ReleaseData;
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
import com.meishai.ui.dialog.CameraDialog;
import com.meishai.ui.dialog.TrialCancelDialog;
import com.meishai.ui.dialog.TrialDetailDialog;
import com.meishai.ui.dialog.TrialOrderDialog;
import com.meishai.ui.dialog.TrialScreenDialog;
import com.meishai.ui.dialog.TrialScreenPopup;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.camera.CameraActivity;
import com.meishai.ui.fragment.camera.CameraActivity1;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.ui.fragment.camera.ImageChooseActivity1;
import com.meishai.ui.fragment.usercenter.req.MemberReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.StringUtils;

public class TrialIngAdapter extends BaseAdapter implements OnClickListener {

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
    private View pView;
    private TrialScreenPopup screenPopup;
    private CameraDialog cameraDialog;

    public TrialIngAdapter(Context context, String type,
                           List<TrialInfo> trialInfos, View view, CameraDialog cameraDialog) {
        this.mContext = context;
        this.tInfos = trialInfos;
        pView = view;
        this.type = type;
        this.cameraDialog = cameraDialog;
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
        screenDialog = new TrialScreenDialog(mContext, cameraDialog);
        screenPopup = new TrialScreenPopup(mContext, cameraDialog);
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

    public void addImageData(ImageData data) {
        // screenDialog.addImageData(data);
        screenPopup.addImageData(data);
    }

    public void addAllImageData(List<ImageData> datas) {
        // screenDialog.addAllImageData(datas);
        screenPopup.addAllImageData(datas);
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
        int m = 1000;
        //
        String noDbj = mContext.getString(R.string.btn_no_dbj);
        String noOrder = mContext.getString(R.string.btn_no_orderno);
        // 已过期
        String txt_ordertime_expire = mContext
                .getString(R.string.txt_ordertime_expire);
        if (info.getOtype() == 2) {
            holder.btn_down_order.setVisibility(View.GONE);
            holder.tv_down_price.setText(mContext
                    .getString(R.string.tv_commdity_price));
            holder.lay_fp.setVisibility(View.GONE);
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
            if (info.getRstatus() == 0) {
                String frOrderTime = mContext
                        .getString(R.string.txt_ordertime_bg);
                if (info.getReporttime() < info.getNowtime()) {
                    holder.ordertime.setText(String.format(frOrderTime,
                            txt_ordertime_expire));
                } else {
                    holder.ordertime.setText(String
                            .format(frOrderTime,
                                    DateUtil.timeFormat(m
                                            * (info.getReporttime() - info
                                            .getNowtime()))));
                }
            } else {
                // 报告已提交
                String frOrderTime = mContext
                        .getString(R.string.txt_ordertime_bg_yjt);
                holder.ordertime.setText(frOrderTime);
            }
            holder.mBtnCancelTrial.setVisibility(View.GONE);
            holder.mBtnDetail.setVisibility(View.GONE);
            holder.btn_down_order.setVisibility(View.GONE);
            holder.btn_down_order.setOnClickListener(null);
            holder.mBtnSubmiOrder.setOnClickListener(null);
            holder.btn_dbj.setOnClickListener(null);
        } else {
            holder.mBtnCancelTrial.setVisibility(View.VISIBLE);
            holder.mBtnDetail.setVisibility(View.VISIBLE);
            holder.btn_down_order.setVisibility(View.VISIBLE);

            holder.mBtnDetail.setTag(position);
            holder.mBtnCancelTrial.setTag(position);
            holder.mBtnDetail.setOnClickListener(this);
            holder.mBtnCancelTrial.setOnClickListener(this);

            holder.tv_down_price.setText(mContext
                    .getString(R.string.tv_down_price));
            holder.lay_fp.setVisibility(View.VISIBLE);

            // 提交订单号逻辑
            if (info.getOstatus() < 99) {
                holder.mBtnSubmiOrder.setText(R.string.title_submit_order);
                holder.mBtnSubmiOrder.setTextColor(mContext.getResources()
                        .getColor(R.color.blue_color));
                holder.mBtnSubmiOrder
                        .setBackgroundResource(R.drawable.layout_blue_shape_selector);
                holder.mBtnSubmiOrder.setTag(position);
                holder.mBtnSubmiOrder.setOnClickListener(this);
            } else {
                holder.mBtnSubmiOrder.setText(R.string.title_submit_order);
                holder.mBtnSubmiOrder.setTextColor(mContext.getResources()
                        .getColor(R.color.txt_color));
                holder.mBtnSubmiOrder
                        .setBackgroundResource(R.drawable.t_btn_grey_shape_sel);
                holder.mBtnSubmiOrder.setOnClickListener(null);
            }

            if (info.getFstatus() < 1) {
                holder.btn_dbj.setText(R.string.title_apply_dbj);
                holder.btn_dbj
                        .setBackgroundResource(R.drawable.layout_blue_shape_selector);
                holder.btn_dbj.setTextColor(mContext.getResources().getColor(
                        R.color.blue_color));
                holder.btn_dbj.setTag(position);
                holder.btn_dbj.setOnClickListener(this);
            } else {
                holder.btn_dbj.setText(R.string.title_apply_dbj);
                holder.btn_dbj
                        .setBackgroundResource(R.drawable.t_btn_grey_shape_sel);
                holder.btn_dbj.setTextColor(mContext.getResources().getColor(
                        R.color.txt_color));
                holder.btn_dbj.setOnClickListener(null);
            }

            // 提交订单剩余时间
            String orderTime = mContext.getString(R.string.txt_ordertime);
            // 提交试用报告剩余时间
            String bgTime = mContext.getString(R.string.txt_ordertime_bg);
            // 提交评价截图剩余时间
            String jtTime = mContext.getString(R.string.txt_ordertime_jt);

            // 商家审核试用报告剩余时间
            String bizTime = mContext.getString(R.string.txt_ordertime_biz);
            // 商家审核评价截图剩余时间
            String bizJtTime = mContext
                    .getString(R.string.txt_ordertime_biz_jt);
            // 系统审核试用报告剩余时间
            String sysTime = mContext.getString(R.string.txt_ordertime_sys);
            // 系统审核评价截图剩余时间
            String sysJtTime = mContext
                    .getString(R.string.txt_ordertime_sys_jt);
            // 重新提交试用报告剩余时间
            String refTime = mContext.getString(R.string.txt_ordertime_ref);
            // 重新提交评价截图剩余时间
            String refJtTime = mContext
                    .getString(R.string.txt_ordertime_ref_jt);
            long time = info.getNowtime();
            if (info.getOstatus() == 0) {
                // 提交订单剩余时间
                if (info.getOrdertime() < time) {
                    // 已过期
                    holder.ordertime.setText(String.format(orderTime,
                            txt_ordertime_expire));
                } else {
                    holder.ordertime
                            .setText(String.format(
                                    orderTime,
                                    DateUtil.timeFormat(m
                                            * (info.getOrdertime() - info
                                            .getNowtime()))));
                }
            } else {
                if (info.getRstatus() == 0) {
                    if (info.getStype() == 0) {
                        // 提交试用报告剩余时间
                        if (info.getReporttime() < time) {
                            // 已过期
                            holder.ordertime.setText(String.format(bgTime,
                                    txt_ordertime_expire));
                        } else {
                            holder.ordertime.setText(String.format(
                                    bgTime,
                                    DateUtil.timeFormat(m
                                            * (info.getReporttime() - info
                                            .getNowtime()))));
                        }
                    } else {
                        // 提交评价截图剩余时间
                        if (info.getReporttime() < time) {
                            // 已过期
                            holder.ordertime.setText(String.format(jtTime,
                                    txt_ordertime_expire));
                        } else {
                            holder.ordertime.setText(String.format(
                                    jtTime,
                                    DateUtil.timeFormat(m
                                            * (info.getReporttime() - info
                                            .getNowtime()))));
                        }
                    }
                } else if (info.getRstatus() == 1 || info.getRstatus() == 3) {
                    if (info.getChecktime() > time) {
                        if (info.getStype() == 0) {
                            // 商家审核试用报告剩余时间
                            holder.ordertime.setText(String.format(
                                    bizTime,
                                    DateUtil.timeFormat(m
                                            * (info.getChecktime() - info
                                            .getNowtime()))));
                        } else {
                            // 商家审核评价截图剩余时间
                            holder.ordertime.setText(String.format(
                                    bizJtTime,
                                    DateUtil.timeFormat(m
                                            * (info.getChecktime() - info
                                            .getNowtime()))));
                        }
                    } else {
                        if (info.getStype() == 0) {
                            // 系统审核试用报告剩余时间
                            holder.ordertime.setText(String.format(
                                    sysTime,
                                    DateUtil.timeFormat(m
                                            * (info.getSystemtime() - info
                                            .getNowtime()))));
                        } else {
                            // 系统审核评价截图剩余时间
                            holder.ordertime.setText(String.format(
                                    sysJtTime,
                                    DateUtil.timeFormat(m
                                            * (info.getSystemtime() - info
                                            .getNowtime()))));
                        }
                    }
                } else if (info.getRstatus() == 2) {
                    if (info.getStype() == 0) {
                        // 重新提交试用报告剩余时间
                        if (info.getReporttime() < time) {
                            holder.ordertime.setText(String.format(refTime,
                                    txt_ordertime_expire));
                        } else {
                            holder.ordertime.setText(String.format(
                                    refTime,
                                    DateUtil.timeFormat(m
                                            * (info.getReporttime() - info
                                            .getNowtime()))));
                        }
                    } else {
                        // 重新提交评价截图剩余时间
                        if (info.getReporttime() < time) {
                            holder.ordertime.setText(String.format(refJtTime,
                                    txt_ordertime_expire));
                        } else {
                            holder.ordertime.setText(String.format(
                                    refJtTime,
                                    DateUtil.timeFormat(m
                                            * (info.getReporttime() - info
                                            .getNowtime()))));
                        }
                    }
                } else if (info.getRstatus() == 99) {
                    if (info.getFstatus() == 0) {
                        holder.ordertime.setText(R.string.txt_dbj_dsq);
                    } else if (info.getFstatus() == 1) {
                        holder.ordertime.setText(R.string.txt_dbj_yfh);
                    } else if (info.getFstatus() == 2) {
                        holder.ordertime.setText(R.string.txt_dbj_rzz);
                    }
                }
            }

            // if (StringUtils.isBlank(info.getOrderno())
            // || info.getOrderno().equals("0")) {
            // if (info.getStype() == 0) {
            // holder.ordertime.setText("提交试用报告");
            // } else {
            // String frOrderTime = mContext
            // .getString(R.string.txt_ordertime_screen);
            // holder.ordertime.setText(String.format(frOrderTime,
            // DateUtil.timeFormat(info.getOrdertime())));
            // }
            // } else {
            // // 提交订单号剩余
            // String frOrderTime = mContext.getString(R.string.txt_ordertime);
            // holder.ordertime.setText(String.format(frOrderTime,
            // DateUtil.timeFormat(info.getOrdertime())));
            // }
            holder.btn_down_order.setTag(position);
            holder.btn_down_order.setOnClickListener(this);
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
            // System.out.println("ostatus:"+info.getOstatus());
            TrialStatus ostatus = TrialStatus.getTrialStatus(info.getOstatus());
            holder.ostatus.setTextColor(Color.parseColor(ostatus.getColor()));
            holder.ostatus.setText(ostatus.getText());

            // if (info.getRstatus() != TrialStatus.S0.getStatus()) {
            // holder.mBtnScreen.setVisibility(View.GONE);
            // }else{
            // holder.mBtnScreen.setVisibility(View.VISIBLE);
            // }
            if (info.getStype() == 0) {
                // 提交试用报告
                if (info.getRstatus() < 99) {
                    holder.mBtnScreen.setText(R.string.title_submit_try);
                    holder.mBtnScreen
                            .setBackgroundResource(R.drawable.layout_blue_shape_selector);
                    holder.mBtnScreen.setTextColor(mContext.getResources()
                            .getColor(R.color.blue_color));
                    holder.mBtnScreen.setTag(position);
                    holder.mBtnScreen.setOnClickListener(this);
                } else {
                    holder.mBtnScreen.setText(R.string.title_submit_try);
                    holder.mBtnScreen
                            .setBackgroundResource(R.drawable.t_btn_grey_shape_sel);
                    holder.mBtnScreen.setTextColor(mContext.getResources()
                            .getColor(R.color.txt_color));
                    holder.mBtnScreen.setOnClickListener(null);
                }
            } else {
                if (info.getRstatus() < 99) {
                    holder.mBtnScreen.setText(R.string.title_submit_screen);
                    holder.mBtnScreen
                            .setBackgroundResource(R.drawable.layout_blue_shape_selector);
                    holder.mBtnScreen.setTextColor(mContext.getResources()
                            .getColor(R.color.blue_color));
                    holder.mBtnScreen.setTag(position);
                    holder.mBtnScreen.setOnClickListener(this);
                } else {
                    // 提交评价截图
                    holder.mBtnScreen.setText(R.string.title_submit_screen);
                    holder.mBtnScreen
                            .setBackgroundResource(R.drawable.t_btn_grey_shape_sel);
                    holder.mBtnScreen.setTextColor(mContext.getResources()
                            .getColor(R.color.txt_color));
                    holder.mBtnScreen.setOnClickListener(null);
                }
            }
        }

        return convertView;
    }

    private CustomProgress mProgressDialog = null;

    private void showProgressDialog() {
        String message = "验证中，请稍候...";
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
    }

    private void hideProgressDialog() {
        if (null != mProgressDialog) {
            mProgressDialog.hide();
        }
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
    private void tryOrder(final TrialInfo info) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("appid", info.getAppid() + "");
        showProgressDialog();
        MemberReq.tryOrder(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgressDialog();
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    TrialInfo trialInfo = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()),
                            TrialInfo.class);
                    if (null != trialInfo) {
                        trialInfo.setAppid(info.getAppid());
                        orderDialog.setTrialInfo(trialInfo);
                    }
                    orderDialog.show();
                }
                // else {
                // AndroidUtil.showToast(response.getTips());
                // }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
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
        showProgressDialog();
        MemberReq.tryReceipt(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgressDialog();
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    if (info.getStype() == 0) {
//						Intent intent = CameraActivity.newTryIntent(info
//								.getSid());
                        ReleaseData releaseData = new ReleaseData();
//						releaseData.setSid(info.getSid());
//						Intent intent = ImageChooseActivity1.newIntent(releaseData,CameraActivity1.ADD_IMAGE);

                        releaseData.setGid(info.getGid());
                        Intent intent = ChooseImageActivity.newIntent(releaseData, ConstantSet.MAX_IMAGE_COUNT);
                        mContext.startActivity(intent);
                    } else {
                        // screenDialog.setTrialInfo(info);
                        // screenDialog.show();
                        screenPopup.setTrialInfo(info);
                        screenPopup.showPop(pView);
                    }
                }
                // else {
                // AndroidUtil.showToast(response.getTips());
                // }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
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
        showProgressDialog();
        MemberReq.tryDeposit(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                hideProgressDialog();
                AndroidUtil.showToast(response.getTips());
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgressDialog();
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    public class ViewHolder {
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
        public TextView ordertime;
        // 订单号
        private TextView orderno;
        // 订单号状态
        private Button ostatus;
        // 下单流程
        private Button btn_down_order;
    }
}
