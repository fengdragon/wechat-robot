package me.odirus.wechat.Message;

import me.odirus.wechat.Singleton;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.ConcurrentLinkedQueue;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 11:36
 */
public class MessageSenderWechat implements IMessageSender {
	private static Logger logger = LoggerFactory.getLogger(MessageSenderWechat.class);
	private static ConcurrentLinkedQueue<Message> messageQueue = new ConcurrentLinkedQueue<Message>();

	/**
	 * 添加消息到待发送队列
	 *
	 * @param message
	 */
	public void add(Message message) {
		messageQueue.add(message);
	}

	/**
	 * 执行发送消息功能，一次发送一条
	 */
	public void send() {
		Message message = messageQueue.poll();
		if (null != message) {
			Singleton.getWechat().webWxSendMsg(message.getContent(), message.getReceiver());
		}
	}
}
