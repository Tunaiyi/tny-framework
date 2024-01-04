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

package com.tny.game.common.io.word;

import com.google.common.collect.ImmutableList;
import com.tny.game.common.io.config.*;
import org.apache.commons.io.IOUtils;

import java.io.*;
import java.util.*;

/**
 * 敏感词过滤
 *
 * @author rongjin.zheng
 * @since 2010-10-26 上午10:51:52
 */
public class LocalWordsFilter extends FileLoader implements WordsFilter {

    /**
     * 敏感词ROOT节点
     *
     * @uml.property name="rootNode"
     * @uml.associationEnd
     */
    private Node rootNode = null;

    /**
     * 屏蔽符号
     */
    private char maskChar = FILTER;

    public LocalWordsFilter(String file, String filterChar) {
        super(file);
        this.maskChar = filterChar.charAt(0);
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
    protected void doLoad(InputStream inputStream, boolean reload) {
        List<String> badWords = ImmutableList.of();
        try {
            badWords = IOUtils.readLines(inputStream, "UTF-8");
        } catch (IOException e) {
            e.printStackTrace();
        }
        Node node = new Node('R');
        for (String str : badWords) {
            if (str != null && str.length() > 0) {
                char[] chars = str.toLowerCase().toCharArray();
                if (chars.length > 0) {
                    this.insertNode(node, chars, 0);
                }
            }
        }
        this.rootNode = node;
    }

}
