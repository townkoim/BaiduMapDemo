package com.lb.baidumapdemo.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerDragListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeOption;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.base.BaseActivity;
import com.lb.baidumapdemo.face.LocationFace;
import com.lb.baidumapdemo.util.LocationFaceUtil;

public class BaseMapActivity extends BaseActivity implements OnMarkerDragListener,OnMarkerClickListener,OnGetGeoCoderResultListener{
	private MapView mBaiduMapView;  //地图界面
	private ProgressDialog progressDialog;//进度条
	private View popupView; // 弹框
	private TextView tvpopup;//弹框上的文字
	
	private BaiduMap mBaiduMap; //地图的管理类
	private BDLocation bdLocation; //定位类
	private GeoCoder geoCoder; // 经纬度地理位置坐标反转类
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_basemap);
		mBaiduMapView=(MapView) findViewById(R.id.basemap);
		mBaiduMap=mBaiduMapView.getMap();
		initLocation();
	}
	/**
	 * @Title: initLocation 
	 * @Description: 发起定位
	 * @return: void
	 */
	private void initLocation() {
		progressDialog =ProgressDialog.show(BaseMapActivity.this,"定位","正在定位......");
		new LocationFaceUtil(getApplicationContext(), new LocationFace() {
			
			@Override
			public void locationResult(BDLocation location) {
				if(progressDialog!=null){
					progressDialog.dismiss();
				}
				bdLocation=location;
				addMarker(null);
			}
		});
	}
	private void addMarker(Marker marker){
		// 设置地图类型 MAP_TYPE_NORMAL 普通图； MAP_TYPE_SATELLITE 卫星图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启交通图
		mBaiduMap.setTrafficEnabled(true); 
		// 设置地图当前级别
		MapStatusUpdate statusUpdate = MapStatusUpdateFactory.zoomTo(19);
		mBaiduMap.setMapStatus(statusUpdate);
		//构建覆盖物的信息
		LatLng latLng=null;
		if(marker==null){
			 latLng = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
		}else{
			 latLng = new LatLng(marker.getPosition().latitude,marker.getPosition().longitude);
		}
		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		OverlayOptions option = new MarkerOptions().position(latLng).icon(descriptor).draggable(true);
		//清楚地图上所有的覆盖物
		mBaiduMap.clear();
		//将覆盖物添加到地图上
		mBaiduMap.addOverlay(option);
		// 将覆盖物设置为地图中心
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		//以动画方式更新地图状态，动画耗时 300 ms
		mBaiduMap.animateMapStatus(u);
		mBaiduMap.setOnMarkerClickListener(BaseMapActivity.this);
		mBaiduMap.setOnMarkerDragListener(BaseMapActivity.this);
	}
	/**
	 * @Title: onMarkerClick 
	 * @Description:覆盖物的点击事件
	 * @param arg0
	 * @return
	 */
	@Override
	public boolean onMarkerClick(Marker arg0) {
		if(geoCoder==null){
			geoCoder = GeoCoder.newInstance();
			geoCoder.setOnGetGeoCodeResultListener(BaseMapActivity.this);// 设置反地理查询监听器
		}
		geoCoder.reverseGeoCode(new ReverseGeoCodeOption().location(arg0.getPosition()));
		return true;
	}
	/**
	 * @Title: onMarkerDrag 
	 * @Description:覆盖物拖拽过程中的方法
	 * @param arg0
	 */
	@Override
	public void onMarkerDrag(Marker arg0) {
		
	}
	/**
	 * @Title: onMarkerDrag 
	 * @Description:覆盖物拖拽结束的方法
	 * @param arg0
	 */
	@Override
	public void onMarkerDragEnd(Marker arg0) {
		
	}
	/**
	 * @Title: onMarkerDrag 
	 * @Description:覆盖物拖拽开始的方法
	 * @param arg0
	 */
	@Override
	public void onMarkerDragStart(Marker arg0) {
		mBaiduMap.hideInfoWindow();
	}
	/**
	 * @Title: onGetGeoCodeResult 
	 * @Description: 坐标换算 根据地址得到坐标
	 * @param arg0
	 */
	@Override
	public void onGetGeoCodeResult(GeoCodeResult arg0) {
		
	}
	/**
	 * @Title: onGetReverseGeoCodeResult 
	 * @Description: 坐标换算，根据坐标得到地质
	 * @param arg0
	 */
	@Override
	public void onGetReverseGeoCodeResult(ReverseGeoCodeResult arg0) {
		addPoupMarker(arg0.getLocation(),arg0.getAddress());
	}
	
	private void addPoupMarker(LatLng pt,String address){
		//创建InfoWindow展示的view  
		if(popupView==null){
			popupView =LayoutInflater.from(BaseMapActivity.this).inflate(R.layout.popup, null);
			tvpopup=(TextView) popupView.findViewById(R.id.popup_text);
		}
		tvpopup.setText(address);
		//创建InfoWindow , 传入 view， 地理坐标， y 轴偏移量 
		InfoWindow mInfoWindow = new InfoWindow(popupView, pt, -47);  
		//显示InfoWindow  
		mBaiduMap.showInfoWindow(mInfoWindow);
		
	}
}
