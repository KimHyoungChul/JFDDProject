package org.linphone;

import java.util.List;
import java.util.Map;

import org.linphone.mediastream.Log;

import com.jfdd.dataMaker.UserInfoData;

import android.content.Context;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 * 一个简单的ListViewAdapter适配器写法，内含跳转方法。
 * 
 * @author 山鹰1985
 *
 *         http://blog.csdn.net/u012137924
 *
 */
public class ListViewAdapter extends BaseAdapter implements OnClickListener {

	// 上下文
	private Context context;
	private LayoutInflater inflater;
	// List表
	private List<UserInfoData> mList;

	// 构造函数
	public ListViewAdapter(Context context, List<UserInfoData> list) {
		this.context = context;
		mList = list;
	}



	// List表单的总数
	public int getCount() {
		return mList.size();
	}

	// 位于position处的List表单的一项
	public Object getItem(int position) {
		return mList.get(position);
	}

	// List表单的各项的索引
	public long getItemId(int position) {
		return position;
	}

	// 最重要的获得视图内容方法
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView =  LayoutInflater.from(context).inflate(
				R.layout.list_item, null);

		// 根据布局并通过控件各自的ID找控件。
		TextView username = (TextView) convertView.findViewById(R.id.list_user_name);
		TextView usernumber = (TextView) convertView
				.findViewById(R.id.list_user_number);
		ImageView phonedialer=(ImageView)convertView.findViewById(R.id.list_phone_dialer);
		

		// 为每项的TextView加个Tag
		phonedialer.setTag(position);
		
		UserInfoData info=mList.get(position);
		username.setText(info.getUserName());
		usernumber.setText(info.getUserNumber());
		phonedialer.setOnClickListener(this);
/*
		if (convertView == null) {
			viewCache = new ViewCache();

			// LayoutInflater布局填充器，名副其实，见名知意。
			convertView =  LayoutInflater.from(context).inflate(
					R.layout.list_item, null);

			// 根据布局并通过控件各自的ID找控件。
			viewCache.username = (TextView) convertView.findViewById(R.id.list_user_name);
			viewCache.usernumber = (TextView) convertView
					.findViewById(R.id.list_user_number);
			viewCache.phonedialer=(ImageView)convertView.findViewById(R.id.list_phone_dialer);
			

			// 为每项的TextView加个Tag
			viewCache.phonedialer.setTag(position);

			// 为convertView添加标记
			convertView.setTag(viewCache);

		} else {

			// 根据标记找到viewCache，达到缓存的目的（软引用）
			viewCache = (ViewCache) convertView.getTag();
		}

		// 给TextView设置内容
		//(String) mList.get(position).get("number")
		
		UserInfoData info=mList.get(position);
		viewCache.username.setText(info.getUserName());
		viewCache.usernumber.setText(info.getUserNumber());

		// 添加监听事件
		viewCache.phonedialer.setOnClickListener(this);
*/
		return convertView;
	}

	// 监听事件实现
	public void onClick(View v) {
		int destination = (Integer) v.getTag();

		LinphoneManager.getInstance().newOutgoingCall(mList.get(destination).getUserNumber(),mList.get(destination).getUserName());
		//LinphoneManager.getInstance().routeAudioToSpeaker();
		Log.d("yxl test  lkajdflafdals.....",mList.get(destination).getUserNumber());
	}

	// 根据传进来的action跳转到不同的Activity
	private void startActivityMethod(String action) {
		//Intent intent = new Intent();

		// 下面2行都可以跳转，不过跳转的方式不一样。
		// intent.setClassName("com.testlistview","com.testlistview.TestActivity");
		//intent.setAction(action);
		//inflater.startActivity(intent);
	}

	// 辅助缓存类
	class ViewCache {
		TextView username, usernumber;
		ImageView phonedialer;
	}

}
