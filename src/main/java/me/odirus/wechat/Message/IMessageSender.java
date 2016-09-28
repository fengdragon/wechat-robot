package me.odirus.wechat.Message;

/**
 * User: huangjing
 * Email: huangjing@tinman.cn
 * Date: 2016/9/28
 * Time: 11:33
 */
public interface IMessageSender {
	/**
	 * 添加消息到待发送队列
	 *
	 * @param message
	 */
	public void add(Message message);

	/**
	 * 执行发送消息功能，一次发送一条
	 */
	public void send();
}
