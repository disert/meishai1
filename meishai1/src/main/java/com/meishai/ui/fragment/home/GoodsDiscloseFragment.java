package com.meishai.ui.fragment.home;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.album.imageloader.ImageChooseActivity;
import com.album.utils.ImageLoader;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.meishai.R;
import com.meishai.app.widget.ProgressAction;
import com.meishai.app.widget.progressbar.RoundProgressBarWidthNumber;
import com.meishai.dao.MeiShaiSP;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.ChooseImageActivity;
import com.meishai.ui.fragment.home.req.HomeReq;
import com.meishai.ui.fragment.meiwu.adapter.Cid12Adapter;
import com.meishai.ui.fragment.meiwu.adapter.CidAllAdapter;
import com.meishai.ui.fragment.meiwu.adapter.CidMinusAdapter;
import com.meishai.ui.fragment.meiwu.adapter.MeiwuAdapter;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.PullToRefreshHelper;
import com.meishai.util.UploadUtilsAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 文件名：
 * 描    述：主页 - 我要爆料 - 商品爆料
 * 作    者：
 * 时    间：2016/3/26
 * 版    权：
 */
public class GoodsDiscloseFragment extends BaseFragment {
    private View view;
    private int mDiscloseType;
    private String mData;
    private PullToRefreshScrollView mScrollView;
    private TextView mTvName;
    private EditText mEditName;
    private TextView mTvAddress;
    private EditText mEditAddress;
    private TextView mTvPrice;
    private EditText mEditPrice;
    private TextView mTvReason;
    private EditText mEditReason;
    private TextView mTvProof;
    private ImageView mImageProof;
    private TextView mTvLabel;
    private Button mCommit;
    private LinearLayout mPriceRoot;
    private UploadUtilsAsync uploadAsync;

    private ArrayList<String> images;

    private ProgressAction mProgressAction;
    private RoundProgressBarWidthNumber progress;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //2：在onCreateView方法内复用RootView
        if (view == null) {
            view = inflater.inflate(R.layout.disclose_fragemt, null);
            mDiscloseType = getArguments().getInt("DISCLOSE_TYPE", -1);
            this.initView();
            initData();


            showProgress("", getString(R.string.network_wait));
            getRequestData();
        }
        return view;
    }

    private void initData() {
        mScrollView.setMode(Mode.PULL_FROM_START);
        PullToRefreshHelper.initIndicatorStart(mScrollView);
        mScrollView.setOnRefreshListener(new OnRefreshListener<ScrollView>() {
            @Override
            public void onRefresh(PullToRefreshBase<ScrollView> refreshView) {
                if (refreshView.getCurrentMode() == Mode.PULL_FROM_START) {
                    getRequestData();
                }
            }
        });

        if (mDiscloseType == DisCloseActivity.DISCOLSE_TYPE_GOODS) {
            mTvName.setText("商品名称");
            mEditName.setHint("请输入商品名字");
            mTvAddress.setText("购买地址/链接");
            mEditAddress.setHint("请输入购买地或购买链接");
            mPriceRoot.setVisibility(View.VISIBLE);
        } else {
            mTvName.setText("店铺名称");
            mEditName.setHint("请输入商铺名字");
            mTvAddress.setText("店铺地址/链接");
            mEditAddress.setHint("请输入店铺地或店铺链接");
            mPriceRoot.setVisibility(View.GONE);
        }
    }

    private void initView() {
        mScrollView = (PullToRefreshScrollView) view.findViewById(R.id.disclose_scrollview);
        mTvName = (TextView) view.findViewById(R.id.disclose_tv_name);
        mEditName = (EditText) view.findViewById(R.id.disclose_edit_name);
        mTvAddress = (TextView) view.findViewById(R.id.disclose_tv_address);
        mEditAddress = (EditText) view.findViewById(R.id.disclose_edit_address);
        mPriceRoot = (LinearLayout) view.findViewById(R.id.disclose_price);
        mTvPrice = (TextView) view.findViewById(R.id.disclose_tv_price);
        mEditPrice = (EditText) view.findViewById(R.id.disclose_edit_price);
        mTvReason = (TextView) view.findViewById(R.id.disclose_tv_reason);
        mEditReason = (EditText) view.findViewById(R.id.disclose_edit_reason);
        mTvProof = (TextView) view.findViewById(R.id.disclose_tv_proof);
        mImageProof = (ImageView) view.findViewById(R.id.disclose_image_proof);
        mTvLabel = (TextView) view.findViewById(R.id.disclose_tv_label);
        mCommit = (Button) view.findViewById(R.id.disclose_btn_commit);
        mCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                commit();
            }
        });
        mImageProof.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                selectImage();
            }

            @Override
            protected Object clone() throws CloneNotSupportedException {
                return super.clone();
            }
        });
    }

    private void selectImage() {
        mContext.startActivity(ImageChooseActivity.newIntent(ConstantSet.ACTION_NAME, 1));
    }


    private void commit() {
        if (checkData()) {

            mProgressAction = ProgressAction.show(mContext);
            progress = mProgressAction.getProgress();
            //假的进度
            new Thread(new Runnable() {

                @Override
                public void run() {
                    try {
                        int prog = 0;
                        int i = 5;
                        while (mProgressAction.isShowing()) {
                            prog += i;
                            Thread.sleep(150);
                            if (prog > 90) {
                                prog = 91;
                                i = 1;
                            }
                            if (mProgressAction.isShowing() && progress != null)
                                progress.setProgress(prog);

                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                }
            }).start();
            uploadAsync = new UploadUtilsAsync(mContext, getReqcommentData(),
                    images);
            uploadAsync.setListener(new UploadUtilsAsync.OnSuccessListener() {

                @Override
                public void onSuccess(RespData respData) {
                    if (mProgressAction.isShowing())
                        mProgressAction.dismiss();
                    if (respData.isSuccess()) {
                        getActivity().finish();
                    }
                    AndroidUtil.showToast(respData.getTips());
                }
            });

            uploadAsync.setUpdateListener(new UploadUtilsAsync.OnUpdateProgress() {

                @Override
                public void onUpdate(final int count, final int position, final int total,
                                     final int currentsize) {
                    //进度条真实数据显示效果不太好,需要做一个假的效果
//                	int centi = total * count / 100;//百分之一
//					int current = ((position+1)*total + currentsize) / centi;//当前百分之几
//					if(mProgressAction.isShowing())
//						if(progress != null)
//							progress.setProgress(current);

                }
            });
            uploadAsync.execute();
        }
    }

    private Map<String, String> getReqcommentData() {

        Map<String, String> data = new HashMap<String, String>();
        data.put("title", mEditName.getText().toString().trim());
        data.put("address", mEditAddress.getText().toString().trim());
        data.put("price", mEditPrice.getText().toString().trim());
        data.put("content", mEditReason.getText().toString().trim());
        data.put("userid", MeiShaiSP.getInstance().getUserInfo().getUserID());
        data.put("type", mDiscloseType + "");//爆料类型,type=3为商品，type=4为店铺

        ReqData reqData = new ReqData();
        reqData.setC("create");
        reqData.setA("baoliao_save");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    private boolean checkData() {
        if (TextUtils.isEmpty(mEditName.getText().toString().trim())) {
            AndroidUtil.showToast("请输入名称");
            return false;
        } else if (TextUtils.isEmpty(mEditAddress.getText().toString().trim())) {
            AndroidUtil.showToast("请输入地址或链接");
            return false;
        } else if (TextUtils.isEmpty(mEditReason.getText().toString().trim())) {
            AndroidUtil.showToast("请输入推荐理由");
            return false;
        } else if (images == null || images.isEmpty()) {
            AndroidUtil.showToast("请添加凭证截图");
            return false;
        } else if (TextUtils.isEmpty(mEditPrice.getText().toString().trim()) && mDiscloseType == DisCloseActivity.DISCOLSE_TYPE_GOODS) {
            AndroidUtil.showToast("请输入价格");
            return false;
        }

        return true;

    }


    protected void getRequestData() {

        showProgress("", getString(R.string.network_wait));

        HomeReq.baoliaoTextReq(mContext, mDiscloseType, new Listener<String>() {

            @Override
            public void onResponse(String response) {
                setNetComplete();
                if (!TextUtils.isEmpty(response)) {
                    DebugLog.w("返回结果:" + response);

                    JSONObject obj = null;
                    try {
                        obj = new JSONObject(response);
                        if (obj.getInt("success") == 1) {
                            mTvLabel.setText(obj.getString("text"));
                        } else {
                            AndroidUtil.showToast(obj.getString("tips"));
                        }


                    } catch (JSONException e) {
                        e.printStackTrace();
                        DebugLog.w("json解析错误!");
                    }

                }
            }
        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                AndroidUtil.showToast(R.string.reqFailed);
                setNetComplete();
            }
        });
    }


    private void setNetComplete() {
        hideProgress();
        mScrollView.onRefreshComplete();
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //1：在onDestroyView方法内把Fragment的RootView从ViewPager中remove
        if (view != null && view.getParent() != null) {
            ((ViewGroup) view.getParent()).removeView(view);
        }

    }

    public void chooseImage(Intent intent) {
        images = intent.getStringArrayListExtra(ConstantSet.CHOOSE_DATA);
        if (images == null || images.isEmpty()) return;
//        mTvProof.setText("图片地址:" + images.get(0));
        ImageLoader.getInstance().loadImage(images.get(0), mImageProof);
        mImageProof.setClickable(false);
        AndroidUtil.showToast("已选择图片!");
    }
}
