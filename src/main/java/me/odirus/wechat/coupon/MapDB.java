package me.odirus.wechat.coupon;

import org.mapdb.DB;
import org.mapdb.DBMaker;

import java.util.concurrent.ConcurrentMap;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/27
 * Time: 14:43
 */
public class MapDB {
	private static DB db;
	private static ConcurrentMap couponMap;
	private static ConcurrentMap wechatContactMap;//<userName, WechatContact>

	static {
		couponMap = DBMaker.fileDB("coupon.db").make().hashMap("couponMap").createOrOpen();
		wechatContactMap = DBMaker.memoryDB().make().hashMap("wechatContactMap").create();
	}

	public static ConcurrentMap getCouponMap() {
		return couponMap;
	}

	public static ConcurrentMap getWechatContactMap() {
		return wechatContactMap;
	}
}
