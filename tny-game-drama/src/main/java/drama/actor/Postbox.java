package drama.actor;

/**
 * Created by Kun Yang on 16/3/2.
 */
public interface Postbox extends SystemPostbox {

	/**
	 * 将系统信息插入队列
	 * @param receiver 接收者
	 * @param message 系统信息
	 */
	void systemEnqueue(ActorRef receiver, SystemMessage message);

	/**
	 * 是否有系统信息
	 * @return 有系统信息返回true 无则返回false
	 */
	boolean hasSystemMessages();

}
