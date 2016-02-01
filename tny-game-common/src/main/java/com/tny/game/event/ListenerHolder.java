package com.tny.game.event;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * @author KGTny
 * @ClassName: ListenerHolder
 * @Description: 监听器持有器
 * @date 2011-10-9 下午8:09:57
 * <p>
 * 监听器持有器
 * <p>
 * 监听器持有器,负责管理监听器<br>
 */
class ListenerHolder {

    /**
     * 处理器持有器Map
     *
     * @uml.property name="holderListMap"
     * @uml.associationEnd multiplicity="(0 -1)" ordering="true"
     * qualifier="handler:java.lang.String java.util.List"
     */
    protected final Map<String, List<ListenerHandlerHolder>> holderListMap = new HashMap<String, List<ListenerHandlerHolder>>();

    private ReadWriteLock lock = new ReentrantReadWriteLock();
    private Lock readLock = this.lock.readLock();
    private Lock writeLock = this.lock.writeLock();

    /**
     * 获取处理器持有器列表
     * <p>
     * <p>
     * 获取指定处理器的处理器持有器列表 <br>
     *
     * @param handler 指定处理器
     * @return 返回处理器持有器列表
     */
    public List<ListenerHandlerHolder> getListenerHandlerHolder(String handler) {
        this.readLock.lock();
        try {
            return this.holderListMap.get(handler);
        } finally {
            this.readLock.unlock();
        }
    }

    /**
     * 添加处理器持有器
     * <p>
     * <p>
     * <p>
     * 添加处理器持有器<br>
     *
     * @param holder 添加的处理器持有器
     */
    public void addListenerHandlerHolder(ListenerHandlerHolder holder) {
        List<ListenerHandlerHolder> holderList = this.holderListMap.get(holder.getName());
        if (holderList == null) {
            this.writeLock.lock();
            try {
                holderList = this.holderListMap.get(holder.getName());
                if (holderList == null) {
                    holderList = new CopyOnWriteArrayList<ListenerHandlerHolder>();
                    List<ListenerHandlerHolder> oldHoldList = this.holderListMap.put(holder.getName(), holderList);
                    if (oldHoldList != null)
                        holderList = oldHoldList;
                    else
                        this.holderListMap.put(holder.getName(), holderList);
                }
            } finally {
                this.writeLock.unlock();
            }
        }
        holderList.add(holder);
    }

    /**
     * 删除监听器对应的处理器持有器
     * <p>
     * <p>
     * 删除监听器对应的处理器持有器 <br>
     *
     * @param listener 指定的监听器
     */
    public void removeListenerHandlerHolder(Object listener) {
        this.writeLock.lock();
        try {
            for (List<ListenerHandlerHolder> handlerHolderList : this.holderListMap.values()) {
                for (ListenerHandlerHolder handlerHolder : handlerHolderList) {
                    if (handlerHolder.isHodlerOnwer(listener))
                        handlerHolderList.remove(handlerHolder);
                }
            }
        } finally {
            this.writeLock.unlock();
        }
    }

}
