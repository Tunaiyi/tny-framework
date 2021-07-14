package com.tny.game.asyndb;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

public class AsyncDBEntityTest {

    private long life = 1000;

    private ReleaseStrategyFactory factory = new DefaultReleaseStrategyFactory();

    private Synchronizer<Object> synchronizer = new Synchronizer<Object>() {

        @Override
        public boolean update(Object object) {
            return true;
        }

        @Override
        public boolean save(Object object) {
            return true;
        }

        @Override
        public boolean insert(Object object) {
            return true;
        }

        @Override
        public boolean delete(Object object) {
            return true;
        }

        @Override
        public Collection<Object> insert(Collection<Object> objects) {
            return null;
        }

        @Override
        public Collection<Object> update(Collection<Object> objects) {
            return null;
        }

        @Override
        public Collection<Object> delete(Collection<Object> objects) {
            return null;
        }

        @Override
        public Collection<Object> save(Collection<Object> objects) {
            return null;
        }

        @Override
        public Object get(Class<? extends Object> clazz, String key) {
            return null;
        }

        @Override
        public Map<String, ? extends Object> get(Class<? extends Object> clazz, Collection<String> keyValues) {
            return null;
        }

    };

    private AsyncDBEntity create(AsyncDBState state) {
        Object object = new long[3];
        AsyncDBEntity entity = new AsyncDBEntity(object, this.synchronizer, state, this.factory.createStrategy(object, this.life));
        return entity;
    }

    //	@Test
    //	public void testTrySync() throws AsynDBReleaseException, SumitAtWrongStateException {
    //		AsynDBEntity entity = null;
    //
    //		entity = this.create(AsynDBState.NORMAL);
    //		Object value = entity.getValue();
    //		assertTrue(entity.trySync(1));
    //		entity = this.create(AsynDBState.NORMAL);
    //		entity.mark(Operation.DELETE, value);
    //		assertTrue(entity.trySync(1));
    //		assertTrue(entity.trySync(1));
    //
    //
    //		entity = this.create(AsynDBState.DELETE);
    //		entity.mark(Operation.INSERT, value);
    //		assertTrue(entity.trySync(1));
    //
    //		entity = this.create(AsynDBState.NORMAL);
    //		entity.mark(Operation.SAVE, value);
    //		assertTrue(entity.trySync(1));
    //
    //		entity = this.create(AsynDBState.NORMAL);
    //		entity.mark(Operation.UPDATE, value);
    //		assertTrue(entity.trySync(1));
    //	}

    @Test
    public void testSumit() {
        final Class<?> WCLASS = OperationStateException.class;
        // ## NOMAL
        this.doTestSumit(AsyncDBState.NORMAL, Operation.DELETE, true, null);
        this.doTestSumit(AsyncDBState.NORMAL, Operation.INSERT, false, WCLASS);
        this.doTestSumit(AsyncDBState.NORMAL, Operation.SAVE, true, null);
        this.doTestSumit(AsyncDBState.NORMAL, Operation.UPDATE, true, null);
        // ## DELETE
        this.doTestSumit(AsyncDBState.DELETE, Operation.DELETE, false, null);
        this.doTestSumit(AsyncDBState.DELETE, Operation.INSERT, false, null);
        this.doTestSumit(AsyncDBState.DELETE, Operation.SAVE, false, null);
        this.doTestSumit(AsyncDBState.DELETE, Operation.UPDATE, false, WCLASS);
        // ## DE
        this.doTestSumit(AsyncDBState.DELETED, Operation.DELETE, true, null);
        this.doTestSumit(AsyncDBState.DELETED, Operation.INSERT, true, null);
        this.doTestSumit(AsyncDBState.DELETED, Operation.SAVE, true, null);
        this.doTestSumit(AsyncDBState.DELETED, Operation.UPDATE, false, WCLASS);
        // ## INSERT
        this.doTestSumit(AsyncDBState.INSERT, Operation.DELETE, false, null);
        this.doTestSumit(AsyncDBState.INSERT, Operation.INSERT, false, WCLASS);
        this.doTestSumit(AsyncDBState.INSERT, Operation.SAVE, false, null);
        this.doTestSumit(AsyncDBState.INSERT, Operation.UPDATE, false, null);
        // ## UPDATE
        this.doTestSumit(AsyncDBState.UPDATE, Operation.DELETE, false, null);
        this.doTestSumit(AsyncDBState.UPDATE, Operation.INSERT, false, WCLASS);
        this.doTestSumit(AsyncDBState.UPDATE, Operation.SAVE, false, null);
        this.doTestSumit(AsyncDBState.UPDATE, Operation.UPDATE, false, null);
        // ## SAVE
        this.doTestSumit(AsyncDBState.SAVE, Operation.DELETE, false, null);
        this.doTestSumit(AsyncDBState.SAVE, Operation.INSERT, false, WCLASS);
        this.doTestSumit(AsyncDBState.SAVE, Operation.SAVE, false, null);
        this.doTestSumit(AsyncDBState.SAVE, Operation.UPDATE, false, null);
    }

    @Test
    public void testTryVisit() {
        AsyncDBEntity entity = null;
        entity = this.create(AsyncDBState.NORMAL);
        assertTrue(entity.tryVisit());
        entity = this.create(AsyncDBState.DELETE);
        assertTrue(entity.tryVisit());
        entity = this.create(AsyncDBState.INSERT);
        assertTrue(entity.tryVisit());
        entity = this.create(AsyncDBState.SAVE);
        assertTrue(entity.tryVisit());
        entity = this.create(AsyncDBState.UPDATE);
        assertTrue(entity.tryVisit());
    }

    @Test
    public void testVisit() {
        AsyncDBEntity entity = null;
        entity = this.create(AsyncDBState.NORMAL);
        assertNotNull(entity.visit());
        entity = this.create(AsyncDBState.DELETE);
        assertNotNull(entity.visit());
        entity = this.create(AsyncDBState.INSERT);
        assertNotNull(entity.visit());
        entity = this.create(AsyncDBState.SAVE);
        assertNotNull(entity.visit());
        entity = this.create(AsyncDBState.UPDATE);
        assertNotNull(entity.visit());
    }

    @Test
    public void testRelease() throws AsyncDBReleaseException, OperationStateException {
        int index = 0;
        int releaseTime = 300;
        AsyncDBEntity entity = null;
        // NOMAL
        entity = this.create(AsyncDBState.NORMAL);
        entity.visit();
        index = 0;
        while (index++ < releaseTime) {
            System.gc();
        }
        assertFalse(entity.release(System.currentTimeMillis()));
        index = 0;
        while (!entity.release(System.currentTimeMillis())) {
            index++;
            System.gc();
        }
        System.out.println(releaseTime = index);
        assertTrue(entity.release(System.currentTimeMillis()));

        // DELETE
        index = 0;
        entity = this.create(AsyncDBState.NORMAL);
        entity.mark(Operation.DELETE, entity.getObject());
        assertFalse(entity.release(System.currentTimeMillis()));
        while (index++ < releaseTime) {
            System.gc();
        }
        assertFalse(entity.release(System.currentTimeMillis()));
        entity.trySync();
        index = 0;
        while (!entity.release(System.currentTimeMillis())) {
            index++;
            System.gc();
        }
        System.out.println(releaseTime = index);
        assertTrue(entity.release(System.currentTimeMillis()));

        // INSERT
        index = 0;
        entity = this.create(AsyncDBState.DELETE);
        entity.mark(Operation.INSERT, entity.getObject());
        assertFalse(entity.release(System.currentTimeMillis()));
        while (index++ < releaseTime) {
            System.gc();
        }
        assertFalse(entity.release(System.currentTimeMillis()));

        // SAVE
        index = 0;
        entity = this.create(AsyncDBState.NORMAL);
        entity.mark(Operation.SAVE, entity.getObject());
        assertFalse(entity.release(System.currentTimeMillis()));
        while (index++ < releaseTime) {
            System.gc();
        }
        assertFalse(entity.release(System.currentTimeMillis()));

        // UPDATE
        index = 0;
        entity = this.create(AsyncDBState.NORMAL);
        entity.mark(Operation.UPDATE, entity.getObject());
        assertFalse(entity.release(System.currentTimeMillis()));
        while (index++ < releaseTime) {
            System.gc();
        }
        assertFalse(entity.release(System.currentTimeMillis()));
    }

    public void sleep(long time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    private void doTestSumit(AsyncDBState state, Operation operation, boolean sumint, Class<?> clazz) {
        AsyncDBEntity entity = null;
        // #### nomal
        entity = this.create(state);
        Exception exception = null;
        try {
            assertEquals(sumint, entity.mark(operation, entity.getObject()));
        } catch (AsyncDBReleaseException e) {
            System.out.println(operation + " AsynDBReleaseException");
            exception = e;
        } catch (OperationStateException e) {
            System.out.println(operation + " SumitAtWrongStateException");
            exception = e;
        }
        if (clazz == null) {
            assertNull(exception);
        } else {
            assertTrue(clazz.isInstance(exception));
        }
    }

}
