package com.meishai.ui.fragment.usercenter;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshScrollView;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.app.widget.CircleTextView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseFragment;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.home.HomePageActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.PullToRefreshHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 用户信息主页
 *
 * @author
 */
public class UserCenterFragment extends BaseFragment implements OnClickListener {

    // 应用推荐
    private String linkUrl = "http://www.meishai.com/applink";
    // 联系我们
//	private String contactUrl = "http://www.meishai.com/pop/contact";
    private String contactUrl = "http://www.meishai.com/index.php?m=app&c=pop&a=show&id=98";

    private RelativeLayout mLayInfo;
    private Button mBtnBack;
    private Button btn_home;
    // 我的试用
    private RelativeLayout mLayTrial;
    // 我的设置
    private LinearLayout mMySetting;
    // 我的资金
    private LinearLayout mLayMoney;
    // 我的积分
    private LinearLayout mLayPoint;
    // 我的信用
    private LinearLayout mLayCredit;
    // 我的消息
    private RelativeLayout lay_msg;
    // 试用提醒 联系我们
    private RelativeLayout lay_call_our;
    // 我的私信 推荐应用
    private RelativeLayout lay_rec_app;
    // 我的福利
    private RelativeLayout lay_welfare;
    // 我的收藏
    private LinearLayout lay_fav;
    private CircleNetWorkImageView mIbPic;
    private TextView userName;
    private TextView desc;
    private UserInfo userInfo;
    private CircleTextView tryalert_num;
    private CircleTextView msg_num;
    private CircleTextView inbox_num;


    private TextView vip_grade;
    private TextView point_num;
    private TextView meidou_num;
    private LinearLayout meishai_ll;
    private LinearLayout meiwu_ll;
    private LinearLayout strat_ll;
    private LinearLayout special_ll;
    private PullToRefreshScrollView mRefreshView;
    private CircleTextView fuli_num;
    private CircleTextView try_num;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.usercenter, null);
        initView(view);
        syncUserInfo();
        return view;
    }

    public void refreshUserInfo() {
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        syncUserInfo();
    }

    @Override
    public void onResume() {
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        ImageRequest imageRequest = new ImageRequest(userInfo.getAvatarUrl(),
                new Response.Listener<Bitmap>() {
                    @Override
                    public void onResponse(Bitmap response) {
                        mIbPic.setImageBitmap(response);
                    }
                }, 0, 0, Config.RGB_565, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                mIbPic.setImageResource(R.drawable.head_default);
            }
        });
        GlobalContext.getInstance().getRequestQueue().add(imageRequest);
        updateUI();
        super.onResume();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView(View view) {
        mRefreshView = (PullToRefreshScrollView) view.findViewById(R.id.my_refresh_scrollview);
        PullToRefreshHelper.initIndicatorStart(mRefreshView);
        PullToRefreshHelper.initIndicator(mRefreshView);
        mRefreshView.setMode(PullToRefreshBase.Mode.PULL_FROM_START);
        mRefreshView.setOnRefreshListener(new PullToRefreshBase.OnRefreshListener2<ScrollView>() {
            @Override
            public void onPullDownToRefresh(PullToRefreshBase<ScrollView> refreshView) {
                syncUserInfo();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<ScrollView> refreshView) {

            }
        });
        mBtnBack = (Button) view.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        btn_home = (Button) view.findViewById(R.id.btn_home);
        btn_home.setOnClickListener(this);
        mIbPic = (CircleNetWorkImageView) view.findViewById(R.id.ib_pic);

        mLayInfo = (RelativeLayout) view.findViewById(R.id.lay_info);
        mLayInfo.setOnClickListener(this);

        mLayTrial = (RelativeLayout) view.findViewById(R.id.lay_trial);
        mLayTrial.setOnClickListener(this);

        mMySetting = (LinearLayout) view.findViewById(R.id.my_setting);
        mMySetting.setOnClickListener(this);

        mLayCredit = (LinearLayout) view.findViewById(R.id.lay_credit);
        mLayCredit.setOnClickListener(this);

        mLayPoint = (LinearLayout) view.findViewById(R.id.lay_point);
        mLayPoint.setOnClickListener(this);

        mLayMoney = (LinearLayout) view.findViewById(R.id.lay_money);
        mLayMoney.setOnClickListener(this);

        lay_msg = (RelativeLayout) view.findViewById(R.id.lay_msg);
        lay_msg.setOnClickListener(this);

        lay_call_our = (RelativeLayout) view.findViewById(R.id.lay_call_our);
        lay_call_our.setOnClickListener(this);

        lay_rec_app = (RelativeLayout) view.findViewById(R.id.lay_rec_app);
        lay_rec_app.setOnClickListener(this);

        lay_welfare = (RelativeLayout) view.findViewById(R.id.lay_welfare);
        lay_welfare.setOnClickListener(this);

        lay_fav = (LinearLayout) view.findViewById(R.id.lay_fav);
        lay_fav.setOnClickListener(this);

        userName = (TextView) view.findViewById(R.id.nickname);
        desc = (TextView) view.findViewById(R.id.desc);
        tryalert_num = (CircleTextView) view.findViewById(R.id.tryalert_num);
        msg_num = (CircleTextView) view.findViewById(R.id.msg_num);
        inbox_num = (CircleTextView) view.findViewById(R.id.inbox_num);
        fuli_num = (CircleTextView) view.findViewById(R.id.fuli_num);
        try_num = (CircleTextView) view.findViewById(R.id.try_num);
        tryalert_num.setVisibility(View.GONE);
        msg_num.setVisibility(View.GONE);
        inbox_num.setVisibility(View.GONE);

        vip_grade = (TextView) view.findViewById(R.id.vip_grade);
        view.findViewById(R.id.vip_container).setOnClickListener(this);
        point_num = (TextView) view.findViewById(R.id.point_num);
        view.findViewById(R.id.point_container).setOnClickListener(this);
        meidou_num = (TextView) view.findViewById(R.id.meidou_num);
        view.findViewById(R.id.meidou_container).setOnClickListener(this);
        meishai_ll = (LinearLayout) view.findViewById(R.id.meishai_ll);
        meishai_ll.setOnClickListener(this);
        meiwu_ll = (LinearLayout) view.findViewById(R.id.meiwu_ll);
        meiwu_ll.setOnClickListener(this);
        strat_ll = (LinearLayout) view.findViewById(R.id.strat_ll);
        strat_ll.setOnClickListener(this);
        special_ll = (LinearLayout) view.findViewById(R.id.special_ll);
        special_ll.setOnClickListener(this);
        reloadUserInfo();
    }

    private void reloadUserInfo() {
        if (userInfo != null && userInfo.getUserNickName() != null) {
            userName.setText(userInfo.getUserNickName());
        }
        if (userInfo != null) {
            point_num.setText(userInfo.getPointNum() + "");
            meidou_num.setText(userInfo.getMeidou() + "");
            vip_grade.setText("V" + userInfo.getGroupid());
            desc.setText(userInfo.getText());
        }
        if (null != userInfo && StringUtil.isNotBlank(userInfo.getAvatarUrl())) {
            setHeadImage(userInfo.getAvatarUrl(), mIbPic);
        }
    }


    private void updateUI() {
        userInfo = MeiShaiSP.getInstance().getUserInfo();
//		if(userInfo.getTryAlertNum()>0){
//			tryalert_num.setBackgroundColor(mContext.getResources()
//					.getColor(R.color.red));
//			tryalert_num.setText(userInfo.getTryAlertNum()+"");
//			tryalert_num.setVisibility(View.VISIBLE);
//		}else{
//			tryalert_num.setVisibility(View.GONE);
//		}
        if (userInfo.getMsgNum() > 0) {
            msg_num.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.red));
            msg_num.setText(userInfo.getMsgNum() + "");
            msg_num.setVisibility(View.VISIBLE);
        } else {
            msg_num.setVisibility(View.GONE);
        }
        if (userInfo.getInboxNum() > 0) {
            inbox_num.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.red));
            inbox_num.setText(userInfo.getInboxNum() + "");
            inbox_num.setVisibility(View.VISIBLE);
        } else {
            inbox_num.setVisibility(View.GONE);
        }

        if (userInfo.getTryNum() > 0) {
            try_num.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.red));
            try_num.setText(userInfo.getTryNum() + "");
            try_num.setVisibility(View.VISIBLE);
        } else {
            try_num.setVisibility(View.GONE);
        }
        if (userInfo.getFuliNum() > 0) {
            fuli_num.setBackgroundColor(mContext.getResources()
                    .getColor(R.color.red));
            fuli_num.setText(userInfo.getFuliNum() + "");
            fuli_num.setVisibility(View.VISIBLE);
        } else {
            fuli_num.setVisibility(View.GONE);
        }
    }
//    private void setVipResId(ImageView vip_iv,int groupid) {
//        vip_iv.setVisibility(View.VISIBLE);
//        if (groupid == 1) {
//            vip_iv.setImageResource(R.drawable.v1);
//        } else if (groupid == 2) {
//            vip_iv.setImageResource(R.drawable.v2);
//        } else if (groupid == 3) {
//            vip_iv.setImageResource(R.drawable.v3);
//        } else if (groupid == 4) {
//            vip_iv.setImageResource(R.drawable.v4);
//        } else if (groupid == 5) {
//            vip_iv.setImageResource(R.drawable.v5);
//        } else if (groupid == 5) {
//            vip_iv.setImageResource(R.drawable.v6);
//        } else if (groupid == 7) {
//            vip_iv.setImageResource(R.drawable.v7);
//        } else {
//            vip_iv.setVisibility(View.INVISIBLE);
//        }
//    }

    /**
     * <p>
     * Title: onClick
     * </p>
     * <p>
     * Description:
     * </p>
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                backToMain();
                break;
            case R.id.btn_home:
                startActivity(HomePageActivity.newIntent(userInfo.getUserID()));
                break;
            case R.id.lay_info:
                startActivity(UserInfoActivity.newIntent());
                break;
            case R.id.lay_trial:
                startActivity(UserTrialActivity.newIntent());
                break;

            case R.id.my_setting:
                startActivity(UserSettingActivity.newIntent());
                break;
            case R.id.lay_point:
                startActivity(UserPointActivity.newIntent());
                break;
            case R.id.lay_credit:
                startActivity(UserCreditActivity.newIntent());
                break;
            case R.id.lay_money:
                startActivity(UserMoneyActivity.newIntent());
                break;
            case R.id.lay_msg://我的消息
//			startActivity(UserMsgActivity.newIntent());
                startActivity(UserMyMsgActivity.newIntent());
                break;
            case R.id.lay_call_our://联系我们
//			startActivity(UserTrialRemindActivity.newIntent());
                mContext.startActivity(MeishaiWebviewActivity.newIntent(contactUrl,
                        mContext.getString(R.string.txt_contact)));
                break;
            case R.id.lay_rec_app://推荐应用
//			startActivity(UserPriMsgActivity.newIntent());
                startActivity(MeishaiWebviewActivity.newIntent(linkUrl,
                        mContext.getString(R.string.txt_applink)));

                break;
            case R.id.lay_welfare:
                startActivity(UserWelfareActivity.newIntent());
                break;
            case R.id.strat_ll://我的收藏-攻略 0
                startActivity(CollectedActivity.newIntent(0));
                break;
//        case R.id.special_ll://我的收藏-专场 1
//            startActivity(CollectedActivity.newIntent(1));
//			break;
            case R.id.meiwu_ll://我的收藏-美物 2
                startActivity(CollectedActivity.newIntent(1));
                break;
            case R.id.meishai_ll://我的收藏-美晒 3
                startActivity(CollectedActivity.newIntent(2));
                break;
            case R.id.vip_container:
                String url = "http://www.meishai.com/index.php?m=app&c=member&a=level&code=" + userInfo.getCode();
                startActivity(MeishaiWebviewActivity.newIntent(url));
                break;
            case R.id.point_container:
                String url1 = "http://www.meishai.com/index.php?m=app&c=member&a=point&code=" + userInfo.getCode();
                startActivity(MeishaiWebviewActivity.newIntent(url1));
                break;
            case R.id.meidou_container:
                String url2 = "http://www.meishai.com/index.php?m=app&c=member&a=meidou&code=" + userInfo.getCode();
                startActivity(MeishaiWebviewActivity.newIntent(url2));
                break;
            case R.id.lay_fav:
                startActivity(UserFavActivity.newIntent());
                break;
            default:
                break;
        }
    }

    private void syncUserInfo() {
        try {
            DebugLog.d("用户 ID:" + userInfo.getUserID());
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("index");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", userInfo.getUserID());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d("个人信息:" + url);
                showProgress("", getString(R.string.network_wait));
                sendRegPhoneReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRegPhoneReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                mRefreshView.onRefreshComplete();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    reloadUserInfo();
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                mRefreshView.onRefreshComplete();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        }));
    }

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            if (successID.equals("1")) {
                org.json.JSONObject dataObj = jsonObj.getJSONObject("data");

                userInfo.setUserID(dataObj.getString("userid").trim());
                userInfo.setAvatarUrl(dataObj.getString("avatar").trim());
                userInfo.setUserNickName(dataObj.getString("username").trim());
                userInfo.setGroupid(dataObj.getInt("groupid"));
                int isdaren = dataObj.getInt("isdaren");
                userInfo.setDaren(isdaren == 1 ? true : false);//isdaren	1
                userInfo.setTryNum(dataObj.getInt("try_num"));
                userInfo.setPostNum(dataObj.getInt("post_num"));
                userInfo.setFavNum(dataObj.getInt("fav_num"));
                userInfo.setPointNum(dataObj.getInt("point_num"));
                userInfo.setCreditNum(dataObj.getInt("credit_num"));
                userInfo.setTryAlertNum(dataObj.getInt("tryalert_num"));
                userInfo.setMsgNum(dataObj.getInt("msg_num"));
                userInfo.setInboxNum(dataObj.getInt("inbox_num"));
                userInfo.setCode(dataObj.getString("code"));
                userInfo.setText(dataObj.getString("text"));
                userInfo.setMeidou(dataObj.getInt("meidou"));
                userInfo.setFuliNum(dataObj.getInt("fuli_num"));
                DebugLog.d("UserID:" + userInfo.getUserID());
                DebugLog.d("Acatar:" + userInfo.getAvatarUrl());
                DebugLog.d("UserName:" + userInfo.getUserNickName());
                MeiShaiSP.getInstance().setUserInfo(userInfo);
                updateUI();
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            String s = e.toString();
            e.printStackTrace();
            return false;
        }

    }

    protected void backToMain() {
        DebugLog.d("*************backToMain**************");
        doBackClick();
    }
}
