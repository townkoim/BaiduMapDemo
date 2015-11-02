package com.lb.baidumapdemo.activity.search;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;
import java.util.ArrayList;
import java.util.List;

import javax.microedition.khronos.opengles.GL10;

import android.graphics.Color;
import android.graphics.PointF;
import android.os.Bundle;

import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BaiduMap.OnMapDrawFrameCallback;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.map.PolylineOptions;
import com.baidu.mapapi.model.LatLng;
import com.lb.baidumapdemo.R;
import com.lb.baidumapdemo.base.BaseActivity;
import com.lb.baidumapdemo.model.MyLatLng;

/***
* @ClassName: GLMapActivity  
* @Description: 得到搜索结果界面传递过来的经纬度，进行线路绘制的界面 
* @author libiao 
* @date 2015-8-24 上午9:56:13  
*
 */
public class GLMapActivity extends BaseActivity {
	private MapView mMapView;
	private BaiduMap mBaiduMap;
	private List<MyLatLng> listLat;
	private float[] vertexs;
	private FloatBuffer vertexBuffer;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_glmap);
		mMapView = (MapView) findViewById(R.id.glmap);
		initMap();
	}

	private void initMap() {
		mBaiduMap = mMapView.getMap();
		listLat = (List<MyLatLng>) getIntent().getSerializableExtra("latlng");
		// 设置地图类型 MAP_TYPE_NORMAL 普通图； MAP_TYPE_SATELLITE 卫星图
		mBaiduMap.setMapType(BaiduMap.MAP_TYPE_NORMAL);
		// 开启交通图
		mBaiduMap.setTrafficEnabled(true);
		MapStatusUpdate statusUpdate = MapStatusUpdateFactory.zoomTo(19);
		mBaiduMap.setMapStatus(statusUpdate);
		MyLatLng myLatLng = listLat.get(0);
		MapStatusUpdate u = MapStatusUpdateFactory.newLatLng(new LatLng(myLatLng.getLatitude(), myLatLng.getLongitude()));
		mBaiduMap.setMapStatus(u);
		// mBaiduMap.setOnMapDrawFrameCallback(callback);
		List<LatLng> lists = new ArrayList<LatLng>();
		for (MyLatLng mll : listLat) {
			LatLng ll = new LatLng(mll.getLatitude(), mll.getLongitude());
			lists.add(ll);
		}

		/**
		 * 地图SDK提供多种结合图形覆盖物，利用这些图形，可帮助您构建更加丰富多彩的地图应用。目前提供的几何图形有：点（Dot）、折线（
		 * Polyline）、弧线（Arc）、圆（Circle）、多边形（Polygon）。 此处绘制折线
		 */
		OverlayOptions polygonOption = new PolylineOptions().points(lists).color(Color.parseColor("#FF0000")).width(7);
		// 在地图上添加多边形Option，用于显示
		mBaiduMap.addOverlay(polygonOption);

	}

	/******************** 使用OpenGl绘制，是出现Bug，坐标的转换和屏幕上的点的转换，会随着地图大小的拉伸，OpenGl的线不拉伸的情况，建议不要使用此方法 *********************/
	// 定义地图绘制每一帧时 OpenGL 绘制的回调接口
	OnMapDrawFrameCallback callback = new OnMapDrawFrameCallback() {
		public void onMapDrawFrame(GL10 gl, MapStatus drawingMapStatus) {
			if (mBaiduMap.getProjection() != null) {
				// 计算折线的 opengl 坐标
				calPolylinePoint(drawingMapStatus);
				// 绘制折线
				drawPolyline(gl, Color.argb(255, 255, 0, 0), vertexBuffer, 10, 3, drawingMapStatus);
			}
		}
	};

	// 计算折线 OpenGL 坐标
	public void calPolylinePoint(MapStatus mspStatus) {
		PointF[] polyPoints = new PointF[listLat.size()];
		vertexs = new float[3 * listLat.size()];
		int i = 0;
		for (MyLatLng xy : listLat) {
			// 将地理坐标转换成 openGL 坐标
			polyPoints[i] = mBaiduMap.getProjection().toOpenGLLocation(new LatLng(xy.getLatitude(), xy.getLongitude()), mspStatus);
			vertexs[i * 3] = polyPoints[i].x;
			vertexs[i * 3 + 1] = polyPoints[i].y;
			vertexs[i * 3 + 2] = 0.0f;
			i++;
		}
		vertexBuffer = makeFloatBuffer(vertexs);
	}

	// 创建OpenGL绘制时的顶点Buffer
	private FloatBuffer makeFloatBuffer(float[] fs) {
		ByteBuffer bb = ByteBuffer.allocateDirect(fs.length * 4);
		bb.order(ByteOrder.nativeOrder());
		FloatBuffer fb = bb.asFloatBuffer();
		fb.put(fs);
		fb.position(0);
		return fb;
	}

	// 绘制折线
	private void drawPolyline(GL10 gl, int color, FloatBuffer lineVertexBuffer, float lineWidth, int pointSize, MapStatus drawingMapStatus) {

		gl.glEnable(GL10.GL_BLEND);
		gl.glEnableClientState(GL10.GL_VERTEX_ARRAY);

		gl.glBlendFunc(GL10.GL_SRC_ALPHA, GL10.GL_ONE_MINUS_SRC_ALPHA);

		float colorA = Color.alpha(color) / 255f;
		float colorR = Color.red(color) / 255f;
		float colorG = Color.green(color) / 255f;
		float colorB = Color.blue(color) / 255f;

		gl.glVertexPointer(3, GL10.GL_FLOAT, 0, lineVertexBuffer);
		gl.glColor4f(colorR, colorG, colorB, colorA);
		gl.glLineWidth(lineWidth);
		gl.glDrawArrays(GL10.GL_LINE_STRIP, 0, pointSize);

		gl.glDisable(GL10.GL_BLEND);
		gl.glDisableClientState(GL10.GL_VERTEX_ARRAY);
	}
}
