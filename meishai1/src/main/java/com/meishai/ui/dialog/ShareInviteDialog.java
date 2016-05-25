package com.meishai.ui.dialog;

import java.util.HashMap;
import java.util.Map;

import android.app.Dialog;
import android.content.Context;
import android.os.Message;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
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

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.InviteData;
import com.meishai.entiy.ShareData;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.ToastUtlis;
import com.nimbusds.jose.JOSEException;

/**
 * 邀请好友的分享弹窗
 *
 * @author yl
 */
public class ShareInviteDialog extends Dialog implements
        PlatformActionListener {
    private Context context;
    private ShareParams shareParams;
    private LinearLayout mMoments;
    private LinearLayout mSinal;
    private LinearLayout mQzone;
    private LinearLayout mQQ;
    private LinearLayout mWechat;
    private Button mCancel;
    private Listener<String> listener;
    private ErrorListener errorListener;
    private int tid = 0;
    private int currentType;

    private PlatformActionListener actionListener;


    public ShareInviteDialog(Context cx) {
        super(cx, R.style.dialog_transparent);
        this.context = cx;
        setContentView(R.layout.dialog_share_center);
        mMoments = (LinearLayout) findViewById(R.id.share_center_moments);
        mSinal = (LinearLayout) findViewById(R.id.share_center_sinal);
        mQzone = (LinearLayout) findViewById(R.id.share_center_Qzone);
        mQQ = (LinearLayout) findViewById(R.id.share_center_qq);
        mWechat = (LinearLayout) findViewById(R.id.share_center_wechat);
        mCancel = (Button) findViewById(R.id.cancel);


        initListener();

    }

    public void sendShareMsg() {
        UserInfo userInfo = MeiShaiSP.getInstance().getUserInfo();
        ReqData reqData = new ReqData();
        reqData.setC("public");
        reqData.setA("invite");
        Map<String, String> data = new HashMap<String, String>();
        data.put("userid", userInfo.getUserID());
        reqData.setData(data);

        try {
            String url = GlobalContext.getInstance().getString(R.string.base_url) + reqData.toReqString();
            DebugLog.d(url);
            GlobalContext.getInstance().getRequestQueue().add(
                    new StringRequest(url, new Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            DebugLog.d("分享好友:" + response);
                            if (TextUtils.isEmpty(response)) return;

                            InviteData inviteData = GsonHelper.parseObject(response, InviteData.class);
                            if (inviteData != null && inviteData.success == 1) {
                                initShareParams(inviteData.data);
                                show();
                            }
                        }
                    }, new ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            ToastUtlis.showToast(context, "请求分享数据失败!");
                        }
                    }));

        } catch (JOSEException e) {
            e.printStackTrace();

        }
    }


    /**
     * 分享前干的事儿在这里面做,可以在new对象的时候使用子类来实现,分享后要做的可以通过设置actionLinstener来实现
     */
    public void sharePre(String name) {

    }

    private void initListener() {
        mCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                hide();
            }
        });
        mMoments.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //分享到朋友圈
                share(WechatMoments.NAME);

                hide();
            }
        });
        mSinal.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  分享到新浪微博
                share(SinaWeibo.NAME);

                hide();
            }
        });
        mQzone.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  分享到qq空间
                share(QZone.NAME);
                hide();
            }
        });
        mQQ.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  分享到qq好友
                share(QQ.NAME);
                hide();
            }
        });
        mWechat.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //  分享到微信好友
                share(Wechat.NAME);
                hide();
            }
        });


        listener = new Listener<String>() {

            @Override
            public void onResponse(String response) {
                DebugLog.d("分享请求发送成功");
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
     * @param name 分享到的平台名
     */
    public void share(String name) {
        int type = getPlatformType(name);
        currentType = type;
        MeiWuReq.share(tid, currentType, 0, listener, errorListener);

        Platform plat = null;
        plat = ShareSDK.getPlatform(context, name);
        plat.setPlatformActionListener(actionListener == null ? this
                : actionListener);
        plat.share(shareParams);
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

    /**
     * 初始化分享参数
     *
     * @param shareData
     */
    public void initShareParams(ShareData shareData) {
        if (shareData != null) {
            ShareParams sp = new ShareParams();
            // sp.setShareType(Platform.SHARE_TEXT);
            sp.setTitle(shareData.getTitle());
            sp.setText(shareData.getContent());
            sp.setImageUrl(shareData.getPic());
            sp.setUrl(shareData.getUrl());
            sp.setShareType(Platform.SHARE_WEBPAGE);
            shareParams = sp;
        }
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
