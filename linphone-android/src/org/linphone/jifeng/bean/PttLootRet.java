package org.linphone.jifeng.bean;



public class PttLootRet {
	
	private boolean openMic = false;
	private boolean suc = false;
	private String msg;
	public boolean isSuc() {
		return suc;
	}
	public void setSuc(boolean suc) {
		this.suc = suc;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public boolean isOpenMic() {
		return openMic;
	}
	public void setOpenMic(boolean openMic) {
		this.openMic = openMic;
	}

}
