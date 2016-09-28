package me.odirus.wechat.Wechat;

import java.io.Serializable;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/27
 * Time: 17:58
 */
public class WechatContact implements Serializable {
	private String userName;
	private String nickName;
	private String remarkName;

	public WechatContact(String userName, String nickName, String remarkName) {
		this.userName = userName;
		this.nickName = nickName;
		this.remarkName = remarkName;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getNickName() {
		return nickName;
	}

	public void setNickName(String nickName) {
		this.nickName = nickName;
	}

	public String getRemarkName() {
		return remarkName;
	}

	public void setRemarkName(String remarkName) {
		this.remarkName = remarkName;
	}
}
