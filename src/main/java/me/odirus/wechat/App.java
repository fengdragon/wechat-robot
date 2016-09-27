package me.odirus.wechat;

import me.odirus.wechat.coupon.Job;
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
	public static void main(String[] args) {
		try {
			// Grab the Scheduler instance from the Factory
			Scheduler scheduler = StdSchedulerFactory.getDefaultScheduler();
			// and start it off
			scheduler.start();

			// define the job and tie it to our MyJob class
			JobDetail job = newJob(Job.class)
				.withIdentity("job1", "group1")
				.build();

			// Trigger the job to run now, and then repeat every 40 seconds
			Trigger trigger = newTrigger()
				.withIdentity("trigger1", "group1")
				.startNow()
				.withSchedule(simpleSchedule()
					.withIntervalInSeconds(30)
					.repeatForever())
				.build();

			// Tell quartz to schedule the job using our trigger
			scheduler.scheduleJob(job, trigger);
		} catch (SchedulerException se) {
			se.printStackTrace();
		}
	}
}
