package com.meishai.ui.popup;

import java.util.HashMap;

import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.PopupWindow;
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
import com.meishai.entiy.ShareData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.ui.fragment.meiwu.req.MeiWuReq;
import com.meishai.ui.popup.adapter.ShareAdapter;
import com.meishai.util.DebugLog;

/**
 * 自定义分享面板
 *
 * @author sh
 */
public class SharePopupWindow extends PopupWindow {
    private View view;
    private Context context;
    private PlatformActionListener platformActionListener;
    private ShareParams shareParams;
    private int tid;
    private int currentType;
    private Listener<String> listener;
    private ErrorListener errorListener;

    public SharePopupWindow(Context cx) {
        this.context = cx;
        view = LayoutInflater.from(context)
                .inflate(R.layout.share_layout, null);
        GridView gridView = (GridView) view.findViewById(R.id.share_gridview);
        ShareAdapter adapter = new ShareAdapter(context);
        gridView.setAdapter(adapter);
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
        gridView.setOnItemClickListener(new ShareItemClickListener(this));

        platformActionListener = new PlatformActionListener() {

            @Override
            public void onCancel(Platform platform, int action) {
                // 取消
                Toast.makeText(context, "取消分享", Toast.LENGTH_SHORT).show();
                if (tid != 0) {
                    MeiWuReq.share(tid, currentType, -1, listener, errorListener);
                }

            }

            @Override
            public void onComplete(Platform platform, int action, HashMap<String, Object> arg2) {
                // 成功
                Toast.makeText(context, "分享成功", Toast.LENGTH_SHORT).show();
                if (tid != 0) {
                    MeiWuReq.share(tid, currentType, 1, listener, errorListener);
                }

            }

            @Override
            public void onError(Platform platform, int action, Throwable t) {
                // 失敗
                Toast.makeText(context, "分享失败", Toast.LENGTH_SHORT).show();
                if (tid != 0) {
                    MeiWuReq.share(tid, currentType, -99, listener, errorListener);
                }
            }
        };

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

    public void setTid(int tid) {
        this.tid = tid;
    }

    public PlatformActionListener getPlatformActionListener() {
        return platformActionListener;
    }

    public void setPlatformActionListener(
            PlatformActionListener platformActionListener) {
        this.platformActionListener = platformActionListener;
    }

    public void showShareWindow() {

    }

    private class ShareItemClickListener implements OnItemClickListener {
        private PopupWindow pop;


        public ShareItemClickListener(PopupWindow pop) {
            this.pop = pop;
        }

        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position,
                                long id) {
            //用于发送给服务器的分享类型的标识
            currentType = getPlatformType(getPlatform(position));
            //发送用户点击分享的请求
            if (tid != 0) {
                MeiWuReq.share(tid, currentType, 0, listener, errorListener);
            }
            //启动分享
            share(position);
            pop.dismiss();
        }
    }

    /**
     * 分享
     *
     * @param position
     */
    private void share(int position) {
        if (position == 2) {
            qq();
        } else if (position == 4) {
            qzone();
        } else {
            Platform plat = null;
            plat = ShareSDK.getPlatform(context, getPlatform(position));
            if (platformActionListener != null) {
                plat.setPlatformActionListener(platformActionListener);
            }
            plat.share(shareParams);
        }
    }

    /**
     * 初始化分享参数
     *
     * @param shareData
     */
    public void initShareParams(ShareData shareData) {
        if (shareData != null) {
            ShareParams sp = new ShareParams();
            sp.setShareType(Platform.SHARE_TEXT);
            sp.setShareType(Platform.SHARE_WEBPAGE);
            sp.setTitle(shareData.getTitle());
            sp.setText(shareData.getContent());
            sp.setUrl(shareData.getUrl());
            sp.setImageUrl(shareData.getPic());
            shareParams = sp;
        }
    }


    /**
     * 获取平台
     *
     * @param position
     * @return
     */
    private String getPlatform(int position) {
        String platform = "";
        switch (position) {
            case 0:
                platform = Wechat.NAME;
                break;
            case 1:
                platform = WechatMoments.NAME;
                break;
            case 2:
                platform = QQ.NAME;
                break;
            case 3:
                platform = SinaWeibo.NAME;
                break;
            case 4:
                platform = QZone.NAME;
                break;
        }
        return platform;
    }

    /**
     * 分享到QQ空间
     */
    private void qzone() {
        ShareParams sp = new ShareParams();
        sp.setTitle(shareParams.getTitle());
        sp.setTitleUrl(shareParams.getUrl()); // 标题的超链接
        sp.setText(shareParams.getText());
        sp.setImageUrl(shareParams.getImageUrl());
        sp.setComment("我对此分享内容的评论");
        sp.setSite(shareParams.getTitle());
        sp.setSiteUrl(shareParams.getUrl());
        Platform qzone = ShareSDK.getPlatform(context, QZone.NAME);
        qzone.setPlatformActionListener(platformActionListener); // 设置分享事件回调 //
        // 执行图文分享
        qzone.share(sp);
    }

    private void qq() {
        ShareParams sp = new ShareParams();
        sp.setTitle(shareParams.getTitle());
        sp.setTitleUrl(shareParams.getUrl()); // 标题的超链接
        sp.setText(shareParams.getText());
        sp.setImageUrl(shareParams.getImageUrl());
        sp.setComment("我对此分享内容的评论");
        sp.setSite(shareParams.getTitle());
        sp.setSiteUrl(shareParams.getUrl());
        Platform qq = ShareSDK.getPlatform(context, QQ.NAME);
        qq.setPlatformActionListener(platformActionListener);
        qq.share(sp);
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

}
