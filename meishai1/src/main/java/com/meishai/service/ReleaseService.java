package com.meishai.service;

import com.meishai.dao.MeiShaiSP;
import com.meishai.entiy.ReleaseData;
import com.meishai.entiy.ShareData;
import com.meishai.net.ReqData;
import com.meishai.net.RespData;
import com.meishai.ui.constant.ConstantSet;
import com.meishai.ui.fragment.camera.CameraActivity1;
import com.meishai.ui.fragment.usercenter.LoginActivity;
import com.meishai.util.AndroidUtil;
import com.meishai.util.DebugLog;
import com.meishai.util.StringUtils;
import com.meishai.util.UploadUtilsAsync;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.wechat.moments.WechatMoments;

public class ReleaseService extends Service {

    private UploadUtilsAsync uploadAsync;
    private String action;

    @Override
    public IBinder onBind(Intent intent) {
        //		stopSelf();
        //通过Intent把数据传递进来
        //		ReleaseData data = intent.getParcelableExtra("releaseDate");
        //		if (CameraActivity1.OPER_POST.equals(data.getOper())){
        //			//修改帖子
        //			modPost(data);
        //		}else{
        //			//发布晒晒
        //			releasePost(data);
        //		}
        //		AndroidUtil.showToast("绑定服务");
        return null;
    }


    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        //		AndroidUtil.showToast("创建服务");
        //通过Intent把数据传递进来
        ReleaseData data = intent.getParcelableExtra("releaseDate");
        action = intent.getStringExtra("receiver");
        if (data != null && !TextUtils.isEmpty(data.getOper())) {
            if (CameraActivity1.OPER_POST.equals(data.getOper())) {
                //修改帖子
                modPost(data);
            } else {
                //发布晒晒
                releasePost(data);
            }
        } else {
            stopSelf();
        }
        return super.onStartCommand(intent, flags, startId);
    }


    public void modPost(ReleaseData data) {
        uploadAsync = new UploadUtilsAsync(this, getReqModPostData(data),
                getNewPath(data.getPaths()));
        uploadAsync.setListener(new UploadUtilsAsync.OnSuccessListener() {

            @Override
            public void onSuccess(RespData respData) {
                if (respData == null) {
                    DebugLog.w("上传图片的返回数据为null");
                    stopSelf();
                    return;
                }
                if (respData.isSuccess()) {
                    AndroidUtil.showToast(respData.getTips());
                    if (respData.getIsshare() == 1) {
                        //分享到微信朋友圈
                        Platform plat = ShareSDK.getPlatform(getApplicationContext(), WechatMoments.NAME);
                        plat.share(generateParams(respData.getShare()));
                    }

                } else if (respData.isLogin()) {
                    startActivity(LoginActivity.newOtherIntent());
                } else {
                    AndroidUtil.showToast(respData.getTips());
                }
                //发送广播
                if (!TextUtils.isEmpty(action)) {
                    Intent intent = new Intent();
                    intent.setAction(action);
                    sendBroadcast(intent);
                }
                stopSelf();
            }
        });
        uploadAsync.setUpdateListener(new UploadUtilsAsync.OnUpdateProgress() {

            @Override
            public void onUpdate(final int count, final int position,
                                 final int total, final int currentsize) {

            }
        });
        uploadAsync.execute();
    }


    /**
     * 修改帖子所需要的参数
     *
     * @param mReleaseData
     * @return
     */
    private Map<String, String> getReqModPostData(ReleaseData mReleaseData) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // 帖子ID
        data.put("pid", String.valueOf(mReleaseData.getPid()));
        // 回复评论ID，如果不是回复则传入0
        data.put("dataid", "0");

        //没有修改的图片
        List<String> oldpics = getOldPath(mReleaseData.getPaths());
        data.put("oldpic", StringUtils.join(oldpics.toArray(), ","));

        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", mReleaseData.getIsShowLocation() + "");

        // 地区坐标，格式：经度,纬度
        data.put("coordinate", mReleaseData.getLongLatPoint());

        // 广东省,深圳市,龙岗区，格式：省,市,区
        data.put("areaname", mReleaseData.getLocation());

        // 文字内容
        data.put("content", mReleaseData.getContent());

        //--------------------我是华丽的分割线-----------------新增的字段
        //是否分享到微信
        data.put("isshare", mReleaseData.getIsShare2Wechat() + "");

        //itemurl：分享链接
        data.put("itemurl", mReleaseData.getUrl());

        // tids
        data.put("tids", mReleaseData.getTids());


        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("edit_save");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 以前的提交修改的参数,已经被改过了
     *
     * @param mReleaseData
     * @return
     */
    private Map<String, String> getReqModPostData1(ReleaseData mReleaseData) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // 帖子ID
        data.put("pid", String.valueOf(mReleaseData.getPid()));
        // 回复评论ID，如果不是回复则传入0
        data.put("dataid", "0");

        //没有修改的图片
        List<String> oldpics = getOldPath(mReleaseData.getPaths());
        data.put("oldpic", StringUtils.join(oldpics.toArray(), ","));

        // 贴纸id,如果封面设置了贴纸,则传入ptid,否则为0
        // data.put("ptid", String.valueOf(mReleaseData.getPtid()));
        data.put("cid", String.valueOf(mReleaseData.getCid()/*chkCateInfo.getCid()*/));

        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", mReleaseData.getIsShowLocation() == 1 ? "1" : "0");

        // 地区坐标，格式：经度,纬度
        //		data.put("coordinate", localModel.toLongLatPoint());
        data.put("coordinate", mReleaseData.getLongLatPoint());

        // 广东省,深圳市,龙岗区，格式：省,市,区
        //		data.put("areaname",
        //				localModel.getProvince() + "," + localModel.getCity() + ","
        //						+ localModel.getDistrict());
        data.put("areaname", mReleaseData.getLocation());

        // 文字内容
        //		data.put("content", mReleaseEdit.getText().toString());
        data.put("content", mReleaseData.getContent());

        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("edit_save");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private void releasePost(ReleaseData data) {
        // 发布晒晒
        uploadAsync = new UploadUtilsAsync(this, getReqcommentData(data),
                data.getPaths());
        uploadAsync.setListener(new UploadUtilsAsync.OnSuccessListener() {

            @Override
            public void onSuccess(RespData respData) {
                if (null == respData) {
                    return;
                }
                if (respData.isSuccess()) {
                    AndroidUtil.showToast(respData.getTips());
                    if (respData.getIsshare() == 1) {
                        Platform plat = null;
                        plat = ShareSDK.getPlatform(getApplicationContext(), WechatMoments.NAME);
                        plat.share(generateParams(respData.getShare()));
                    }
                } else if (respData.isLogin()) {
                    startActivity(LoginActivity.newOtherIntent());
                } else {
                    AndroidUtil.showToast(respData.getTips());
                }
                //发送广播
                if (!TextUtils.isEmpty(action)) {
                    Intent intent = new Intent();
                    intent.setAction(action);
                    sendBroadcast(intent);
                }
                stopSelf();
            }
        });
        uploadAsync.setUpdateListener(new UploadUtilsAsync.OnUpdateProgress() {

            @Override
            public void onUpdate(final int count, final int position, final int total,
                                 final int currentsize) {

            }
        });
        uploadAsync.execute();
    }

    /**
     * 生成发布晒晒的map数据集合
     *
     * @param releaseData
     * @return
     */
    private Map<String, String> getReqcommentData(ReleaseData releaseData) {
        Map<String, String> data = new HashMap<String, String>();

        //userid
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());
        // gid
        data.put("gid", String.valueOf(releaseData.getGid()));
        // tids
        data.put("tids", releaseData.getTids());
        // 是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", releaseData.getIsShowLocation() + "");
        // 文字内容
        data.put("content", releaseData.getContent());
        // 地区坐标，格式：经度,纬度
        data.put("coordinate", releaseData.getLongLatPoint());
        // 广东省,深圳市,龙岗区，格式：省,市,区
        data.put("areaname", releaseData.getLocation());
        //是否分享到微信
        data.put("isshare", releaseData.getIsShare2Wechat() + "");
        //itemurl：分享链接
        data.put("itemurl", releaseData.getUrl());
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("create");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    /**
     * 以前的发布参数,已经被换过了
     *
     * @param releaseData
     * @return
     */
    private Map<String, String> getReqcommentData1(ReleaseData releaseData) {
        Map<String, String> data = new HashMap<String, String>();
        data.put(ConstantSet.USERID, MeiShaiSP.getInstance().getUserInfo()
                .getUserID());

        // 帖子ID
        data.put("pid", String.valueOf(releaseData.getPid()));
        // 话题ID，如果是话题里面的，则传入话题ID，否则为0
        //		data.put("tid", releaseData.getTid() + ","
        //				+ releaseData.getPtids().get(0));
        data.put("tid", generateTids(releaseData));
        // data.put("tid", mReleaseData.getTid() + "");
        // 试用ID，如果是提交试用报告，则传入试用商品ID，否则为0
        data.put("sid", String.valueOf(releaseData.getSid()));
        // 活动ID，如果是积分兑换里的晒福利，则传入福利里的aid，否则为0
        data.put("aid", String.valueOf(releaseData.getAid()));
        // 贴纸id,如果封面设置了贴纸,则传入ptid,否则为0
        // data.put("ptid", String.valueOf(mReleaseData.getPtid()));
        data.put("cid", String.valueOf(releaseData.getCid()));
        // isarea:1//是否显示地区，isarea=1显示地区，isarea=0不显示地区，当不显示地区的时候仍然传入坐标
        data.put("isarea", releaseData.getIsShowLocation() == 1 ? "1" : "0");
        // 地区坐标，格式：经度,纬度
        data.put("coordinate", releaseData.getLongLatPoint());
        // 广东省,深圳市,龙岗区，格式：省,市,区
        //		data.put("areaname",
        //				localModel.getProvince() + "," + localModel.getCity() + ","
        //						+ localModel.getDistrict());
        data.put("areaname", releaseData.getLocation());
        // 文字内容
        data.put("content", releaseData.getContent());
        ReqData reqData = new ReqData();
        reqData.setC("post");
        reqData.setA("create");
        reqData.setData(data);

        Map<String, String> map = new HashMap<String, String>();
        try {
            map.put("op", reqData.toReqString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return map;
    }

    private String generateTids(ReleaseData releaseData) {
        StringBuilder tids = new StringBuilder();
        tids.append(releaseData.getTid());
        for (int i = 0; i < releaseData.getPtids().size(); i++) {
            tids.append("," + releaseData.getPtids().get(i));
        }

        return tids.toString();
    }


    /**
     * 获得图片集合中的网络图片(也就是在修改的时候没有发生变化的图片)
     *
     * @param source 图片集合
     * @return 返回图片集合的一个子集, 只包含网络图片的
     */
    private List<String> getOldPath(List<String> source) {
        List<String> resault = new ArrayList<String>();
        for (String path : source) {
            if (path.startsWith("http")) {
                resault.add(path);
            }
        }
        return resault;
    }

    /**
     * 获得图片集中的本地图片的集(也就是需要上传的图片)
     *
     * @param source
     * @return
     */
    private List<String> getNewPath(List<String> source) {
        List<String> resault = new ArrayList<String>();
        for (String path : source) {
            if (!path.startsWith("http")) {
                resault.add(path);
            }
        }
        return resault;
    }

    /**
     * 生成分享参数
     *
     * @param shareData
     * @return
     */
    public Platform.ShareParams generateParams(ShareData shareData) {
        if (shareData != null) {
            Platform.ShareParams sp = new Platform.ShareParams();
            // sp.setShareType(Platform.SHARE_TEXT);
            sp.setTitle(shareData.getTitle());
            sp.setText(shareData.getContent());
            sp.setImageUrl(shareData.getPic());
            sp.setUrl(shareData.getUrl());
            sp.setShareType(Platform.SHARE_WEBPAGE);
            return sp;
        }
        return null;
    }

}
