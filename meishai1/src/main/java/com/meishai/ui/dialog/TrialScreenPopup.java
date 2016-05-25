package com.meishai.ui.dialog;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.support.v7.widget.LinearLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CustomProgress;
import com.meishai.app.widget.horizontalscrollview.MyRecyclerView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ImageData;
import com.meishai.entiy.TrialInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.adapter.ScreenAdapter;
import com.meishai.ui.dialog.adapter.ScreenAdapter.OnDelClickLitener;
import com.meishai.ui.dialog.adapter.ScreenAdapter.OnItemClickLitener;
import com.meishai.ui.fragment.usercenter.req.TrialReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.UploadUtilsAsync;
import com.meishai.util.UploadUtilsAsync.OnSuccessListener;

/**
 * 试用-提交评价截图
 *
 * @author sh
 */
public class TrialScreenPopup extends PopupWindow {

    private View mPopView;
    private Context mContext;
    private CustomProgress mProgressDialog;
    private Button mBtnCancel;
    private Button mBtnSubmit;
    private TrialInfo trialInfo;
    private TextView mOrderno;

    private UploadUtilsAsync uploadAsync;

    private int maxSelected = 1;

    private ScreenAdapter mAdapter;
    private MyRecyclerView screen_photo;
    private List<ImageData> mDatas;
    private CameraDialog cameraDialog;

    public TrialScreenPopup(Context context, CameraDialog cameraDialog) {
        super(context);
        mContext = context;
        this.cameraDialog = cameraDialog;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.popup_trial_screen, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.init();
        this.initView();
        this.setListener();
//		mPopView.setOnTouchListener(new OnTouchListener() {
//
//			public boolean onTouch(View v, MotionEvent event) {
//				int height = mPopView.findViewById(R.id.pop_layout).getTop();
//				int y = (int) event.getY();
//				if (event.getAction() == MotionEvent.ACTION_UP) {
//					if (y > height) {
//						dismissPop();
//					}
//				}
//				return true;
//			}
//		});
    }

    private void init() {
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
        this.setAnimationStyle(R.style.popupAnimation);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0000000000);
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(dw);
    }

    public void showPop(View v) {
        if (!isShowing()) {
            this.fillData();
            this.showAtLocation(v, Gravity.CENTER | Gravity.CENTER_HORIZONTAL,
                    0, 0);
        }
    }

    private void dismissPop() {
        if (isShowing()) {
            this.dismiss();
        }
    }

    public void setTrialInfo(TrialInfo trialInfo) {
        this.trialInfo = trialInfo;
    }

    public void addImageData(ImageData data) {
        mDatas.add(0, data);
        int c = hasSelectedCount();
        if (maxSelected <= c) {
            mDatas.remove(mDatas.size() - 1);
        }
        mAdapter.notifyDataSetChanged();
    }

    public void addAllImageData(List<ImageData> datas) {
        mDatas.addAll(0, datas);
        int c = hasSelectedCount();
        if (maxSelected <= c) {
            mDatas.remove(mDatas.size() - 1);
        }
        mAdapter.notifyDataSetChanged();
    }

    //清除数据
    private void clearImageData() {
        mDatas.clear();
        mDatas.add(ImageData.getDefImage());
        mAdapter.notifyDataSetChanged();
    }

    private void initView() {
        this.mBtnCancel = (Button) mPopView.findViewById(R.id.btn_cancel);
        this.mBtnSubmit = (Button) mPopView.findViewById(R.id.btn_submit);
        this.mOrderno = (TextView) mPopView.findViewById(R.id.orderno);

        mDatas = new ArrayList<ImageData>();
        mDatas.add(ImageData.getDefImage());
        screen_photo = (MyRecyclerView) mPopView
                .findViewById(R.id.screen_photo);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(
                mContext);
        linearLayoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        screen_photo.setLayoutManager(linearLayoutManager);
        mAdapter = new ScreenAdapter(mContext, mDatas,
                R.layout.user_trial_gallery_item);
        screen_photo.setAdapter(mAdapter);

        if (null != cameraDialog) {
            mAdapter.setOnItemClickLitener(new OnItemClickLitener() {
                @Override
                public void onItemClick(View view, int position) {
                    ImageData data = mDatas.get(position);
                    if (ImageData.IMAGE_DEF == data.getType()) {
                        int c = hasSelectedCount();
                        cameraDialog.setMaxSelected(maxSelected - c);
                        cameraDialog.showDialog();
                    }
                }
            });
            mAdapter.setOnDelClickLitener(new OnDelClickLitener() {

                @Override
                public void onDelClick(View view, int position) {
                    mDatas.remove(position);
                    // 是否需要默认 添加图片 默认需要
                    boolean isNeedDef = true;
                    if (maxSelected > mDatas.size()) {
                        for (ImageData imgData : mDatas) {
                            if (ImageData.IMAGE_DEF == imgData.getType()) {
                                isNeedDef = false;
                                break;
                            }
                        }
                        if (isNeedDef) {
                            mDatas.add(mDatas.size(), ImageData.getDefImage());
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                }
            });
        }
    }

    private void setListener() {
        this.mBtnCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        this.mBtnSubmit.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                uploadAsync = new UploadUtilsAsync(mContext,
                        getReqReceiptData(), getPicsPath(ImageData.IMAGE_LOCAL));
                uploadAsync.setListener(new OnSuccessListener() {

                    @Override
                    public void onSuccess(RespData respData) {
                        AndroidUtil.showToast(respData.getTips());
                        if (respData.isSuccess()) {
                            dismiss();
                        }
                    }
                });
                uploadAsync.execute();
            }
        });
    }

    /**
     * 获取提交图片的路径集合
     *
     * @return
     */
    private ArrayList<String> getPicsPath(int type) {
        ArrayList<String> paths = new ArrayList<String>();
        for (ImageData data : mDatas) {
            if (type == data.getType()) {
                paths.add(data.getPath());
            }
        }
        return paths;
    }

    private Map<String, String> getReqReceiptData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("appid", String.valueOf(trialInfo.getAppid()));

        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("try_receipt_add");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    // 获取已经选择的图片数量
    private int hasSelectedCount() {
        int c = 0;
        for (ImageData data : mDatas) {
            if (ImageData.IMAGE_DEF != data.getType()) {
                c++;
            }
        }
        return c;
    }

    private void fillData() {
        this.mOrderno.setText(trialInfo.getOrderno());
    }

    private void tryReceipt() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("appid", String.valueOf(trialInfo.getAppid()));
        // 图片文件
        data.put("image", "");
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress.show(mContext,
                    mContext.getString(R.string.network_wait), true, null);
        } else {
            mProgressDialog.show();
        }
        TrialReq.tryReceipt(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                mProgressDialog.hide();
                AndroidUtil.showToast(response.getTips());
                if (response.isSuccess()) {
                    dismiss();
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
