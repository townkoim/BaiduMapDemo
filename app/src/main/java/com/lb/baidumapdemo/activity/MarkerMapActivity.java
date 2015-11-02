package com.lb.baidumapdemo.activity;

import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapClickListener;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.InfoWindow;
import com.baidu.mapapi.map.MapPoi;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.Marker;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.base.BaseActivity;

public class MarkerMapActivity extends BaseActivity implements OnMarkerClickListener,OnMapClickListener {
	private MapView mBaiduMapView; // 地图界面
	private BaiduMap mBaiduMap; // 地图的管理类
	private String[] titles = new String[] { "one", "two", "three", "four" };
	private LatLng[] latlngs = new LatLng[] { new LatLng(22.539895,114.058935), new LatLng(22.540729,114.066337),
			new LatLng(22.543763,114.06458), new LatLng(22.538614,114.062811) };

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_markermap);
		mBaiduMapView = (MapView) findViewById(R.id.markermap);
		mBaiduMap = mBaiduMapView.getMap();
		mBaiduMap.setOnMapClickListener(this);
		initMarker();
	}

	private void initMarker() {
		 mBaiduMap.clear();  
		LatLng latLng = null;
		OverlayOptions overlayOptions = null;
		// 设置地图类型 MAP_TYPE_NORMAL 普通图； MAP_TYPE_SATELLITE 卫星图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启交通图
		mBaiduMap.setTrafficEnabled(true);
		MapStatusUpdate statusUpdate = MapStatusUpdateFactory.zoomTo(17);
		mBaiduMap.setMapStatus(statusUpdate);
		BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
		//循环添加四个覆盖物到地图上
		for (int i = 0; i < titles.length; i++) {
			latLng=latlngs[i];
			overlayOptions = new MarkerOptions().position(latLng).icon(descriptor);
			// 将覆盖物添加到地图上
			Marker marker=(Marker) mBaiduMap.addOverlay(overlayOptions);
			Bundle bundle = new Bundle();
			bundle.putString("info",titles[i]+"个");
			marker.setExtraInfo(bundle);
		}
		// 将最后一个坐标设置为地图中心
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
		mBaiduMap.setMapStatus(u);
		mBaiduMap.setOnMarkerClickListener(MarkerMapActivity.this);
		
	}

	/**
	 * @Title: onMarkerClick 
	 * @Description: 覆盖物点击事件,每次点击一个覆盖物则会在相应的覆盖物上显示一个InfoWindow
	 * @param marker
	 * @return
	 */
	@Override
	public boolean onMarkerClick(Marker marker) {
		final String msg = marker.getExtraInfo().getString("info");
		InfoWindow mInfoWindow;
		// 生成一个TextView用户在地图中显示InfoWindow
		TextView location = new TextView(getApplicationContext());
		location.setBackgroundResource(R.drawable.shape_popup);
		location.setPadding(30, 20, 30, 20);
		location.setText(msg);
		final LatLng ll = marker.getPosition();
		Point p = mBaiduMap.getProjection().toScreenLocation(ll);
		p.y -= 47;
		LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
		mInfoWindow = new InfoWindow(location, llInfo, -47);
		mBaiduMap.showInfoWindow(mInfoWindow);
		location.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				Toast.makeText(MarkerMapActivity.this, msg, Toast.LENGTH_SHORT).show();
			}
		});
		return true;
	}
	
	/**
	 * @Title: onMapClick 
	 * @Description: 地图点击事件，点击地图的时候要让InfoWindow消失
	 * @param arg0
	 */
	@Override
	public void onMapClick(LatLng arg0) {
		mBaiduMap.hideInfoWindow();
		
	}
	
	/**
	 * @Title: onMapPoiClick 
	 * @Description: 兴趣点点击事件
	 * @param arg0
	 * @return
	 */
	@Override
	public boolean onMapPoiClick(MapPoi arg0) {
		// TODO Auto-generated method stub
		return false;
	}

}
