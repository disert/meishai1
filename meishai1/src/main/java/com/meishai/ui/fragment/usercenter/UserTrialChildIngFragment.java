/**
 */
package com.meishai.ui.fragment.usercenter;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.google.gson.reflect.TypeToken;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.meishai.R;
import com.meishai.app.util.DateUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.TrialInfo;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.CameraDialog;
import com.meishai.ui.fragment.usercenter.adapter.TrialIngAdapter;
import com.meishai.ui.fragment.usercenter.req.TrialReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.GsonHelper;
import com.meishai.util.PullToRefreshHelper;

/**
 * @ClassName: UserTrialActivity
 * @Description: 我的试用
 */
public class UserTrialChildIngFragment extends BaseFragment {

    static final String ARG_TYPE = "type";
    private String type = null;
    private View mView;
    private PullToRefreshListView mtryList;
    private TrialIngAdapter tiralAdapter;
    private List<TrialInfo> trialInfos = null;
    // 初始页(第一页数据)
    private int currentPage = 1;

    private CameraDialog cameraDialog;
    private LinearLayout lay_main;

    public UserTrialChildIngFragment() {

    }

    public static UserTrialChildIngFragment newInstance(String type) {
        UserTrialChildIngFragment f = new UserTrialChildIngFragment();
        Bundle args = new Bundle();
        args.putString(ARG_TYPE, type);
        f.setArguments(args);
        return f;
    }

    public void setType(String type) {
        this.type = type;
        currentPage = 1;
        trialInfos.clear();
        this.loadData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        type = getArguments().getString(ARG_TYPE);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        isRun = false;
        mView = inflater.inflate(R.layout.user_trial_child, null);
        currentPage = 1;
        trialInfos = new ArrayList<TrialInfo>();
        registerBoradcastReceiver();
        this.initDialog();
        this.initView();
        this.loadData();
        return mView;
    }

    private void initDialog() {
        cameraDialog = new CameraDialog(mContext);
        cameraDialog.setActionName(ConstantSet.IMAGE_SCREEN);
    }

    private void initView() {
        lay_main = (LinearLayout) mView.findViewById(R.id.lay_main);
        mtryList = (PullToRefreshListView) mView.findViewById(R.id.try_list);
        PullToRefreshHelper.initIndicatorStart(mtryList);
        PullToRefreshHelper.initIndicator(mtryList);

        mtryList.setOnRefreshListener(new OnRefreshListener2<ListView>() {
            @Override
            public void onPullDownToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage = 1;
                trialInfos.clear();
                loadData();
            }

            @Override
            public void onPullUpToRefresh(
                    PullToRefreshBase<ListView> refreshView) {
                currentPage++;
                loadData();
            }
        });
    }

    // 默认不运行
    private boolean isRun = false;

    private void notifyTrialAdapter() {
        if (null == tiralAdapter) {
            tiralAdapter = new TrialIngAdapter(mContext, type, trialInfos,
                    lay_main, cameraDialog);
            mtryList.setAdapter(tiralAdapter);
        } else {
            tiralAdapter.settInfos(trialInfos);
            tiralAdapter.notifyDataSetChanged();
        }
        if (!isRun) {
            handler.postDelayed(runnable, 1000);
            isRun = true;
        }
    }

    public void loadData() {
        showProgress(null, mContext.getString(R.string.network_wait));
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("status", type);
        data.put("page", String.valueOf(currentPage));
        data.put("pagesize", ConstantSet.PAGE_SIZE);
        TrialReq.tryList(mContext, data, new Listener<RespData>() {
            @Override
            public void onResponse(RespData response) {
                hideProgress();
                if (response.isSuccess()) {
                    onRefreshComplete();
                    Type type = new TypeToken<List<TrialInfo>>() {
                    }.getType();
                    List<TrialInfo> resultTrialInfos = GsonHelper.parseObject(
                            GsonHelper.toJson(response.getData()), type);
                    if (null != resultTrialInfos && !resultTrialInfos.isEmpty()) {
                        trialInfos.addAll(resultTrialInfos);
                        notifyTrialAdapter();
                    } else if (trialInfos.isEmpty()) {
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

    // 刷新完成
    private void onRefreshComplete() {
        mtryList.onRefreshComplete();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent intent) {
        if (requestCode == ConstantSet.SELECT_PIC_BY_TACK_PHOTO) {
            if (cameraDialog.getPhotoUri() == null) {
                AndroidUtil.showToast("拍照图片文件出错");
                return;
            }
            // 将拍照后图片偏差修正
            // String path = AndroidUtil.imageUri2Path(photoUri);
            // int degree = AndroidUtil.readPictureDegree(path);
            // Bitmap bmp = ImageUtil.rotateBitmap(path, degree);
            // File f = ImageUtil.saveBitMap2File(path, bmp);
            // bmp.recycle();
            // bmp = null;
            // cropImage(Uri.fromFile(f));

            // 裁剪
            // cropImage(photoUri);
            String uriPath = AndroidUtil.imageUri2Path(cameraDialog
                    .getPhotoUri());
            ImageData data = ImageData.getLocalImage(uriPath);
            tiralAdapter.addImageData(data);
        }
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(ConstantSet.IMAGE_SCREEN)) {
                ArrayList<String> imagePaths = intent
                        .getStringArrayListExtra(ConstantSet.CHOOSE_DATA);
                List<ImageData> datas = new ArrayList<ImageData>();
                if (null != imagePaths && !imagePaths.isEmpty()) {
                    for (String path : imagePaths) {
                        ImageData data = ImageData.getLocalImage(path);
                        datas.add(data);
                    }
                }
                tiralAdapter.addAllImageData(datas);
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(ConstantSet.IMAGE_SCREEN);
        // myIntentFilter.addAction(ConstantSet.ACTION_CATE);
        // 注册广播
        mContext.registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopHandler();
        mContext.unregisterReceiver(mBroadcastReceiver);
    }

    private Handler handler = new Handler();

    private int TIME_COUNT_INTERVAL = 1000;

    // private void startHandler() {
    // if (null != handler && null != runnable) {
    // handler.postDelayed(runnable, 1000);
    // }
    // }

    private void stopHandler() {
        if (null != handler) {
            handler.removeCallbacksAndMessages(null);
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            // 更新数据
            updateTryinfos();
            // 更新时间
            updateTime();
            handler.postDelayed(this, TIME_COUNT_INTERVAL);
        }
    };

    private void updateTryinfos() {
        if (null == trialInfos || trialInfos.isEmpty()) {
            return;
        }
        for (TrialInfo info : trialInfos) {
            // Nowtime单位未秒
            info.setNowtime(info.getNowtime() + 1);
        }
    }

    private void updateTime() {
        if (null == mtryList || null == trialInfos || trialInfos.isEmpty()) {
            return;
        }
        int firstVisibleItemIndex = mtryList.getRefreshableView()
                .getFirstVisiblePosition();
        int m = 1000;
        for (int i = 0; i < mtryList.getRefreshableView().getChildCount(); i++) {
            View v = mtryList.getRefreshableView().getChildAt(i);
            TrialInfo info = (TrialInfo) mtryList.getRefreshableView()
                    .getAdapter().getItem(firstVisibleItemIndex + i);
            com.meishai.ui.fragment.usercenter.adapter.TrialIngAdapter.ViewHolder holder = (com.meishai.ui.fragment.usercenter.adapter.TrialIngAdapter.ViewHolder) v
                    .getTag();
            // 已过期
            String txt_ordertime_expire = mContext
                    .getString(R.string.txt_ordertime_expire);
            if (info.getOtype() == 2) {
                if (info.getRstatus() == 0) {
                    // 提交试用报告剩余时间
                    String frOrderTime = mContext
                            .getString(R.string.txt_ordertime_bg);
                    if (info.getReporttime() < info.getNowtime()) {
                        holder.ordertime.setText(String.format(frOrderTime,
                                txt_ordertime_expire));

                    } else {
                        holder.ordertime.setText(String.format(
                                frOrderTime,
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
            } else {

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
                        holder.ordertime.setText(String.format(
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
                                holder.ordertime.setText(String.format(
                                        refJtTime, txt_ordertime_expire));
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
            }
        }

    }

}