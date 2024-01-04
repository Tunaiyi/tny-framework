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
