package org.linphone.jifeng.bean;



/**
 *
 *
 * @author lh
 *
 * 2016年4月24日 下午3:25:11
 */
public class PttLoot {
	
	private String groupId;
	private String sipUser;
	private Boolean getOrRelease;

	public String getGroupId() {
		return groupId;
	}

	public void setGroupId(String groupId) {
		this.groupId = groupId;
	}

	public String getSipUser() {
		return sipUser;
	}

	public void setSipUser(String sipUser) {
		this.sipUser = sipUser;
	}

	public Boolean getGetOrRelease() {
		return getOrRelease;
	}

	public void setGetOrRelease(Boolean getOrRelease) {
		this.getOrRelease = getOrRelease;
	}

}
