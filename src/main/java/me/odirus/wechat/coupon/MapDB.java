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
	private static ConcurrentMap map;

	static {
		db = DBMaker.memoryDB().make();
		map = db.hashMap("map").make();
	}

	public static ConcurrentMap getMap() {
		return map;
	}
}
