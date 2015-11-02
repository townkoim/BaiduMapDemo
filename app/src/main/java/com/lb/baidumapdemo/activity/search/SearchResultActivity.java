package com.lb.baidumapdemo.activity.search;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;

import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.core.RouteStep;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.adapter.SearchResultAdapter;
import com.lb.baidumapdemo.base.BaseActivity;
import com.lb.baidumapdemo.base.BaseApplication;
import com.lb.baidumapdemo.model.MyLatLng;

/**
* @ClassName: SearchResultActivity  
* @Description: 搜索结果界面
* @author libiao 
* @date 2015-8-24 上午9:55:58  
*
 */
public class SearchResultActivity extends BaseActivity implements OnChildClickListener{
	private ExpandableListView listView;
	private int type; //接收上个界面传过来的type的值， 当type=1的时候表示是公交查询，2表示驾车查询，3表示走路查询
	private List<RouteLine> listRoute=new ArrayList<RouteLine>(); 
	private SearchResultAdapter adapter;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_result);
		listView=(ExpandableListView) findViewById(R.id.result_expandableListView);
		type=getIntent().getIntExtra("type", 0);
		initUI();
		initParams();
	}
	private void initUI() {
		adapter = new SearchResultAdapter(SearchResultActivity.this, listRoute,type);
		listView.setAdapter(adapter);
		listView.setOnChildClickListener(SearchResultActivity.this);
	}
	/**
	 * @Title: initParams 
	 * @Description: 初始化数据，因为不同的type。传递过来的的返回值不同（都为RouteLine的子类）
	 * @return: void
	 */
	private void initParams() {
		if(type==1){
			List<TransitRouteLine> list=BaseApplication.transitRouteResult.getRouteLines();
			for(int i=0;i<list.size();i++){
				RouteLine lines=list.get(i);
				listRoute.add(lines);
			}
		}else if(type ==2){
			List<DrivingRouteLine> list=BaseApplication.drivingRouteResult.getRouteLines();
			for(int i=0;i<list.size();i++){
				RouteLine lines=list.get(i);
				listRoute.add(lines);
			}
			
		}else if(type==3){
			List<WalkingRouteLine> list=BaseApplication.walkingRouteResult.getRouteLines();
			for(int i=0;i<list.size();i++){
				RouteLine lines=list.get(i);
				listRoute.add(lines);
			}
		}
		adapter.notifyDataSetChanged();
	}
	/**
	 * @Title: onChildClick 
	 * @Description: ExpandableListView的子项的点击事件，通过自定义MyLatLng实体类来传递经纬度过去，因为百度自带的LatLng没有实现序列化接口
	 * @param parent
	 * @param v
	 * @param groupPosition 当前组的position
	 * @param childPosition  当前组的子项的position
	 * @param id
	 * @return
	 */
	@Override
	public boolean onChildClick(ExpandableListView parent, View v, int groupPosition, int childPosition, long id) {
		RouteStep step=(RouteStep) listRoute.get(groupPosition).getAllStep().get(childPosition);
		List<LatLng> listLat=step.getWayPoints();
		List<MyLatLng> mListLat = new ArrayList<MyLatLng>();
		for(int i=0;i<listLat.size();i++){
			MyLatLng mll = new MyLatLng(listLat.get(i).latitude, listLat.get(i).longitude);
			mListLat.add(mll);
		}
		Intent intent = new Intent(SearchResultActivity.this,GLMapActivity.class);
		intent.putExtra("latlng",(Serializable) mListLat);
		startActivity(intent);
		return true;
	}
}
