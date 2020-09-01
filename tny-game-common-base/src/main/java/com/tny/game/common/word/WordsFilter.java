package com.tny.game.common.word;

/**
 * 屏蔽字接口
 *
 * @author Kun.y
 */
public interface WordsFilter {

    /**
     * 用来替换敏感词的符号
     */
    char FILTER = '*';

    /**
     * 过滤屏蔽字,替代为*
     *
     * @param content 要检测的内容
     * @return 返回过滤后的内容
     */
    String filterWords(String content);

    /**
     * 过滤屏蔽字,替代为 replace
     *
     * @param content 要检测的内容
     * @param replace 替代为的文字
     * @return 返回过滤后的内容
     */
    String filterWords(String content, char replace);

    /**
     * 检测内容是否有屏蔽字
     *
     * @param content 要检测的内容
     * @return 有屏蔽字返回true 无则返回false
     */
    boolean hasBadWords(String content);

    /**
     * @return 过滤器顺序, 越小越先过滤
     */
    int order();

}
