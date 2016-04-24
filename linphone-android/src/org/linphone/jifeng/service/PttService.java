/*
LinphoneService.java
Copyright (C) 2010  Belledonne Communications, Grenoble, France

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
package org.linphone.jifeng.service;

import org.linphone.core.LinphoneCall;
import org.linphone.core.LinphoneCore;
import org.linphone.jifeng.bean.PttLootRet;
import org.linphone.jifeng.util.StringUtils;
import org.linphone.jifeng.util.Util;

/**
 *
 *
 * @author lh
 *
 * 2016年4月24日 下午3:25:16
 */
public class PttService  {
	
	private String urlPrefix =  "http://120.26.42.182:26000";
	
	private static class Single {
		static PttService instance = new PttService();
	}
	
	public static synchronized PttService getInstance() {
		return Single.instance;
	}
	
	//控制方逻辑
//	private String controlUsername = "1015";
	private boolean isControlEnable = false;
	public boolean isControl(LinphoneCore lc) {
//		if (null == lc || 0 == lc.getAuthInfosList().length) {
//			return false;
//		}
//		//这个可以调用服务器确定控制账号
//		return controlUsername.equals(lc.getAuthInfosList()[0].getUsername());
		return isControlEnable();
	}
	public boolean isControlEnable() {
		return isControlEnable;
	}
	public void setControlEnable(boolean isControlEnable) {
		this.isControlEnable = isControlEnable;
	}

	//会议判断逻辑
	private String conferenceUsername = "0000000000";
//	private String conferenceUsername = "1010";
	public boolean isConference(LinphoneCall call) {
		if (null == call) {
			return false;
		}
		//call.getRemoteParams().getCustomHeader(name) 可以自定义协议头来判断
		return conferenceUsername.equals(call.getRemoteAddress().getUserName());
	}
	
	public boolean isConfAndNotCtrl(LinphoneCore lc) {
		if (null == lc) {
			return false;
		}
		return isConference(lc.getCurrentCall()) && !isControl(lc);
	}
	
	//先服务器申请抢麦
	private String lootUrlReqFormat = urlPrefix + "/ptt?groupId=%s&sipUser=%s&getOrRelease=%s";
	public PttLootRet lootMicReq(LinphoneCore lc, boolean getOrRelease) {
		PttLootRet ret = new PttLootRet();
		if (null == lc || 0 == lc.getAuthInfosList().length) {
			ret.setMsg("未找到用户");
			return ret;
		}
		LinphoneCall call = lc.getCurrentCall();
		if (null == call) {
			ret.setMsg("当前没有处于通话状态");
			return ret;
		}
		String url = String.format(lootUrlReqFormat,
				call.getRemoteAddress().getUserName(), 
				lc.getAuthInfosList()[0].getUsername(), getOrRelease);
		
		byte[] buf = Util.httpGet(url);
		if (buf != null && buf.length > 0) {
			String content = new String(buf);
			if (!StringUtils.isEmpty(content) && "true".equalsIgnoreCase(content)) {
				ret.setSuc(true);
				ret.setOpenMic(getOrRelease);
			} else {
				ret.setMsg(content);
			}
		}
		return ret;
	}
	
	private String confEndDetectReqFormat = urlPrefix + "/group/status?groupId=%s";
	public Integer confEndDetectReq(LinphoneCore lc) {
		Integer ret = -1;
		if (null == lc) {
			return ret;
		}
		LinphoneCall call = lc.getCurrentCall();
		if (null == call) {
			return ret;
		}
		String url = String.format(confEndDetectReqFormat,
				call.getRemoteAddress().getUserName());
		
		byte[] buf = Util.httpGet(url);
		if (buf != null && buf.length > 0) {
			String content = new String(buf);
			if (!StringUtils.isEmpty(content) && "0".equalsIgnoreCase(content)) {
				ret = 0;
			}
		}
		return ret;
	}
}

