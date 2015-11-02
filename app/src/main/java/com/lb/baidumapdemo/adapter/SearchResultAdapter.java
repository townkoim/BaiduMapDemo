package com.lb.baidumapdemo.adapter;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.RouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine;
import com.baidu.mapapi.search.route.DrivingRouteLine.DrivingStep;
import com.baidu.mapapi.search.route.TransitRouteLine;
import com.baidu.mapapi.search.route.TransitRouteLine.TransitStep;
import com.baidu.mapapi.search.route.WalkingRouteLine;
import com.baidu.mapapi.search.route.WalkingRouteLine.WalkingStep;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.util.TimeUtil;

public class SearchResultAdapter extends BaseExpandableListAdapter {
	private List<RouteLine> listRoute;
	private Context context;
	private int type;

	public SearchResultAdapter(Context context, List<RouteLine> listRoute,int type) {
		super();
		this.context=context;
		this.listRoute = listRoute;
		this.type=type;
	}

	@Override
	public int getGroupCount() {
		// TODO Auto-generated method stub
		return listRoute.size();
	}

	@Override
	public int getChildrenCount(int groupPosition) {
		// TODO Auto-generated method stub
		return listRoute.get(groupPosition).getAllStep().size();
	}

	@Override
	public Object getGroup(int groupPosition) {
		return listRoute.get(groupPosition);
	}

	@Override
	public Object getChild(int groupPosition, int childPosition) {
		return listRoute.get(groupPosition).getAllStep().get(childPosition);
	}

	@Override
	public long getGroupId(int groupPosition) {
		// TODO Auto-generated method stub
		return groupPosition;
	}

	@Override
	public long getChildId(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return childPosition;
	}

	@Override
	public boolean hasStableIds() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean isChildSelectable(int groupPosition, int childPosition) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
		TextView text =getGenericView("线路"+(groupPosition+1)+":"+listRoute.get(groupPosition).getDistance()+"米/"+TimeUtil.formatTime(listRoute.get(groupPosition).getDuration()));
		text.setBackgroundColor(Color.parseColor("#1874CD"));
		text.setTextColor(Color.parseColor("#000000"));
		text.setTextSize(18);
		text.setPadding(50,20, 30, 20);
		return text;
	}

	@Override
	public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
		TextView text = null;
		if(type==1){
			TransitRouteLine.TransitStep tran=(TransitStep) listRoute.get(groupPosition).getAllStep().get(childPosition);
			text=getGenericView(tran.getInstructions());
		}else if(type==2){
			DrivingRouteLine.DrivingStep tran=(DrivingStep) listRoute.get(groupPosition).getAllStep().get(childPosition);
			text=getGenericView(tran.getInstructions());
		}else if(type==3){
			WalkingRouteLine.WalkingStep tran=(WalkingStep) listRoute.get(groupPosition).getAllStep().get(childPosition);
			text=getGenericView(tran.getInstructions());
		}
		return text;
	}

	// 创建组/子视图
	public TextView getGenericView(String msg) {
		TextView text = new TextView(context);
		// Center the text vertically
		text.setGravity(Gravity.CENTER_VERTICAL | Gravity.LEFT);
		text.setBackgroundResource(R.drawable.shape_popup);
		text.setPadding(30, 20, 30, 20);
		text.setText(msg);
		return text;
	}

}
