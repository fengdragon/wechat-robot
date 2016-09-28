package me.odirus.wechat.Wechat;

import blade.kit.DateKit;
import org.json.JSONObject;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 13:28
 */
public class WechatData {
	private static String base_uri;
	private static String redirect_uri;
	private static String webpush_url = "https://webpush2.weixin.qq.com/cgi-bin/mmwebwx-bin";

	private static String UUID;
	private static boolean isLogin = false;

	private static String deviceId = "e" + DateKit.getCurrentUnixTime();
	private static String skey;
	private static String wxsid;
	private static String wxuin;
	private static String passTicket;

	private static String syncKey;
	private static JSONObject syncKeyJSONObject;

	public static String getBase_uri() {
		return base_uri;
	}

	public static void setBase_uri(String base_uri) {
		WechatData.base_uri = base_uri;
	}

	public static String getRedirect_uri() {
		return redirect_uri;
	}

	public static void setRedirect_uri(String redirect_uri) {
		WechatData.redirect_uri = redirect_uri;
	}

	public static String getWebpush_url() {
		return webpush_url;
	}

	public static void setWebpush_url(String webpush_url) {
		WechatData.webpush_url = webpush_url;
	}

	public static String getUUID() {
		return UUID;
	}

	public static void setUUID(String UUID) {
		WechatData.UUID = UUID;
	}

	public static boolean getIsLogin() {
		return isLogin;
	}

	public static void setIsLogin(boolean isLogin) {
		WechatData.isLogin = isLogin;
	}

	public static String getWxsid() {
		return wxsid;
	}

	public static void setWxsid(String wxsid) {
		WechatData.wxsid = wxsid;
	}

	public static String getDeviceId() {
		return deviceId;
	}

	public static void setDeviceId(String deviceId) {
		WechatData.deviceId = deviceId;
	}

	public static boolean isLogin() {
		return isLogin;
	}

	public static String getPassTicket() {
		return passTicket;
	}

	public static void setPassTicket(String passTicket) {
		WechatData.passTicket = passTicket;
	}

	public static String getSkey() {
		return skey;
	}

	public static void setSkey(String skey) {
		WechatData.skey = skey;
	}

	public static String getWxuin() {
		return wxuin;
	}

	public static void setWxuin(String wxuin) {
		WechatData.wxuin = wxuin;
	}

	public static String getSyncKey() {
		return syncKey;
	}

	public static void setSyncKey(String syncKey) {
		WechatData.syncKey = syncKey;
	}

	public static JSONObject getSyncKeyJSONObject() {
		return syncKeyJSONObject;
	}

	public static void setSyncKeyJSONObject(JSONObject syncKeyJSONObject) {
		WechatData.syncKeyJSONObject = syncKeyJSONObject;
	}
}
