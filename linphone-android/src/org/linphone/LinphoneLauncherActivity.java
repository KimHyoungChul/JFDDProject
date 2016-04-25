/*
LinphoneLauncherActivity.java
Copyright (C) 2011  Belledonne Communications, Grenoble, France

This program is free software; you can redistribute it and/or
modify it under the terms of the GNU General Public License
as published by the Free Software Foundation; either version 2
of the License, or (at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program; if not, write to the Free Software
Foundation, Inc., 59 Temple Place - Suite 330, Boston, MA  02111-1307, USA.
 */
package org.linphone;

import static android.content.Intent.ACTION_MAIN;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.linphone.jifeng.service.GetPostUtil;
import org.linphone.mediastream.Log;
import org.linphone.setup.RemoteProvisioningActivity;
import org.linphone.tutorials.TutorialLauncherActivity;

import android.R.string;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

/**
 * 
 * Launch Linphone main activity when Service is ready.
 * 
 * @author Guillaume Beraudo
 *
 */
public class LinphoneLauncherActivity extends Activity {

	private Handler mHandler;
	private ServiceWaitThread mThread;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		// Used to change for the lifetime of the app the name used to tag the logs
		new Log(getResources().getString(R.string.app_name), !getResources().getBoolean(R.bool.disable_every_log));
		
		// Hack to avoid to draw twice LinphoneActivity on tablets
//        if (getResources().getBoolean(R.bool.isTablet)) {
//        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
//        } else {
//        	setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.launcher);
		
		final Handler h = new Handler(){  
            @Override  
            public void handleMessage(Message msg) {  
                if(msg.what==0x123){  
                    Log.d("result",msg.obj.toString()); 
                }  
            }  
        };  
		 new Thread(new AccessNetwork("GET", "http://120.26.42.182:26000/location/upload", "userName=1016&latitude=108.52222&longitude=29.42222", h)).start(); 
		
		mHandler = new Handler();
		
		if (LinphoneService.isReady()) {
			onServiceReady();
		} else {
			// start linphone as background  
			startService(new Intent(ACTION_MAIN).setClass(this, LinphoneService.class));
			mThread = new ServiceWaitThread();
			mThread.start();
		}
	}

	protected void onServiceReady() {
		final Class<? extends Activity> classToStart;
//		if (getResources().getBoolean(R.bool.show_tutorials_instead_of_app)) {
//			classToStart = TutorialLauncherActivity.class;
//		} else if (getResources().getBoolean(R.bool.display_sms_remote_provisioning_activity) && LinphonePreferences.instance().isFirstRemoteProvisioning()) {
//			classToStart = RemoteProvisioningActivity.class;
//		} else {
//			classToStart = LinphoneActivity.class;
//		}
		if(LinphonePreferences.instance().getAccountCount() > 0){
			classToStart = AllControlsOperationActivity.class;			
		}else{
			classToStart = UserFirstLogin.class;
		}
		
		
		LinphoneService.instance().setActivityToLaunchOnIncomingReceived(classToStart);
		mHandler.postDelayed(new Runnable() {
			@Override
			public void run() {
				Intent intent = new Intent(LinphoneLauncherActivity.this,classToStart);
				startActivity(intent);
//				startActivity(new Intent().setClass(LinphoneLauncherActivity.this, classToStart).setData(getIntent().getData()));
				finish();
			}
		}, 1000);
	}


	private class ServiceWaitThread extends Thread {
		public void run() {
			while (!LinphoneService.isReady()) {
				try {
					sleep(30);
				} catch (InterruptedException e) {
					throw new RuntimeException("waiting thread sleep() has been interrupted");
				}
			}

			mHandler.post(new Runnable() {
				@Override
				public void run() {
					onServiceReady();
				}
			});
			mThread = null;
		}
	}
	
//	private void sendLocationInfoToServer(){
//		/*URL可以随意改*/
//		String uriAPI = "http://120.26.42.182:26000/location/upload?userName=1018&latitude=108.52222&longitude=29.42222";
//
//		 HttpGet httpRequest = new HttpGet(uriAPI);
//
//			 try {
//				HttpResponse httpResponse = new DefaultHttpClient()
//						.execute(httpRequest);
//				if (httpResponse.getStatusLine().getStatusCode() == 200) {
//					String strResult = EntityUtils.toString(httpResponse
//							.getEntity());
//					Log.d("result",strResult);
//
//				}
//			} catch (Exception e) {
//				Log.d("result",e.toString());
//			}
//	}
	class AccessNetwork implements Runnable{  
	    private String op ;  
	    private String url;  
	    private String params;  
	    private Handler h;  
	      
	    public AccessNetwork(String op, String url, String params,Handler h) {  
	        super();  
	        this.op = op;  
	        this.url = url;  
	        this.params = params;  
	        this.h = h;  
	    }  
	  
	    @Override  
	    public void run() {  
	        Message m = new Message();  
	        m.what = 0x123;  
	        if(op.equals("GET")){  
	            Log.i("iiiiiii","发送GET请求");  
	            m.obj = GetPostUtil.sendGet(url, params);  
	            Log.i("iiiiiii",">>>>>>>>>>>>"+m.obj);  
	        }  
	        if(op.equals("POST")){  
	            Log.i("iiiiiii","发送POST请求");  
	            m.obj = GetPostUtil.sendPost(url, params);  
	            Log.i("gggggggg",">>>>>>>>>>>>"+m.obj);  
	        }  
	        h.sendMessage(m);  
	    }  
	}  
	
	
	
}


