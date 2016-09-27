package me.odirus.wechat.coupon;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/27
 * Time: 9:36
 */
public enum CouponProvider {
	SMZDM("smzdm-", "smzdm.com");

	private String idPrefix;
	private String describe;


	CouponProvider(String idPrefix, String describe) {
		this.idPrefix = idPrefix;
		this.describe = describe;
	}

	public String getIdPrefix() {
		return idPrefix;
	}

	public void setIdPrefix(String idPrefix) {
		this.idPrefix = idPrefix;
	}

	public String getDescribe() {
		return describe;
	}

	public void setDescribe(String describe) {
		this.describe = describe;
	}
}
