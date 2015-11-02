package com.lb.baidumapdemo.activity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.geocode.GeoCodeOption;
import com.baidu.mapapi.search.geocode.GeoCodeResult;
import com.baidu.mapapi.search.geocode.GeoCoder;
import com.baidu.mapapi.search.geocode.OnGetGeoCoderResultListener;
import com.baidu.mapapi.search.geocode.ReverseGeoCodeResult;
import com.baidu.navisdk.adapter.BNRoutePlanNode;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.base.BaseActivity;

/**
 * @ClassName: NavigationMapActivity
 * @Description: 导航界面
 * @author libiao
 * @date 2015-8-24 下午2:11:25
 * 
 */
public class NavigationMapActivity extends BaseActivity implements OnClickListener {
	private EditText etBegin, etEnd;
	private Button btn;
	private GeoCoder mSearch;

	private String begin;
	private String end;
	private BNRoutePlanNode sNode = null;
	private BNRoutePlanNode eNode = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_navigationmap);
		etBegin = (EditText) findViewById(R.id.navigation_start);
		etEnd = (EditText) findViewById(R.id.navigation_end);
		btn = (Button) findViewById(R.id.navigation_btn);
		btn.setOnClickListener(this);
		mSearch = GeoCoder.newInstance();
		mSearch.setOnGetGeoCodeResultListener(listener);
	}

	@Override
	public void onClick(View v) {
		begin = etBegin.getText().toString();
		end = etEnd.getText().toString();
		if (!TextUtils.isEmpty(begin) && !TextUtils.isEmpty(end)) {
			mSearch.geocode(new GeoCodeOption().city("深圳").address(begin));
			mSearch.geocode(new GeoCodeOption().city("深圳").address(end));
		}
	}

	OnGetGeoCoderResultListener listener = new OnGetGeoCoderResultListener() {
		public void onGetGeoCodeResult(GeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有检索到结果
			}
			// 获取地理编码结果
		}

		@Override
		public void onGetReverseGeoCodeResult(ReverseGeoCodeResult result) {
			if (result == null || result.error != SearchResult.ERRORNO.NO_ERROR) {
				// 没有找到检索结果
			}
			// 获取反向地理编码结果
		}
	};
}
