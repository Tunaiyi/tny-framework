/*
 * Copyright (c) 2020 Tunaiyi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.tny.game.common.utils;

import com.tny.game.common.collection.CollectorsAide;
import com.tny.game.common.collection.IterableAide;
import com.tny.game.common.number.NumberAide;
import com.tny.game.common.version.Version;
import org.junit.jupiter.api.*;

import java.time.*;
import java.util.Iterator;

import static org.junit.jupiter.api.Assertions.*;

/**
 * utils/math/version 修复契约：StringAide.ifNotBlank 语义反转、NumberAide.compare int 溢出、
 * NumberFormatAide 溢出/NPE/越界、Version.getSubVersions 差一、DateTimeAide 三处必抛缺陷。
 */
class UtilsContractFixTest {

    // ---- StringAide.ifNotBlank ----

    @Test
    void ifNotBlankSemanticsAligned() {
        assertEquals("keep", StringAide.ifNotBlank("keep", "else"));
        assertEquals("else", StringAide.ifNotBlank(null, "else"));
        assertEquals("else", StringAide.ifNotBlank("  ", "else"));
        // 与 Supplier 重载一致
        assertEquals("keep", StringAide.ifNotBlank("keep", () -> "else"));
        assertEquals("else", StringAide.ifNotBlank(null, () -> "else"));
    }

    // ---- NumberAide.compare ----

    @Test
    void numberCompareNoIntOverflow() {
        assertTrue(NumberAide.compare(Integer.MAX_VALUE, Integer.MIN_VALUE) > 0,
                "int 相减溢出翻号（原实现返回负数）");
        assertTrue(NumberAide.compare(Integer.MIN_VALUE, Integer.MAX_VALUE) < 0);
        assertEquals(0, NumberAide.compare(7, 7));
        assertTrue(NumberAide.compare(7L, 7) == 0 || NumberAide.compare(7, 7L) == 0);
    }

    // ---- NumberFormatAide.alignDigits ----

    @Test
    void alignDigitsMatrix() {
        assertEquals("005", NumberFormatAide.alignDigits(5, 999));
        assertEquals("999", NumberFormatAide.alignDigits(999, 999));
        assertEquals("099", NumberFormatAide.alignDigits(99, 100));
        // 位数达标（含 hashCode 位数超出 maxCode：原实现负索引越界）
        assertEquals("1000", NumberFormatAide.alignDigits(1000, 999));
        // Long.MAX_VALUE 上界：原实现 higherEntry 返回 null → NPE
        assertEquals(String.valueOf(Long.MAX_VALUE), NumberFormatAide.alignDigits(Long.MAX_VALUE, Long.MAX_VALUE));
        assertEquals("0" + (Long.MAX_VALUE / 10), NumberFormatAide.alignDigits(Long.MAX_VALUE / 10, Long.MAX_VALUE));
    }

    // ---- Version ----

    @Test
    void versionSubSegmentsNoOffByOne() {
        Version version = Version.of("1.2.3");
        assertEquals(3, version.getSubVersions(3).length, "getSubVersions(3) 应取满 3 段（原差一只给 2）");
        assertArrayEquals(new String[]{"1", "2", "3"}, version.getSubVersions(5));
        assertArrayEquals(new String[]{"1", "2", "3"}, version.getSubVersions(0, 3));
        assertEquals("2", version.getSubVersion(1));
    }

    // ---- DateTimeAide ----

    @Test
    void dateParseFixed() {
        // 原实现：日期串无时间字段，ZonedDateTime.parse 必抛
        ZonedDateTime parsed = assertDoesNotThrow(() -> DateTimeAide.date("2016-01-01"));
        assertEquals(2016, parsed.getYear());
        assertEquals(1, parsed.getMonthValue());
        assertEquals(1, parsed.getDayOfMonth());
        assertEquals(0, parsed.getHour());
        // 带 formatter 的重载必须真正使用 formatter（原忽略参数）
        ZonedDateTime display = assertDoesNotThrow(() ->
                DateTimeAide.date("01/02/2016", DateTimeAide.DISPLAY_DATE_FORMAT));
        assertEquals(2016, display.getYear());
        assertEquals(1, display.getMonthValue());
        assertEquals(2, display.getDayOfMonth());
    }

    @Test
    void dateTimeRoundTrips() {
        LocalDateTime origin = LocalDateTime.of(2016, 1, 1, 12, 30, 45, 123_000_000);
        long encoded = DateTimeAide.time2MillisLong(origin);
        LocalDateTime decoded = DateTimeAide.millisLong2Time(encoded);
        // 原实现把毫秒当纳秒写入：毫秒位丢失
        assertEquals(origin, decoded, "time2MillisLong ↔ millisLong2Time 必须可逆含毫秒");
    }

    @Test
    void date2IntFromInstant() {
        Instant instant = Instant.ofEpochMilli(1_700_000_000_000L);
        LocalDate local = instant.atZone(ZoneId.systemDefault()).toLocalDate();
        int viaInstant = assertDoesNotThrow(() -> DateTimeAide.date2Int(instant));
        assertEquals(DateTimeAide.date2Int(local), viaInstant);
    }

    // ---- IterableAide ----

    @Test
    void emptyIteratorViolatorsFixed() {
        Iterable<Object> empty = IterableAide.empty();
        Iterator<Object> iterator = empty.iterator();
        assertFalse(iterator.hasNext());
        assertThrows(java.util.NoSuchElementException.class, iterator::next);
    }

    // ---- CollectorsAide.toMap ----

    @Test
    void toMapDuplicateKeyLastWin() {
        // 两个同 class 实例共享键：原实现抛 IllegalStateException（与单条注册的覆盖语义不一致）
        java.util.ArrayList<String> r1 = new java.util.ArrayList<>();
        java.util.ArrayList<String> r2 = new java.util.ArrayList<>();
        var map = java.util.stream.Stream.of(r1, r2)
                .collect(CollectorsAide.toMap(Object::getClass));
        assertEquals(1, map.size());
        assertSame(r2, map.get(java.util.ArrayList.class), "重复键 last-win");
    }

    // ---- RandomObject compareTo ----

    @Test
    void randomObjectCompareNoOverflow() {
        var a = new com.tny.game.common.math.RandomObject<>("a", 0);
        var min = new com.tny.game.common.math.RandomObject<>("m", Integer.MIN_VALUE);
        // 降序比较器：大值排前。a(0) 应排在 min(MIN) 之前
        assertTrue(a.compareTo(min) < 0, "降序：0 应排在 MIN 之前");
        // 关键回归：MIN.compareTo(0) 原实现 (MIN-0)*-1 溢出后仍为负（错判 MIN 在前）
        assertTrue(min.compareTo(a) > 0, "MIN 应排在 0 之后（原溢出翻号判成在前）");
    }

    // ---- Booleans ----

    @Test
    void booleansStaticUsability() {
        assertTrue(Booleans.isFalse(null));
        assertTrue(Booleans.isFalse(Boolean.FALSE));
        assertFalse(Booleans.isFalse(Boolean.TRUE));
        assertTrue(Booleans.isTrue(Boolean.TRUE));
    }

    // ---- DoneResults.map ----

    @Test
    void doneResultMapOnSuccessTransforms() {
        var success = com.tny.game.common.result.DoneResults.success("raw");
        var mapped = com.tny.game.common.result.DoneResults.map(success, String::length);
        assertTrue(mapped.isSuccess());
        assertEquals(3, mapped.get());
        // 失败结果：不再把 null 喂给 mapper（原断言反转只允许失败进入）
        var failure = com.tny.game.common.result.DoneResults.failure();
        var mappedFailure = com.tny.game.common.result.DoneResults.map(failure, s -> {
            fail("失败结果不得调用 mapper");
            return 0;
        });
        assertTrue(mappedFailure.isFailure());
    }

}
