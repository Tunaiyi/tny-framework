package com.tny.game.common.word;

import com.tny.game.common.config.FileLoader;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

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
     * 用来替换敏感词的符号
     */
    private char FILTER = '*';

    public LocalWordsFilter(String file, String filterChar) {
        super(file);
        this.FILTER = filterChar.charAt(0);
    }

    @Override
    public String filterWords(String content, char replace) {
        int a = 0;
        char[] chars = (new String(content)).toLowerCase().toCharArray();
        char[] sourceChars = content.toCharArray();
        Node node = this.rootNode;
        if (node == null)
            return content;
        List<String> word = new ArrayList<String>();
        while (a < chars.length) {
            node = node.findNode(chars[a]);
            if (node == null) {
                node = this.rootNode;
                a = a - word.size();
                word.clear();
            } else if (node.flag == 1) {
                word.add(String.valueOf(chars[a]));
                for (int i = 0; i < word.size(); i++) {
                    sourceChars[a - i] = replace;
                }
                a = a - word.size() + 1;
                word.clear();
                node = this.rootNode;
            } else {
                word.add(String.valueOf(chars[a]));
            }
            a++;
        }
        return String.valueOf(sourceChars);
    }

    @Override
    public String filterWords(String content) {
        return this.filterWords(content, this.FILTER);
    }

    @Override
    public boolean hasBadWords(String content) {
        int a = 0;
        char[] chars = content.toLowerCase().toCharArray();
        Node node = this.rootNode;
        if (node == null)
            return false;
        List<String> word = new ArrayList<String>();
        while (a < chars.length) {
            node = node.findNode(chars[a]);
            if (node == null) {
                node = this.rootNode;
                a = a - word.size();
                word.clear();
            } else if (node.flag == 1) {
                return true;
            }
            a++;
        }
        return false;
    }

    private void insertNode(Node node, char[] cs, int index) {
        Node n = node.findNode(cs[index]);
        if (n == null) {
            n = new Node(cs[index]);
            node.nodes.add(n);
        }
        if (index == (cs.length - 1))
            n.flag = 1;

        index++;
        if (index < cs.length)
            this.insertNode(n, cs, index);
    }

    private class Node implements Comparable<Node> {
        private char c;
        private int flag;
        private Set<Node> nodes = new TreeSet<Node>();

        private Node findNode(char c) {
            for (Node n : this.nodes) {
                if (n.c == c) {
                    return n;
                }
            }
            return null;
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
    protected void doLoad(InputStream inputStream) {
        List<String> badwords = new ArrayList<String>();
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(inputStream));
            String line;
            line = reader.readLine();
            while (line != null) {
                badwords.add(line);
                line = reader.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Node node = new Node('R');
        for (String str : badwords) {
            if (str != null && str.length() > 0) {
                char[] chars = str.toCharArray();
                if (chars.length > 0)
                    this.insertNode(node, chars, 0);
            }
        }
        this.rootNode = node;
    }

}
