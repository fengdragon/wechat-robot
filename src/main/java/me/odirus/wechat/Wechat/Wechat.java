package me.odirus.wechat.Wechat;

import java.awt.EventQueue;
import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;

import javax.swing.UIManager;

import blade.kit.DateKit;
import blade.kit.StringKit;
import blade.kit.http.HttpRequest;
import me.odirus.wechat.Singleton;
import me.odirus.wechat.util.CookieUtil;
import me.odirus.wechat.util.JSUtil;
import me.odirus.wechat.util.Matchers;
import org.json.JSONArray;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Wechat {
	private static final Logger logger = LoggerFactory.getLogger(Wechat.class);
	private static boolean shouldExitListenMsgMode = false;


	private String uuid;
	private int tip = 0;

	
	private String cookie;
	private QRCodeFrame qrCodeFrame;

	// 用户自己
	private JSONObject User;
	// 微信联系人列表，可聊天的联系人列表
	private JSONArray MemberList, ContactList;
	// 微信特殊账号
	private List<String> SpecialUsers = Arrays.asList("newsapp", "fmessage", "filehelper", "weibo", "qqmail", "fmessage", "tmessage", "qmessage", "qqsync", "floatbottle", "lbsapp", "shakeapp", "medianote", "qqfriend", "readerapp", "blogapp", "facebookapp", "masssendapp", "meishiapp", "feedsapp", "voip", "blogappweixin", "weixin", "brandsessionholder", "weixinreminder", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "officialaccounts", "notification_messages", "wxid_novlwrv3lqwv11", "gh_22b87fa7cb3c", "wxitil", "userexperience_alarm", "notification_messages");

	public Wechat() {
		System.setProperty("jsse.enableSNIExtension", "false");
	}

	public JSONArray getContactList() {
		return ContactList;
	}

	/**
	 * 获取微信 UUID
	 *
	 * @return uuid or null if fail
	 */
	public String getUUID() {
		logger.info("从微信服务器获取微信 UUID");

		String url = "https://login.weixin.qq.com/jslogin";
		HttpRequest request = HttpRequest.get(url, true, 
				"appid", "wx782c26e4c19acffb", 
				"fun", "new",
				"lang", "zh_CN",
				"_" , DateKit.getCurrentUnixTime());
		
		String res = request.body();
		request.disconnect();

		if(StringKit.isNotBlank(res)){
			String code = Matchers.match("window.QRLogin.code = (\\d+);", res);
			if(null != code){
				if(code.equals("200")){
					this.uuid = Matchers.match("window.QRLogin.uuid = \"(.*)\";", res);
					logger.info("获取微信 UUID 成功, UUID = " + uuid);
					return this.uuid;
				} else {
					logger.info(String.format("获取微信 UUID 失败，状态码 code = %s", code));
				}
			}
		}

		return null;
	}

	/**
	 * 显示登录二维码，等待被扫描
	 */
	public void showQrCode() {
		String url = "https://login.weixin.qq.com/qrcode/" + Singleton.getWechatData().getUUID();
		final File output = new File("temp.jpg");
		HttpRequest.post(url, true, 
				"t", "webwx", 
				"_" , DateKit.getCurrentUnixTime())
				.receive(output);

		if(output.exists() && output.isFile()){
			EventQueue.invokeLater(new Runnable() {
				public void run() {
					try {
						UIManager.setLookAndFeel("com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel");
						qrCodeFrame = new QRCodeFrame(output.getPath());
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			});
		}
	}
	
	/**
	 * 等待登录
	 */
	public String waitForLogin() {
		this.tip = 1;
		String url = "https://login.weixin.qq.com/cgi-bin/mmwebwx-bin/login";
		HttpRequest request = HttpRequest.get(url, true, 
				"tip", this.tip, 
				"uuid", this.uuid,
				"_" , DateKit.getCurrentUnixTime());
		
		String res = request.body();
		request.disconnect();

		if(null == res){
			logger.info("扫描二维码验证失败");
			return "";
		}
		
		String code = Matchers.match("window.code=(\\d+);", res);
		if(null == code){
			logger.info("扫描二维码验证失败");
			return "";
		} else {
			if(code.equals("201")) {
				logger.info("成功扫描, 请在手机上点击确认以登录");
				tip = 0;
			} else if(code.equals("200")) {
				logger.info("[*] 正在登录...");

				String pm = Matchers.match("window.redirect_uri=\"(\\S+?)\";", res);

				String redirectHost = "wx.qq.com";
				try {
					URL pmURL = new URL(pm);
					redirectHost = pmURL.getHost();
				} catch (MalformedURLException e) {
					e.printStackTrace();
				}

				String pushServer = JSUtil.getPushServer(redirectHost);
				WechatData.setWebpush_url("https://" + pushServer + "/cgi-bin/mmwebwx-bin");
				WechatData.setRedirect_uri(pm + "&fun=new");

				logger.info("获取到 redirect_uri = " + WechatData.getRedirect_uri());

				WechatData.setBase_uri(WechatData.getRedirect_uri().substring(0, WechatData.getRedirect_uri().lastIndexOf("/")));
				logger.info("获取到 base_uri = " + WechatData.getBase_uri());

			} else if(code.equals("408")) {
				logger.info("登录超时");
			} else {
				logger.info("扫描后遇到未知 code, code = " + code);
			}
		}
		return code;
	}

	/**
	 * 关闭二维码界面
	 */
	public void closeQrWindow() {
		qrCodeFrame.dispose();
	}
	
	/**
	 * 执行登录
	 */
	public boolean login() {
		logger.info("执行微信登录功能");

		HttpRequest request = HttpRequest.get(WechatData.getRedirect_uri());
		String res = request.body();
		this.cookie = CookieUtil.getCookie(request);
		request.disconnect();
		
		if(StringKit.isBlank(res)) {
			logger.info("请求结果为空");
			return false;
		}
		
		String skey = Matchers.match("<skey>(\\S+)</skey>", res);
		String wxsid = Matchers.match("<wxsid>(\\S+)</wxsid>", res);
		String wxuin = Matchers.match("<wxuin>(\\S+)</wxuin>", res);
		String pass_ticket = Matchers.match("<pass_ticket>(\\S+)</pass_ticket>", res);

		WechatData.setSkey(skey);
		WechatData.setWxsid(wxsid);
		WechatData.setWxuin(wxuin);
		WechatData.setPassTicket(pass_ticket);

		return true;
	}
	
	/**
	 * 微信初始化
	 */
	public boolean wxInit() {
		logger.info("执行微信初始化功能");

		String url = WechatData.getBase_uri() + "/webwxinit?r=" + DateKit.getCurrentUnixTime() +
			"&pass_ticket=" + WechatData.getPassTicket() +
			"&skey=" + WechatData.getSkey();
		
		JSONObject body = new JSONObject();
		body.put("BaseRequest", WechatBaseRequest.get());

		logger.info("请求地址 url = " + url);

		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)) {
			logger.info("请求结果为空");
			return false;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(res);

			//@todo 解析ChatSet 中的用户ID
			String charSet = jsonObject.getString("ChatSet");

			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret");
				if(ret == 0){
					WechatData.setSyncKeyJSONObject(jsonObject.getJSONObject("SyncKey"));
					this.User = jsonObject.getJSONObject("User");
					StringBuffer synckey = new StringBuffer();
					JSONArray list = WechatData.getSyncKeyJSONObject().getJSONArray("List");
					for(int i=0, len=list.length(); i<len; i++) {
						JSONObject item = list.getJSONObject(i);
						synckey.append("|" + item.getInt("Key") + "_" + item.getInt("Val"));
					}
					WechatData.setSyncKey(synckey.substring(1));

					return true;
				}
			}
		} catch (Exception e) {
			logger.warn("解析返回结果时遇到错误:" + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}
	
	/**
	 * 微信状态通知
	 */
	public boolean wxStatusNotify() {
		logger.info("尝试开启微信状态通知");

		String url = WechatData.getBase_uri() + "/webwxstatusnotify?lang=zh_CN&pass_ticket=" + WechatData.getPassTicket();

		logger.info("请求地址 url = " + url);

		JSONObject body = new JSONObject();
		body.put("BaseRequest", WechatBaseRequest.get());
		body.put("Code", 3);
		body.put("FromUserName", this.User.getString("UserName"));
		body.put("ToUserName", this.User.getString("UserName"));
		body.put("ClientMsgId", DateKit.getCurrentUnixTime());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)) {
			logger.info("请求返回结果为空");
			return false;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(res);
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret");
				return ret == 0;
			}
		} catch (Exception e) {
			logger.warn("解析JSON数据时出现错误: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}
	
	/**
	 * 获取联系人
	 */
	public boolean getContact() {
		logger.info("准备获取微信联系人信息");

		String url = WechatData.getBase_uri() + "/webwxgetcontact?pass_ticket=" + WechatData.getPassTicket() +
			"&skey=" + WechatData.getSkey() + "&r=" + DateKit.getCurrentUnixTime();

		logger.info("请求地址 url = " + url);

		JSONObject body = new JSONObject();
		body.put("BaseRequest", WechatBaseRequest.get());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)) {
			logger.info("请求返回结果为空");
			return false;
		}
		
		try {
			JSONObject jsonObject = new JSONObject(res);
			JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
			if(null != BaseResponse){
				int ret = BaseResponse.getInt("Ret");
				if(ret == 0){
					this.MemberList = jsonObject.getJSONArray("MemberList");
					this.ContactList = new JSONArray();
					if(null != MemberList){
						for(int i=0, len=MemberList.length(); i<len; i++){
							JSONObject contact = this.MemberList.getJSONObject(i);
							//公众号/服务号
							if(contact.getInt("VerifyFlag") == 8) {
								continue;
							}
							//特殊联系人
							if(SpecialUsers.contains(contact.getString("UserName"))){
								continue;
							}
							//群聊
							if(contact.getString("UserName").contains("@@")) {
								continue;
							}
							//自己
							if(contact.getString("UserName").equals(this.User.getString("UserName"))) {
								continue;
							}
							ContactList.put(contact);
						}
						return true;
					}
				}
			}
		} catch (Exception e) {
			logger.warn("解析返回JSON数据时出现错误: " + e.getMessage());
			e.printStackTrace();
		}

		return false;
	}

	/**
	 * 消息检查
	 */
	public int[] syncCheck() {
		logger.info("执行 syncCheck 操作");

		int[] arr = new int[2];
		String url = WechatData.getWebpush_url() + "/synccheck";

		logger.info("请求地址 url = " + url);

		JSONObject body = new JSONObject();
		body.put("BaseRequest", WechatBaseRequest.get());
		
		HttpRequest request = HttpRequest.get(url, true,
				"r", DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5),
				"skey", WechatData.getSkey(),
				"uin", WechatData.getWxuin(),
				"sid", WechatData.getWxsid(),
				"deviceid", WechatData.getDeviceId(),
				"synckey", WechatData.getSyncKey(),
				"_", System.currentTimeMillis())
				.header("Cookie", this.cookie);
		String res = request.body();
		request.disconnect();

		if(StringKit.isBlank(res)) {
			logger.info("请求返回结果为空");
			return arr;
		}
		
		String retcode = Matchers.match("retcode:\"(\\d+)\",", res);
		String selector = Matchers.match("selector:\"(\\d+)\"}", res);

		if(null != retcode && null != selector){
			arr[0] = Integer.parseInt(retcode);
			arr[1] = Integer.parseInt(selector);
			return arr;
		}

		return arr;
	}

	/**
	 * 发送消息
	 *
	 * @param content
	 * @param toUserName
	 */
	public void webWxSendMsg(String content, String toUserName) {
		logger.info("执行微信消息发送功能");

		String url = WechatData.getBase_uri() + "/webwxsendmsg?lang=zh_CN&pass_ticket=" + WechatData.getPassTicket();

		logger.info("请求地址 url = " + url);

		JSONObject body = new JSONObject();
		
		String clientMsgId = DateKit.getCurrentUnixTime() + StringKit.getRandomNumber(5);
		JSONObject Msg = new JSONObject();
		Msg.put("Type", 1);
		Msg.put("Content", content);
		Msg.put("FromUserName", User.getString("UserName"));
		Msg.put("ToUserName", toUserName);
		Msg.put("LocalID", clientMsgId);
		Msg.put("ClientMsgId", clientMsgId);
		body.put("BaseRequest", WechatBaseRequest.get());
		body.put("Msg", Msg);
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		request.body();
		request.disconnect();
	}
	
	/**
	 * 获取最新消息
	 */
	public JSONObject webWxSync() {
		logger.info("获取微信最新消息");

		String url = WechatData.getBase_uri() + "/webwxsync?lang=zh_CN&pass_ticket=" + WechatData.getPassTicket() +
			"&skey=" + WechatData.getSkey() +
			"&sid=" + WechatData.getWxsid() +
			"&r=" + DateKit.getCurrentUnixTime();

		logger.info("请求地址 url = " + url);

		JSONObject body = new JSONObject();
		body.put("BaseRequest", WechatBaseRequest.get());
		body.put("SyncKey", WechatData.getSyncKeyJSONObject());
		body.put("rr", DateKit.getCurrentUnixTime());
		
		HttpRequest request = HttpRequest.post(url)
				.header("Content-Type", "application/json;charset=utf-8")
				.header("Cookie", this.cookie)
				.send(body.toString());
		String res = request.body();
		request.disconnect();
		
		if(StringKit.isBlank(res)) {
			System.out.println("请求返回结果为空");
			return null;
		}
		
		JSONObject jsonObject = new JSONObject(res);
		JSONObject BaseResponse = jsonObject.getJSONObject("BaseResponse");
		if(null != BaseResponse) {
			int ret = BaseResponse.getInt("Ret");
			if(ret == 0) {
				WechatData.setSyncKeyJSONObject(jsonObject.getJSONObject("SyncKey"));
				StringBuffer synckey = new StringBuffer();
				JSONArray list = WechatData.getSyncKeyJSONObject().getJSONArray("List");
				for(int i=0, len=list.length(); i<len; i++) {
					JSONObject item = list.getJSONObject(i);
					synckey.append("|" + item.getInt("Key") + "_" + item.getInt("Val"));
				}
				WechatData.setSyncKey(synckey.substring(1));
			}
		}
		return jsonObject;
	}

	/**
	 * 处理消息
	 *
	 * @param data
	 */
	public void handleMsg(JSONObject data) {
		if(null == data){
			return;
		}
		
		JSONArray AddMsgList = data.getJSONArray("AddMsgList");
		for (int i=0, len=AddMsgList.length(); i<len; i++) {
			logger.info("你有新的消息，请注意查收");
			JSONObject msg = AddMsgList.getJSONObject(i);
			int msgType = msg.getInt("MsgType");
			String name = getUserRemarkName(msg.getString("FromUserName"));
			String content = msg.getString("Content");

			if(msgType == 51) {
				logger.info("成功截获微信初始化消息");
			} else if(msgType == 1) {
				if(SpecialUsers.contains(msg.getString("ToUserName"))) {
					continue;
				} else if(msg.getString("FromUserName").equals(User.getString("UserName"))){
					continue;
				} else if (msg.getString("ToUserName").contains("@@")) {
					String[] peopleContent = content.split(":<br/>");
					logger.info("|" + name + "| " + peopleContent[0] + ":\n" + peopleContent[1].replace("<br/>", "\n"));
				}
			}
		}
	}

	/**
	 * 获取用户备注
	 *
	 * @param id
	 * @return
	 */
	private String getUserRemarkName(String id) {
		String name = "这个人物名字未知";
		for(int i=0, len = MemberList.length(); i<len; i++){
			JSONObject member = this.MemberList.getJSONObject(i);
			if(member.getString("UserName").equals(id)){
				if(StringKit.isNotBlank(member.getString("RemarkName"))){
					name = member.getString("RemarkName");
				} else {
					name = member.getString("NickName");
				}
				return name;
			}
		}
		return name;
	}

	/**
	 * 消息监听线程
	 */
	public void listenMsgMode() {
		new Thread(new Runnable() {
			@Override
			public void run() {
				WechatData.setIsLogin(true);

				logger.info("进入消息监听模式 ...");
				int playWeChat = 0;

				while(!shouldExitListenMsgMode) {
					int[] arr = syncCheck();
					int retcode = arr[0];
					int selector = arr[1];
					
					if(retcode == 1100) {
						syncCheck();
					}

					if (retcode == 1101) {
						logger.info("已经从其他设备登录微信，准备退出");
						shouldExitListenMsgMode = true;
					}
					
					if(retcode == 0) {
						if(selector == 2) {
							JSONObject data = webWxSync();
							handleMsg(data);
						} else if(selector == 6) {
							JSONObject data = webWxSync();
							handleMsg(data);
						} else if(selector == 7){
							playWeChat += 1;
							webWxSync();
						}
					}
				}

				WechatData.setIsLogin(false);
			}
		}, "listenMsgMode").start();
	}
}