package com.meishai.ui.popup;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.util.KeyBoardUtils;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.CommentInfo;
import com.meishai.entiy.PostItem;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.UploadUtilsAsync;
import com.meishai.util.UploadUtilsAsync.OnSuccessListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


/**
 * 文件名：
 * 描    述：评论的对话框
 * 作    者：yl
 * 时    间：2016/3/14
 * 版    权：
 */
public class CommentPopupWindow1 extends PopupWindow {


    private UploadUtilsAsync uploadAsync;//上传评论的异步处理类
    //评论成功后的回调
    private CommentPopupWindow.OnCommentSuccessListener mCommentListener;

    private View mPopView;
    private TextView mBtnPublish;//发表评论的按钮
    private TextView mCancel;//取消评论按钮
    private EditText mTvComment;//评论输入框


    private PostItem postItem;
    private CommentInfo commentInfo;
    private Context mContext;


    private OnClickListener onClickListener;


    public CommentPopupWindow1(Context context) {
        super(context);
        mContext = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        mPopView = inflater.inflate(R.layout.home_comment_popup1, null);
        this.setContentView(mPopView);
        this.setWidth(LayoutParams.MATCH_PARENT);
        this.setHeight(LayoutParams.MATCH_PARENT);
        this.init();
        this.initViews();
        this.setListener();
        mPopView.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = mPopView.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismissPop();
                    }
                }
                return true;
            }
        });
    }

    /**
     * 设置评论成功后的监听器
     *
     * @param l
     */
    public void setOnCommentSuccessListener(CommentPopupWindow.OnCommentSuccessListener l) {
        mCommentListener = l;
    }

    public void setPostItem(PostItem postItem) {
        this.postItem = postItem;
    }

    public void setCommentInfo(CommentInfo commentInfo) {
        this.commentInfo = commentInfo;
    }

    public void setOnClickListener(OnClickListener onClickListener) {
        this.onClickListener = onClickListener;
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

    private void initViews() {

        mCancel = (TextView) mPopView.findViewById(R.id.cancel);
        mBtnPublish = (TextView) mPopView.findViewById(R.id.publish);
        mTvComment = (EditText) mPopView.findViewById(R.id.tv_comment);

    }

    private void setListener() {
        //点击取消的事件
        mCancel.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                dismissPop();
            }
        });
        //点击发布的事件
        mBtnPublish.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                if (checkData()) {
                    closeKeybord();
                    uploadAsync = new UploadUtilsAsync(mContext,
                            getAddCommentData(), new ArrayList<String>());
                    uploadAsync.setListener(new OnSuccessListener() {

                        @Override
                        public void onSuccess(RespData respData) {
                            if (respData == null) return;
                            //发布评论成功后的处理
                            if (respData.isSuccess()) {
                                AndroidUtil.showToast(respData.getTips());
                                mTvComment.setText("");
                                dismissPop();
                                if (mCommentListener != null) {
                                    CommentInfo info = new CommentInfo();
                                    mCommentListener.onCommentSuccess(info);
                                }

                            } else if (respData.isLogin()) {
                                mContext.startActivity(LoginActivity
                                        .newOtherIntent());
                            } else {
                                AndroidUtil.showToast(respData.getTips());
                            }
                        }
                    });
                    uploadAsync.execute();
                }
            }
        });
    }


    public void showPop(View v) {
        if (!isShowing()) {
            // AndroidUtil.toggleSoftInput(mContext);
            updateUI();

            this.showAtLocation(v, Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL,
                    0, 0);
        }
    }

    public void dismissPop() {
        if (isShowing()) {
            //关闭输入框
            KeyBoardUtils.closeKeybord(mTvComment, mContext);
            //清除数据
            postItem = null;
            commentInfo = null;
            // 关闭弹出窗体
            dismiss();
        }
    }

    private void updateUI() {
        KeyBoardUtils.openKeybord(mTvComment, mContext);
        if (null != commentInfo) {
            String commentFint = mContext.getString(R.string.comment_hint);
            mTvComment
                    .setHint(String.format(commentFint, commentInfo.username));
        } else {
            mTvComment.setHint("评论一下送积分");
        }
    }

    private boolean checkData() {
        if (TextUtils.isEmpty(mTvComment.getText().toString())) {
            AndroidUtil.showToast(R.string.tip_comment);
            return false;
        }
        return true;
    }

    private Map<String, String> getAddCommentData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // 帖子ID
        data.put("pid", null != postItem ? String.valueOf(postItem.pid) : "0");
        // 回复评论ID，如果不是回复则传入0
        data.put("dataid",
                null != commentInfo ? String.valueOf(commentInfo.dataid) : "0");
        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", "0");
        // 地区坐标，格式：经度,纬度
        data.put("coordinate", "");
        // 广东省,深圳市,龙岗区，格式：省,市,区
        data.put("areaname", "");
        // 文字内容
        data.put("content", mTvComment.getText().toString());
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("addcomment");
        reqData.setData(data);
        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }


    public void closeKeybord() {
        KeyBoardUtils.closeKeybord(mTvComment, mContext);
    }
}
