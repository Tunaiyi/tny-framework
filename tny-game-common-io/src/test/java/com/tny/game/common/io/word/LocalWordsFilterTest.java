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
package com.tny.game.common.io.word;

import org.junit.jupiter.api.*;

import java.io.*;
import java.nio.charset.StandardCharsets;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 敏感词过滤契约（starter-net 的 TextCheckFilter 热路径依赖 hasBadWords）。
 * 覆盖：基础屏蔽/命中、大小写、跨词不误报、未加载 fail-open 可见性、
 * 以及回归修复——热更读取 IO 失败必须保留旧词表（原实现把词表清空导致审核整体失效）。
 */
class LocalWordsFilterTest {

    private static InputStream wordsStream(String... words) {
        StringBuilder sb = new StringBuilder();
        for (String w : words) {
            sb.append(w).append('\n');
        }
        return new ByteArrayInputStream(sb.toString().getBytes(StandardCharsets.UTF_8));
    }

    private static LocalWordsFilter loadedWith(String... words) throws Exception {
        LocalWordsFilter filter = new LocalWordsFilter("unused-path", "*");
        filter.doLoad(wordsStream(words), false);
        return filter;
    }

    /** 基础：中文词命中即全量屏蔽，无关字符不动 */
    @Test
    void masksMatchedChineseWords() throws Exception {
        LocalWordsFilter filter = loadedWith("敏感词", "外挂");
        assertEquals("这是***啊", filter.filterWords("这是敏感词啊"));
        assertTrue(filter.hasBadWords("开外挂真爽"));
        assertFalse(filter.hasBadWords("正常聊天内容"));
    }

    /** 大小写不敏感：词表小写、输入大写命中；掩码按原位置 */
    @Test
    void caseInsensitiveMatching() throws Exception {
        LocalWordsFilter filter = loadedWith("abc", "de");
        assertEquals("XX***xx", filter.filterWords("XXABCxx"));
        assertTrue(filter.hasBadWords("xxABdE")); // "ab" 后接 "d" 不成词，但 "de" 命中
        assertTrue(filter.hasBadWords("xxde"));
    }

    /** 跨词不误报："xxabcdxx" 中 abc 成词、d 单独不成词（de 需要 e），恰好掩 3 个字符 */
    @Test
    void crossWordBoundaryNoFalsePositive() throws Exception {
        LocalWordsFilter filter = loadedWith("abc", "de");
        assertEquals("xx***dxx", filter.filterWords("xxabcdxx"));
    }

    /** 空串与无命中：原样返回，不抛异常 */
    @Test
    void emptyAndCleanInput() throws Exception {
        LocalWordsFilter filter = loadedWith("abc");
        assertEquals("", filter.filterWords(""));
        assertFalse(filter.hasBadWords(""));
        assertEquals("hello", filter.filterWords("hello"));
    }

    /** 未 load：过滤原样返回、hasBadWords false——fail-open 契约（安全语义要求调用方必须先 load） */
    @Test
    void unloadedFilterPassesThrough() {
        LocalWordsFilter filter = new LocalWordsFilter("no-such-file", "*");
        assertEquals("敏感词", filter.filterWords("敏感词"));
        assertFalse(filter.hasBadWords("敏感词"));
    }

    /**
     * 回归（D2）：热更读取抛 IOException 时必须保留旧词表，不得清空。
     * 原实现 catch 后继续用空 trie 替换 rootNode → 敏感词过滤整体失效。
     */
    @Test
    void reloadFailureKeepsOldTrie() throws Exception {
        LocalWordsFilter filter = loadedWith("敏感词");
        assertTrue(filter.hasBadWords("含敏感词句子"));

        InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("模拟文件瞬时不可读");
            }
        };
        filter.doLoad(broken, true); // reload 语义

        assertTrue(filter.hasBadWords("含敏感词句子"), "热更失败后旧词表必须保留（fail-closed）");
        assertEquals("含***句子", filter.filterWords("含敏感词句子"));
    }

    /** 首载（reload=false）IO 失败必须上抛，而不是带空词表"成功"启动 */
    @Test
    void firstLoadFailurePropagates() {
        LocalWordsFilter filter = new LocalWordsFilter("unused-path", "*");
        InputStream broken = new InputStream() {
            @Override
            public int read() throws IOException {
                throw new IOException("模拟首读失败");
            }
        };
        assertThrows(IOException.class, () -> filter.doLoad(broken, false));
    }

    /** 屏蔽符号为空白/null 时回落默认 '*'（原实现 filterChar.charAt(0) 直接抛异常） */
    @Test
    void blankMaskCharFallsBackToDefault() throws Exception {
        LocalWordsFilter filter = new LocalWordsFilter("unused-path", "");
        filter.doLoad(wordsStream("abc"), false);
        assertEquals("xx***xx", filter.filterWords("xxabcxx"));
        LocalWordsFilter nullCharFilter = new LocalWordsFilter("unused-path", null);
        nullCharFilter.doLoad(wordsStream("abc"), false);
        assertEquals("xx***xx", nullCharFilter.filterWords("xxabcxx"));
    }

}
