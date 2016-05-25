package com.meishai.app.location;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.meishai.util.DebugLog;

/**
 * 定位使用 3.1的,已经过时
 *
 * @author sh
 */
public class TsLocation {

    public LocationClient mLocationClient = null;
    public MyLocationListenner myListener = new MyLocationListenner();
    //定位结果
    private LocalModel localModel = new LocalModel();
    // 访问的回调函数
    public Handler localHander = null;
    /**
     * isManyLoc:true:表示持续定位,false:表示一次定位
     */
    private boolean isManyLoc = false;

    public TsLocation(Context ctx, Handler handler) {
        this.localHander = handler;
        mLocationClient = new LocationClient(ctx);
        mLocationClient.registerLocationListener(myListener);
    }

    public void setManyLoc(boolean isManyLoc) {
        this.isManyLoc = isManyLoc;
    }

    /**
     * 开始定位
     */
    public void startLoction() {
        this.setLocationOption();
        if (!mLocationClient.isStarted()) {
            mLocationClient.start();
        }
        if (mLocationClient != null && mLocationClient.isStarted()) {
            mLocationClient.requestLocation();
        } else {
            DebugLog.w("locClient is null or not started");
        }
    }

    /**
     * 停止定位
     */
    public void stopLoction() {
        if (mLocationClient != null)
            mLocationClient.stop();
    }

    // 设置定位相关参数
    private void setLocationOption() {
        LocationClientOption option = new LocationClientOption();
        // 打开gps
//		 option.setOpenGps(true);
        option.setCoorType(LocalModel.localClass); // 设置坐标类型
//		option.setServiceName("com.baidu.location.service_v2.9");
//		option.setPoiExtraInfo(LocalModel.isShowAddr);
        if (LocalModel.isShowAddr)
            option.setAddrType("all");
        // 设置定位模式，小于1秒则一次定位;大于等于1秒则定时定位
        option.setScanSpan(LocalModel.interval);
        // 设置网络优先
        option.setPriority(LocalModel.priority);
//		option.setPoiNumber(10);
        option.disableCache(true);
        mLocationClient.setLocOption(option);
    }

    /**
     * 显示请求字符串
     *
     * @param str
     */
    /**
     * 监听函数，有更新位置的时候，格式化成字符串，输出到屏幕中
     */
    public class MyLocationListenner implements BDLocationListener {

        @Override
        public void onReceiveLocation(BDLocation location) {
            if (location == null)
                return;

            DebugLog.d("latitude:" + location.getLatitude() + ",Longitude:" + location.getLongitude());

            localModel.setLatitude(String.valueOf(location.getLatitude()));
            localModel.setLongitude(String.valueOf(location.getLongitude()));
            localModel.setAddr(location.getAddrStr());
            localModel.setRadius(String.valueOf(location.getRadius()));
            localModel.setProvince(location.getProvince());
            localModel.setCity(location.getCity());
            localModel.setDistrict(location.getDistrict());
            localModel.setStreet(location.getStreet());
            localModel.setStreCode(location.getStreetNumber());
            Message msg = localHander.obtainMessage();
            msg.what = LocalModel.MSG_WHAT;
            msg.obj = localModel;
            msg.sendToTarget();
            if (!isManyLoc) {
                stopLoction();
            }
        }

        // 离线
        public void onReceivePoi(BDLocation poiLocation) {
            if (poiLocation == null) {
                return;
            }
            localModel.setLatitude(String.valueOf(poiLocation.getLatitude()));
            localModel.setLongitude(String.valueOf(poiLocation.getLongitude()));
            localModel.setAddr(poiLocation.getAddrStr());
            localModel.setRadius(String.valueOf(poiLocation.getRadius()));
            Message msg = localHander.obtainMessage();
            msg.what = LocalModel.MSG_WHAT;
            msg.obj = localModel;
            msg.sendToTarget();
        }
    }
//	private LocalModel localModel = null;
//	private TsLocation location = null;
//	protected void onStart() {
//		super.onStart();
//		location = new TsLocation(MainActivity.this, new Handler() {
//
//			public void handleMessage(android.os.Message msg) {
//				switch (msg.what) {
//					case LocalModel.MSG_WHAT:
//						localModel = (LocalModel) msg.obj;
//						System.out.println("addr:" + localModel.getAddr());
//						break;
//					default:
//						break;
//				}
//			};
//		});
//		location.startLoction();
//	};
//	protected void onDestroy() {
//		super.onDestroy();
//		location.stopLoction();
//	};
}
