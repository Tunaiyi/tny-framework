package com.tny.game.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeTask implements Comparable<TimeTask>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @uml.property name="executeTime"
     */
    private long executeTime;

    /**
     * @uml.property name="handlerList"
     */
    private List<String> handlerList = new ArrayList<String>();

    protected TimeTask(TimeTaskModel holder) {
        this.executeTime = holder.nextFireTime().getTime() / 1000 * 1000;
        this.addTaskHandler(holder);
    }

    public TimeTask(List<String> handlerList, long executeTime) {
        this.executeTime = executeTime;
        this.handlerList.addAll(handlerList);
    }

    /**
     * @return
     * @uml.property name="executeTime"
     */
    public long getExecuteTime() {
        return this.executeTime;
    }

    public List<String> getHandlerList() {
        return Collections.unmodifiableList(this.handlerList);
    }

    protected void addTaskHandler(TimeTaskModel taskModel) {
        this.handlerList.addAll(taskModel.getHandlerList());
    }

    @Override
    public int compareTo(TimeTask handler) {
        long value = handler.executeTime - this.executeTime;
        if (value == 0L)
            return 0;
        return value < 0L ? -1 : 1;
    }

    @Override
    public String toString() {
        return "\nTimeTaskHandlerHolder [executeTime=" + this.executeTime + ", handlerList=" + this.handlerList +
                "]\n";
    }

    public int size() {
        return this.handlerList.size();
    }

}
