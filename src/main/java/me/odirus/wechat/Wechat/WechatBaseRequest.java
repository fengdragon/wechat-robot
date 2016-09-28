package me.odirus.wechat.Wechat;

import org.json.JSONObject;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 13:48
 */
public class WechatBaseRequest {
	public static JSONObject get() {
		JSONObject baseRequest = new JSONObject();
		baseRequest.put("Uin", WechatData.getWxuin());
		baseRequest.put("Sid", WechatData.getWxsid());
		baseRequest.put("Skey", WechatData.getSkey());
		baseRequest.put("DeviceID", WechatData.getDeviceId());

		return baseRequest;
	}
}
