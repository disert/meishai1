package com.meishai.ui.popup;

import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.text.ClipboardManager;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.Platform.ShareParams;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.tencent.qzone.QZone;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;

/**
 * 自定义分享面板 ,默认情况下,该面板用于美物模块的分享,需要在构造的时候传入tid,
 * 后期做福利社模块的时候进行了复用,要复用需要设定自己的PlatformActionListener,这时tid属性已经无用,建议传递0
 *
 * @author yl
 */
public class ShareMorePopupWindow extends PopupWindow implements
        PlatformActionListener {
    private View view;
    private Context context;
    private ShareParams shareParams;
    private LinearLayout mMoments;
    private LinearLayout mFriend;
    private LinearLayout mCopy;
    private Listener<String> listener;
    private ErrorListener errorListener;
    private int tid;
    private int currentType;

    private PlatformActionListener actionListener;

    private TextView mHint;
    private Button mCancel;
    private TextView mHint1;
    private View mFri;
    private View mMomen;
    private TextView mMomentPoint;
    private TextView mFriendPoint;

    public ShareMorePopupWindow(Context cx, int tid) {
        super(cx);
        this.context = cx;
        this.tid = tid;
        view = LayoutInflater.from(context).inflate(
                R.layout.share_more_popupwindow, null);
        mMoments = (LinearLayout) view
                .findViewById(R.id.share_more_pop_moments);
        mFriend = (LinearLayout) view.findViewById(R.id.share_more_pop_friend);
        mFri = view.findViewById(R.id.fri);
        mMomen = view.findViewById(R.id.moments);
        mCopy = (LinearLayout) view.findViewById(R.id.share_more_pop_copy);
        mHint = (TextView) view.findViewById(R.id.hint);
        mHint1 = (TextView) view.findViewById(R.id.hint_1);
        mCancel = (Button) view.findViewById(R.id.cancel);
        mFriendPoint = (TextView) view.findViewById(R.id.friend_point);
        mMomentPoint = (TextView) view.findViewById(R.id.moments_point);

        mCopy.setClickable(false);
        mMomen.setClickable(false);
        mFri.setClickable(false);

        // 设置SelectPicPopupWindow的View
        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(LayoutParams.MATCH_PARENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(0xb0000000);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        initListener();

    }

    public void setHint(String hint) {
        mHint.setText(hint);
    }

    public void setHint1(String hint) {
        mHint1.setText(hint);
    }

    public void setHint1Visibility(int visibility) {
        mHint1.setVisibility(visibility);
    }

    public void setHintVisibility(int visibility) {
        if (mHint != null)
            mHint.setVisibility(visibility);
    }

    /**
     * 分享前干的事儿在这里面做,可以在new对象的时候使用子类来实现,分享后要做的可以通过设置actionLinstener来实现
     */
    public void sharePre(String name) {

    }

    private void initListener() {
        mCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
        view.setOnTouchListener(new OnTouchListener() {

            public boolean onTouch(View v, MotionEvent event) {
                int height = view.findViewById(R.id.pop_layout).getTop();
                int y = (int) event.getY();
                if (event.getAction() == MotionEvent.ACTION_UP) {
                    if (y < height) {
                        dismiss();
                    }
                }
                return true;
            }
        });
        mMomen.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                share(WechatMoments.NAME);

                if (isShowing()) {
                    dismiss();
                }
            }
        });
        mFri.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //  分享到微信
                share(Wechat.NAME);

                if (isShowing()) {
                    dismiss();
                }
            }
        });
        mCopy.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                //  复制链接
                copy(shareParams.getUrl(), context);

                if (isShowing()) {
                    dismiss();
                }
            }
        });

        listener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                DebugLog.d("分享请求发送成功");
                try {
                    JSONObject obj = new JSONObject(response);
                    String tips = obj.getString("tips");
                    if (!TextUtils.isEmpty(tips)) {
//						AndroidUtil.showToast(tips);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        };

        errorListener = new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                DebugLog.w("分享请求发送出错");
                error.printStackTrace();
            }
        };

    }

    public ShareParams getShareParams() {
        return shareParams;
    }

    /**
     * 分享
     *
     * @param name   分享到的平台名
     * @param tid    攻略的tid
     * @param status 分享的状态
     */
    public void share(String name, int tid, int status) {
        int type = getPlatformType(name);

        if (tid != 0) {
            MeiWuReq.share(tid, type, status, listener, errorListener);
        }
        currentType = type;

        Platform plat = null;
        plat = ShareSDK.getPlatform(context, name);
        plat.setPlatformActionListener(actionListener == null ? this
                : actionListener);
        plat.share(shareParams);
    }

    public void
    share(String name) {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        if (!userInfo.isLogin()) {
            context.startActivity(LoginActivity.newOtherIntent());
            return;
        }
        sharePre(name);
        share(name, tid, 0);
    }

    /**
     * 获取到平台所对应的type值,用于传递给服务器
     * <p/>
     * 1、朋友圈；2、微信；3、QQ空间；4、QQ好友；5、腾讯微博；6、新浪微博
     *
     * @param name 平台的名字
     * @return
     */
    public int getPlatformType(String name) {
        int type = 0;
        if (name.equals(WechatMoments.NAME)) {
            type = 1;
        } else if (name.equals(Wechat.NAME)) {
            type = 2;
        } else if (name.equals(QZone.NAME)) {
            type = 3;
        } else if (name.equals(QQ.NAME)) {
            type = 4;
        } else if (name.equals(SinaWeibo.NAME)) {
            type = 6;
        }
        return type;
    }

    private int isPoint;

    /**
     * 初始化分享参数
     *
     * @param shareData
     */
    public void initShareParams(ShareData shareData) {
        if (shareData != null) {
            mCopy.setClickable(true);
            mMomen.setClickable(true);
            mFri.setClickable(true);
            ShareParams sp = new ShareParams();
            // sp.setShareType(Platform.SHARE_TEXT);
            sp.setTitle(shareData.getTitle());
            sp.setText(shareData.getContent());
            sp.setImageUrl(shareData.getPic());
            sp.setUrl(shareData.getUrl());
            sp.setShareType(Platform.SHARE_WEBPAGE);
            shareParams = sp;

            //设置是否显示分享积分
            isPoint = shareData.getIsPoint();
            if (isPoint == 1) {
                mMomentPoint.setVisibility(View.VISIBLE);
                mFriendPoint.setVisibility(View.VISIBLE);
            } else {
                mMomentPoint.setVisibility(View.GONE);
                mFriendPoint.setVisibility(View.GONE);

            }

            if (!TextUtils.isEmpty(shareData.getShare_title())) {
                mHint.setText(shareData.getShare_title());
            }
            if (!TextUtils.isEmpty(shareData.getShare_tips())) {
                mHint1.setText(shareData.getShare_tips());
            }
        }
    }

    public static void copy(String content, Context context) {
        // 得到剪贴板管理器
        ClipboardManager cmb = (ClipboardManager) context
                .getSystemService(Context.CLIPBOARD_SERVICE);
        cmb.setText(content.trim());
        AndroidUtil.showToast(R.string.copy_complete);
    }

    @Override
    public void onCancel(Platform platform, int action) {
        // 取消
        Toast.makeText(context, "取消分享", Toast.LENGTH_SHORT).show();
        MeiWuReq.share(tid, currentType, -1, listener, errorListener);

    }

    @Override
    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> arg2) {
        // 成功
        Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
        MeiWuReq.share(tid, currentType, 1, listener, errorListener);

    }

    @Override
    public void onError(Platform platform, int action, Throwable t) {
        // 失敗
        Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
        MeiWuReq.share(tid, currentType, -99, listener, errorListener);
    }

    public PlatformActionListener getActionListener() {
        return actionListener;
    }

    public void setActionListener(PlatformActionListener actionListener) {
        this.actionListener = actionListener;
    }

}
