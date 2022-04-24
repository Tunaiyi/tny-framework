package com.tny.game.basics.upgrade;

import com.tny.game.basics.item.*;

/**
 * 经验模型
 * Created by Kun Yang on 2017/4/5.
 */
public interface ExpModel extends StuffModel {

    /**
     * @return 获取经验类型
     */
    ExpType getExpType();

}
