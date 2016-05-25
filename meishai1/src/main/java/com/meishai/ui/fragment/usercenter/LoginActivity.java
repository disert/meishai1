package com.meishai.ui.fragment.usercenter;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Handler.Callback;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.PlatformDb;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.sina.weibo.SinaWeibo;
import cn.sharesdk.tencent.qq.QQ;
import cn.sharesdk.wechat.friends.Wechat;

import com.google.gson.reflect.TypeToken;
import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.OpenLogin;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.MeishaiWebviewActivity;
import com.meishai.ui.fragment.usercenter.req.LoginReq;
import com.meishai.ui.fragment.usercenter.req.LoginReq.OPEN_SOURCE;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.nimbusds.jose.JOSEException;

/**
 * 登录login
 *
 * @author sh
 */
public class LoginActivity extends BaseActivity implements OnClickListener,
        Callback, PlatformActionListener {

    private Activity mContext = LoginActivity.this;
    private static final String TAG = LoginActivity.class.getName();
    private Handler handler;

    private UserInfo userInfo;
    private EditText mobileEt, pwdEt;

    private Button backMain;
    private ImageButton mBtnLoginQq;
    private ImageButton mBtnLoginWeixin;
    private ImageButton mBtnLoginSina;
    //忘记密码
    private String forgetUrl = "http://www.meishai.com/forget";
    private TextView tv_forget;

    /**
     * 默认
     */
    private static final String SOURCE_DEF = "dscource";
    /**
     * 加载数据时 ，未登录。使用的登录入口
     */
    private static final String SOURCE_OT = "oscource";

    private String dSource = SOURCE_DEF;

    /**
     * 加载数据时 ，未登录。使用的登录入口
     *
     * @return
     */
    public static Intent newOtherIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                LoginActivity.class);
        intent.putExtra("scource", SOURCE_OT);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    /**
     * 默认登录入口
     *
     * @return
     */
    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                LoginActivity.class);
        intent.putExtra("scource", SOURCE_DEF);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        userInfo = MeiShaiSP.getInstance().getUserInfo();
        dSource = getIntent().getExtras().getString("scource");
        setContentView(R.layout.user_login);
        handler = new Handler(this);
        ShareSDK.initSDK(this);
        initView();
        // initUMSocialService();
        // initSSO();
    }

    // private void initUMSocialService() {
    // mController = UMServiceFactory.getUMSocialService("com.umeng.login");
    // }
    //
    // private void initSSO() {
    // // 设置新浪SSO handler
    // mController.getConfig().setSsoHandler(new SinaSsoHandler());
    // UMQQSsoHandler qqSsoHandler = new UMQQSsoHandler(mContext,
    // ConstantShare.QQ_APP_ID, ConstantShare.QQ_APP_KEY);
    // qqSsoHandler.addToSocialSDK();
    //
    // // 添加微信平台
    // UMWXHandler wxHandler = new UMWXHandler(mContext,
    // ConstantShare.WX_APP_ID, ConstantShare.WX_APP_KEY);
    // wxHandler.addToSocialSDK();
    // wxHandler.setRefreshTokenAvailable(false);
    // }

    private void initView() {
        backMain = (Button) this.findViewById(R.id.backMain);
        backMain.setOnClickListener(this);
        TextView regist = (TextView) this.findViewById(R.id.regist);
        regist.setOnClickListener(this);
        Button btnLogin = (Button) this.findViewById(R.id.btn_login);
        btnLogin.setOnClickListener(this);
        mBtnLoginQq = (ImageButton) this.findViewById(R.id.btn_login_qq);
        mBtnLoginQq.setOnClickListener(this);
        mBtnLoginWeixin = (ImageButton) this
                .findViewById(R.id.btn_login_weixin);
        mBtnLoginWeixin.setOnClickListener(this);
        mBtnLoginSina = (ImageButton) this.findViewById(R.id.btn_login_sina);
        mBtnLoginSina.setOnClickListener(this);
        mobileEt = (EditText) this.findViewById(R.id.moblie);
        pwdEt = (EditText) this.findViewById(R.id.pwd);

        tv_forget = (TextView) this.findViewById(R.id.tv_forget);
        tv_forget.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.backMain:
                finish();
                break;
            case R.id.regist:
//			startActivity(RegistActivity.newIntent());
                startActivity(RegistSceondSetpActivity.newIntent());
                finish();
                break;
            case R.id.btn_login:
                meishaiLogin();
                break;
            case R.id.btn_login_qq:
                // QQ
                Platform qq = ShareSDK.getPlatform(QQ.NAME);
                authorize(qq);
                break;
            case R.id.btn_login_weixin:
                Platform wechat = ShareSDK.getPlatform(Wechat.NAME);
                authorize(wechat);
                break;
            case R.id.btn_login_sina:
                // 新浪微博
                Platform sina = ShareSDK.getPlatform(SinaWeibo.NAME);
//			sina.SSOSetting(true);
                authorize(sina);
                break;
            case R.id.tv_forget:
                startActivity(MeishaiWebviewActivity.newIntent(forgetUrl,
                        mContext.getString(R.string.txt_forget_pass)));
                break;
        }
    }

    private void authorize(Platform plat) {
        if (plat == null) return;
        if (plat.isValid()) {
            String userId = plat.getDb().getUserId();
            if (userId != null) {
                OpenLogin openLogin = new OpenLogin();
                openLogin.setConnectid(plat.getDb().getUserId());
                openLogin.setToken(plat.getDb().getToken());
                openLogin.setAvatar(plat.getDb().getUserIcon());
                openLogin.setNick(plat.getDb().getUserName());
                if (plat.getName().equals(QQ.NAME)) {
                    login(OPEN_SOURCE.qq, openLogin);
                } else if (plat.getName().equals(Wechat.NAME)) {
                    openLogin.setUnionid(plat.getDb().get("unionid"));

                    login(OPEN_SOURCE.weixin, openLogin);
                } else if (plat.getName().equals(SinaWeibo.NAME)) {
                    login(OPEN_SOURCE.sina, openLogin);
                }
                return;
            }
        }

        plat.setPlatformActionListener(this);
        plat.SSOSetting(false);
        plat.showUser(null);
    }

    private void meishaiLogin() {
        if (checkInputData()) {
            sendMeishaiLoginReq();
        }
    }

    private void sendMeishaiLoginReq() {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("login");
            Map<String, String> map = new HashMap<String, String>();
            map.put("username", userInfo.getUserMobile());
            map.put("password", userInfo.getUserPWD());
            reqData.setData(map);
            String jwtData = reqData.toReqString();
            if (jwtData != null && jwtData.length() > 0) {
                String url = getString(R.string.base_url) + jwtData;
                DebugLog.d(url);
                showProgress("", getString(R.string.network_wait));
                sendRegReq(url);
            } else {
                DebugLog.d("Get JWT JSONData Error.");
            }
        } catch (JOSEException e) {
            e.printStackTrace();
        }
    }

    private void sendRegReq(String url) {
        getRequestQueue().add(new StringRequest(url, new Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideProgress();
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    storageUserInfo();
                    if (dSource.equals(SOURCE_DEF)) {
                        loginSuccess();
                    } else if (dSource.equals(SOURCE_OT)) {
                        finish();
                    }
                }
            }
        }, new ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                hideProgress();
                AndroidUtil.showToast(R.string.reqFailed);
            }
        }));
    }

    // 登录成功发送广播
    private void loginSuccess() {
        Intent mIntent = new Intent(ConstantSet.ACTION_LOGIN_SUCCESS);
        sendBroadcast(mIntent);
        finish();
    }

    private void storageUserInfo() {
        MeiShaiSP.getInstance().setUserInfo(userInfo);
    }

    private boolean checkResult(String json) {
        try {
            System.out.println(json);
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            String tips = jsonObj.getString("tips").trim();
            AndroidUtil.showToast(tips);
            if (successID.equals("1")) {
                userInfo.setUserID(jsonObj.getJSONObject("data")
                        .getString("userid").trim());
                DebugLog.d("UserID=" + userInfo.getUserID());
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }

    private boolean checkInputData() {
        String mobile = mobileEt.getText().toString().trim();
        String pwd = pwdEt.getText().toString().trim();

        if (!StringUtil.isBlank(mobile)) {
            userInfo.setUserMobile(mobile);
        } else {
            mobileEt.setText("");
            AndroidUtil.showToast(R.string.invalid_phone_numb);
            return false;
        }

        // if (StringUtil.checkUserNamePwd(mobile, 6)) {
        // userInfo.setUserMobile(mobile);
        // } else {
        // mobileEt.setText("");
        // AndroidUtil.showToast(R.string.invalid_phone_numb);
        // return false;
        // }

        if (StringUtil.isNotBlank(pwd)) {
            userInfo.setUserPWD(pwd);
        } else {
            pwdEt.setText("");
            AndroidUtil.showToast(R.string.pwd_not_null);
            return false;
        }
        return true;
    }

    // private void doLogin(SHARE_MEDIA media) {
    // mController.doOauthVerify(mContext, media, new UMAuthListener() {
    // @Override
    // public void onStart(SHARE_MEDIA platform) {
    // AndroidUtil.showToast("授权开始");
    // }
    //
    // @Override
    // public void onError(SocializeException e, SHARE_MEDIA platform) {
    // e.printStackTrace();
    // AndroidUtil.showToast("授权错误");
    // }
    //
    // @Override
    // public void onComplete(Bundle value, SHARE_MEDIA platform) {
    // Set<String> keys = value.keySet();
    // for (String key : keys) {
    // System.out.println("key:" + key + ",value:"
    // + value.getString(key));
    // }
    // AndroidUtil.showToast("授权完成");
    // if (SHARE_MEDIA.SINA.name().equals(platform.name())) {
    // if (value != null
    // && !TextUtils.isEmpty(value.getString("uid"))) {
    // // 获取相关授权信息
    // getPlatformInfo(platform);
    // }
    // } else {
    // // 获取相关授权信息
    // getPlatformInfo(platform);
    // }
    // }
    //
    // @Override
    // public void onCancel(SHARE_MEDIA platform) {
    // AndroidUtil.showToast("授权取消");
    // }
    // });
    // }
    //
    // private void getPlatformInfo(final SHARE_MEDIA data) {
    // // final OpenLogin openLogin = new OpenLogin();
    // // switch (data) {
    // // case QQ:
    // // String openid = value.getString("openid");
    // // openLogin.setConnectid(openid);
    // // String access_token = value.getString("access_token");
    // // openLogin.setToken(access_token);
    // // break;
    // // case SINA:
    // // break;
    // // case WEIXIN:
    // // break;
    // // default:
    // // break;
    // // }
    // mController.getPlatformInfo(mContext, data, new UMDataListener() {
    // @Override
    // public void onStart() {
    // AndroidUtil.showToast("获取平台数据开始...");
    // }
    //
    // @Override
    // public void onComplete(int status, Map<String, Object> info) {
    // StringBuilder sb = new StringBuilder();
    // Set<String> keys = info.keySet();
    // for (String key : keys) {
    // sb.append(key + "=" + info.get(key).toString() + "\r\n");
    // }
    // AndroidUtil.showToast(sb.toString());
    // OpenLogin openLogin = new OpenLogin();
    // if (status == 200 && info != null) {
    // switch (data) {
    // case QQ:
    // String openid = OauthHelper.getUsid(mContext,
    // SHARE_MEDIA.QQ);
    // String access_token = OauthHelper.getAccessToken(
    // mContext, SHARE_MEDIA.QQ)[0];
    // openLogin.setConnectid(openid);
    // openLogin.setToken(access_token);
    // // 昵称
    // String screen_name = (String) info.get("screen_name");
    // openLogin.setNick(screen_name);
    // // 头像
    // String profile_image_url = (String) info
    // .get("profile_image_url");
    // openLogin.setAvatar(profile_image_url);
    // login(OPEN_SOURCE.qq, openLogin);
    // break;
    // case SINA:
    // break;
    // case WEIXIN:
    // break;
    // default:
    // break;
    // }
    // } else {
    // DebugLog.d("发生错误：" + status);
    // }
    // }
    // });
    // }

    private void login(OPEN_SOURCE source, OpenLogin openLogin) {

        LoginReq.openLogin(mContext, source, openLogin.toMap(),
                new Listener<RespData>() {

                    @Override
                    public void onResponse(RespData response) {
                        if (response.isSuccess()) {
                            Type type = new TypeToken<Map<String, String>>() {
                            }.getType();
                            Map<String, String> resultMap = GsonHelper
                                    .parseObject(GsonHelper.toJson(response
                                            .getData()), type);
                            String userId = resultMap.get("userid");
                            userInfo.setUserID(userId);
                            storageUserInfo();
                            // gotoFragment(UserCenterFragment.class,
                            // UserCenterFragment.class.getName(), null);
                            if (dSource.equals(SOURCE_DEF)) {
                                loginSuccess();
                            } else if (dSource.equals(SOURCE_OT)) {
//								finish();
                                loginSuccess();
                            }
                        }
                        AndroidUtil.showToast(response.getTips());


                    }
                }, new ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        AndroidUtil.showToast(mContext
                                .getString(R.string.reqFailed));
                    }
                });
    }

    /**
     * 当 SSO 授权 Activity 退出时，该函数被调用。
     *
     * @see {@link Activity#onActivityResult}
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private static final int MSG_AUTH_CANCEL = 2;
    private static final int MSG_AUTH_ERROR = 3;
    private static final int MSG_AUTH_COMPLETE = 4;

    public void onComplete(Platform platform, int action,
                           HashMap<String, Object> res) {
        if (action == Platform.ACTION_USER_INFOR) {
            Message msg = new Message();
            msg.what = MSG_AUTH_COMPLETE;
            msg.obj = new Object[]{platform.getName(), res};
            handler.sendMessage(msg);
        }
    }

    public void onError(Platform platform, int action, Throwable t) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_ERROR);
        }
        t.printStackTrace();
    }

    public void onCancel(Platform platform, int action) {
        if (action == Platform.ACTION_USER_INFOR) {
            handler.sendEmptyMessage(MSG_AUTH_CANCEL);
        }
    }

    public boolean handleMessage(Message msg) {
        switch (msg.what) {
            case MSG_AUTH_CANCEL:
                // 取消授权
                AndroidUtil.showToast("取消授权");
                break;
            case MSG_AUTH_ERROR:
                // 授权失败
                AndroidUtil.showToast("授权失败");
                break;
            case MSG_AUTH_COMPLETE:
                // 授权成功
                AndroidUtil.showToast("授权成功");
                Object[] objs = (Object[]) msg.obj;
                String platform = (String) objs[0];
                // HashMap<String, Object> res = (HashMap<String, Object>) objs[1];
                Platform loginPlat = ShareSDK.getPlatform(platform);

                OpenLogin openLogin = new OpenLogin();
                openLogin.setConnectid(loginPlat.getDb().getUserId());
                openLogin.setToken(loginPlat.getDb().getToken());
                openLogin.setAvatar(loginPlat.getDb().getUserIcon());
                openLogin.setNick(loginPlat.getDb().getUserName());
                if (platform.equals(QQ.NAME)) {
                    String iconQzone = loginPlat.getDb().get("iconQzone");
                    if (!TextUtils.isEmpty(iconQzone)) {
                        openLogin.setAvatar(iconQzone);
                    }
                    login(OPEN_SOURCE.qq, openLogin);
                } else if (platform.equals(Wechat.NAME)) {
                    openLogin.setUnionid(loginPlat.getDb().get("unionid"));
//				Log.e("LoginActivity", openLogin.toString());
                    login(OPEN_SOURCE.weixin, openLogin);
                } else if (platform.equals(SinaWeibo.NAME)) {
                    login(OPEN_SOURCE.sina, openLogin);
                }
                break;
        }
        return false;
    }
}