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

import com.tny.game.common.io.config.*;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.*;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * 敏感词过滤
 *
 * @author rongjin.zheng
 * @since 2010-10-26 上午10:51:52
 */
public class LocalWordsFilter extends FileLoader implements WordsFilter {

    private static final Logger LOG = LoggerFactory.getLogger(LocalWordsFilter.class);

    /**
     * 敏感词ROOT节点
     *
     * @uml.property name="rootNode"
     * @uml.associationEnd
     */
    private volatile Node rootNode = null;

    /**
     * 屏蔽符号
     */
    private char maskChar = FILTER;

    public LocalWordsFilter(String file, String filterChar) {
        super(file);
        if (StringUtils.isNotBlank(filterChar)) {
            this.maskChar = filterChar.charAt(0);
        }
    }

    @Override
    public String filterWords(String content) {
        return this.filterWords(content, this.maskChar);
    }

    @Override
    public String filterWords(String content, char replace) {
        int a = 0;
        char[] chars = content.toLowerCase().toCharArray();
        char[] sourceChars = content.toCharArray();
        Node node = this.rootNode;
        if (node == null) {
            return content;
        }
        List<Character> word = new ArrayList<>();
        while (a < chars.length) {
            node = node.findNode(chars[a]);
            if (node == null) {
                node = this.rootNode;
                a = a - word.size();
                word.clear();
            } else if (node.flag == 1) {
                word.add(chars[a]);
                for (int i = 0; i < word.size(); i++) {
                    sourceChars[a - i] = replace;
                }
                a = a - word.size() + 1;
                word.clear();
                node = this.rootNode;
            } else {
                word.add(chars[a]);
            }
            a++;
        }
        return String.valueOf(sourceChars);
    }

    @Override
    public boolean hasBadWords(String content) {
        int a = 0;
        char[] chars = content.toLowerCase().toCharArray();
        Node node = this.rootNode;
        if (node == null) {
            return false;
        }
        List<Character> word = new ArrayList<>();
        while (a < chars.length) {
            node = node.findNode(chars[a]);
            if (node == null) {
                node = this.rootNode;
                a = a - word.size();
                word.clear();
            } else if (node.flag == 1) {
                return true;
            } else {
                word.add(chars[a]);
            }
            a++;
        }
        return false;
    }

    @Override
    public int order() {
        return -1;
    }

    private void insertNode(Node node, char[] cs, int index) {
        Node n = node.findNode(cs[index]);
        if (n == null) {
            char c = cs[index];
            n = new Node(c);
            node.nodes.put(c, n);
        }
        if (index == (cs.length - 1)) {
            n.flag = 1;
        }

        index++;
        if (index < cs.length) {
            this.insertNode(n, cs, index);
        }
    }

    private static class Node implements Comparable<Node> {

        private final char c;

        private int flag;

        private final Map<Character, Node> nodes = new HashMap<>();

        private Node findNode(char c) {
            return this.nodes.get(c);
        }

        public Node(char c) {
            this.c = c;
            this.flag = 0;
        }

        @Override
        public int compareTo(Node o) {
            return this.c - o.c;
        }

        @Override
        public String toString() {
            return " [ " + this.c + "] ";
        }

    }

    @Override
    protected void doLoad(InputStream inputStream, boolean reload) throws IOException {
        List<String> badWords;
        try {
            // 词表逐行读取走 JDK 标准库：readLine 失败抛出的就是 IOException 本身，守卫的 catch 契约
            // 不随第三方库实现漂移（commons-io readLines 与 BufferedReader.lines() 在 2.14 系把读失败
            // 包装成 UncheckedIOException，守卫会 catch 不到——adapt-commons-io-214-word-filter 教训）。
            // 行内容语义与 IOUtils.readLines 一致：逐行收集、不含行尾符；关流由基类 finally 统一负责。
            badWords = new ArrayList<>();
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                badWords.add(line);
            }
        } catch (IOException e) {
            if (reload && this.rootNode != null) {
                // 热更读取失败：保留旧词表（fail-closed），不得清空导致敏感词过滤整体失效
                LOG.error("#词表热更#读取 {} 失败，保留当前词表", getPath(), e);
                return;
            }
            throw e;
        }
        Node node = new Node('R');
        for (String str : badWords) {
            if (str != null && !str.isEmpty()) {
                char[] chars = str.toLowerCase().toCharArray();
                if (chars.length > 0) {
                    this.insertNode(node, chars, 0);
                }
            }
        }
        this.rootNode = node;
    }

}
