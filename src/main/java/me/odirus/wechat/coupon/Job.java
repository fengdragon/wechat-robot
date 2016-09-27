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
		entrance.saveInDB(couponList);
	}
}
