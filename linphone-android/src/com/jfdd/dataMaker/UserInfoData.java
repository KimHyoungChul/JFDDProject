package com.jfdd.dataMaker;

import java.util.ArrayList;
import java.util.List;

import com.esri.core.geometry.Point;

public class UserInfoData {
	
	private String userName;
	private String userNumber;
	private String userImageIcon;
	private Point userLocation;
	
	
	public List<UserInfoData> getUserInfoData(){
		
		List<UserInfoData> users=new ArrayList<UserInfoData>();;
		for(int i=0;i<31;i++){
			UserInfoData user=new UserInfoData();
			user.setUserName("yangxuanlun");
			user.setUserNumber((i+1000)+"");
			user.setUserImageIcon("photo_image");
			Point tPoint=new Point(106.5+0.001*i, 29.5+0.001*i);
			user.setUserLocation(tPoint);
			users.add(user);
			
		}
		return users;
		
	}


	public String getUserName() {
		return userName;
	}


	public void setUserName(String userName) {
		this.userName = userName;
	}


	public String getUserNumber() {
		return userNumber;
	}


	public void setUserNumber(String userNumber) {
		this.userNumber = userNumber;
	}


	public String getUserImageIcon() {
		return userImageIcon;
	}


	public void setUserImageIcon(String userImageIcon) {
		this.userImageIcon = userImageIcon;
	}


	public Point getUserLocation() {
		return userLocation;
	}


	public void setUserLocation(Point userLocation) {
		this.userLocation = userLocation;
	}

}
