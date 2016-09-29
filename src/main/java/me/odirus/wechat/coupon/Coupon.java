package me.odirus.wechat.coupon;

import java.io.Serializable;
import java.sql.Timestamp;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/27
 * Time: 9:34
 */
//@todo override hashcode method
public class Coupon implements Serializable {
	private String id;
	private String title;
	private String promotion;
	private CouponProvider couponProvider;
	private Timestamp timestamp;
	private String link;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getPromotion() {
		return promotion;
	}

	public void setPromotion(String promotion) {
		this.promotion = promotion;
	}

	public CouponProvider getCouponProvider() {
		return couponProvider;
	}

	public void setCouponProvider(CouponProvider couponProvider) {
		this.couponProvider = couponProvider;
	}

	public Timestamp getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Timestamp timestamp) {
		this.timestamp = timestamp;
	}

	public String getLink() {
		return link;
	}

	public void setLink(String link) {
		this.link = link;
	}

	@Override
	public String toString() {
		return "id: " + id +
			"; title: " + title +
			"; promotion: " + promotion +
			"; couponProvider: " + couponProvider.getDescribe() +
			"; timestamp: " + timestamp.toString();
	}
}
