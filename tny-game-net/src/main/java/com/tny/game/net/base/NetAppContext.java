package com.tny.game.net.base;

import com.tny.game.common.context.*;
import com.tny.game.common.lifecycle.unit.annotation.*;

import java.util.List;

@UnitInterface
public interface NetAppContext {

    /**
     * @return 应用名字
     */
    String getName();

    /**
     * @return 应用类型标识
     */
    String getAppType();

    /**
     * @return 应用类型
     */
    default AppType appType() {
        return AppTypes.of(this.getAppType());
    }

    /**
     * @return 作用域类型标识
     */
    String getScopeType();

    /**
     * @return 作用域类型
     */
    default ScopeType scopeType() {
        return ScopeTypes.of(this.getScopeType());
    }

    /**
     * @return 本地
     */
    String getLocale();

    /**
     * 全局唯一 id
     * 确保所有的服务器类型的 id 都不重复
     *
     * @return 唯一 id
     */
    long getServerId();

    /**
     * @return 获取加载包路径
     */
    List<String> getScanPackages();

    /**
     * @return 全局上下文
     */
    Attributes attributes();

}
