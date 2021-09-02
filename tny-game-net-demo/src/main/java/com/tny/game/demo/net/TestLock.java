package com.tny.game.demo.net;

import com.tny.game.common.number.*;
import com.tny.game.common.runtime.*;

import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.locks.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/5/15 2:16 上午
 */
public class TestLock {

	public static void main(String[] args) throws ExecutionException, InterruptedException {
		StampedLock stampedLock = new StampedLock();
		ReadWriteLock readWriteLock = new ReentrantReadWriteLock();
		ExecutorService executorService = Executors.newCachedThreadPool();
		ProcessWatcher stampedLockWatcher = ProcessWatcher.of(StampedLock.class, TrackPrintOption.CLOSE);
		ProcessWatcher readWriteLockWatcher = ProcessWatcher.of(ReadWriteLock.class, TrackPrintOption.CLOSE);
		ProcessWatcher noneLockWatcher = ProcessWatcher.of(TestLock.class, TrackPrintOption.CLOSE);
		List<Future<?>> futureList = new ArrayList<>();
		int threads = 4;
		CyclicBarrier cyclicBarrier = new CyclicBarrier(threads);
		long times = 100000000;
		int sleepTimes = 100000;
		for (int index = 0; index < threads; index++) {
			long task = index;
			futureList.add(executorService.submit(() -> {
				IntLocalNum readNum = new IntLocalNum(0);
				IntLocalNum successNum = new IntLocalNum(0);
				await(cyclicBarrier);
				for (int i = 0; i < times; i++) {
					ProcessTracer tracer = stampedLockWatcher.trace();
					long stamp = stampedLock.tryOptimisticRead();
					if (stampedLock.validate(stamp)) {
						successNum.add(1);
					} else {
						long readStamp = stampedLock.readLock();
						try {
							readNum.add(1);
						} finally {
							stampedLock.unlock(readStamp);
						}
					}
					tracer.done();
					sleep(i, sleepTimes);
				}
				System.out.println("stampedLock [" + task + "] successNum : " + successNum.intValue() + " | readNum : " + readNum.intValue());
			}));
		}
		for (Future<?> future : futureList) {
			future.get();
		}

		futureList.clear();
		cyclicBarrier.reset();
		for (int index = 0; index < threads; index++) {
			long task = index;
			futureList.add(executorService.submit(() -> {
				IntLocalNum num = new IntLocalNum(0);
				Lock readLock = readWriteLock.readLock();
				await(cyclicBarrier);
				for (int i = 0; i < times; i++) {
					ProcessTracer tracer = readWriteLockWatcher.trace();
					readLock.lock();
					try {
						num.add(1);
					} finally {
						readLock.unlock();
					}
					tracer.done();
					sleep(i, sleepTimes);
				}
				System.out.println("readWriteLock [" + task + "] num : " + num.intValue());
			}));
		}
		for (Future<?> future : futureList) {
			future.get();
		}

		futureList.clear();
		cyclicBarrier.reset();
		for (int index = 0; index < threads; index++) {
			long task = index;
			futureList.add(executorService.submit(() -> {
				IntLocalNum num = new IntLocalNum(0);
				await(cyclicBarrier);
				for (int i = 0; i < times; i++) {
					ProcessTracer tracer = noneLockWatcher.trace();
					num.add(1);
					tracer.done();
					sleep(i, sleepTimes);
				}
				System.out.println("NoLock [" + task + "] num : " + num.intValue());
			}));
		}
		for (Future<?> future : futureList) {
			future.get();
		}

		Thread.sleep(3 * 1000);
		stampedLockWatcher.statisticsLog();
		readWriteLockWatcher.statisticsLog();
		noneLockWatcher.statisticsLog();
	}

	private static void await(CyclicBarrier cyclicBarrier) {
		try {
			cyclicBarrier.await();
		} catch (Throwable e) {
			e.printStackTrace();
		}
	}

	private static void sleep(int i, int sleepTimes) {
		try {
			if (i % sleepTimes == 0) {
				Thread.sleep(5);
			}
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

}
