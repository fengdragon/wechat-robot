package me.odirus.wechat;

import me.odirus.wechat.Job.GetCouponJob;
import me.odirus.wechat.Job.SendMessageJob;
import me.odirus.wechat.Wechat.Wechat;
import me.odirus.wechat.Wechat.WechatData;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static org.quartz.JobBuilder.newJob;
import static org.quartz.SimpleScheduleBuilder.simpleSchedule;
import static org.quartz.TriggerBuilder.newTrigger;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/27
 * Time: 14:31
 */
public class App {
	private static final Logger logger = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		App app = new App();
		app.startWechat();
		//app.startGetCoupon();
	}

	private void startGetCoupon() {
		try {
			// Grab the Scheduler instance from the Factory
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			// and start it off
			scheduler.start();

			// define the job and tie it to our MyJob class
			JobDetail getCouponJob = newJob(GetCouponJob.class)
				.withIdentity("job1", "group1")
				.build();
			JobDetail sendMessageJob = newJob(SendMessageJob.class)
				.withIdentity("job2", "group2")
				.build();

			// Trigger the job to run now, and then repeat every 40 seconds
			Trigger triggerGroup1 = newTrigger()
				.withIdentity("trigger1", "group1")
				.startNow()
				.withSchedule(simpleSchedule()
					.withIntervalInSeconds(30)
					.repeatForever())
				.build();
			Trigger triggerGroup2 = newTrigger()
				.withIdentity("trigger2", "group2")
				.startNow()
				.withSchedule(simpleSchedule()
					.withIntervalInSeconds(10)
					.repeatForever())
				.build();

			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(getCouponJob, triggerGroup1);
			scheduler.scheduleJob(sendMessageJob, triggerGroup2);

		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}

	private void startWechat() {
		try {
			Wechat wechat = Singleton.getWechat();
			WechatData wechatData = Singleton.getWechatData();
			String uuid = wechat.getUUID();

			if (null != uuid) {
				wechatData.setUUID(uuid);
				wechat.showQrCode();
				while (!wechat.waitForLogin().equals("200")) {
					Thread.sleep(2000);
				}
				wechat.closeQrWindow();

				if (!wechat.login()) {
					logger.info("微信登录失败");
					return;
				}

				logger.info("微信登录成功");

				if (!wechat.wxInit()) {
					logger.info("微信初始化失败");
					return;
				}

				logger.info("微信初始化成功");

				if (!wechat.wxStatusNotify()) {
					logger.info("开启状态通知失败");
					return;
				}

				logger.info("开启状态通知成功");

				if (!wechat.getContact()) {
					logger.info("获取联系人失败");
					return;
				}

				logger.info("获取联系人成功");
				logger.info(String.format("一共获取到联系人 %d 位", wechat.getContactList().length()));

				// 监听消息
				wechat.listenMsgMode();
			} else {
				logger.info("由于微信 UUID 获取失败，无法继续运行");
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}