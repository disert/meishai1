package com.meishai.app.location;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

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
public class TsLocation6 {

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

    public TsLocation6(Context ctx, Handler handler) {
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
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy
        );//可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        option.setCoorType("bd09ll");//可选，默认gcj02，设置返回的定位结果坐标系
        int span = 1000;
        option.setScanSpan(span);//可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
        option.setIsNeedAddress(true);//可选，设置是否需要地址信息，默认不需要
        option.setOpenGps(true);//可选，默认false,设置是否使用gps
        option.setLocationNotify(true);//可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
        option.setIsNeedLocationDescribe(true);//可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
        option.setIsNeedLocationPoiList(true);//可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
        option.setIgnoreKillProcess(false);//可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
        option.SetIgnoreCacheException(false);//可选，默认false，设置是否收集CRASH信息，默认收集
        option.setEnableSimulateGps(false);//可选，默认false，设置是否需要过滤gps仿真结果，默认需要
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
            DebugLog.d("地址类型码:" + location.getLocType());
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
