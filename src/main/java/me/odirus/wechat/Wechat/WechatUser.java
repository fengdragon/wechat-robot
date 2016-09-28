package me.odirus.wechat.Wechat;

import me.odirus.wechat.coupon.MapDB;

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

	public static WechatContact getSpecifyUser() {
		return specifyUser;
	}

	/**
	 * 存储用户信息到DB中
	 *
	 * @param isLoginUser
	 * @param contact
	 */
	public static void save(boolean isLoginUser, WechatContact contact) {
		MapDB.getWechatContactMap().put(contact.getUserName(), contact);
		if (contact.getNickName().equals("滴滴领券分享")) {
			specifyUser = contact;
		}
		if (isLoginUser) {
			loginUser = contact;
		}
	}

	/**
	 * 从DB中获取用户信息
	 *
	 * @param userName
	 * @return wechatContact or null if not exists
	 */
	public static WechatContact get(String userName) {
		WechatContact contact = (WechatContact)MapDB.getWechatContactMap().get(userName);

		return contact;
	}

	/**
	 * 获取数据库中存储的联系人数量
	 *
	 * @return
	 */
	public static int getCount() {
		return MapDB.getWechatContactMap().size();
	}
}
