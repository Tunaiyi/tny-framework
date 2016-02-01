package com.tny.game.lock;

import java.util.concurrent.locks.Lock;

/**
 * 对象锁接口
 *
 * @author KGTny
 */
public interface ObjectLock extends Lock, Comparable<ObjectReadWriteLock> {

}
