package me.odirus.wechat.coupon;

import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

import java.util.List;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/27
 * Time: 14:30
 */
public class Job implements org.quartz.Job {
	public Job() {}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		Entrance entrance = new Entrance();
		List<Coupon> couponList = entrance.getSmzdmCouponList();
		List<Coupon> newCouponList = entrance.saveInDB(couponList);

		for (int i = 0; i < newCouponList.size(); i++) {
			Message.send(newCouponList.get(i).toString());
		}
	}
}
