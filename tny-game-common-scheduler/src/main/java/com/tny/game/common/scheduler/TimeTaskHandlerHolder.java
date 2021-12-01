package com.tny.game.common.scheduler;

import java.util.*;

/**
 * @author KGTny
 * @ClassName: TimeTaskHandlerHolder
 * @Description: 任务持有器
 * @date 2011-10-28 下午3:59:49
 * <p>
 * 任务持有器
 * <p>
 * <br>
 */
public interface TimeTaskHandlerHolder {

	/**
	 * 获取任务处理器 <br>
	 *
	 * @return 返回处理器, 没有则返回null
	 */
	List<TimeTaskHandler> getHandlerList(TaskReceiverType group, Collection<String> nameColl);

	/**
	 * 获取处理器
	 *
	 * @param handlerName 处理器名字
	 * @return
	 */
	TimeTaskHandler getHandler(String handlerName);

}
