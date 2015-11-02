package com.lb.baidumapdemo.activity.search;

import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMarkerClickListener;
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
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.base.BaseActivity;
import com.lb.baidumapdemo.face.LocationFace;
import com.lb.baidumapdemo.model.LocationInfo;
import com.lb.baidumapdemo.util.LocationFaceUtil;
/**
* @ClassName: SearchMapActivity  
* @Description:地图搜索的开始
* @author libiao 
* @date 2015-8-24 上午9:40:16  
*
 */
public class SearchMapActivity extends BaseActivity implements OnClickListener,OnMarkerClickListener{
	private Button btn1,btn2;
	private MapView mapView;
	private BaiduMap mBaiduMap;
	private BDLocation bdLocation=null;
	private boolean isFirst=true;//因为如果是第一次调用initLocation（）不会显示InfoWindow，通过isFirst来判断是否调用initLocation（）两次，就能显示InfoWindow
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_searchmap);
		mapView=(MapView) findViewById(R.id.searchmap);
		btn1=(Button) findViewById(R.id.search_btn1);
		btn2=(Button) findViewById(R.id.search_btn2);
		btn1.setOnClickListener(SearchMapActivity.this);
		btn2.setOnClickListener(SearchMapActivity.this);
		mBaiduMap=mapView.getMap();
		initLocation();
	}
	/**
	 * @Title: initLocation 
	 * @Description: 发起定位请求
	 * @return: void
	 */
	private void initLocation() {
		new LocationFaceUtil(SearchMapActivity.this, new LocationFace() {
			
			@Override
			public void locationResult(BDLocation location) {
				if(isFirst){
					isFirst=false;
					initLocation();
				}else{
					mBaiduMap.clear();
					bdLocation=location;
					LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
					// 设置地图类型 MAP_TYPE_NORMAL 普通图； MAP_TYPE_SATELLITE 卫星图
					mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
					// 开启交通图
					mBaiduMap.setTrafficEnabled(true);
					MapStatusUpdate statusUpdate = MapStatusUpdateFactory.zoomTo(18);
					mBaiduMap.setMapStatus(statusUpdate);	
					BitmapDescriptor descriptor = BitmapDescriptorFactory.fromResource(R.drawable.icon_gcoding);
					OverlayOptions overlayOptions = new MarkerOptions().position(latLng).icon(descriptor);
					Marker marker =(Marker) mBaiduMap.addOverlay(overlayOptions);
					// 坐标设置为地图中心
					MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(latLng);
					mBaiduMap.setMapStatus(u);
					mBaiduMap.setOnMarkerClickListener(SearchMapActivity.this);
					addWindow(marker.getPosition());
				}
			}
		});
	}
	/**]
	 * 
	 * @Title: addWindow 
	 * @Description: 此处存在BUG，在定位完成之后调用次方法，地图上根本不显示InfoWindow，只有在initLocation（）方法第二次调用的时候才会显示InfoWindow
	 * @param latLng
	 * @return: void
	 */
	private void addWindow(LatLng latLng){
		// 生成一个TextView用户在地图中显示InfoWindow
		TextView tv = new TextView(SearchMapActivity.this);
		tv.setBackgroundResource(R.drawable.shape_popup);
		tv.setPadding(30, 20, 30, 20);
		tv.setText(bdLocation.getAddrStr());
		Point p = mBaiduMap.getProjection().toScreenLocation(latLng);
		p.y -= 47;
		LatLng llInfo = mBaiduMap.getProjection().fromScreenLocation(p);
		InfoWindow mInfoWindow = new InfoWindow(tv, llInfo, -47);
		mBaiduMap.showInfoWindow(mInfoWindow);
	}
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.search_btn1:
			initLocation();
			break;
		case R.id.search_btn2:
			Intent intent = new Intent(SearchMapActivity.this,SearchInfoActivity.class);
			if(bdLocation!=null){
				Bundle bundle = new Bundle();
				bundle.putSerializable("locationInfo", getLocationInfo(bdLocation));
				intent.putExtras(bundle);
				startActivity(intent);
			}
			break;
		default:
			break;
		}
	}
	/**
	 * @Title: getLocationInfo 
	 * @Description: 因为BDLocation对象没有实现序列化的接口，所以不可以通过Bundle传递，所以只能将BDLocation对象的数据转化为自己的LocationInfo
	 * @param location
	 * @return
	 * @return: LocationInfo
	 */
	private LocationInfo getLocationInfo(BDLocation location){
		LocationInfo info = new LocationInfo();
		info.setAddress(location.getAddrStr());
		info.setCity(location.getCity());
		info.setCityCode(location.getCityCode());
		info.setCountry(location.getCountry());
		info.setCountryCode(location.getCountryCode());
		info.setDistrict(location.getDistrict());
		info.setLatitude(location.getLatitude());
		info.setLongitude(location.getLongitude());
		info.setProvince(location.getProvince());
		info.setStreet(location.getStreet());
		info.setStreetNumber(location.getStreetNumber());
		return info;
	}
	@Override
	public boolean onMarkerClick(Marker arg0) {
		addWindow(arg0.getPosition());
		return true;
	}
}
