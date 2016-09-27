package me.odirus.wechat.coupon;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/26
 * Time: 10:30
 */
public class Entrance {
	public List<Coupon> getSmzdmCouponList() {
		Smzdm smzdm = new Smzdm();
		List<Coupon> couponList = smzdm.get();
		return couponList;
	}

	//return coupon list which is the first time save in db
	public List<Coupon> saveInDB(List<Coupon> couponList) {
		List<Coupon> resList = new ArrayList<Coupon>();
		Iterator<Coupon> couponIterator = couponList.iterator();
		while(couponIterator.hasNext()) {
			Coupon coupon = couponIterator.next();
			if (MapDB.getMap().get(coupon.getId()) == null) {
				resList.add(coupon);
			}
			MapDB.getMap().put(coupon.getId(), coupon);
		}

		return resList;
	}
}
