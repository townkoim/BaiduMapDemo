package com.lb.baidumapdemo.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.activity.search.SearchMapActivity;


public class MainActivity extends Activity implements OnClickListener{
	private Button btn1,btn2,btn3,btn4,btn5;
	private Button[] btn = new Button[]{btn1,btn2,btn3,btn4,btn5};
	private int[] id=new int[]{R.id.button1,R.id.button2,R.id.button3,R.id.button4,R.id.button5};
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for(int i = 0;i<btn.length;i++){
        	btn[i]=(Button) findViewById(id[i]);
        	btn[i].setOnClickListener(this);
        }
    }
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.button1:
			//地图定位，显示定位图标
			startActivity(new Intent(MainActivity.this,BaseMapActivity.class));
			break;
		case R.id.button2:
			//多覆盖物，并显示弹框
			startActivity(new Intent(MainActivity.this,MarkerMapActivity.class));
			break;
		case R.id.button3:
			//检索、线路查询与GL绘制
			startActivity(new Intent(MainActivity.this,SearchMapActivity.class));
			break;
		case R.id.button4:
			//导航
			startActivity(new Intent(MainActivity.this,NavigationMapActivity.class));
			break;
		case R.id.button5:
			//全景
			startActivity(new Intent(MainActivity.this,PanoramaMapActivity.class));
			break;
		default:
			break;
		}
	}

}
