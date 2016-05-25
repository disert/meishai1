package com.meishai.ui.fragment.usercenter;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.meishai.GlobalContext;
import com.meishai.R;
import com.meishai.app.util.StringUtil;
import com.meishai.app.widget.CircleNetWorkImageView;
import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.UploadHead;
import com.meishai.entiy.UserInfo;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.net.volley.Response;
import com.meishai.net.volley.Response.ErrorListener;
import com.meishai.net.volley.Response.Listener;
import com.meishai.net.volley.VolleyError;
import com.meishai.net.volley.toolbox.ImageRequest;
import com.meishai.net.volley.toolbox.NetworkImageView;
import com.meishai.net.volley.toolbox.StringRequest;
import com.meishai.ui.base.BaseActivity;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.dialog.CameraDialog;
import com.meishai.util.AndroidUtil;
import com.meishai.util.ComUtils;
import com.meishai.util.DebugLog;
import com.meishai.util.GsonHelper;
import com.meishai.util.ImageUtil;
import com.meishai.util.UploadUtilsAsync;
import com.meishai.util.UploadUtilsAsync.OnSuccessListener;
import com.nimbusds.jose.JOSEException;

/**
 * 个人信息中心
 *
 * @author
 */
public class UserInfoActivity extends BaseActivity implements OnClickListener {

    private Button mBtnBack;
    // 最大选择图片
    private int maxSelected = 1;
    private UploadUtilsAsync uploadAsync;
    private CameraDialog cameraDialog;
    private RelativeLayout mLayHead;
    // 我的昵称
    private RelativeLayout mMyNickname;
    // 安全设置
    private LinearLayout mLaySec;
    // 个人介绍
    private LinearLayout mPersonalIntrod;
    // 收货地址
    private LinearLayout mReceiveAddress;
    // 修改密码
    private LinearLayout mModPass;
    // 支付信息
    private LinearLayout mLayPay;
    // 淘宝账号
    private LinearLayout mLayTaobao;

    private UserInfo user;

    // UserInfo Label
    private CircleNetWorkImageView mIbPic;
    private TextView nickName;

    private String actionName = "IMAGE_CHOOSE_INFO";

    public static Intent newIntent() {
        Intent intent = new Intent(GlobalContext.getInstance(),
                UserInfoActivity.class);
        return intent;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.user_info);
        user = MeiShaiSP.getInstance().getUserInfo();
        initView();
        registerBoradcastReceiver();
        initDialog();
    }

    @Override
    protected void onResume() {
        user = MeiShaiSP.getInstance().getUserInfo();
        nickName.setText(user.getUserNickName());
        super.onResume();
    }

    /**
     * @param
     * @return void
     * @throws
     * @Title: initView
     * @Description:
     */
    private void initView() {
        mBtnBack = (Button) this.findViewById(R.id.backMain);
        mBtnBack.setOnClickListener(this);
        mLaySec = (LinearLayout) this.findViewById(R.id.lay_sec);
        mLaySec.setOnClickListener(this);
        mIbPic = (CircleNetWorkImageView) this.findViewById(R.id.ib_pic);
        mPersonalIntrod = (LinearLayout) this
                .findViewById(R.id.personal_introd);
        mPersonalIntrod.setOnClickListener(this);
        mReceiveAddress = (LinearLayout) this
                .findViewById(R.id.receive_address);
        mReceiveAddress.setOnClickListener(this);
        mMyNickname = (RelativeLayout) this.findViewById(R.id.my_nickname);
        mMyNickname.setOnClickListener(this);
        mLayHead = (RelativeLayout) this.findViewById(R.id.lay_head);
        mLayHead.setOnClickListener(this);
        mModPass = (LinearLayout) this.findViewById(R.id.mod_pass);
        mModPass.setOnClickListener(this);
        mLayPay = (LinearLayout) this.findViewById(R.id.lay_pay);
        mLayPay.setOnClickListener(this);
        mLayTaobao = (LinearLayout) this.findViewById(R.id.lay_taobao);
        mLayTaobao.setOnClickListener(this);
        nickName = (TextView) this.findViewById(R.id.nickName);
        getUserInfo();
    }

    public void setHeadImage(String url, NetworkImageView imageView) {

        imageView.setDefaultImageResId(R.drawable.head_default);
        imageView.setErrorImageResId(R.drawable.head_default);
        imageView.setImageUrl(url, getImageLoader());
    }

    private void initViewData() {
        nickName.setText(user.getUserNickName());
        if (null != user && StringUtil.isNotBlank(user.getAvatarUrl())) {
            setHeadImage(user.getAvatarUrl(), mIbPic);
        }
    }

    private void initDialog() {
        cameraDialog = new CameraDialog(this);
        user = MeiShaiSP.getInstance().getUserInfo();
        nickName.setText(user.getUserNickName());
        if (null != user && StringUtil.isNotBlank(user.getAvatarUrl())) {
            setHeadImage(user.getAvatarUrl(), mIbPic);
        }
    }

    private void getUserInfo() {
        // syncUserInfo(user.getUserID());
        if (user.getUserNickName() == null || user.getUserMobile() == null) {
            syncUserInfo(user.getUserID());
        }
    }

    private void syncUserInfo(String userID) {
        try {
            ReqData reqData = new ReqData();
            reqData.setC("member");
            reqData.setA("userinfo");
            Map<String, String> map = new HashMap<String, String>();
            map.put("userid", user.getUserID());
            reqData.setData(map);
            String reqUrl = reqData.toReqString();
            if (reqUrl != null && reqUrl.length() > 0) {
                String url = getString(R.string.base_url) + reqUrl;
                DebugLog.d(url);
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
                DebugLog.d("RSP:" + response);
                if (checkResult(response)) {
                    initViewData();
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

    private boolean checkResult(String json) {
        try {
            org.json.JSONObject jsonObj = new org.json.JSONObject(json);
            String successID = jsonObj.getString("success").trim();
            AndroidUtil.showToast(jsonObj.getString("tips").trim());
            if (successID.equals("1")) {
                user.setUserID(jsonObj.getJSONObject("data")
                        .getString("userid").trim());
                user.setAvatarUrl(jsonObj.getJSONObject("data")
                        .getString("avatar").trim());
                user.setUserNickName(jsonObj.getJSONObject("data")
                        .getString("username").trim());
                MeiShaiSP.getInstance().setUserInfo(user);
                return true;
            } else {
                return false;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            AndroidUtil.showToast("解析数据失败");
            return false;
        }

    }

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
                finish();
                break;
            case R.id.lay_head:
                cameraDialog.setActionName(actionName);
                cameraDialog.showDialog();
                break;
            case R.id.lay_sec:
                startActivity(UserSecActivity.newIntent());
                break;
            case R.id.personal_introd:
                startActivity(UserIntrodActivity.newIntent());
                break;
            case R.id.receive_address:
                startActivity(UserAddressActivity.newIntent());
                break;
            case R.id.my_nickname:
                startActivity(UserNicknameActivity.newIntent());
                break;
            case R.id.mod_pass:
                startActivity(UserModPassActivity.newIntent());
                break;
            case R.id.lay_pay:
                startActivity(UserPayActivity.newIntent());
                break;
            case R.id.lay_taobao:
                startActivity(UserTaobaoActivity.newIntent());
                break;
            default:
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            doPhoto(requestCode, data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private void doPhoto(int requestCode, Intent intent) {
        if (requestCode == ConstantSet.SELECT_PIC_BY_TACK_PHOTO) {//拍照
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
            // String uriPath = AndroidUtil.imageUri2Path(cameraDialog
            // .getPhotoUri());
            // System.out.println(uriPath);
            cropImage(cameraDialog.getPhotoUri());
        } else if (requestCode == ConstantSet.PHOTO_RESOULT) {//选择头像
            Bundle d = intent.getExtras();
            if (d != null) {
                Bitmap bm = d.getParcelable("data");
                String picN = ComUtils.getRandomAlphamericStr(8) + ".jpg";
                picN = "image.jpg";
                File temp = new File(this.getCacheDir(), picN);
                try {
                    if (temp.exists()) {
                        temp.delete();
                    }
                    temp.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                ImageUtil.saveBitMap2FileJpg(temp, bm);
                // 上传
                // Bitmap bitmap = ImageUtil.getImageFromSDCard(temp
                // .getAbsolutePath());
                // mIbPic.setImageBitmap(bitmap);
                uploadAvatar(temp.getAbsolutePath());
                // camera_photo.setImageBitmap(bm);

                // bm.recycle();
                // bm = null;

                // 缩小图片质量
                // bm = NativeImageLoader.getInstance().decodeBitmapForFile(
                // temp.getAbsolutePath(), 2);
                // ImageUtil.saveBitMap2File(temp, bm);
                // bm.recycle();
                // bm = null;
            }
        }
    }

    private int width = 300; // 裁剪后默认图片宽度
    private int height = 300; // 裁剪后默认图片高度

    private void cropImage(Uri uri) {
        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setDataAndType(uri, "image/*")
                // 发送裁剪信号
                .putExtra("crop", "true")
                        // X方向上的比例
                .putExtra("aspectX", 1)
                        // Y方向上的比例
                .putExtra("aspectY", 1)
                        // 裁剪区的宽
                .putExtra("outputX", width)
                        // 裁剪区的高
                .putExtra("outputY", height)
                        // 是否返回数据
                .putExtra("return-data", true)
                        // 是否保留比例
                .putExtra("scale", true)
                        // 黑边
                .putExtra("scaleUpIfNeeded", true)
                        //保存位置
//				 .putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(new File(getCacheDir(), ComUtils.getRandomAlphamericStr(8) +".jpg")))
                .putExtra("noFaceDetection", true)
                        // 关闭人脸检测
                .putExtra("outputFormat", Bitmap.CompressFormat.JPEG.toString());
        startActivityForResult(intent, ConstantSet.PHOTO_RESOULT);
    }

    /**
     * 生成上传文件的路径集合
     *
     * @param path
     * @return
     */
    private ArrayList<String> getPicPath(String path) {
        ArrayList<String> pics = new ArrayList<String>();
        pics.add(path);
        return pics;
    }

    private void uploadAvatar(String path) {
        uploadAsync = new UploadUtilsAsync(this, getReqAvatarData(),
                getPicPath(path));
        uploadAsync.setListener(new OnSuccessListener() {

            @Override
            public void onSuccess(RespData respData) {
                if (respData == null) return;
                if (respData.isSuccess()) {
                    UploadHead headInfo = GsonHelper.parseObject(
                            GsonHelper.toJson(respData.getData()),
                            UploadHead.class);
                    if (null != headInfo) {
                        user.setUserID(headInfo.getUserid());
                        user.setAvatarUrl(headInfo.getAvatar());
                        MeiShaiSP.getInstance().setUserInfo(user);

                        ImageRequest imageRequest = new ImageRequest(
                                user.getAvatarUrl(),
                                new Response.Listener<Bitmap>() {
                                    @Override
                                    public void onResponse(Bitmap response) {
                                        mIbPic.setImageBitmap(response);
                                    }
                                }, 0, 0, Config.RGB_565,
                                new Response.ErrorListener() {
                                    @Override
                                    public void onErrorResponse(
                                            VolleyError error) {
                                        mIbPic.setImageResource(R.drawable.head_default);
                                    }
                                });
                        GlobalContext.getInstance().getRequestQueue().add(imageRequest);

                        // setHeadImage(user.getAvatarUrl(), mIbPic);
                    }

                }
            }
        });
        uploadAsync.execute();
    }

    /**
     * 获取上传图像请求数据
     *
     * @return
     */
    private Map<String, String> getReqAvatarData() {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        ReqData reqData = new ReqData();
        reqData.setC(ConstantSet.C_MEMBER);
        reqData.setA("avatar");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private BroadcastReceiver mBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (action.equals(actionName)) {
                //TODO 用户头像上传无法更新,需解决
                ArrayList<String> imagePaths = intent
                        .getStringArrayListExtra(ConstantSet.CHOOSE_DATA);
                if (null != imagePaths && !imagePaths.isEmpty()) {
                    String path = imagePaths.get(0);
                    File f = new File(path);
                    Uri pUri = Uri.fromFile(f);
                    // uploadAvatar(path);
                    cropImage(pUri);
                }
            }
        }
    };

    public void registerBoradcastReceiver() {
        IntentFilter myIntentFilter = new IntentFilter();
        myIntentFilter.addAction(actionName);
        // 注册广播
        registerReceiver(mBroadcastReceiver, myIntentFilter);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mBroadcastReceiver);
    }
}
