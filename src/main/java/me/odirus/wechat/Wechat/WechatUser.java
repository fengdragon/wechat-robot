package me.odirus.wechat.Wechat;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 14:58
 */
public class WechatUser {
	private static WechatContact loginUser;
	private static WechatContact specifyUser;

	public static WechatContact getLoginUser() {
		return loginUser;
	}

	public static void setLoginUser(WechatContact loginUser) {
		WechatUser.loginUser = loginUser;
	}

	public static WechatContact getSpecifyUser() {
		return specifyUser;
	}

	public static void setSpecifyUser(WechatContact specifyUser) {
		WechatUser.specifyUser = specifyUser;
	}
}
