package com.tny.game.common.concurrent.lock.locker;

import java.util.Optional;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Lock;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/9/15 4:07 下午
 */
public interface ObjectLocker<O> {

	Lock lock(O object);

	Lock lockInterruptibly(O object) throws InterruptedException;

	Optional<Lock> tryLock(O object);

	Optional<Lock> tryLock(O object, long timeout, TimeUnit unit) throws InterruptedException;

	void unlock(O object, Lock lock);

}
