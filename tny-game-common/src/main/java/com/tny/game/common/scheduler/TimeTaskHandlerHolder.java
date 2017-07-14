package com.tny.game.common.scheduler;

import java.util.Collection;
import java.util.List;

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
     * @param name 处理器名称
     * @return 返回处理器, 没有则返回null
     */
    public List<TimeTaskHandler> getHandlerList(Object group, Collection<String> nameColl);

    public TimeTaskHandler getHandler(String handlerName);

    public void init();

}
