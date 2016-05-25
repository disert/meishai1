package com.meishai.app.widget.layout;

import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.R;
import com.meishai.app.widget.CircleImageView;
import com.meishai.app.widget.CustomProgress;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.HomePageDatas.UserInfo;
import com.meishai.entiy.HomepageUser;
import com.meishai.entiy.Master;
import com.meishai.net.RespData;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.toolbox.ImageLoader;
import com.meishai.net.volley.toolbox.ListImageListener;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.SendPriMsgDialog;
import com.meishai.ui.fragment.common.req.PublicReq;
import com.meishai.util.AndroidUtil;

/**
 * 个人主页头部所对应的view
 *
 * @author Administrator yl
 */
public class HomePageHeader extends LinearLayout {

    private ImageLoader mImageLoader;
    private UserInfo mData;
    private CustomProgress mProgressDialog = null;
    private SendPriMsgDialog msgDialog;

    // 个人信息部分
    private ImageButton message;
    private CircleImageView avatar;
    private TextView username, attNum, fansNum;
    private Button btnAtt;
    private TextView location;
    private TextView desc;
    private Context mContext;
    private RelativeLayout root;

    public HomePageHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
        initView(context);
        initDialog();
        initListener();
    }


    public HomePageHeader(Context context) {
        this(context, null);
    }

    private void initDialog() {
        msgDialog = new SendPriMsgDialog(mContext);
    }

    private void initListener() {
        message.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                msgDialog.setTuserid(mData.userid + "");
                msgDialog.show();
            }
        });

        btnAtt.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                String attState = btnAtt.getText().toString().trim();
                HomepageUser user = new HomepageUser();
                user.setUserid(mData.userid);
                if (mData.isattention == Master.HAS_ATENTION) {
                    delfriend(user);
                } else {
                    addfriend(user);
                }
            }
        });
    }


    private void initView(Context context) {
        View.inflate(mContext, R.layout.home_page_header, this);
        root = (RelativeLayout) this.findViewById(R.id.header_root);
        // 个人信息部分
        message = (ImageButton) this.findViewById(R.id.image_message);
        avatar = (CircleImageView) this.findViewById(R.id.avatar);
        username = (TextView) this.findViewById(R.id.username);
        attNum = (TextView) this.findViewById(R.id.att_num);
        fansNum = (TextView) this.findViewById(R.id.fans_num);
        btnAtt = (Button) this.findViewById(R.id.btn_att);
        location = (TextView) this.findViewById(R.id.location);
        desc = (TextView) this.findViewById(R.id.desc);
    }

    /**
     * 为该view设置数据
     *
     * @param userinfo
     * @param imageLoader
     */
    public void setData(UserInfo userinfo, ImageLoader imageLoader) {
        mData = userinfo;
        mImageLoader = imageLoader;
        updateUI();
    }

    /**
     * 当调用setData方法为view设置数据之后调用它来更新界面
     */
    protected void updateUI() {

        avatar.setTag(mData.avatar);
        ListImageListener listener = new ListImageListener(avatar,
                R.drawable.head_default, R.drawable.head_default,
                mData.avatar);
        mImageLoader.get(mData.avatar, listener);

        username.setText(mData.username);
        attNum.setText(mData.follow_num + "");
        fansNum.setText(mData.fans_num + "");

        // 关注按钮
        setAttention();

        location.setText(mData.areaname);
        desc.setText(mData.intro);

    }

    private void setAttention() {
        if (mData.isattention == Master.HAS_ATENTION) {
            btnAtt.setText(mContext.getString(R.string.del_attention));
            btnAtt.setBackgroundResource(R.drawable.btn_gray_selector);
        } else {
            btnAtt.setText(mContext.getString(R.string.txt_attention));
            btnAtt.setBackgroundResource(R.drawable.btn_sign_point_selector);
        }

    }

    /**
     * 取消关注
     *
     * @param user
     */
    private void delfriend(final HomepageUser user) {
        if (null == user) {
            return;
        }
        String message = mContext.getString(R.string.can_att_wait);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }

        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", String.valueOf(user.getUserid()));
        PublicReq.delfriend(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    mData.isattention = Master.NO_ATENTION;
                    setAttention();
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }

    /**
     * 添加关注
     *
     * @param user
     */
    private void addfriend(final HomepageUser user) {
        if (null == user) {
            return;
        }
        String message = mContext.getString(R.string.add_att_wait);
        if (null == mProgressDialog) {
            mProgressDialog = CustomProgress
                    .show(mContext, message, true, null);
        } else {
            mProgressDialog.setMessage(message);
            mProgressDialog.show();
        }
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        data.put("fuserid", String.valueOf(user.getUserid()));
        PublicReq.addfriend(mContext, data, new Listener<RespData>() {

            @Override
            public void onResponse(RespData response) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                if (response.isSuccess()) {
                    mData.isattention = Master.HAS_ATENTION;
                    setAttention();
                } else {
                    AndroidUtil.showToast(response.getTips());
                }
            }

        }, new ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                if (null != mProgressDialog) {
                    mProgressDialog.hide();
                }
                AndroidUtil.showToast(mContext.getString(R.string.reqFailed));
            }
        });
    }
}
