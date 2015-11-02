package com.lb.baidumapdemo.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.lb.baidumapdemo.R;

public class SearchInfoAdapter extends BaseAdapter{
	private Context context;
	private List<PoiInfo> list;
	private ViewHolder holder;
	
	
	public SearchInfoAdapter(Context context, List<PoiInfo> list) {
		super();
		this.context = context;
		this.list = list;
	}

	@Override
	public int getCount() {
		return list.size();
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if(convertView==null){
			convertView=LayoutInflater.from(context).inflate(R.layout.popup,parent,false);
			holder = new ViewHolder();
			holder.tv=(TextView) convertView.findViewById(R.id.popup_text);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder) convertView.getTag();
		}
		holder.tv.setText(list.get(position).address);
		return convertView;
	}
	private class ViewHolder{
		private TextView tv;
	}

}
