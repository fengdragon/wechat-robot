package me.odirus.wechat;

import blade.kit.logging.Logger;
import blade.kit.logging.LoggerFactory;
import me.odirus.wechat.Job.GetCouponJob;
import me.odirus.wechat.Job.SendMessageJob;
import me.odirus.wechat.Wechat.Wechat;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.Trigger;
import org.quartz.impl.StdSchedulerFactory;

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
	private static final Logger LOGGER = LoggerFactory.getLogger(App.class);

	public static void main(String[] args) {
		App app = new App();
		//app.startWechat();
		app.startGetCoupon();
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
			String uuid = wechat.getUUID();

			if (null == uuid) {
				LOGGER.info("uuid获取失败");
			} else {
				LOGGER.info("[*] 获取到uuid为 [%s]", wechat.getUuid());
				wechat.showQrCode();
				while (!wechat.waitForLogin().equals("200")) {
					Thread.sleep(2000);
				}
				wechat.closeQrWindow();

				if (!wechat.login()) {
					LOGGER.info("微信登录失败");
					return;
				}

				LOGGER.info("[*] 微信登录成功");

				if (!wechat.wxInit()) {
					LOGGER.info("[*] 微信初始化失败");
					return;
				}

				LOGGER.info("[*] 微信初始化成功");

				if (!wechat.wxStatusNotify()) {
					LOGGER.info("[*] 开启状态通知失败");
					return;
				}

				LOGGER.info("[*] 开启状态通知成功");

				if (!wechat.getContact()) {
					LOGGER.info("[*] 获取联系人失败");
					return;
				}

				LOGGER.info("获取联系人成功");
				LOGGER.info("共有 %d 位联系人", wechat.getContactList().length());

				// 监听消息
				wechat.listenMsgMode();
			}
		} catch (InterruptedException ie) {
			ie.printStackTrace();
		}
	}
}