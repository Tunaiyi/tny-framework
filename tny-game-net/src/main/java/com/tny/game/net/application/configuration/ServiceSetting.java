package com.tny.game.net.application.configuration;

import static com.tny.game.common.utils.StringAide.*;

/**
 * <p>
 *
 * @author kgtny
 * @date 2023/12/28 09:37
 **/
public interface ServiceSetting {

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
