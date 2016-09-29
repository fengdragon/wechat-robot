package me.odirus.wechat.Wechat;

import me.odirus.wechat.coupon.MapDB;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 14:58
 */
public class WechatUser {
	private static final Logger logger = LoggerFactory.getLogger(WechatUser.class);
	private static final String specifyUserNickName = "优惠自动分享群1";

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
		if (contact.getNickName().equals(specifyUserNickName)) {
			logger.info("目标用户信息已经被记录, nickName=" + specifyUserNickName + "; userName=" + contact.getUserName());
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
