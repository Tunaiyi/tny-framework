package com.tny.game.net.serve;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author : kgtny
 * @date : 2021/11/13 1:09 上午
 */
public interface Serve {

    /**
     * @return 获取服务名
     */
    String getService();

    /**
     * @return 发现服务器服务名
     */
    String getServeName();

    /**
     * @return 获取服务名(获取服务名 未设置则返回ServeName)
     */
    default String serviceName() {
        return ifBlank(this.getService(), this.getServeName());
    }

    /**
     * @return 获取服务名(获取服务名 未设置则返回ServeName)
     */
    default String discoverService() {
        return ifBlank(this.getServeName(), this.getService());
    }

}
