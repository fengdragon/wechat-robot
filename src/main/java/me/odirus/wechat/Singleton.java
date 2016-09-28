package me.odirus.wechat;

import me.odirus.wechat.Message.IMessageSender;
import me.odirus.wechat.Message.MessageSenderWechat;
import me.odirus.wechat.Wechat.Wechat;
import me.odirus.wechat.Wechat.WechatData;
import me.odirus.wechat.coupon.Entrance;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 11:44
 */
public class Singleton {
	private static IMessageSender messageSender = new MessageSenderWechat();
	private static Entrance entrance = new Entrance();
	private static Wechat wechat = new Wechat();
	private static WechatData wechatData = new WechatData();

	public static IMessageSender getMessageSender() {
		return messageSender;
	}

	public static Entrance getEntrance() {
		return entrance;
	}

	public static Wechat getWechat() {
		return wechat;
	}

	public static WechatData getWechatData() {
		return wechatData;
	}
}
