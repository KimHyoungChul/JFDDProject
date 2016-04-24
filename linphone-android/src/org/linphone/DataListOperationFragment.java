package org.linphone;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.esri.core.geometry.Point;
import com.jfdd.dataMaker.UserInfoData;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;

public class DataListOperationFragment extends Fragment {
	private ListView mListView;
	private ListViewAdapter mListViewAdapter;
	private List<UserInfoData> mList;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
    	View view=inflater.inflate(R.layout.fragment_data_list_operation, container, false);
    	UserInfoData info=new UserInfoData();
    	mList=info.getUserInfoData();
//    	mList = new ArrayList<Map<String, Object>>();
//		Point tPoint=new Point(106.52252214551413, 29.55847182396155);
//		Map<String, Object> map = new HashMap<String, Object>();
//		map.put("number", 1001);
//		map.put("name", "ÂÞ»¶");
//		
//		Point tPoint2=new Point(106.52251114551413, 29.55737082396155);
//		Map<String, Object> map2 = new HashMap<String, Object>();
//		map2.put("number", 3000);
//		map2.put("name", "Íõ×Ó");
//		
//		Point tPoint3=new Point(106.52241114551413, 29.53737082396155);
//		Map<String, Object> map3 = new HashMap<String, Object>();
//		map3.put("number", 1003);
//		map3.put("name", "ÕÂÌÎ");
//		
//		Point tPoint4=new Point(106.52351114551413, 29.55237082396155);
//		Map<String, Object> map4 = new HashMap<String, Object>();
//		map4.put("number", 1009);
//		map4.put("name", "ÑîÑ¡Â×");
//		
//		Point tPoint5=new Point(106.52221114551413, 29.53237082396155);
//		Map<String, Object> map5 = new HashMap<String, Object>();
//		map5.put("number", 1012);
//		map5.put("name", "ÑîÑ¡Â×");
//		
//		mList.add(map);
//		mList.add(map2);
//		mList.add(map3);
//		mList.add(map4);
//		mList.add(map5);
		
		mListView = (ListView)view.findViewById(R.id.testListView);

		mListViewAdapter = new ListViewAdapter(this.getActivity(), mList);
//		SimpleAdapter adapter = new SimpleAdapter(getActivity(), getData(list), R.layout.item_list, new String[]{"title"}, new int[]{R.id.title});  
		mListView.setAdapter(mListViewAdapter);
 
	       

		
        return view;
    }

}
