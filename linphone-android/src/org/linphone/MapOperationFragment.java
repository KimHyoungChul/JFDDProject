package org.linphone;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.linphone.LinphoneManager.AddressType;
import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.ui.AddressText;

import com.dlxxzx.tianditu.TianDiTuTiledMapServiceLayer;
import com.dlxxzx.tianditu.TianDiTuTiledMapServiceType;
import com.esri.android.map.*;
import com.esri.android.map.ags.ArcGISDynamicMapServiceLayer;
import com.esri.android.map.ags.ArcGISFeatureLayer;
import com.esri.android.map.event.OnSingleTapListener;
import com.esri.android.map.event.OnStatusChangedListener;
import com.esri.android.runtime.ArcGISRuntime;

import com.esri.core.geometry.Point;
import com.esri.core.map.Graphic;
import com.esri.core.symbol.SimpleMarkerSymbol;
import com.esri.core.symbol.SimpleMarkerSymbol.STYLE;
import com.jfdd.dataMaker.UserInfoData;



import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.widget.DrawerLayout.LayoutParams;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import android.widget.Button;
import android.widget.TextView;

public class MapOperationFragment extends Fragment  {

	private static final double DEFAULT_SCALE = 350000;
	private static final double GPS_SCALE = 30000;
	private MapView mMapView;// 地图view
	private GraphicsLayer mGraphicsLayer;
//	private ArcGISTiledMapServiceLayer mVectorLayer; // 在线矢量图层
	
	
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view = inflater.inflate(R.layout.fragment_map_operation, container, false);
    	initMap(view);
        return view;
    }   
    
	/**
	 * 初始化地图
	 * 
	 * @param view
	 */
	private void initMap(final View view) {
		ArcGISRuntime.setClientId(getResources().getString(R.string.arcgiskey));
		mMapView=(MapView)view.findViewById(R.id.mvMap_basemap);
		mMapView.setEsriLogoVisible(false);
		mMapView.setMapBackground(0xffffff, 0xffffff, 1, 1);

		TianDiTuTiledMapServiceLayer t_vec_label = new TianDiTuTiledMapServiceLayer(TianDiTuTiledMapServiceType.CVA_C);
		TianDiTuTiledMapServiceLayer t_vec_map = new TianDiTuTiledMapServiceLayer(TianDiTuTiledMapServiceType.VEC_C);
		mMapView.addLayer(t_vec_map);
		mMapView.addLayer(t_vec_label);
		mMapView.setOnStatusChangedListener(layerLoadListener);
		
		mMapView.setOnSingleTapListener(onSingleTapListener);
		
		mGraphicsLayer = new GraphicsLayer();
	    mMapView.addLayer(mGraphicsLayer);
		
	}

	@SuppressWarnings("serial")
	OnSingleTapListener onSingleTapListener=new OnSingleTapListener(){

		@SuppressWarnings("null")
		@Override
		public void onSingleTap(float x, float y) {

			int[]selectGraphicIDs=mGraphicsLayer.getGraphicIDs(x, y, 20);
			if(selectGraphicIDs.length>0){

				Graphic graphic=mGraphicsLayer.getGraphic(selectGraphicIDs[0]);
				Map<String, Object> map =graphic.getAttributes();
				String number=(String) map.get("number");
				String userName=(String)map.get("name");
			    Callout callout = mMapView.getCallout();  

	            callout.setOffset(0, 10); 
	            Point pointClicked = (Point) graphic.getGeometry(); 
	            
	            TextView textView = new TextView(getActivity());
	            textView.setText(number);
	            LayoutParams textViewLayoutParams = new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	            textView.setLayoutParams(textViewLayoutParams);
	            callout.show(pointClicked, textView);
	            
	            LinphoneManager.getInstance().newOutgoingCall(number+"",userName);
			}

		}
		
	};
/**
 * 地图上添加graphic
 * 
 * */	
	private void addGraphicsOnMap(){
		
		UserInfoData info=new UserInfoData();
    	List<UserInfoData> list=info.getUserInfoData();
    	for(int i=0;i<list.size();i++){
    		UserInfoData data=list.get(i);
    		Point tPoint=data.getUserLocation();
    		Map<String, Object> map = new HashMap<String, Object>();
    		map.put("number", data.getUserNumber());
    		map.put("name", data.getUserName());
    		Graphic graphic = new Graphic(tPoint,new SimpleMarkerSymbol(Color.RED,25,STYLE.CIRCLE),map);
    		mGraphicsLayer.addGraphic(graphic);
    	}
		
//		
//		Point tPoint=new Point(106.52252214551413, 29.55847182396155);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("number", 1001);
//		map.put("name", "罗欢");
//		
//		Point tPoint2=new Point(106.52251114551413, 29.55737082396155);
//		Map<String, Object> map2 = new HashMap<String, Object>();
//		map2.put("number", 3000);
//		map2.put("name", "王子");
//		
//		Point tPoint3=new Point(106.52241114551413, 29.53737082396155);
//		Map<String, Object> map3 = new HashMap<String, Object>();
//		map3.put("number", 1003);
//		map3.put("name", "章涛");
//		
//		Point tPoint4=new Point(106.52351114551413, 29.55237082396155);
//		Map<String, Object> map4 = new HashMap<String, Object>();
//		map4.put("number", 1009);
//		map4.put("name", "杨选伦");
//		
//		Point tPoint5=new Point(106.52221114551413, 29.53237082396155);
//		Map<String, Object> map5 = new HashMap<String, Object>();
//		map5.put("number", 10016);
//		map5.put("name", "杨选伦");
//		
//		
//		Graphic graphic = new Graphic(tPoint,new SimpleMarkerSymbol(Color.RED,25,STYLE.CIRCLE),map);
//		Graphic graphic2 = new Graphic(tPoint2,new SimpleMarkerSymbol(Color.BLUE,25,STYLE.CIRCLE),map2);
//		Graphic graphic3 = new Graphic(tPoint3,new SimpleMarkerSymbol(Color.YELLOW,25,STYLE.CIRCLE),map3);
//		Graphic graphic4 = new Graphic(tPoint4,new SimpleMarkerSymbol(Color.GREEN,25,STYLE.CIRCLE),map4);
//		Graphic graphic5 = new Graphic(tPoint5,new SimpleMarkerSymbol(Color.WHITE,25,STYLE.CIRCLE),map5);
//		mGraphicsLayer.addGraphic(graphic);	
//		mGraphicsLayer.addGraphic(graphic2);
//		mGraphicsLayer.addGraphic(graphic3);
//		mGraphicsLayer.addGraphic(graphic4);
//		mGraphicsLayer.addGraphic(graphic5);
	}
	
	
	
	@SuppressWarnings("serial")
	OnStatusChangedListener layerLoadListener = new OnStatusChangedListener() {
		@Override
		public void onStatusChanged(Object source, STATUS status) {
//			dismissProgressDialog();
			if (status == STATUS.INITIALIZED) {
				//mMapView.zoomToScale(new Point(106.52252214551413, 29.55847182396155), DEFAULT_SCALE);
				
			} else if (STATUS.LAYER_LOADED == status) {
				mMapView.postInvalidate();
				locationMapView();
				addGraphicsOnMap();
				
				if (source instanceof ArcGISDynamicMapServiceLayer) {
					// new FetchLegendTask().execute();
				}
				if (source instanceof ArcGISFeatureLayer) {
					
				}
			} else if (STATUS.INITIALIZATION_FAILED == status) {
				//showToast("地图初始化失败");
			} else if (STATUS.LAYER_LOADING_FAILED == status) {
				//showToast("地图加载失败");
			}
		}
	};
  
	/***
	 * 地图定位
	 * 
	 * **/
    private void locationMapView(){
    	final LocationDisplayManager locationManager = mMapView.getLocationDisplayManager();
		locationManager.setAllowNetworkLocation(true);
		locationManager.setLocationListener(locationListener);
		locationManager.setAutoPanMode(LocationDisplayManager.AutoPanMode.OFF);
		locationManager.start();
    	
    }
	LocationListener locationListener = new LocationListener() {
		@Override
		public void onStatusChanged(String provider, int status, Bundle extras) {
		}

		@Override
		public void onProviderEnabled(String provider) {
		}

		@Override
		public void onProviderDisabled(String provider) {
		}

		@Override
		public void onLocationChanged(Location location) {
			if (null != location) {
				Point point = new Point(location.getLongitude(), location.getLatitude());
				point=new Point(106.52252214551413, 29.55847182396155);
				mMapView.zoomToScale(point, GPS_SCALE);
			}
		}
	};
	
}
