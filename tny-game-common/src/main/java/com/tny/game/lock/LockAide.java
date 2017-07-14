package com.tny.game.lock;

import java.util.*;
import java.util.Map.Entry;
import java.util.concurrent.locks.Lock;

/**
 * 锁工具对象 获取的锁只能在获取锁的线程内使用
 *
 * @author KGTny
 */
public class LockAide {

    /**
     * 锁持有者，用于避免重复的锁创建
     */
    private static ObjectLockHolder holder = new ObjectLockHolder();

    /**
     * 获取多个对象的同步写锁 获取的锁只能在获取锁的线程内使用
     *
     * @param objectCollection 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的写锁对象
     */
    public static Lock getWriteLock(LockEntity<?>... objects) {
        Collection<ObjectLock> locks = loadLocks(LockType.WRITE, objects);
        return new LinkedLock(new ArrayList<ObjectLock>(locks));
    }

    /**
     * 获取多个对象的同步读锁 获取的锁只能在获取锁的线程内使用
     *
     * @param objectCollection 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的写锁对象
     */
    public static Lock getReadLock(LockEntity<?>... objects) {
        Collection<ObjectLock> locks = loadLocks(LockType.READ, objects);
        return new LinkedLock(new ArrayList<ObjectLock>(locks));
    }

    /**
     * 获取多个对象的同步读写锁 当同个对象同时获取读锁和写锁时,只获取写锁 获取的锁只能在获取锁的线程内使用
     *
     * @param objectCollection 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的读或写锁对象
     */
    public static Lock getLock(LockEntity<?>[] readObjects, LockEntity<?>[] writeObjects) {
        Collection<ObjectLock> set = loadLocks(LockType.WRITE, writeObjects);
        set.addAll(loadLocks(LockType.READ, readObjects));
        return new LinkedLock(new ArrayList<ObjectLock>(set));
    }

    /**
     * 获取多个对象的同步锁 获取的锁只能在获取锁的线程内使用
     *
     * @param lockType         获取的所类型
     * @param objectCollection 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的锁对象
     */
    public static Lock getLock(LockType lockType, LockEntity<?>... objects) {
        Collection<ObjectLock> locks = loadLocks(lockType, objects);
        return new LinkedLock(new ArrayList<ObjectLock>(locks));
    }

    /**
     * 获取多个对象的同步写锁 获取的锁只能在获取锁的线程内使用
     *
     * @param objectCollection 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的锁对象
     */
    public static Lock getWriteLock(Collection<LockEntity<?>> objectCollection) {
        Collection<ObjectLock> locks = loadLocks(null, objectCollection.toArray(new LockEntity<?>[0]));
        return new LinkedLock(new ArrayList<ObjectLock>(locks));
    }

    /**
     * 获取多个对象的同步读锁 获取的锁只能在获取锁的线程内使用
     *
     * @param objectCollection 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的锁对象
     */
    public static LinkedLock getReadLock(Collection<LockEntity<?>> objectCollection) {
        Collection<ObjectLock> locks = loadLocks(null, objectCollection.toArray(new LockEntity<?>[0]));
        return new LinkedLock(new ArrayList<ObjectLock>(locks));
    }

    /**
     * 获取多个对象的同步读写锁 获取的锁只能在获取锁的线程内使用
     *
     * @param objects 要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的锁对象
     */
    public static LinkedLock getLock(Collection<LockEntity<?>> readObjectCol, Collection<LockEntity<?>> writeObjectCol) {
        Collection<ObjectLock> set = loadLocks(LockType.WRITE, writeObjectCol);
        set.addAll(loadLocks(LockType.READ, readObjectCol));
        return new LinkedLock(new ArrayList<ObjectLock>(set));
    }

    /**
     * 获取多个对象的同步锁 获取的锁只能在获取锁的线程内使用
     *
     * @param lockType 获取的所类型
     * @param objects  要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的锁对象
     */
    public static LinkedLock getLock(LockType lockType, Collection<LockEntity<?>> objectCol) {
        Collection<ObjectLock> set = loadLocks(lockType, objectCol);
        return new LinkedLock(new ArrayList<ObjectLock>(set));
    }

    /**
     * 获取多个对象的同步锁 获取的锁只能在获取锁的线程内使用
     *
     * @param lockType 获取的所类型
     * @param objects  要获得锁的对象或实体实例数组
     * @return 可同时锁定参数对象的锁对象
     */
    public static LinkedLock getLock(Map<LockEntity<?>, LockType> objectMap) {
        Collection<ObjectLock> set = loadLocks(objectMap);
        return new LinkedLock(new ArrayList<ObjectLock>(set));
    }

    /**
     * 获取锁定顺序正确的锁列表 获取的锁只能在获取锁的线程内使用
     *
     * @param objectCol 要获得锁的对象或实体实例集合
     * @return
     */
    private static Collection<ObjectLock> loadLocks(LockType lockType, Collection<LockEntity<?>> objectCol) {
        // 获取锁并排序
        Set<ObjectReadWriteLock> locks = new HashSet<ObjectReadWriteLock>(objectCol.size());
        for (LockEntity<?> obj : objectCol) {
            if (obj != null) {
                ObjectReadWriteLock lock = holder.getLock(obj, lockType);
                locks.add(lock);
            }
        }
        return new TreeSet<ObjectLock>(locks);
    }

    /**
     * 获取锁定顺序正确的锁列表 获取的锁只能在获取锁的线程内使用
     *
     * @param objects 要获得锁的对象或实体实例数组
     * @return
     */
    private static Collection<ObjectLock> loadLocks(LockType lockType, LockEntity<?>... objects) {
        // 获取锁并排序
        Set<ObjectReadWriteLock> locks = new HashSet<ObjectReadWriteLock>(objects.length);
        for (LockEntity<?> obj : objects) {
            if (obj != null) {
                ObjectReadWriteLock lock = holder.getLock(obj, lockType);
                locks.add(lock);
            }
        }
        return new TreeSet<ObjectLock>(locks);
    }

    /**
     * 获取锁定顺序正确的锁列表 获取的锁只能在获取锁的线程内使用
     *
     * @param objects 要获得锁的对象或实体实例数组
     * @return
     */
    private static Collection<ObjectLock> loadLocks(Map<LockEntity<?>, LockType> objectMap) {
        // 获取锁并排序
        Set<ObjectReadWriteLock> locks = new HashSet<ObjectReadWriteLock>(objectMap.size());
        for (Entry<LockEntity<?>, LockType> entry : objectMap.entrySet()) {
            if (entry.getKey() != null) {
                ObjectReadWriteLock lock = holder.getLock(entry.getKey(), entry.getValue());
                locks.add(lock);
            }
        }
        return new TreeSet<ObjectLock>(locks);
    }

    // /**
    // * 获取多个对象的同步锁
    // * @param objects 要获得锁的对象或实体实例数组
    // * @return 可同时锁定参数对象的锁对象
    // */
    // public static LinkedLock testLocks(Collection<?> objectCollection) {
    // List<ObjectLock> locks = testLocks(objectCollection.toArray(new Object
    // [0]));
    // return new LinkedLock(locks);
    // }
    //
    // /**
    // * 获取锁定顺序正确的锁列表
    // * @param objects 要获得锁的对象或实体实例数组
    // * @return
    // */
    // private static List<ObjectLock> testLocks(Object...objects) {
    // // 获取锁并排序
    // Set<ObjectReadWriteLock> locks = new
    // HashSet<ObjectReadWriteLock>(objects.length);
    // for (Object obj : objects) {
    // if (obj != null) {
    // ObjectReadWriteLock lock = holder.justGetLock(obj);
    // if (lock != null)
    // locks.add(lock);
    // }
    // }
    // locks = new TreeSet<ObjectReadWriteLock>(locks);
    // return new ArrayList<ObjectLock>(locks);
    // }

}
