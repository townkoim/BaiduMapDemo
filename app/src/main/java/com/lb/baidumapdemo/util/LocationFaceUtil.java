package com.lb.baidumapdemo.util;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.LocationClientOption.LocationMode;
import com.lb.baidumapdemo.db.DBConstants;
import com.lb.baidumapdemo.db.ShareDB;
import com.lb.baidumapdemo.face.LocationFace;

import android.content.Context;

/**
 * @ClassName: LocationFaceUtil
 * @Description: 定位帮助类，这个类只用来做定位用
 * @author libiao
 * @date 2015-8-20 下午2:48:07
 * 
 */
public class LocationFaceUtil implements BDLocationListener {
	private LocationFace locationFace;
	public LocationClient mLocationClient = null;
	private Context context;

	public LocationFaceUtil(Context context, LocationFace locationFace) {
		super();
		this.locationFace = locationFace;
		this.context=context;
		mLocationClient = new LocationClient(context);
		mLocationClient.registerLocationListener(LocationFaceUtil.this);
		startLocation();
	}

	private void startLocation() {
		LocationClientOption option = new LocationClientOption();
		option.setLocationMode(LocationMode.Hight_Accuracy);// 可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
		option.setCoorType("bd09ll");// 可选，默认gcj02，设置返回的定位结果坐标系
		option.setScanSpan(0);// 可选，默认0，即仅定位一次，设置发起定位请求的间隔需要大于等于1000ms才是有效的
		option.setIsNeedAddress(true);// 可选，设置是否需要地址信息，默认不需要
		option.setOpenGps(true);// 可选，默认false,设置是否使用gps
		option.setLocationNotify(true);// 可选，默认false，设置是否当gps有效时按照1S1次频率输出GPS结果
		option.setIsNeedLocationDescribe(true);// 可选，默认false，设置是否需要位置语义化结果，可以在BDLocation.getLocationDescribe里得到，结果类似于“在北京天安门附近”
		option.setIsNeedLocationPoiList(true);// 可选，默认false，设置是否需要POI结果，可以在BDLocation.getPoiList里得到
		option.setIgnoreKillProcess(false);// 可选，默认false，定位SDK内部是一个SERVICE，并放到了独立进程，设置是否在stop的时候杀死这个进程，默认杀死
		option.SetIgnoreCacheException(false);// 可选，默认false，设置是否收集CRASH信息，默认收集
		option.setEnableSimulateGps(false);// 可选，默认false，设置是否需要过滤gps仿真结果，默认需要
		mLocationClient.setLocOption(option);
		mLocationClient.start();
	}

	@Override
	public void onReceiveLocation(BDLocation arg0) {
		if (locationFace != null)
			if (arg0.getLocType() == 61 || arg0.getLocType() == 161 && arg0.getLatitude() != 0.0) {
				new ShareDB(context).save(DBConstants.CITY_NAME, arg0.getCity());
				locationFace.locationResult(arg0);
			}
	}

}
