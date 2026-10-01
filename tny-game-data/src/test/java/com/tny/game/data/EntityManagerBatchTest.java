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

package com.tny.game.data;

import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

/**
 * reduce-code-duplication D8-EntityManager 行为钉桩（重构前绿）：
 * insert/update/save/deleteEntities 四个批量 default 方法的现状语义账——
 * 逐元素按迭代序调用对应单实体方法、返回值=成功（true）条数、空集合返回 0、
 * null 集合按现状抛 NullPointerException（禁止顺手修）。
 * 实现收敛为 applyEach 后本文件期望值一字不改仍须全绿。
 */
class EntityManagerBatchTest {

    /**
     * 桩实现：四个单实体方法按各自脚本队列返回 true/false（队列空即失败），
     * 全部调用以 "op:entity" 记入 order 流水；其余抽象方法以计次桩敷衍（批量 default 不得触达）。
     */
    private static final class StubEntityManager implements EntityManager<String, Integer> {

        final List<String> order = new ArrayList<>();
        final Deque<Boolean> insertResults = new ArrayDeque<>();
        final Deque<Boolean> updateResults = new ArrayDeque<>();
        final Deque<Boolean> saveResults = new ArrayDeque<>();
        final Deque<Boolean> deleteResults = new ArrayDeque<>();

        @Override
        public List<Integer> find(Map<String, Object> query, EntityOnLoad<String, Integer> onLoad) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Integer> findAll(EntityOnLoad<String, Integer> onLoad) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer loadEntity(String id, EntityCreator<String, Integer> creator, EntityOnLoad<String, Integer> load) {
            throw new UnsupportedOperationException();
        }

        @Override
        public Integer getEntity(String id, EntityOnLoad<String, Integer> onLoad) {
            throw new UnsupportedOperationException();
        }

        @Override
        public List<Integer> getEntities(List<String> idList, EntityOnLoad<String, Integer> onLoad) {
            throw new UnsupportedOperationException();
        }

        @Override
        public boolean insertEntity(Integer entities) {
            order.add("insert:" + entities);
            return insertResults.poll();
        }

        @Override
        public boolean updateEntity(Integer entities) {
            order.add("update:" + entities);
            return updateResults.poll();
        }

        @Override
        public boolean saveEntity(Integer entities) {
            order.add("save:" + entities);
            return saveResults.poll();
        }

        @Override
        public boolean deleteEntity(Integer entities) {
            order.add("delete:" + entities);
            return deleteResults.poll();
        }
    }

    private static StubEntityManager stub(List<Boolean> insert, List<Boolean> update,
                                         List<Boolean> save, List<Boolean> delete) {
        StubEntityManager em = new StubEntityManager();
        em.insertResults.addAll(insert);
        em.updateResults.addAll(update);
        em.saveResults.addAll(save);
        em.deleteResults.addAll(delete);
        return em;
    }

    @Test
    @DisplayName("insertEntities：按迭代序逐条 insertEntity，返回值=成功条数")
    void insertEntitiesCountsSuccessInIterationOrder() {
        StubEntityManager em = stub(List.of(true, false, true), List.of(), List.of(), List.of());
        int count = em.insertEntities(new ArrayList<>(List.of(1, 2, 3)));
        assertEquals(2, count, "true 计数现状：3 条中 2 条成功");
        assertEquals(List.of("insert:1", "insert:2", "insert:3"), em.order, "委托与迭代顺序现状");
    }

    @Test
    @DisplayName("updateEntities：全 false 返回 0 且仍逐条调用")
    void updateEntitiesAllFalseReturnsZeroButCallsEach() {
        StubEntityManager em = stub(List.of(), List.of(false, false), List.of(), List.of());
        int count = em.updateEntities(new ArrayList<>(List.of(7, 8)));
        assertEquals(0, count);
        assertEquals(List.of("update:7", "update:8"), em.order);
    }

    @Test
    @DisplayName("saveEntities：混合成败计数")
    void saveEntitiesCountsMixedResults() {
        StubEntityManager em = stub(List.of(), List.of(), List.of(true, true, false), List.of());
        int count = em.saveEntities(new ArrayList<>(List.of(1, 2, 3)));
        assertEquals(2, count);
        assertEquals(List.of("save:1", "save:2", "save:3"), em.order);
    }

    @Test
    @DisplayName("deleteEntities：逐条委托 deleteEntity 并计数")
    void deleteEntitiesCountsSuccess() {
        StubEntityManager em = stub(List.of(), List.of(), List.of(), List.of(false, true));
        int count = em.deleteEntities(new ArrayList<>(List.of(4, 5)));
        assertEquals(1, count);
        assertEquals(List.of("delete:4", "delete:5"), em.order);
    }

    @Test
    @DisplayName("空集合边界：四方法均返回 0 且零调用")
    void emptyCollectionReturnsZeroWithoutCalls() {
        StubEntityManager em = stub(List.of(), List.of(), List.of(), List.of());
        List<Integer> empty = new ArrayList<>();
        assertEquals(0, em.insertEntities(empty));
        assertEquals(0, em.updateEntities(empty));
        assertEquals(0, em.saveEntities(empty));
        assertEquals(0, em.deleteEntities(empty));
        assertTrue(em.order.isEmpty(), "空集合不产生任何单实体调用");
    }

    @Test
    @DisplayName("null 集合现状边界：for-each 直接 NPE（禁止顺手修，钉桩在册）")
    void nullCollectionThrowsNpeByCurrentContract() {
        StubEntityManager em = stub(List.of(), List.of(), List.of(), List.of());
        assertThrows(NullPointerException.class, () -> em.insertEntities(null));
        assertThrows(NullPointerException.class, () -> em.updateEntities(null));
        assertThrows(NullPointerException.class, () -> em.saveEntities(null));
        assertThrows(NullPointerException.class, () -> em.deleteEntities(null));
    }

    @Test
    @DisplayName("LinkedHashSet 传入：迭代插入序保持（收敛后遍历语义一致）")
    void insertionOrderedCollectionKeepsIterationOrder() {
        StubEntityManager em = stub(List.of(true, true), List.of(), List.of(), List.of());
        LinkedHashSet<Integer> entities = new LinkedHashSet<>();
        entities.add(9);
        entities.add(8);
        assertEquals(2, em.insertEntities(entities));
        assertEquals(List.of("insert:9", "insert:8"), em.order);
    }

}
