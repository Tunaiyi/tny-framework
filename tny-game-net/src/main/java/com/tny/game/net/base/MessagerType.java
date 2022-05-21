package com.tny.game.net.base;

import com.tny.game.common.enums.*;

/**
 * 通讯者类型
 * <p>
 *
 * @author Kun Yang
 * @date 2022/4/21 04:20
 **/
public interface MessagerType extends IntEnumerable {

    String DEFAULT_USER_TYPE = "_user";

    String ANONYMITY_USER_TYPE = "_anonymity";

    String getGroup();

}
