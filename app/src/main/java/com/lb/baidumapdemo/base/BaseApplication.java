package com.lb.baidumapdemo.base;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.lb.baidumapdemo.face.LocationFace;
import com.lb.baidumapdemo.util.LocationFaceUtil;

import android.app.Application;

public class BaseApplication extends Application {
	public static DrivingRouteResult drivingRouteResult=null;
	public static TransitRouteResult transitRouteResult=null;
	public static WalkingRouteResult walkingRouteResult=null;
	@Override
	public void onCreate() {
		super.onCreate();
		   SDKInitializer.initialize(getApplicationContext());  
		   new LocationFaceUtil(getApplicationContext(),new LocationFace() {
			
			@Override
			public void locationResult(BDLocation location) {
				
			}
		});
	}
	
	
}
