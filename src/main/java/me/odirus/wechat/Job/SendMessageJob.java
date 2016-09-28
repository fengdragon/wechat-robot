package me.odirus.wechat.Job;

import me.odirus.wechat.Message.IMessageSender;
import me.odirus.wechat.Singleton;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 12:09
 */
public class SendMessageJob implements org.quartz.Job {
	public SendMessageJob() {}

	public void execute(JobExecutionContext context) throws JobExecutionException {
		sendMessage();
	}

	private void sendMessage() {
		IMessageSender messageSender = Singleton.getMessageSender();
		messageSender.send();
	}
}
