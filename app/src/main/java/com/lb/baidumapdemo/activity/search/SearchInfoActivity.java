package com.lb.baidumapdemo.activity.search;

import java.util.ArrayList;
import java.util.List;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.baidu.location.BDLocation;
import com.baidu.mapapi.model.LatLng;
import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.baidu.mapapi.search.route.DrivingRoutePlanOption;
import com.baidu.mapapi.search.route.DrivingRouteResult;
import com.baidu.mapapi.search.route.OnGetRoutePlanResultListener;
import com.baidu.mapapi.search.route.PlanNode;
import com.baidu.mapapi.search.route.RoutePlanSearch;
import com.baidu.mapapi.search.route.TransitRoutePlanOption;
import com.baidu.mapapi.search.route.TransitRouteResult;
import com.baidu.mapapi.search.route.WalkingRoutePlanOption;
import com.baidu.mapapi.search.route.WalkingRouteResult;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.adapter.SearchInfoAdapter;
import com.lb.baidumapdemo.base.BaseActivity;
import com.lb.baidumapdemo.base.BaseApplication;
import com.lb.baidumapdemo.db.DBConstants;
import com.lb.baidumapdemo.db.ShareDB;
import com.lb.baidumapdemo.face.LocationFace;
import com.lb.baidumapdemo.model.LocationInfo;
import com.lb.baidumapdemo.util.LocationFaceUtil;
/**
* @ClassName: SearchInfoActivity  
* @Description: 搜索、线路规划的搜索界面
* @author libiao 
* @date 2015-8-24 上午9:41:51  
*
 */
public class SearchInfoActivity extends BaseActivity implements OnClickListener, OnItemClickListener, OnGetPoiSearchResultListener,
		OnGetRoutePlanResultListener {
	private Button btnBus, btnCar, btnWalk;// 公交。驾车。步行按钮
	private EditText etBegin, etEnd; // 开始和结束的输入文本框
	private ProgressBar barBegin, barEnd; // 文本框对应的进度条
	private ListView listView; // 用来显示兴趣点的列表
	private ProgressDialog dialog;

	private int currentId;// 当前控件的ID(区分起点和终点的EditText)
	private LocationInfo myLocationInfo;// 我的位置的信息

	private List<PoiInfo> listInfo;
	private SearchInfoAdapter adapter;
	// 使用经纬度查询线路
	private LatLng beginLat;
	private LatLng endLat;

	private PoiSearch poiSearch;// 兴趣点检索类
	private RoutePlanSearch routeSearch;// 线路检索类

	private int getLocationCount = 0;// 定义一个变量用来判断定位发起了几次，超过三次就直接跑出网络异常

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_search_info);
		initUI();
		initParams();
	}

	private void initUI() {
		btnBus = (Button) findViewById(R.id.info_btn_bus);
		btnCar = (Button) findViewById(R.id.info_btn_car);
		btnWalk = (Button) findViewById(R.id.info_btn_walk);
		etBegin = (EditText) findViewById(R.id.info_et_begin);
		etEnd = (EditText) findViewById(R.id.info_et_end);
		barBegin = (ProgressBar) findViewById(R.id.info_progress_begin);
		barEnd = (ProgressBar) findViewById(R.id.info_progress_end);
		listView = (ListView) findViewById(R.id.info_listview);
		etBegin.addTextChangedListener(new MyTextWatcher(R.id.info_et_begin));
		etEnd.addTextChangedListener(new MyTextWatcher(R.id.info_et_end));
		btnBus.setOnClickListener(SearchInfoActivity.this);
		btnCar.setOnClickListener(SearchInfoActivity.this);
		btnWalk.setOnClickListener(SearchInfoActivity.this);
		listView.setOnItemClickListener(SearchInfoActivity.this);
	}
	
	private void initParams() {
		poiSearch = PoiSearch.newInstance();
		poiSearch.setOnGetPoiSearchResultListener(SearchInfoActivity.this);
		routeSearch = RoutePlanSearch.newInstance();
		routeSearch.setOnGetRoutePlanResultListener(SearchInfoActivity.this);
		myLocationInfo = (LocationInfo) getIntent().getSerializableExtra("locationInfo");
		listInfo = new ArrayList<PoiInfo>();
		adapter = new SearchInfoAdapter(SearchInfoActivity.this, listInfo);
		listView.setAdapter(adapter);
	}

	/**
	 * @ClassName: MyTextWatcher
	 * @Description: 文字变化监听事件,当文字发生改变的时候，就调用百度的接口进行查询当前位置的信息。回调在onGetPoiResult中，在onGetPoiResult中加载到ListView当中
	 * @author libiao
	 * @date 2015-8-20 下午2:31:16
	 * 
	 */
	class MyTextWatcher implements TextWatcher {
		private int textId; //用来区分是哪个EditTextView控件

		public MyTextWatcher(int textId) {
			super();
			this.textId = textId;
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count, int after) {

		}

		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if (textId == R.id.info_et_begin) {
				currentId = R.id.info_et_begin;
				barBegin.setVisibility(View.VISIBLE);
				barEnd.setVisibility(View.INVISIBLE);
			} else {
				currentId = R.id.info_et_end;
				barBegin.setVisibility(View.INVISIBLE);
				barEnd.setVisibility(View.VISIBLE);
			}
			// 发起检索，此处调用的是城市内检索（还有范围内检索/周边检索/POI 详情检索三个方法，根据不同的需求来调用）
			if (!TextUtils.isEmpty(s.toString())) {
				PoiCitySearchOption psOtion = new PoiCitySearchOption();
				psOtion.city(new ShareDB(SearchInfoActivity.this).getValue(DBConstants.CITY_NAME));
				psOtion.keyword(s.toString());
				psOtion.pageNum(10);
				psOtion.pageCapacity(50);
				poiSearch.searchInCity(psOtion);
			}
		}

		@Override
		public void afterTextChanged(Editable s) {

		}

	}

	/******************* 兴趣点检索的回调事件 **************************/
	// 获取Place详情页检索结果
	@Override
	public void onGetPoiDetailResult(PoiDetailResult result) {

	}

	// 获取POI检索结果
	@Override
	public void onGetPoiResult(PoiResult result) {
		barBegin.setVisibility(View.INVISIBLE);
		barEnd.setVisibility(View.INVISIBLE);
		// 得到检索结果的集合，有时候检索结果返回的是空数据！
		if (result.getAllPoi() != null) {
			if (result.getAllPoi().size() > 0) {
				listInfo.clear();
				listInfo.addAll(result.getAllPoi());
				adapter.notifyDataSetChanged();
			}
		}
	}

	/**
	 * @Title: onItemClick
	 * @Description: 列表的点击事件
	 * @param parent
	 * @param view
	 * @param position
	 * @param id
	 */
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
		if (currentId == R.id.info_et_begin) {
			etBegin.setText(listInfo.get(position).address);
			etBegin.setSelection(etBegin.getText().toString().length());
			beginLat = listInfo.get(position).location;
		} else {
			etEnd.setText(listInfo.get(position).address);
			etEnd.setSelection(etEnd.getText().toString().length());
			endLat = listInfo.get(position).location;
		}
	}

	/**
	 * @Title: onClick
	 * @Description: 三个Button的点击事件
	 * @param v
	 */
	@Override
	public void onClick(View v) {
		String begin = etBegin.getText().toString();
		String end = etEnd.getText().toString();
		if (!TextUtils.isEmpty(begin)) {
			if (!TextUtils.isEmpty(end)) {
				if (dialog == null) {
					dialog = new ProgressDialog(SearchInfoActivity.this);
					dialog.setTitle("线路查询");
					dialog.setMessage("正在查询所有线路");
				}
				dialog.show();
				switch (v.getId()) {
				case R.id.info_btn_bus:
					// startRouterFilter(1, begin, end);
					startRouter(1, beginLat, endLat);
					break;
				case R.id.info_btn_car:
					// startRouterFilter(2, begin, end);
					startRouter(2, beginLat, endLat);
					break;
				case R.id.info_btn_walk:
					// startRouterFilter(3, begin, end);
					startRouter(3, beginLat, endLat);
					break;

				default:
					break;
				}
			} else {
				Toast.makeText(SearchInfoActivity.this, "终点不能为空", Toast.LENGTH_SHORT).show();
			}
		} else {
			Toast.makeText(SearchInfoActivity.this, "起始点不能为空", Toast.LENGTH_SHORT).show();
		}
	}

	/******************************************** 此处开始是通过地址信息进行线路查询 Begin ***************************************************/

	/**
	 * @Title: startRouterResult
	 * @Description: 根据type开始查询线路
	 * @param type
	 *            当type=1的时候表示是公交查询，2表示驾车查询，3表示走路查询
	 * @return: void
	 */

	private void startRouterFilter(final int type, String startAddr, final String endAddr) {
		if (startAddr.equals("我的位置")) {
			if (myLocationInfo != null) {
				startAddr = myLocationInfo.getAddress();
				startRouterResult(type, startAddr, endAddr);
			} else {
				getLocationCount++;
				new LocationFaceUtil(SearchInfoActivity.this, new LocationFace() {
					@Override
					public void locationResult(BDLocation location) {
						if (location != null && location.hasAddr()) {
							startRouterResult(type, location.getAddress().address, endAddr);
						} else {
							if (getLocationCount > 2) {
								Toast.makeText(SearchInfoActivity.this, "获取我的位置失败，请检查网络", Toast.LENGTH_SHORT).show();
								return;
							} else {
								startRouterFilter(type, "我的位置", endAddr);
							}
						}
					}
				});
			}
		} else {
			startRouterResult(type, startAddr, endAddr);
		}
	}

	/**
	 * @Title: startRouterResult
	 * @Description: 此处查询是根据PlanNodewithCityNameAndPlaceName(java.lang .String
	 *               city, java.lang.String placeName)查询，就是根据城市名称以及出行点的名称查询
	 *               （可以利用经纬度进行查询，经纬度查询调用withLocation(LatLng location)方法即可）
	 *               经过测试，根据地点查询路线，经常会出现地址歧义的错误！！建议使用经纬度查询
	 * @param type
	 *            当type=1的时候表示是公交查询，2表示驾车查询，3表示走路查询
	 * @param startAddr
	 *            起点
	 * @param endAddr
	 *            终点
	 * @return: void
	 */
	private void startRouterResult(int type, String startAddr, String endAddr) {
		String cityName = new ShareDB(SearchInfoActivity.this).getValue(DBConstants.CITY_NAME);
		PlanNode stNode = PlanNode.withCityNameAndPlaceName(cityName, startAddr);
		PlanNode enNode = PlanNode.withCityNameAndPlaceName(cityName, endAddr);
		if (type == 1) {
			routeSearch.transitSearch(new TransitRoutePlanOption().from(stNode).to(enNode).city(cityName));
		} else if (type == 2) {
			routeSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
		} else if (type == 3) {
			routeSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
		}
	}

	/******************************************** 此处开始是通过地址信息进行线路查询 End ***************************************************/

	/******************************************** 此处开始是通过经纬度进行线路查询 ***************************************************/
	private void startRouter(final int type, LatLng beLat, final LatLng endLat) {
		String beginAddr = etBegin.getText().toString();
		if (beginAddr.equals("我的位置")) {
			if (myLocationInfo != null) {
				beLat = new LatLng(myLocationInfo.getLatitude(), myLocationInfo.getLongitude());
				startRouterResult(type, beLat, endLat);
			} else {
				getLocationCount++;
				new LocationFaceUtil(SearchInfoActivity.this, new LocationFace() {
					@Override
					public void locationResult(BDLocation location) {
						if (location != null && location.hasAddr()) {
							startRouterResult(type, new LatLng(location.getLatitude(), location.getLongitude()), endLat);
						} else {
							if (getLocationCount > 2) {
								closeDialog();
								Toast.makeText(SearchInfoActivity.this, "获取我的位置失败，请检查网络", Toast.LENGTH_SHORT).show();
								return;
							} else {
								startRouterResult(type, null, endLat);
							}
						}
					}
				});

			}
		} else {
			startRouterResult(type, beLat, endLat);
		}
	}

	private void startRouterResult(final int type, LatLng beLat, LatLng endLat) {
		/***
		 * 此处应该判断传递过来的经纬度是不是空的，因为有可能不是在listInfo集合里面取出来的数据，如果为空，就要根据控件上的文字，进行坐标反查
		 * ，得到坐标，然后再调用这个方法 ||如果经纬度为空，则用地址信息来进行线路的查询，不过此时查询出来的结果可能为空
		 **/
		if (beLat != null && endLat != null) {
			String cityName = new ShareDB(SearchInfoActivity.this).getValue(DBConstants.CITY_NAME);
			PlanNode stNode = PlanNode.withLocation(beLat);
			PlanNode enNode = PlanNode.withLocation(endLat);
			if (type == 1) {
				routeSearch.transitSearch(new TransitRoutePlanOption().from(stNode).to(enNode).city(cityName));
			} else if (type == 2) {
				routeSearch.drivingSearch(new DrivingRoutePlanOption().from(stNode).to(enNode));
			} else if (type == 3) {
				routeSearch.walkingSearch(new WalkingRoutePlanOption().from(stNode).to(enNode));
			}
		} else {
			startRouterResult(type, etBegin.getText().toString(), etEnd.getText().toString());
		}
	}

	/******************** 线路查询返回的结果 ***********************/
	// 因DrivingRouteResult、TransitRouteResult、WalkingRouteResult都是继承SearchResult，没有实现序列化，所以无法通过bundle来进行传递到下一个页面。
	// 1、可以通过自定义Model类来对数据进行封装，实现序列化的接口来传递给下个界面
	// 2、可以通过在Application类里定义这三个类的对象，然后再此处赋值，在下一个界面的时候就直接得到（本次就是用的这个方法）
	@Override
	public void onGetDrivingRouteResult(DrivingRouteResult drivingRouteResult) {
		closeDialog();
		if (drivingRouteResult.error.toString().equals("NO_ERROR")) {
			BaseApplication.drivingRouteResult = drivingRouteResult;
			startIntent(2);
		} else {
			Toast.makeText(SearchInfoActivity.this, "未找到路线，请重新选择起点或者终点", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetTransitRouteResult(TransitRouteResult transitRouteResult) {
		closeDialog();
		if (transitRouteResult.error.toString().equals("NO_ERROR")) {
			BaseApplication.transitRouteResult = transitRouteResult;
			startIntent(1);
		} else {
			Toast.makeText(SearchInfoActivity.this, "未找到路线，请重新选择起点或者终点", Toast.LENGTH_SHORT).show();
		}
	}

	@Override
	public void onGetWalkingRouteResult(WalkingRouteResult walkingRouteResult) {
		closeDialog();
		if (walkingRouteResult.error.toString().equals("NO_ERROR")) {
			BaseApplication.walkingRouteResult = walkingRouteResult;
			startIntent(3);
		} else {
			Toast.makeText(SearchInfoActivity.this, "未找到路线，请重新选择起点或者终点", Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * @Title: startIntent 
	 * @Description:跳转到SearchResultActivity
	 * @param type
	 * @return: void
	 */
	private void startIntent(int type) {
		Intent intent = new Intent(SearchInfoActivity.this, SearchResultActivity.class);
		intent.putExtra("type", type);
		startActivity(intent);
	}
	/**
	 * @Title: closeDialog 
	 * @Description: 关闭进度条对话框
	 * @return: void
	 */
	private void closeDialog() {
		if (dialog != null && dialog.isShowing()) {
			dialog.dismiss();
		}
	}
}
