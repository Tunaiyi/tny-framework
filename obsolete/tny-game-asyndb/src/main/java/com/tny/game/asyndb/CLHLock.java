package com.tny.game.asyndb;

import java.util.concurrent.atomic.AtomicReference;

public class CLHLock {

    private AtomicReference<QNode> tail;

    private ThreadLocal<QNode> myPred;

    private ThreadLocal<QNode> myNode;

    public CLHLock() {
        this.tail = new AtomicReference<QNode>(new QNode());
        this.myNode = ThreadLocal.withInitial(QNode::new);
        this.myPred = ThreadLocal.withInitial(() -> null);
    }

    public void lock() {
        QNode qnode = this.myNode.get();
        qnode.locked = true;
        QNode pred = this.tail.getAndSet(qnode);
        this.myPred.set(pred);
        while (pred.locked) {
        }
    }

    public void unlock() {
        QNode qnode = this.myNode.get();
        qnode.locked = false;
        this.myNode.set(this.myPred.get());
    }

    static class QNode {

        volatile boolean locked = false;

    }

    static int value = 0;

    //	public static void main(String[] args) {
    //		final CLHLock lock = new CLHLock();
    //
    //		int size = 30;
    //		final CountDownLatch latch = new CountDownLatch(size);
    //		for (int i = 0; i < size; i++) {
    //			final int num = i;
    //			new Thread(new Runnable() {
    //
    //				@Override
    //				public void run() {
    //					latch.countDown();
    //					try {
    //						latch.await();
    //					} catch (InterruptedException e) {
    //						e.printStackTrace();
    //					}
    //					System.out.println("Thread " + num + " 开始 ");
    //					lock.lock();
    //					try {
    //						for (int j = 0; j < 20; j++) {
    //							value++;
    //						}
    //						System.out.println("Thread " + num + " 结束!! " + value);
    //					} finally {
    //						lock.unlock();
    //					}
    //				}
    //
    //			}).start();
    //		}
    //	}
}