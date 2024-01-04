/*
 * Copyright (c) 2020 Tunaiyi
 * Tny Framework is licensed under Mulan PSL v2.
 * You can use this software according to the terms and conditions of the Mulan PSL v2.
 * You may obtain a copy of Mulan PSL v2 at:
 *          http://license.coscl.org.cn/MulanPSL2
 * THIS SOFTWARE IS PROVIDED ON AN "AS IS" BASIS, WITHOUT WARRANTIES OF ANY KIND, EITHER EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO
 * NON-INFRINGEMENT, MERCHANTABILITY OR FIT FOR A PARTICULAR PURPOSE.
 * See the Mulan PSL v2 for more details.
 */

package com.tny.game.common.lock;// package com.tny.game.lock;
//
// import org.junit.Before;
// import org.junit.Test;
//
// import java.util.ArrayList;
// import java.util.Collection;
// import java.util.List;
// import java.util.concurrent.TimeUnit;
// import java.util.concurrent.atomic.AtomicInteger;
// import java.util.concurrent.locks.Lock;
//
// @SuppressWarnings("deprecation")
// public class CollectionLockTest {
//
//     private static AtomicInteger value = new AtomicInteger(0);
//
//     private static final long SLEEP_TIME = 1000L;
//     private static final int OBJECT_SIZE = 100;
//
//     @Before
//     public void reset() {
//         value = new AtomicInteger(0);
//     }
//
//     public static void sleep(long sleep) {
//         if (sleep > 0) {
//             try {
//                 Thread.sleep(sleep);
//             } catch (InterruptedException e) {
//                 e.printStackTrace();
//             }
//         }
//     }
//
//     public static class LockOne implements LockEntity<LockOne>, Comparable<LockOne> {
//
//         private int id;
//
//         public LockOne(int id) {
//             this.id = id;
//         }
//
//         @Override
//         public LockOne getIdentity() {
//             return this;
//         }
//
//         @Override
//         public int compareTo(LockOne other) {
//             return this.id - other.id;
//         }
//
//     }
//
//     public static class LockSame implements LockEntity<Integer>, Comparable<LockSame> {
//
//         private int id;
//
//         public LockSame(int id) {
//             this.id = id;
//         }
//
//         @Override
//         public Integer getIdentity() {
//             return this.id;
//         }
//
//         @Override
//         public int compareTo(LockSame other) {
//             return this.id - other.id;
//         }
//
//         @Override
//         public int hashCode() {
//             final int prime = 31;
//             int result = 1;
//             result = prime * result + id;
//             return result;
//         }
//
//         @Override
//         public boolean equals(Object obj) {
//             if (this == obj)
//                 return true;
//             if (obj == null)
//                 return false;
//             if (getClass() != obj.getClass())
//                 return false;
//             LockSame other = (LockSame) obj;
//             if (id != other.id)
//                 return false;
//             return true;
//         }
//
//     }
//
//     /**
//      * @author KGTny
//      */
//     public static class LockRunner extends TestRunnable {
//
//         protected Collection<LockEntity<?>> object;
//
//         protected long sleep;
//
//         protected String msg;
//
//         protected int[] expectedArrary;
//
//         protected boolean got;
//
//         /**
//          * @uml.property name="other"
//          * @uml.associationEnd
//          */
//         protected LockRunner other;
//
//         public LockRunner(Collection<LockEntity<?>> object, long sleep, String msg,
//                           int[] expectedArrary) {
//             this(object, sleep, msg, expectedArrary, null);
//         }
//
//         public LockRunner(Collection<LockEntity<?>> object, long sleep, String msg,
//                           int[] expectedArrary, LockRunner other) {
//             super();
//             this.object = object;
//             this.expectedArrary = expectedArrary;
//             this.sleep = sleep;
//             this.msg = msg;
//             this.other = other;
//             this.got = false;
//         }
//
//         @Override
//         public void runTest() throws Throwable {
//             Lock lock = LockUtils.getWriteLock(this.object);
//             while (this.other != null && !this.other.got)
//                 Thread.yield();
//             lock.lock();
//             assertEquals(expectedArrary[0], value.incrementAndGet());
//             if (this.other == null)
//                 this.got = true;
//             System.out.println("Lock " + this.msg);
//             sleep(sleep);
//             assertEquals(expectedArrary[1], value.incrementAndGet());
//             lock.unlock();
//
//             System.out.println("unlock " + this.msg);
//         }
//
//     }
//
//     @Test
//     public void testLinkedLock1() {
//         System.out.println();
//         System.out.println("testLinkedLock1");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         List<LockEntity<?>> otherLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockOne(index));
//             otherLockList.add(new LockOne(index));
//         }
//         int[] oneExpected = {1, 4};
//         int[] otherExpected = {2, 3};
//         LockRunner oneRunnable =
//                 new LockRunner(oneLockList, SLEEP_TIME, "lockOneOne lockOneOther 1", oneExpected);
//         LockRunner otherRunnable =
//                 new LockRunner(otherLockList, 0L, "lockOneOne lockOneOther 2", otherExpected,
//                         oneRunnable);
//         TestRunnable[] trs = {oneRunnable, otherRunnable};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     @Test
//     public void testLinkedLock2() {
//         System.out.println();
//         System.out.println("testLinkedLock2");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockOne(index));
//         }
//         int[] oneExpected = {1, 2};
//         int[] otherExpected = {3, 4};
//         LockRunner oneRunnable =
//                 new LockRunner(oneLockList, SLEEP_TIME, "lockOneOne lockOneOther 1", oneExpected);
//         LockRunner otherRunnable =
//                 new LockRunner(oneLockList, 0L, "lockOneOne lockOneOther 1", otherExpected,
//                         oneRunnable);
//
//         TestRunnable[] trs = {oneRunnable, otherRunnable};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     @Test
//     public void testLinkedLock3() {
//         System.out.println();
//         System.out.println("testLinkedLock3");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         List<LockEntity<?>> otherLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockSame(index));
//             otherLockList.add(new LockSame(index));
//         }
//         int[] oneExpected = {1, 2};
//         int[] otherExpected = {3, 4};
//         LockRunner oneRunnable =
//                 new LockRunner(oneLockList, SLEEP_TIME, "lockOneOne lockOneOther 1", oneExpected);
//         LockRunner otherRunnable =
//                 new LockRunner(otherLockList, 0L, "lockOneOne lockOneOther 2", otherExpected,
//                         oneRunnable);
//
//         // TestRunnable[] trs = {oneRunnable, otherRunnable};
//         // MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//         //
//         // try {
//         //     mttr.runTestRunnables();
//         // } catch (Throwable e) {
//         //     e.printStackTrace();
//         // }
//     }
//
//     /**
//      * @author KGTny
//      */
//     // public static class ReadWrtieLockRunner extends TestRunnable {
//     //
//     //     protected Collection<LockEntity<?>> object;
//     //
//     //     protected long sleep;
//     //
//     //     /**
//     //      * @uml.property name="lockType"
//     //      * @uml.associationEnd
//     //      */
//     //     protected LockType lockType;
//     //
//     //     protected int[] expectedArrary;
//     //
//     //     protected boolean got;
//     //
//     //     /**
//     //      * @uml.property name="other"
//     //      * @uml.associationEnd
//     //      */
//     //     protected ReadWrtieLockRunner other;
//     //
//     //     public ReadWrtieLockRunner(Collection<LockEntity<?>> object, long sleep, LockType lockType,
//     //                                int[] expectedArrary) {
//     //         this(object, sleep, lockType, expectedArrary, null);
//     //     }
//     //
//     //     public ReadWrtieLockRunner(Collection<LockEntity<?>> object, long sleep, LockType lockType,
//     //                                int[] expectedArrary, ReadWrtieLockRunner writeRunnable) {
//     //         super();
//     //         this.object = object;
//     //         this.expectedArrary = expectedArrary;
//     //         this.sleep = sleep;
//     //         this.lockType = lockType;
//     //         this.other = writeRunnable;
//     //         this.got = false;
//     //     }
//     //
//     //     @Override
//     //     public void runTest() throws Throwable {
//     //         LinkedLock lock = LockUtils.getLock(this.lockType, this.object);
//     //         assertEquals(OBJECT_SIZE, lock.size());
//     //         while (this.other != null && !this.other.got)
//     //             Thread.yield();
//     //         lock.lock();
//     //         assertEquals(expectedArrary[0], value.incrementAndGet());
//     //         if (this.other == null)
//     //             this.got = true;
//     //         System.out.println("Lock " + this.lockType);
//     //         sleep(sleep);
//     //         assertEquals(expectedArrary[1], value.incrementAndGet());
//     //         lock.unlock();
//     //
//     //         System.out.println("unlock " + this.lockType);
//     //     }
//     //
//     // }
//
//     @Test
//     public void testReadWriteLinkedLock() {
//         System.out.println();
//         System.out.println("testReadWriteLinkedLock");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockSame(index));
//         }
//         int[] writeExpected = {1, 2};
//         int[] readExpected = {3, 4};
//         ReadWrtieLockRunner writeRunnable =
//                 new ReadWrtieLockRunner(oneLockList, SLEEP_TIME, LockType.WRITE, writeExpected);
//         ReadWrtieLockRunner readRunnable =
//                 new ReadWrtieLockRunner(oneLockList, 0L, LockType.READ, readExpected, writeRunnable);
//
//         TestRunnable[] trs = {writeRunnable, readRunnable};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     @Test
//     public void testWriteReadLinkedLock() {
//         System.out.println();
//         System.out.println("testWriteReadLinkedLock");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockSame(index));
//         }
//         int[] readExpected = {1, 2};
//         int[] writeExpected = {3, 4};
//         ReadWrtieLockRunner readRunnable =
//                 new ReadWrtieLockRunner(oneLockList, SLEEP_TIME, LockType.READ, readExpected);
//         ReadWrtieLockRunner writeRunnable =
//                 new ReadWrtieLockRunner(oneLockList, 0L, LockType.WRITE, writeExpected,
//                         readRunnable);
//
//         TestRunnable[] trs = {readRunnable, writeRunnable};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     @Test
//     public void testWriteWriteLinkedLock() {
//         System.out.println();
//         System.out.println("testWriteReadLinkedLock");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockSame(index));
//         }
//         int[] writeExpected1 = {1, 2};
//         int[] writeExpected2 = {3, 4};
//         ReadWrtieLockRunner writeRunnable1 =
//                 new ReadWrtieLockRunner(oneLockList, SLEEP_TIME, LockType.WRITE, writeExpected1);
//         ReadWrtieLockRunner writeRunnable2 =
//                 new ReadWrtieLockRunner(oneLockList, 0L, LockType.WRITE, writeExpected2,
//                         writeRunnable1);
//
//         TestRunnable[] trs = {writeRunnable1, writeRunnable2};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     @Test
//     public void testReadReadLinkedLock() {
//         System.out.println();
//         System.out.println("testReadReadLinkedLock");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++) {
//             oneLockList.add(new LockSame(index));
//         }
//         int[] readExpected1 = {1, 4};
//         int[] readExpected2 = {2, 3};
//         ReadWrtieLockRunner readRunnable1 =
//                 new ReadWrtieLockRunner(oneLockList, SLEEP_TIME, LockType.READ, readExpected1);
//         ReadWrtieLockRunner readRunnable2 =
//                 new ReadWrtieLockRunner(oneLockList, 0L, LockType.READ, readExpected2,
//                         readRunnable1);
//
//         TestRunnable[] trs = {readRunnable1, readRunnable2};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     /**
//      * @author KGTny
//      */
//     public static class LockInterruptiblyRunnale extends TestRunnable {
//
//         public final List<LockEntity<?>> oneLockList;
//
//         protected List<Integer> expectedList;
//
//         /**
//          * @uml.property name="runnale"
//          * @uml.associationEnd
//          */
//         private InterruptRunnale runnale;
//
//         /**
//          * @uml.property name="thread"
//          */
//         private Thread thread;
//
//         private boolean interrupt;
//
//         private int number;
//
//         public LockInterruptiblyRunnale(List<LockEntity<?>> oneLockList, Integer[] expectedArrary,
//                                         InterruptRunnale runnale, boolean interrupt) {
//             this.oneLockList = oneLockList;
//             this.expectedList = new ArrayList<Integer>();
//             for (Integer id : expectedArrary) {
//                 expectedList.add(id);
//             }
//             this.runnale = runnale;
//             this.interrupt = interrupt;
//         }
//
//         @Override
//         public void runTest() throws Throwable {
//             Lock lock = LockUtils.getWriteLock(this.oneLockList);
//             try {
//                 if (interrupt)
//                     runnale.thread = Thread.currentThread();
//                 while (!runnale.isLocked())
//                     Thread.yield();
//                 lock.lockInterruptibly();
//                 number++;
//                 assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                 try {
//                     assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                 } finally {
//                     assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                     lock.unlock();
//                 }
//             } catch (InterruptedException e) {
//                 assertEquals(expectedList.remove(0), (Integer) number);
//             } finally {
//
//             }
//         }
//
//         /**
//          * @return
//          * @uml.property name="thread"
//          */
//         public Thread getThread() {
//             return thread;
//         }
//
//     }
//
//     /**
//      * @author KGTny
//      */
//     public static class InterruptRunnale extends TestRunnable {
//
//         public final List<LockEntity<?>> oneLockList;
//
//         public Thread thread;
//
//         protected List<Integer> expectedList;
//
//         /**
//          * @uml.property name="locked"
//          */
//         protected volatile boolean locked;
//
//         public InterruptRunnale(List<LockEntity<?>> oneLockList, Integer[] expectedArrary) {
//             this.oneLockList = oneLockList;
//             this.expectedList = new ArrayList<Integer>();
//             for (Integer id : expectedArrary) {
//                 expectedList.add(id);
//             }
//             this.locked = false;
//         }
//
//         @Override
//         public void runTest() throws Throwable {
//             Lock lock = LockUtils.getWriteLock(this.oneLockList);
//             try {
//                 lock.lockInterruptibly();
//                 assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                 locked = true;
//                 try {
//                     Thread.sleep(SLEEP_TIME);
//                     assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                     thread.interrupt();
//                 } catch (InterruptedException e) {
//                     e.printStackTrace();
//                 } finally {
//                     assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                     lock.unlock();
//                 }
//             } catch (Exception e) {
//                 e.printStackTrace();
//             } finally {
//
//             }
//         }
//
//         /**
//          * @return
//          * @uml.property name="locked"
//          */
//         public boolean isLocked() {
//             return locked;
//         }
//
//     }
//
//     @Test
//     public void testLockInterruptibly() {
//         System.out.println();
//         System.out.println("testLockInterruptibly");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++)
//             oneLockList.add(new LockSame(index));
//
//         Integer[] beInterruptExpected = {0};
//         Integer[] interruptExpected = {1, 2, 3};
//         Integer[] neverBeInterruptExpected = {4, 5, 6};
//
//         InterruptRunnale interruptRunnable = new InterruptRunnale(oneLockList, interruptExpected);
//         LockInterruptiblyRunnale beInterruptRunnable =
//                 new LockInterruptiblyRunnale(oneLockList, beInterruptExpected, interruptRunnable,
//                         true);
//         LockInterruptiblyRunnale neverBeInterrupt =
//                 new LockInterruptiblyRunnale(oneLockList, neverBeInterruptExpected,
//                         interruptRunnable, false);
//
//         TestRunnable[] trs = {interruptRunnable, beInterruptRunnable, neverBeInterrupt};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     /**
//      * @author KGTny
//      */
//     public static class TryLockRunnale extends TestRunnable {
//
//         public final List<LockEntity<?>> oneLockList;
//
//         protected List<Integer> expectedList;
//
//         /**
//          * @uml.property name="locked"
//          */
//         protected volatile boolean locked;
//
//         /**
//          * @uml.property name="runnale"
//          * @uml.associationEnd
//          */
//         protected TryLockRunnale runnale;
//
//         public TryLockRunnale(List<LockEntity<?>> oneLockList, Integer[] expectedArrary,
//                               TryLockRunnale runnale) {
//             this.oneLockList = oneLockList;
//             this.expectedList = new ArrayList<Integer>();
//             for (Integer id : expectedArrary) {
//                 expectedList.add(id);
//             }
//             this.locked = false;
//             this.runnale = runnale;
//         }
//
//         @Override
//         public void runTest() throws Throwable {
//             Lock lock = LockUtils.getWriteLock(this.oneLockList);
//             try {
//                 while (runnale != null && !runnale.locked)
//                     Thread.yield();
//                 if (this.locked = lock.tryLock()) {
//                     try {
//                         Thread.sleep(SLEEP_TIME);
//                         assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     } finally {
//                         assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                         lock.unlock();
//                     }
//                 } else {
//                     assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                 }
//             } finally {
//
//             }
//         }
//
//         /**
//          * @return
//          * @uml.property name="locked"
//          */
//         public boolean isLocked() {
//             return locked;
//         }
//
//     }
//
//     @Test
//     public void testTryLock() {
//         System.out.println();
//         System.out.println("testTryLock");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++)
//             oneLockList.add(new LockSame(index));
//
//         Integer[] tryLocktryExpected1 = {2, 3};
//         Integer[] tryLocktryExpected2 = {1};
//
//         TryLockRunnale tryLockRunnale1 = new TryLockRunnale(oneLockList, tryLocktryExpected1, null);
//         TryLockRunnale tryLockRunnale2 =
//                 new TryLockRunnale(oneLockList, tryLocktryExpected2, tryLockRunnale1);
//
//         TestRunnable[] trs = {tryLockRunnale1, tryLockRunnale2};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     /**
//      * @author KGTny
//      */
//     public static class TryLockTimeRunnale extends TestRunnable {
//
//         public final List<LockEntity<?>> oneLockList;
//
//         protected List<Integer> expectedList;
//
//         /**
//          * @uml.property name="locked"
//          */
//         protected volatile boolean locked;
//
//         /**
//          * @uml.property name="runnale"
//          * @uml.associationEnd
//          */
//         protected TryLockTimeRunnale runnale;
//
//         protected long waitTime;
//
//         public TryLockTimeRunnale(List<LockEntity<?>> oneLockList, long waitTime,
//                                   Integer[] expectedArrary, TryLockTimeRunnale runnale) {
//             this.oneLockList = oneLockList;
//             this.expectedList = new ArrayList<Integer>();
//             for (Integer id : expectedArrary) {
//                 expectedList.add(id);
//             }
//             this.locked = false;
//             this.waitTime = waitTime;
//             this.runnale = runnale;
//         }
//
//         @Override
//         public void runTest() throws Throwable {
//             Lock lock = LockUtils.getWriteLock(this.oneLockList);
//             try {
//                 while (runnale != null && !runnale.locked)
//                     Thread.yield();
//                 if (this.locked = lock.tryLock(this.waitTime, TimeUnit.MICROSECONDS)) {
//                     try {
//                         System.out.println((runnale == null ? "11" : "22") + 1);
//                         Thread.sleep(SLEEP_TIME);
//                         assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                     } catch (Exception e) {
//                         e.printStackTrace();
//                     } finally {
//                         System.out.println((runnale == null ? "11" : "22") + 2);
//                         assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                         lock.unlock();
//                     }
//                 } else {
//                     System.out.println((runnale == null ? "11" : "22") + 3);
//                     assertEquals(expectedList.remove(0), (Integer) value.incrementAndGet());
//                 }
//             } catch (Exception e) {
//                 System.out.println((runnale == null ? "11" : "22") + 4);
//                 e.printStackTrace();
//             } finally {
//                 System.out.println((runnale == null ? "11" : "22") + 5);
//
//             }
//         }
//
//         /**
//          * @return
//          * @uml.property name="locked"
//          */
//         public boolean isLocked() {
//             return locked;
//         }
//
//     }
//
//     @Test
//     public void testTryLockLongTimeUnit() {
//         System.out.println();
//         System.out.println("testTryLock");
//
//         List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//         for (int index = 0; index < OBJECT_SIZE; index++)
//             oneLockList.add(new LockSame(index));
//
//         Integer[] tryLocktryExpected1 = {2, 3};
//         Integer[] tryLocktryExpected2 = {1};
//
//         TryLockTimeRunnale tryLockRunnale1 =
//                 new TryLockTimeRunnale(oneLockList, 1000L, tryLocktryExpected1, null);
//         TryLockTimeRunnale tryLockRunnale2 =
//                 new TryLockTimeRunnale(oneLockList, 100L, tryLocktryExpected2, tryLockRunnale1);
//
//         TestRunnable[] trs = {tryLockRunnale1, tryLockRunnale2};
//         MultiThreadedTestRunner mttr = new MultiThreadedTestRunner(trs);
//
//         try {
//             mttr.runTestRunnables();
//         } catch (Throwable e) {
//             e.printStackTrace();
//         }
//     }
//
//     // @Test(expected = LockTimeOutException.class)
//     // public void testLockTimeOutExceptionLock() {
//     // System.out.println();
//     // System.out.println("testLockExceptionRelease");
//     //
//     // List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//     // for (int index = 0; index < OBJECT_SIZE; index++) {
//     // oneLockList.add(new LockSame(index));
//     // }
//     // Lock lock = LockUtils.getWriteLock(oneLockList);
//     //
//     // lock.lock();
//     // }
//     //
//     // @Test(expected = LockTimeOutException.class)
//     // public void testLockWithoutHoldExceptionTryLock1() {
//     // System.out.println();
//     // System.out.println("testLockExceptionRelease");
//     //
//     // List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//     // for (int index = 0; index < OBJECT_SIZE; index++) {
//     // oneLockList.add(new LockSame(index));
//     // }
//     // Lock lock = LockUtils.getWriteLock(oneLockList);
//     //
//     // lock.tryLock();
//     // }
//     //
//     // @Test(expected = LockTimeOutException.class)
//     // public void testLockWithoutHoldExceptionTryLock2() {
//     // System.out.println();
//     // System.out.println("testLockExceptionRelease");
//     //
//     // List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//     // for (int index = 0; index < OBJECT_SIZE; index++) {
//     // oneLockList.add(new LockSame(index));
//     // }
//     // Lock lock = LockUtils.getWriteLock(oneLockList);
//     // try {
//     // lock.tryLock(1000L, TimeUnit.SECONDS);
//     // } catch (InterruptedException e) {
//     // e.printStackTrace();
//     // }
//     // }
//     //
//     // @Test(expected = LockTimeOutException.class)
//     // public void testLockWithoutHoldExceptionLockInterruptibly() {
//     // System.out.println();
//     // System.out.println("testLockExceptionRelease");
//     //
//     // List<LockEntity<?>> oneLockList = new ArrayList<LockEntity<?>>();
//     // for (int index = 0; index < OBJECT_SIZE; index++) {
//     // oneLockList.add(new LockSame(index));
//     // }
//     // Lock lock = LockUtils.getWriteLock(oneLockList);
//     // try {
//     // lock.lockInterruptibly();
//     // } catch (InterruptedException e) {
//     // e.printStackTrace();
//     // }
//     // }
//
// }
