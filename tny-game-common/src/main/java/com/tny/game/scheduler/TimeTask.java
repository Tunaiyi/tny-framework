package com.tny.game.scheduler;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TimeTask implements Comparable<TimeTask>, Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * @uml.property name="executTime"
     */
    private long executTime;

    /**
     * @uml.property name="handlerList"
     */
    private List<String> handlerList = new ArrayList<String>();

    protected TimeTask(TimeTaskModel holder) {
        this.executTime = holder.nextFireTime().getTime() / 1000 * 1000;
        this.addTaskHandler(holder);
    }

    public TimeTask(List<String> handlerList, long executTime) {
        this.executTime = executTime;
        this.handlerList.addAll(handlerList);
    }

    /**
     * @return
     * @uml.property name="executTime"
     */
    public long getExecutTime() {
        return this.executTime;
    }

    public List<String> getHandlerList() {
        return Collections.unmodifiableList(this.handlerList);
    }

    protected void addTaskHandler(TimeTaskModel taskModel) {
        this.handlerList.addAll(taskModel.getHandlerList());
    }

    @Override
    public int compareTo(TimeTask handler) {
        long value = handler.executTime - this.executTime;
        if (value == 0L)
            return 0;
        return value < 0L ? -1 : 1;
    }

    @Override
    public String toString() {
        return "\nTimeTaskHandlerHolder [executTime=" + this.executTime + ", handlerList=" + this.handlerList +
                "]\n";
    }

    public int size() {
        return this.handlerList.size();
    }

}
